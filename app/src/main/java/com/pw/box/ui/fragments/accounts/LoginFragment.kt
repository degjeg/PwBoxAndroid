package com.pw.box.ui.fragments.accounts

// import com.pw.box.cache.Counter;
import android.app.Activity
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v7.app.AlertDialog
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import android.widget.Toast
import com.pw.box.App
import com.pw.box.BuildConfig
import com.pw.box.R
import com.pw.box.cache.Cache
import com.pw.box.cache.Constants
import com.pw.box.core.C
import com.pw.box.core.Cm
import com.pw.box.core.ErrorCodes
import com.pw.box.core.cmds.LoginTask
import com.pw.box.databinding.FragmentLoginBinding
import com.pw.box.offline.*
import com.pw.box.tool.DoubleTabHelper
import com.pw.box.ui.base.BaseFragment
import com.pw.box.ui.base.ContainerActivity
import com.pw.box.ui.fragments.home.HomeFragment
import com.pw.box.ui.fragments.setting.HelpFragment
import com.pw.box.ui.widgets.PasswordLengthChecker
import com.pw.box.utils.Aes256
import com.pw.box.utils.L
import com.pw.box.utils.PrefUtil


/**
 * 用户登录界面
 * Created by danger on 16/8/28.
 */
class LoginFragment : BaseFragment(), LoginTask.LoginHandler {

    private val REQ_REG = 1

    private var account: String = ""
    private var password: String = ""
    private var registeredPassword: String = ""
    private var pwFocusChangeListener: View.OnFocusChangeListener = View.OnFocusChangeListener { view, b ->
        if (b) {
            scrollToBottom()
        }
    }

    var binding: FragmentLoginBinding? = null


    override fun onBackPressed(): Boolean {
        DoubleTabHelper.pressAgainToExit(context)
        return true
    }


