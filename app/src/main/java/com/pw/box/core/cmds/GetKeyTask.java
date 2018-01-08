//package com.pw.box.core.cmds;
//
//
//import okio.ByteString;
//
//import com.pw.box.core.Cm;
//import com.pw.box.core.K;
//import com.pw.box.utils.Aes256;
//import com.pw.box.utils.RsaUtils;
//
///**
// * Created by danger on 16/10/26.
// */
//public class GetKeyTask extends Task<M.GetKeyRes> {
//    public GetKeyTask() {
//        super(false);
//        cmd = CmdIds.GETKEY;
//        //        // test start
//        //        String m = "91658215606265720002289105411988711410013273330668439545071200754382227997749871788467396015325274631566114743034861821332058350843439021496162286420009978611015369466893846589376450686508055200642722428451208959130898307816487442794893628934146495110774178761112944238358830764144981556753871049005460383057";
//        //        String public_exponent = "65537";
//        //        String private_exponent = "75724284385700461476783211368032055031261099397526770995442804730843208457972886432317296944531664092825666671450929375073667811283201281411846908401742820774991039105016661906800050395186731742575068847757755773343942228315211115844262390111483116593998726621422714878582713768753581330451708503468822134273";
//        //
//        //        // RsaUtils.encryptByPublicKey(encryptedByAes, K.r());
//        //        RSAPrivateKey rsaPrivateKey = RsaUtils.getPrivateKey(m, private_exponent);
//        //        byte[] decryptedByRsa = RsaUtils.decryptByPrivateKey(encryptedByRsa, rsaPrivateKey);
//        //        // test end
//    }
//
//    @Override
//    protected AbstractMessage prepareData() throws Exception {
//        M.GetKeyReq.Builder builder = M.GetKeyReq.newBuilder();
//        byte[] encryptedByAes = Aes256.encrypt(Cm.get().getConnection().getKeyLocal(), K.k1());
//
//        builder.setLen(encryptedByAes.length);
//        byte[] encryptedByRsa = RsaUtils.encryptByPublicKey(encryptedByAes, K.r());
//        builder.setKey(ByteString.copyFrom(encryptedByRsa));
//        return builder.build();
//    }
//
//    @Override
//    public void onSuccess(AbstractMessage reqPack, M.GetKeyRes retPack) {
//        try {
//            byte[] encryptedByAes = Aes256.decrypt(retPack.getRawKey().toByteArray(), K.k2());
//
//            Cm.get().getConnection().setSerKey(encryptedByAes);
//            if(L.E) logger.e(TAG, "got key");
//            Cm.get().autoLogin();
//        } catch (Exception e) {
//            // e.printStackTrace();
//            if(L.E) logger.e(TAG, "", e);
//        }
//
//        super.onSuccess(reqPack, retPack);
//    }
//}
//
