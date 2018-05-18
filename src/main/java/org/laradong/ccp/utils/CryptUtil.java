/**
 * Copyright (C) 1998-2012 TENCENT Inc.All Rights Reserved.
 * <p/>
 * FileName:DecryptData.java
 * <p/>
 * Description:简要描述本文件的内容
 * <p/>
 * History： 版本号 作者 日期 简要介绍相关操作 x.y harryli 2012-6-1 Create
 */

package org.laradong.ccp.utils;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;


public class CryptUtil {

    /**
     * 加密
     *
     * @param content  需要加密的内容
     * @param password 加密密码
     * @return
     */
    public static String encrypt(String content, String password) {
        String encryptStr = "";
        if (isNullorEmpty(content) || isNullorEmpty(password)) {
            return encryptStr;
        }

        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(password.getBytes());
            kgen.init(128, secureRandom);
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
            Cipher cipher = Cipher.getInstance("AES");// 创建密码器
            byte[] byteContent = content.getBytes("utf-8");
            cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化
            byte[] result = cipher.doFinal(byteContent);
            if (null != result) {
                BASE64Encoder base64encoder = new BASE64Encoder();
                encryptStr = base64encoder.encode(result);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return encryptStr;
    }

    /**
     * 解密
     *
     * @param content 待解密内容
     * @return
     */
    public static String decrypt(String content) {
        return decrypt(content, KEY);
    }

    /**
     * 加密
     *
     * @param content 待加密内容
     * @return
     */
    public static String encrypt(String content) {
        return encrypt(content, KEY);
    }

    /**
     * 解密
     *
     * @param content  待解密内容
     * @param password 解密密钥
     * @return
     */
    public static String decrypt(String content, String password) {
        String decryptStr = "";
        if (isNullorEmpty(content) || isNullorEmpty(password)) {
            return null;
        }

        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(password.getBytes());
            kgen.init(128, secureRandom);
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
            Cipher cipher = Cipher.getInstance("AES");// 创建密码器
            cipher.init(Cipher.DECRYPT_MODE, key);// 初始化
            BASE64Decoder base64decoder = new BASE64Decoder();
            byte[] encodeByte = base64decoder.decodeBuffer(content);
            byte[] result = cipher.doFinal(encodeByte);
            if (null != result) {
                decryptStr = new String(result); // 加密
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return decryptStr;
    }

    public static boolean isNullorEmpty(String str) {
        if (str == null) {
            return true;
        }
        return str.trim().equals("");
    }

    public static String hideStr(String str) {
        String newStr = null;
        if (null != str && str.length() > 0) {
            newStr = "size(" + str.length() + ")str_head(" + str.substring(0, 3) + ")";
        }
        return newStr;
    }

}
