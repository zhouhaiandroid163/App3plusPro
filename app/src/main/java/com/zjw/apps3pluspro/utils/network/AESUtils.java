package com.zjw.apps3pluspro.utils.network;

import android.util.Base64;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class AESUtils {

    public static final String KEY_ALGORITHM = "AES";
    public static String charset = "utf-8";
    //加解密算法/工作模式/填充方式,Java6.0支持PKCS5Padding填充方式,BouncyCastle支持PKCS7Padding填充方式
    public static final String CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";

    /**
     * 生成密钥
     */
    public static String initkey() throws Exception {
        KeyGenerator kg = KeyGenerator.getInstance(KEY_ALGORITHM); //实例化密钥生成器
        kg.init(128);                                              //初始化密钥生成器:AES要求密钥长度为128,192,256位
        SecretKey secretKey = kg.generateKey();                    //生成密钥
        return Base64.encodeToString(secretKey.getEncoded(), Base64.DEFAULT);  //获取二进制密钥编码形式
    }


    /**
     * 转换密钥
     */
    public static Key toKey(byte[] key) throws Exception {
        return new SecretKeySpec(key, KEY_ALGORITHM);
    }


    /**
     * 加密数据
     *
     * @param data 待加密数据
     * @param key  密钥
     * @return 加密后的数据
     */
    public static String encrypt(String data, String key) throws Exception {
//		 Base64.encodeToString(key.getBytes("utf-8"),Base64.DEFAULT);
//		Key k = toKey(Base64.decode(key,Base64.DEFAULT));                           //还原密钥   
        Key k = toKey(key.getBytes(charset));                           //还原密钥
        //使用PKCS7Padding填充方式,这里就得这么写了(即调用BouncyCastle组件实现)
        //Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM, "BC");
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);              //实例化Cipher对象，它用于完成实际的加密操作
        cipher.init(Cipher.ENCRYPT_MODE, k);                               //初始化Cipher对象，设置为加密模式
        return Base64.encodeToString(cipher.doFinal(data.getBytes()), Base64.DEFAULT); //执行加密操作。加密后的结果通常都会用Base64编码进行传输
    }


    /**
     * 解密数据
     *
     * @param data 待解密数据
     * @param key  密钥
     * @return 解密后的数据
     */
    public static String decrypt(String data, String key) throws Exception {
//		Base64.encodeToString(key.getBytes("utf-8"),Base64.DEFAULT);
//		Key k = toKey(Base64.decode((key),Base64.DEFAULT));  
        Key k = toKey(key.getBytes(charset));
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, k);                          //初始化Cipher对象，设置为解密模式
        return new String(cipher.doFinal(Base64.decode(data, Base64.DEFAULT))); //执行解密操作
    }
}
