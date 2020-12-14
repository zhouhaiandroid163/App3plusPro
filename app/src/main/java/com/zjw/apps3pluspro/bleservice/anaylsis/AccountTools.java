package com.zjw.apps3pluspro.bleservice.anaylsis;

import com.xiaomi.wear.protobuf.AccountProtos;
import com.xiaomi.wear.protobuf.WearProtos;

public class AccountTools {


    static final int bind_status = 1; //绑定状态
    static final int bind_key = 2;//绑定key?=和“AccountID”可以对应上？？？？？
    static final int error_code = 3;//错误码
    static final int bind_info = 4;//绑定信息
    static final int bind_result = 5;//绑定结果
    static final int login_status = 6;//登录状态
    static final int verify_key = 7;//验证key
    static final int verify_result = 8;//验证结果


    public static AccountProtos.Account.Builder getAccount(int pos) {

        AccountProtos.Account.Builder account = AccountProtos.Account.newBuilder();


        switch (pos) {
            case bind_status:
                account.setBindStatus(false);
                break;

            case bind_key:
                account.setBindKey("my_key_02");
                break;

            case error_code:
                account.setErrorCode(AccountProtos.Account.ErrorCode.INFO_MISSING);
                break;

            case bind_info:
                account.setBindInfo(getBindInfo("123456789", "did", "modle", "aa:bb:cc:dd:ee:ff", "sing"));
                break;

            case bind_result:
                account.setBindResult(getBindResult("1008601", "token"));
                break;

            case login_status:
                account.setLoginStatus(true);
                break;

            case verify_key:
                account.setVerifyKey("123456");
                break;

            case verify_result:
                account.setVerifyResult(false);
                break;

        }

        return account;
    }

    public static AccountProtos.BindInfo getBindInfo(String bind_key, String did, String modle, String mac, String sign) {
        //绑定信息
        AccountProtos.BindInfo.Builder bind_info = AccountProtos.BindInfo.newBuilder();
        bind_info.setBindKey(bind_key);
        bind_info.setDid(did);
        bind_info.setModel(modle);
        bind_info.setMac(mac);
        bind_info.setSign(sign);

        return bind_info.build();
    }

    public static AccountProtos.BindResult getBindResult(String user_id, String token) {
        //绑定信息
        AccountProtos.BindResult.Builder bind_result = AccountProtos.BindResult.newBuilder();
        bind_result.setUserId(user_id);
        bind_result.setToken(token);
        return bind_result.build();
    }


    public static String analysisAccount(WearProtos.WearPacket wear) {

        AccountProtos.Account account = wear.getAccount();
        int id = wear.getId();
        int pos = account.getPayloadCase().getNumber();

        String result_str = "";
        String msg = "";

        System.out.println("数据封装 = " + "wear/type = " + wear.getType());
        System.out.println("数据封装 = " + "wear/id = " + wear.getId());
        System.out.println("数据封装 = " + "pos = " + pos);


        result_str += "wear/type = " + wear.getType() + "\n";
        result_str += "wear/id = " + wear.getId() + "\n";
        result_str += "pos = " + pos + "\n";

//        AccountProtos.Account.BIND_INFO_FIELD_NUMBER;

        result_str += "描述(参考):账号相关" + "-";
        msg += "描述(参考):账号相关" + "-";


        if (id == 0x00) {
            result_str += "绑定状态" + "\n";
            msg += "绑定状态" + "\n";
        } else if (id == 0x01) {
            result_str += "绑定开始" + "\n";
            msg += "绑定开始" + "\n";
        } else if (id == 0x02) {
            result_str += "绑定结果" + "\n";
            msg += "绑定结果" + "\n";
        } else if (id == 0x03) {
            result_str += "登录状态" + "\n";
            msg= "登录状态";
        } else if (id == 0x04) {
            result_str += "登录开始" + "\n";
            msg += "登录开始" + "\n";
        } else if (id == 0x05) {
            result_str += "账户验证" + "\n";
            msg += "账户验证" + "\n";
        } else {
            result_str += "未知" + "\n";
            msg += "未知" + "\n";
        }

//        BleService.mi_tag = msg;


        switch (pos) {
            case bind_status:

                System.out.println("数据封装 = " + "account" + "/bind_status = " + account.getBindStatus());
                result_str += "account" + "/bind_status = " + account.getBindStatus() + "\n";

//                EventBus.getDefault().post(new BindStatusEvent(account.getBindStatus()));
                break;
            case bind_key:
                System.out.println("数据封装 = " + "account" + "/bind_key = " + account.getBindKey());
                result_str += "account" + "/bind_key = " + account.getBindKey() + "\n";
                break;

            case error_code:
                System.out.println("数据封装 = " + "account" + "/error_code = " + account.getErrorCode());
                result_str += "account" + "/error_code = " + account.getErrorCode() + "\n";
                break;

            case bind_info:
                AccountProtos.BindInfo mBindInfo = account.getBindInfo();
                result_str += analysisBindInfo(mBindInfo);

//                EventBus.getDefault().post(new GetBindInfoSuccessEvent());
                break;

            case bind_result:
                AccountProtos.BindResult mBindResult = account.getBindResult();
                result_str += analysisBindResult(mBindResult);
                break;

            case login_status:
                System.out.println("数据封装 = " + "account" + "/login_status = " + account.getLoginStatus());
                result_str += "account" + "/login_status = " + account.getLoginStatus() + "\n";
                break;

            case verify_key:
                System.out.println("数据封装 = " + "account" + "/verify_key = " + account.getVerifyKey());
                result_str += "account" + "/verify_key = " + account.getVerifyKey() + "\n";
                break;

            case verify_result:
                System.out.println("数据封装 = " + "account" + "/verify_result = " + account.getVerifyResult());
                result_str += "account" + "/verify_result = " + account.getVerifyResult() + "\n";

//                EventBus.getDefault().post(new VerifyResultEvent(account.getVerifyResult()));
                break;

        }

        return result_str;

    }

