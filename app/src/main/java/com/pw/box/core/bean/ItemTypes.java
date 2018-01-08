package com.pw.box.core.bean;

import android.content.Context;

import com.pw.box.App;
import com.pw.box.R;
import com.pw.box.bean.protobuf.Data;
import com.pw.box.cache.GroupManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 密码类型
 * Created by danger on 16/9/13.
 */
public class ItemTypes {
    public static final int TYPE_BANK = 10; // 银行卡
    public static final int TYPE_WEBSITE = 11; // 网站
    public static final int TYPE_IM = 12; // 社交账号
    public static final int TYPE_MEMBER = 13; // 会员卡
    public static final int TYPE_SOCIA = 14; // 社保卡
    public static final int TYPE_GAME = 15; // 游戏

    public static final int SUB_TYPE_OTHER = 1; // 其它

    public static final int SUB_TYPE_TYPE = 2; // 类型
    public static final int SUB_TYPE_SUB_TYPE = 3; // 子类型
    public static final int SUB_TYPE_MARK = 4; //


    public static final int SUB_TYPE_EMAIL = 10; //
    public static final int SUB_TYPE_ACCOUNT = 11; //

    public static final int SUB_TYPE_CARD_NO = 14; // 卡号
    public static final int SUB_TYPE_PC_NUM = 15; // 电脑号

    public static final int SUB_TYPE_LOGIN_PASSWORD = 30; // 登录密码
    public static final int SUB_TYPE_QUERY_PASSWORD = 31; // 查询密码
    public static final int SUB_TYPE_PAY_PASSWORD = 32; // 支付密码
    public static final int SUB_TYPE_SCREEN_PASSWORD = 33; // 锁屏密码
    public static final int SUB_TYPE_CASH_PASSWORD = 34; // 取款密码

    public static final int SUB_TYPE_IM = 51; //
    public static final int SUB_TYPE_ADDRESS = 52; //
    public static final int SUB_TYPE_QA = 53; //
    public static final int SUB_TYPE_WORK = 54; //
    public static final int SUB_TYPE_URL = 55; //
    public static final int SUB_TYPE_NAME = 56; // 名字
    public static final int SUB_TYPE_PHONE = 57; //


    public static String getItemNameByType(Context context, int mainType, int subType) {
        // Item item = new Item(subType, 0);

        switch (subType) {
            case ItemTypes.SUB_TYPE_TYPE: // = 2; // 类型
                switch (mainType) {
                    case ItemTypes.TYPE_BANK:
                        return context.getString(R.string.bank_card);
                    case ItemTypes.TYPE_WEBSITE:
                        return context.getString(R.string.website);
                    case ItemTypes.TYPE_IM:
                        return context.getString(R.string.instant_messaging);
                    case ItemTypes.TYPE_GAME:
                        return context.getString(R.string.game);
                    default:
                        return context.getString(R.string.other);
                }
            case ItemTypes.SUB_TYPE_SUB_TYPE: //  = 3; // 子类型
                switch (mainType) {
                    case ItemTypes.TYPE_BANK:
                        return context.getString(R.string.bank_name);
                    default:
                        return context.getString(R.string.data_type);
                }
            case ItemTypes.SUB_TYPE_MARK: //  = 4; //
                return context.getString(R.string.remark);
            case ItemTypes.SUB_TYPE_EMAIL: // = 10; //
                return context.getString(R.string.email);
            case ItemTypes.SUB_TYPE_ACCOUNT: //  = 11; //
                return context.getString(R.string.account);
            case ItemTypes.SUB_TYPE_PHONE: // = 12; //
                return context.getString(R.string.phone_number);
            case ItemTypes.SUB_TYPE_CARD_NO: // = 14; // 卡号
                return context.getString(R.string.card_no);
            case ItemTypes.SUB_TYPE_PC_NUM: // = 15; // 电脑号
                return context.getString(R.string.computer_number);
            case ItemTypes.SUB_TYPE_LOGIN_PASSWORD: // = 30; // 登录密码
                return context.getString(R.string.password_4_login);
            case ItemTypes.SUB_TYPE_QUERY_PASSWORD: //  = 31; // 查询密码
                return context.getString(R.string.password_4_query);
            case ItemTypes.SUB_TYPE_PAY_PASSWORD: // = 32; // 支付密码
                return context.getString(R.string.password_4_pay);
            case ItemTypes.SUB_TYPE_SCREEN_PASSWORD: // = 33; // 锁屏密码
                return context.getString(R.string.password_4_lock_screen);
            case ItemTypes.SUB_TYPE_CASH_PASSWORD: //  = 34; // 取款密码
                return context.getString(R.string.password_4_cach);
            case ItemTypes.SUB_TYPE_IM: // = 51; //
                return context.getString(R.string.im);
            case ItemTypes.SUB_TYPE_ADDRESS: // = 52; //
                return context.getString(R.string.address);
            case ItemTypes.SUB_TYPE_QA: // = 53; //
                return context.getString(R.string.password_protection);
            case ItemTypes.SUB_TYPE_WORK: //  = 54; //
                return context.getString(R.string.work_unit);
            case ItemTypes.SUB_TYPE_URL: //  = 55; //
                return context.getString(R.string.url);
            case ItemTypes.SUB_TYPE_NAME: //  = 56; //
                return context.getString(R.string.name);
            case ItemTypes.SUB_TYPE_OTHER: // = 1; // 其它
            default:
                return context.getString(R.string.other);

        }

    }

    public static List<Item> getItems(Data data) {
        int type = data.type;
        List<Item> itemList = new ArrayList<>();
        Item itemOfType = new Item(ItemTypes.SUB_TYPE_TYPE, 0); // getItemByType(App.getContext(), type, ItemTypes.SUB_TYPE_TYPE);
        itemOfType.setName(App.getContext().getString(R.string.group));
        itemOfType.setValue(GroupManager.get().getGroupName(data.gpid, ""));

        itemList.add(itemOfType);

        itemOfType = new Item(ItemTypes.SUB_TYPE_SUB_TYPE, 0);  // getItemByType(App.getContext(), type, ItemTypes.SUB_TYPE_SUB_TYPE);
        itemOfType.setValue(data.sub_type);
        // itemOfType.setName(data.sub_type);
        itemList.add(itemOfType);

        for (int i = 0; i < data.items.size(); i++) {
            Item it = new Item(data.items.get(i).type, 0); // getItemByType(App.getContext(), type, data.items.get(i).type);
            it.setValue(data.items.get(i).value);
            it.setName(data.items.get(i).name);

            itemList.add(it);
        }
        return itemList;
    }
}