    // override fun onCreate(savedInstanceState: Bundle?) {
    //     super.onCreate(savedInstanceState)
    //
    //     // if (BuildConfig.DEBUG) {
    //     //     EncryptUtil.main(null)
    //     // }
    // }
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, null, false)

        val binding = this.binding ?: return null

        binding.etPassword.setLengthChecker(PasswordLengthChecker())
        binding.etPassword.setOnClickListener { _ ->
            scrollToBottom()
        }

        binding.btnLogin.setOnClickListener(this)
        binding.btnRegister.setOnClickListener(this)
        binding.btnForgetPassword.setOnClickListener(this)
        binding.btnOfflineUse.setOnClickListener(this)

        binding.titleBar.setRightButtonClickListener {
            ContainerActivity.go(activity, HelpFragment::class.java, null)
        }


        // -for test ☟
        if (BuildConfig.DEBUG) {
            binding.etAccount.setText("测试6")
            binding.etPassword.setText("测试1234")
        }
        // -for test ☝︎

        binding.etPassword.onFocusChangeListener = pwFocusChangeListener
        binding.etPassword.setOnClickListener { scrollToBottom() }


        initTest(binding.btnLogin)
        return binding.root
    }

    private fun scrollToBottom() {
        binding?.scrollView?.postDelayed({
            binding?.scrollView?.fullScroll(ScrollView.FOCUS_DOWN)
        }, 300)
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v.id) {
            R.id.btn_login -> onClickLogin()
            R.id.btn_register -> onClickRegister()
            R.id.btn_forget_password -> onClickForgetPassword()
            R.id.btn_offline_use -> onClickOfflineUse()
        }
    }

    private fun onClickLogin() {
        if (L.E) L.get().e("Task_LoginFragment", "onClickLogin")

        val binding = this.binding ?: return

        account = binding.etAccount.text.toString().trim()
        password = binding.etPassword.text.toString().trim()

        if (account.length < C.min_account_len) {
            toast(R.string.message_error_account)
            return
        } else if (password.toByteArray().size < C.min_password_len) { // 默认utf-8
            toast(R.string.error_password_error)
            return
        }
        showProgressDialog(R.string.message_logging)
        try {
            val pwF = Aes256.fillKey(password, Aes256.FILL_TYPE_PW)
            val pwR = Aes256.fillKey(password, Aes256.FILL_TYPE_RAW_KEY)
            // Cm.get().login(account, pwF, pwR, this);

            val loginTask = LoginTask(account,
                    pwF, pwR, this)

            loginTask.execute()
        } catch (e: Exception) {
            e.printStackTrace()
            onLoginFail(-1)
        }
    }


    private fun onClickRegister() {
        val intent = ContainerActivity.getIntent(activity, RegisterFragment::class.java, null)

        startActivityForResult(intent, REQ_REG)
    }

    private fun onClickForgetPassword() {
        val binding = this.binding ?: return

        account = binding.etAccount.text.toString().trim()
        if (!TextUtils.isEmpty(registeredPassword)) {
            val builder = SpannableStringBuilder()
            val msg = getString(R.string.message_registered_password, registeredPassword)
            builder.append(msg)
            val start = msg.indexOf("\n")
            val end = msg.indexOf("\n", start + 2)
            builder.setSpan(ForegroundColorSpan(-0x10000), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

            showTipDialog(builder)
            return
        }
        if (account.trim().length < 3) {
            toast(R.string.please_input_count)
            return
        }

        val args = Bundle()
        args.putString(RetrievePasswordStep1Fragment.EXTRA_ACCOUNT, account)
        ContainerActivity.go(activity, RetrievePasswordStep1Fragment::class.java, args)
    }


    private fun onClickOfflineUse() {
        val tmpAccount = binding!!.etAccount.text.toString().trim()
        val tmpPassword = binding!!.etPassword.text.toString().trim()
        if (tmpAccount.length < C.min_account_len) {
            toast(R.string.message_error_account)
            return
        } else if (tmpPassword.toByteArray(Charsets.UTF_8).size < C.min_password_len) { // 默认utf-8
            toast(R.string.error_password_len)
            return
        }
        val pwKey = "offline_key_" + tmpAccount
        val requiredLoginKey = PrefUtil.getString(context, pwKey, "")

        if (requiredLoginKey.isEmpty()) { // 新用户
            val dlg = AlertDialog.Builder(context)
            dlg.setTitle(R.string.tip)
            dlg.setMessage(R.string.offline_first_use_tips)
            dlg.setNegativeButton(R.string.cancel, null)
            dlg.setPositiveButton(R.string.sure, { _, _ ->

                showProgressDialog(false)
                Thread {
                    val offlineLoginKey = genLoginKey(context, tmpAccount, tmpPassword)

                    Handler(Looper.getMainLooper()).post {
                        offlineAccount = tmpAccount
                        offlinePassword = tmpPassword
                        PrefUtil.setString(context, pwKey, offlineLoginKey)
                        ContainerActivity.goClearTask(activity, OfflineFragment::class.java, null)
                    }
                }.start()
            })
            dlg.show()
        } else { // 老用户
            showProgressDialog(false)
            Thread {
                val verified = verifyLoginKey(context, tmpAccount, tmpPassword, requiredLoginKey)
                Handler(Looper.getMainLooper()).post {
                    if (verified) {
                        offlineAccount = tmpAccount
                        offlinePassword = tmpPassword
                        ContainerActivity.goClearTask(activity, OfflineFragment::class.java, null)
                    } else {
                        dismissDialog()
                        toast(R.string.error_password_error)
                    }
                }
            }.start()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        val binding = this.binding ?: return

        if (requestCode == REQ_REG) {
            val registeredAccount = data?.getStringExtra("account")
            registeredPassword = data?.getStringExtra("password") ?: ""
            binding.etAccount.setText(registeredAccount)
            binding.etPassword.setText("")
        }
    }

    override fun onLoginSuccess(isIgnored: Boolean) {
        dismissDialog()

        if (L.E) L.get().e("Task_LoginFragment", "onLoginSuccess")
        // save data to local ☟
        /* M.LoginReq loginReq = (M.LoginReq) reqPack;
        User user = Cache.get().getUser();
        Cache.get().setLoginStatus(Cache.LOGIN_STATUS_LOGGIN);
        user.setHavePretection(retPack.getHaveProtection());
        user.setAccount(account);
        user.setPwFilledLogin(loginReq.getPassword().toByteArray());
        byte[] rawKeyEncByPw = retPack.getRawKeyByPw().toByteArray();

        try {
            user.setRawKey(Aes256.decrypt(rawKeyEncByPw, Aes256.fillKey(password, Aes256.FILL_TYPE_RAW_KEY)));
        } catch (Exception e) {
            e.printStackTrace();
        } */
        // save data to local ☝︎

        // 更新账户信息
        if (Cache.get().patternUtil.havePattern()) {
            Cache.get().patternUtil.setLockPattern(
                    Cache.get().patternUtil.patternString,
                    Cache.get().patternUtil.patternCount
            )
        }
        if (!Cache.get().user.isHavePretection) { // 没有密码保护
            val dlgBuilder = AlertDialog.Builder(context)
            dlgBuilder.setTitle(R.string.tip)
            dlgBuilder.setMessage(R.string.message_please_set_protect)
            dlgBuilder.setNegativeButton(R.string.latter) { dialog, which ->
                // finish();
                // showFragment(ItemListFragment.class, null);
                // removeFragment(LoginFragment.this);
                finish()
                ContainerActivity.go(activity, HomeFragment::class.java, null)
            }

            dlgBuilder.setPositiveButton(R.string.set_protection_right_now) { dialog, which ->
                finish()
                ContainerActivity.go(activity, HomeFragment::class.java, null)
                ContainerActivity.go(activity, SetProtectionFragment::class.java, null)
            }
            dlgBuilder.setCancelable(false)
            dlgBuilder.create().show()
        } else {

            finish()

            // Counter.get().setRunning();
            ContainerActivity.go(activity, HomeFragment::class.java, null)
            // showFragment(ItemListFragment.class, null);
        }
        toast(R.string.login_success)
    }

    override fun onLoginFail(code: Int) {
        if (L.E) L.get().e("Task_LoginFragment", "onLoginFail" + code)
        dismissDialog()
        toast(ErrorCodes.getErrorDescription(code))
    }

    companion object {

        fun initTest(view: View) {
            if (!BuildConfig.DEBUG) {
                return
            }

            view.setOnLongClickListener { view ->
                val hosts = arrayOf<CharSequence>("pwbox.dengjun.tech:1000", "pwbox.dengjun.tech:1100", "pwbox.dengjun.tech:1200", "192.168.1.184:1100", // 美乐乐
                        "192.168.1.101:1100", // 下沙
                        "192.168.5.53:1100", "192.168.5.250:1100")

                Toast.makeText(view.context, "当前:"
                        + Constants.PROXY_HOST + ":" + Constants.PROXY_PORT + "\n"
                        + "" + Cm.get().host + ":" + Cm.get().port + "\n", Toast.LENGTH_LONG).show()

                val builder = AlertDialog.Builder(view.context)
                        .setTitle("选择服务器")
                        .setItems(hosts) { dialogInterface, i ->
                            val addr = hosts[i].toString()
                            val hp = addr.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

                            Cm.get().close()
                            // Cm.get().init();

                            PrefUtil.setString(view.context, Constants.PREF_KEY_HOST_PROXY, hp[0])
                            PrefUtil.setInt(view.context, Constants.PREF_KEY_PORT_PROXY, Integer.valueOf(hp[1])!!)

                            App.initTest()
                            // new GetServerAddrTask().enqueue();
                            Cm.get().init(hp[0], 1 + Integer.valueOf(hp[1])!!)
                            Cm.get().close()
                            // Cm.get().connect();
                        }

                builder.create().show()
                true
            }
        }
    }
}
