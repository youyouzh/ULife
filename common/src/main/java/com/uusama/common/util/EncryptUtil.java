package com.uusama.common.util;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * 加密工具类
 * @author zhaohai
 */
public class EncryptUtil {
    private static final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    /** AES填充方式 */
    private static final String AES_CIPHER_PADDING = "AES/ECB/PKCS5Padding";
    private static final Charset DEFAULT_CHARACTER = StandardCharsets.UTF_8;
    private static final String DEFAULT_AES_KEY = "0fuVPqNPKhLBVpVZ";
    private static final MessageDigest CIPHER_MD5;

    static {
        MessageDigest md5DigestTemp;
        try {
            // MD5加密解密
            md5DigestTemp = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            md5DigestTemp = null;
        }
        CIPHER_MD5 = md5DigestTemp;
    }

    private static final String RSA_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDU8Hh0zK5f7eZSNtrtIMMxA0rn53CcWfcJvzxMzXV9fY5A6nirtSrkghGSYflVwXC/6v8TNhHU78D1iVa84s3hrMwg/s5xlHRpDC9uq5+yYedVGfIHkUrldCclGpyUQdsCaZPx0JD6B8cSq6BPw9qnJzINHOsJ8HRlxclG5wEeWwIDAQAB";
    private static final String RSA_PRIVATE_KEY = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBANTweHTMrl/t5lI22u0gwzEDSufncJxZ9wm/PEzNdX19jkDqeKu1KuSCEZJh+VXBcL/q/xM2EdTvwPWJVrzizeGszCD+znGUdGkML26rn7Jh51UZ8geRSuV0JyUanJRB2wJpk/HQkPoHxxKroE/D2qcnMg0c6wnwdGXFyUbnAR5bAgMBAAECgYEAzxMa0NrLKFa9iG338GnP57X//g0/oYyLhFsIVNfozaRv1gkuWCzSDW2/kM7eHdsDvl7UtuX56U1OJHjudLlK48e/d5T+yxXbg7YEtaxD0lx1ixejvArSRvg5VKdS5Tz6ZIXvzuzPJV54m8p9EBvH+28yECdUZmhLgtF8Iv5mXGECQQD+6L0NMwh3Xb9Sa1Rv3AzRlPqT512RZLdkIQtUt0whqgExFaRt62UehZAfxXS47m8fIYGgL4MMKfctBAwh7RafAkEA1dnAssZC/9oI6wrtpJCrt2XQGWY/tEsTRLUNxKTp+NP8OPEhtkTIm/LBTwlsm92FPPO9JUatvVoeA+1lVoOKxQJBAKn9GtWKisLPSZ705EIURJge+VtYlxU2TPYA80VzVtm8PT82Z4jFyZEpEIufac9JceEYvxDLnmCmO6dRY6XcLZECQGtj6ZgdZiHzzUyzdTmSCRmVQFaw+UbB+NAlF9/rbS+uzNePh/1vN0rRMnBNbEQtjy5XvGGOx8ryCWaxfZVfg30CQBpMxNgQltNHjdKmhk4WrLI+Gt6dTqc3xrSQzZTLNMl96UQnuXA7ZyThhJm5yTNvhrQRXVmqGRYScLq/x6ez7ek=";

    public static String getRsaPublicKey() {
        return RSA_PUBLIC_KEY;
    }