    public static String analysisBindInfo(AccountProtos.BindInfo bind_info) {

        String result_str = "";

        System.out.println("数据封装 = " + "account" + "/bind_info===========");
        System.out.println("数据封装 = " + "account" + "/bind_info/bind_key = " + bind_info.getBindKey());
        System.out.println("数据封装 = " + "account" + "/bind_info/did = " + bind_info.getDid());
        System.out.println("数据封装 = " + "account" + "/bind_info/model = " + bind_info.getModel());
        System.out.println("数据封装 = " + "account" + "/bind_info/mac = " + bind_info.getMac());
        System.out.println("数据封装 = " + "account" + "/bind_info/sing = " + bind_info.getSign());

        result_str += "account" + "/bind_info===========";
        result_str += "account" + "/bind_info/bind_key = " + bind_info.getBindKey() + "\n";
        result_str += "account" + "/bind_info/did = " + bind_info.getDid() + "\n";
        result_str += "account" + "/bind_info/model = " + bind_info.getModel() + "\n";
        result_str += "account" + "/bind_info/mac = " + bind_info.getMac() + "\n";
        result_str += "account" + "/bind_info/sing = " + bind_info.getSign() + "\n";

        return result_str;
    }

    public static String analysisBindResult(AccountProtos.BindResult bind_result) {

        String result_str = "";

        System.out.println("数据封装 = " + "account" + "/bind_result===========");
        System.out.println("数据封装 = " + "account" + "/bind_info/user_id = " + bind_result.getUserId());
        System.out.println("数据封装 = " + "account" + "/bind_info/token = " + bind_result.getToken());

        result_str += "account" + "/bind_result===========";
        result_str += "account" + "/bind_info/user_id = " + bind_result.getUserId() + "\n";
        result_str += "account" + "/bind_info/token = " + bind_result.getToken() + "\n";

        return result_str;

    }

    public static void showLog(AccountProtos.Account account) {


        int pos = account.getPayloadCase().getNumber();


        System.out.println("数据封装 = pos = " + pos);

        switch (pos) {
            case bind_status:
                System.out.println("数据封装 = " + "account" + "/bind_status = " + account.getBindStatus());
                break;

            case bind_key:
                System.out.println("数据封装 = " + "account" + "/bind_key = " + account.getBindKey());
                break;

            case error_code:
                System.out.println("数据封装 = " + "account" + "/error_code = " + account.getErrorCode());
                break;

            case bind_info:

                AccountProtos.BindInfo bind_info = account.getBindInfo();

                System.out.println("数据封装 = " + "account" + "/bind_info===========");
                System.out.println("数据封装 = " + "account" + "/bind_info/bind_key = " + bind_info.getBindKey());
                System.out.println("数据封装 = " + "account" + "/bind_info/did = " + bind_info.getDid());
                System.out.println("数据封装 = " + "account" + "/bind_info/model = " + bind_info.getModel());
                System.out.println("数据封装 = " + "account" + "/bind_info/mac = " + bind_info.getMac());
                System.out.println("数据封装 = " + "account" + "/bind_info/sing = " + bind_info.getSign());
                break;

            case bind_result:
                System.out.println("数据封装 = " + "account" + "/bind_result = " + account.getBindResult());

                AccountProtos.BindResult bind_result = account.getBindResult();

                System.out.println("数据封装 = " + "account" + "/bind_result===========");
                System.out.println("数据封装 = " + "account" + "/bind_info/user_id = " + bind_result.getUserId());
                System.out.println("数据封装 = " + "account" + "/bind_info/token = " + bind_result.getToken());
                break;

            case login_status:
                System.out.println("数据封装 = " + "account" + "/login_status = " + account.getLoginStatus());
                break;

            case verify_key:
                System.out.println("数据封装 = " + "account" + "/verify_key = " + account.getVerifyKey());
                break;

            case verify_result:
                System.out.println("数据封装 = " + "account/" + "verify_result = " + account.getVerifyResult());
                break;
        }
    }


    public static byte[] getBindState() {
        AccountProtos.Account.Builder account = AccountProtos.Account.newBuilder();

        WearProtos.WearPacket.Builder wear1 = WearProtos.WearPacket.newBuilder()
                .setType(WearProtos.WearPacket.Type.ACCOUNT)
                .setId((byte) AccountProtos.Account.AccountID.BIND_STATUS.getNumber())
                .setAccount(account);
        return wear1.build().toByteArray();
    }

    public static byte[] getVerifyKey() {
        AccountProtos.Account.Builder account = AccountProtos.Account.newBuilder();
        account.setVerifyKey("1057190627");
        WearProtos.WearPacket.Builder wear1 = WearProtos.WearPacket.newBuilder()
                .setType(WearProtos.WearPacket.Type.ACCOUNT)
                .setId((byte) AccountProtos.Account.AccountID.ACCOUNT_VERIFY.getNumber())
                .setAccount(account);
        return wear1.build().toByteArray();
    }
}
