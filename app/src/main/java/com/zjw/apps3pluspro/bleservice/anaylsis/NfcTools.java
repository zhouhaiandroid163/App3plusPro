package com.zjw.apps3pluspro.bleservice.anaylsis;

import com.google.protobuf.ByteString;
import com.xiaomi.wear.protobuf.AccountProtos;
import com.xiaomi.wear.protobuf.NfcProtos;
import com.xiaomi.wear.protobuf.WearProtos;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

public class NfcTools {


    static final int command = 1;//命令？？NfcID？？？


    public static String analysisNfc(WearProtos.WearPacket wear) {

        NfcProtos.Nfc nfc = wear.getNfc();
        int id = wear.getId();
        int pos = nfc.getPayloadCase().getNumber();

        String result_str = "";

        System.out.println("数据封装 = " + "wear/type = " + wear.getType());
        System.out.println("数据封装 = " + "wear/id = " + wear.getId());
        System.out.println("数据封装 = " + "pos = " + pos);


        result_str += "wear/type = " + wear.getType() + "\n";
        result_str += "wear/id = " + wear.getId() + "\n";
        result_str += "pos = " + pos + "\n";

        result_str += "描述(参考):NFC" + "-";

        if (id == 0x00) {
            result_str += "命令" + "\n";
        } else if (id == 0x01) {
            result_str += "发行卡" + "\n";
        } else if (id == 0x02) {
            result_str += "添加卡" + "\n";
        } else if (id == 0x03) {
            result_str += "删除卡" + "\n";
        } else if (id == 0x04) {
            result_str += "同步卡列表" + "\n";
        } else if (id == 0x05) {
            result_str += "设置默认卡" + "\n";
        } else if (id == 0x06) {
            result_str += "获取默认卡" + "\n";
        } else if (id == 0x07) {
            result_str += "设置余额" + "\n";
        } else if (id == 0x08) {
            result_str += "获取余额" + "\n";
        } else if (id == 0x09) {
            result_str += "设置配置" + "\n";
        } else {
            result_str += "未知" + "\n";
        }


//        switch (pos) {
//            case command:
////                System.out.println("数据封装 = " + "nfc" + "/Command = " + bytesToString(nfc.getCommand()));
////                System.out.println("数据封装 = " + "nfc" + "/Command = " + nfc.getCommand().toStringUtf8());
////                System.out.println("数据封装 = " + "nfc" + "/Command = " + nfc.getCommand().toByteArray());
//                System.out.println("数据封装 = " + "nfc" + "/Command = " + MainActivity.printHexString(nfc.getCommand().toByteArray()));
//                break;
//
//
//
//        }

        return result_str;

    }


    public static NfcProtos.Nfc.Builder getNfc(int pos) {

        NfcProtos.Nfc.Builder nfc = NfcProtos.Nfc.newBuilder();


        switch (pos) {
            case command:

                String str = "test_dta";
                try {
                    ByteString abc = ByteString.copyFrom(str, "UTF-8");
                    nfc.setCommand(abc);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                break;

        }

        return nfc;
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


    public static void showLog(NfcProtos.Nfc nfc) {


        int pos = nfc.getPayloadCase().getNumber();


        System.out.println("数据封装 = pos = " + pos);

        switch (pos) {
            case command:
//                System.out.println("数据封装 = " + "nfc" + "/Command = " + nfc.getCommand());
//                System.out.println("数据封装 = " + "nfc" + "/Command = " + bytesToString(nfc.getCommand()));
                System.out.println("数据封装 = " + "nfc" + "/Command = " + nfc.getCommand().toStringUtf8());
                break;

        }
    }


    public static String bytesToString(ByteString src) {

        return bytesToString(src.toByteArray());
    }

    public static String bytesToString(byte[] input) {

        ByteBuffer buffer = ByteBuffer.allocate(input.length);
        buffer.put(input);
        buffer.flip();

        Charset charset = null;
        CharsetDecoder decoder = null;
        CharBuffer charBuffer = null;

        try {
            charset = Charset.forName("utf-8");
            decoder = charset.newDecoder();
            charBuffer = decoder.decode(buffer.asReadOnlyBuffer());

            return charBuffer.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

}