    public static byte[] rsaEncrypt(byte[] data) {
        try {
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.decode(RSA_PUBLIC_KEY));
            PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(keySpec);
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return cipher.doFinal(data);
        } catch (IllegalArgumentException | NoSuchAlgorithmException | IllegalBlockSizeException |
                 NoSuchPaddingException | BadPaddingException | InvalidKeySpecException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] rsaDecrypt(byte[] encryptData) {
        try {
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.decode(RSA_PRIVATE_KEY));
            PrivateKey privateKey = KeyFactory.getInstance("RSA").generatePrivate(keySpec);
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return cipher.doFinal(encryptData);
        } catch (IllegalArgumentException | NoSuchAlgorithmException | IllegalBlockSizeException | InvalidKeyException |
                 NoSuchPaddingException | BadPaddingException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    public static String aesDecrypt(String encryptedData, String aesKey) {
        try {
            // 设置解密算法，生成秘钥
            SecretKeySpec secretKeySpec = new SecretKeySpec(aesKey.getBytes(DEFAULT_CHARACTER), "AES");
            // "算法/模式/补码方式"
            Cipher cipher = Cipher.getInstance(AES_CIPHER_PADDING);
            // 选择解密
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            // 根据待解密内容进行解密
            byte[] decrypted = cipher.doFinal(encryptedData.getBytes(DEFAULT_CHARACTER));
            // 将字节数组转成字符串
            return new String(decrypted, DEFAULT_CHARACTER);
        } catch (IllegalArgumentException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                 IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String aesDecrypt(String encryptedData) {
        return aesDecrypt(encryptedData, DEFAULT_AES_KEY);
    }

    public static String aesEncrypt(String content, String aesKey) {
        try {
            // 设置加密算法，生成秘钥
            SecretKeySpec secretKeySpec = new SecretKeySpec(aesKey.getBytes(DEFAULT_CHARACTER), "AES");
            // "算法/模式/补码方式"
            Cipher cipher = Cipher.getInstance(AES_CIPHER_PADDING);
            // 选择加密
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            // 根据待加密内容生成字节数组
            byte[] encrypted = cipher.doFinal(content.getBytes(DEFAULT_CHARACTER));
            // 返回base64字符串
            return Base64.encode(encrypted);
        } catch (IllegalArgumentException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                 IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String aesEncrypt(String content) {
        return aesEncrypt(content, DEFAULT_AES_KEY);
    }

    /**
     * 自定义实现按位解密函数
     * @param encryptedStr 加密字符串
     * @return 解密后字符串
     */
    public static String bitDecrypt(String encryptedStr) {
        if (StringUtils.isEmpty(encryptedStr)) {
            return null;
        } else {
            byte[] rawBytes = Base64.decode(encryptedStr);
            if (rawBytes.length <= 2) {
                return null;
            } else {
                int len = rawBytes.length;
                byte randomByte = rawBytes[len - 2];
                for (int i = 0; i < len - 2; ++i) {
                    rawBytes[i] ^= randomByte;
                }

                return new String(rawBytes, 0, len - 2, StandardCharsets.UTF_8);
            }
        }
    }

    /**
     * 自定义实现按位加密函数
     * @param content 解密后字符串
     * @return 加密后字符串
     */
    public static String bitEncrypt(String content) {
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
        byte[] randomBytes = RandomUtils.nextBytes(1);
        ByteBuffer byteBuffer = ByteBuffer.allocate(bytes.length + 2);
        for (int i = 0, byteLength = bytes.length; i < byteLength; i++) {
            bytes[i] ^= randomBytes[0];
        }
        byteBuffer.put(bytes);
        byteBuffer.put(randomBytes[0]);
        byteBuffer.put(randomBytes[0]);
        return Base64.encode(byteBuffer.array());
    }

    /**
     * 生成32位小写MD5字符串
     *
     * @param content content
     * @return md5 string
     */
    public static String md5(String content) {
        if (CIPHER_MD5 != null) {
            byte[] btInput = content.getBytes();
            CIPHER_MD5.update(btInput);
            byte[] md = CIPHER_MD5.digest();
            char[] str = new char[md.length * 2];
            int k = 0;
            for (byte byte0 : md) {
                str[k++] = HEX_DIGITS[byte0 >>> 4 & 0xf];
                str[k++] = HEX_DIGITS[byte0 & 0xf];
            }
            return new String(str).toLowerCase();
        }
        return content;
    }
}
