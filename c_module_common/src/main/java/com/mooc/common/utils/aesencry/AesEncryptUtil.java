package com.mooc.common.utils.aesencry;


import android.text.TextUtils;
import android.util.Base64;

import com.mooc.common.global.AppGlobals;
import com.mooc.common.utils.Base64Util;
import com.netease.NetSecKit.interfacejni.SecruityInfo;

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AesEncryptUtil {

    private static SecruityInfo secruityInfo;
    private static final String AES_SECRET_KEY = "hzj284mfilq06u3a";
    private static final String ivParameter = "2863719402672957";
    private static final Boolean isAesEncrypt = true; //是否Aes加密 true：Aes加密  false:EDun加密

    public static String encrypt(String sSrc) {
        if (TextUtils.isEmpty(sSrc)) {
            return "";
        }
        if (isAesEncrypt) {
            return aesEncrypt(sSrc);
        } else {
            return encryptWithEDun(sSrc);
        }
    }


    // 易盾加密
    public static String encryptWithEDun(String sSrc) {
        if (TextUtils.isEmpty(sSrc)) {
            return "";
        }
        if (secruityInfo == null) {
            secruityInfo = new SecruityInfo(AppGlobals.INSTANCE.getApplication());
        }
        return secruityInfo.encryptStringToServer(sSrc, 0);

    }

    //Aes加密
    public static String aesEncrypt(String src) {
        if (TextUtils.isEmpty(src)) {
            return "";
        }
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            byte[] raw = AES_SECRET_KEY.getBytes();
            SecretKeySpec sKeySpec = new SecretKeySpec(raw, "AES");
            IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes());//使用CBC模式，需要一个向量iv，可增加加密算法的强度
            cipher.init(Cipher.ENCRYPT_MODE, sKeySpec, iv);
            byte[] encrypted = cipher.doFinal(src.getBytes(StandardCharsets.UTF_8));
            return Base64Util.encode(encrypted);//此处使用BASE64做转码。
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
            return src;
        }

    }

    // 解密
    public static String decrypt(String sSrc) {

        String deStr;
        if (isAesEncrypt) {
            deStr = decryptAes(sSrc);
        } else {
            deStr = decryptAes(sSrc);
            if (TextUtils.isEmpty(deStr)) {
                deStr = decryptEDun(sSrc);
            }
        }
        return deStr;
    }

    // 易盾解密
    public static String decryptEDun(String sSrc) {

        String deStr;
        if (secruityInfo == null) {
            secruityInfo = new SecruityInfo(AppGlobals.INSTANCE.getApplication());
        }
        deStr = secruityInfo.decryptStringFromServer(sSrc, 0);
        return deStr;


    }

    //Aes解密
    public static String decryptAes(String sSrc) {

        String deStr;
        try {
            byte[] raw = AES_SECRET_KEY.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec sKeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, sKeySpec, iv);
            byte[] encrypted1;//先用base64解密
            encrypted1 = Base64.decode(sSrc, Base64.DEFAULT);
            byte[] original = cipher.doFinal(encrypted1);
            deStr = new String(original, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            deStr = "";
        }
        return deStr;

    }

    //给智慧树提供的
    final static String zhs_partner_key = "WMeHr0xXBvQApGb";

    /**
     * 使用异或进行解密
     *
     * @param res 需要解密的密文
     */
    public static String XODecode(String res) {
        byte[] bs = parseHexStr2Byte(res);
        for (int i = 0; i < Objects.requireNonNull(bs).length; i++) {
            bs[i] = (byte) ((bs[i]) ^ zhs_partner_key.hashCode());
        }
        return new String(bs);
    }

    /**
     * 将16进制转换为二进制
     */
    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1)
            return null;
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

}
