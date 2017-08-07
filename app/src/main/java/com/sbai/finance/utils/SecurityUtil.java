package com.sbai.finance.utils;

import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class SecurityUtil {

    private static final String AES_SECRET_KEY = "7634f7c34c02805afd241dec53b7fa53";

    public static String md5Encrypt(String value) throws NoSuchAlgorithmException {
        MessageDigest digester = MessageDigest.getInstance("MD5");
        digester.update(value.getBytes());
        return convertByteArrayToHexString(digester.digest());
    }

    private static String convertByteArrayToHexString(byte[] arrayBytes) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < arrayBytes.length; i++) {
            stringBuffer.append(Integer.toString((arrayBytes[i] & 0xff) + 0x100, 16)
                    .substring(1));
        }
        return stringBuffer.toString();
    }

    static final String CIPHER_ALGORITHM_CBC = "AES/CBC/PKCS5Padding";

    /**
     * AES算法密钥生成器.
     *
     * @return 生成的密钥 它是一个32个字符的16进制字符串.
     */
    @SuppressWarnings("unused")
    public static String AESKey() {
        try {
            // Get the KeyGenerator
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(128); // 192 and 256 bits may not be available
            // Generate the secret key specs.
            SecretKey key = keyGenerator.generateKey();
            byte[] raw = key.getEncoded();
            return byte2Str(raw);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 使用AES算法解密字符串.
     * AES加密算法（美国国家标准局倡导的AES即将作为新标准取代DES）
     *
     * @param encrypted 要解密的字符串
     * @param rawKey    密钥字符串, 要求为一个32位(或64位，或128位)的16进制数的字符串,否则会出错.
     *                  可以使用{@link #AESKey()}方法生成一个密钥,
     * @return 解密之后的字符串
     */
    public static String AESDecrypt(String encrypted, String rawKey) {
        byte[] tmp = Str2Byte(encrypted);
        byte[] key = Str2Byte(rawKey);
        try {
            SecretKeySpec sks = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM_CBC);
            cipher.init(Cipher.DECRYPT_MODE, sks, new IvParameterSpec(getIV()));
            byte[] decrypted = cipher.doFinal(tmp);
            return new String(decrypted);
        } catch (Exception e) {
            return null;
        }
    }

    public static String AESDecrypt(String encrypted) {
        byte[] tmp = Str2Byte(encrypted);
        byte[] key = Str2Byte(AES_SECRET_KEY);
        try {
            SecretKeySpec sks = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM_CBC);
            cipher.init(Cipher.DECRYPT_MODE, sks, new IvParameterSpec(getIV()));
            byte[] decrypted = cipher.doFinal(tmp);
            return new String(decrypted);
        } catch (Exception e) {
            return null;
        }
    }

    static byte[] getIV() {
        return Str2Byte(AES_SECRET_KEY);
    }

    /**
     * 使用DES算法解密字符串.
     *
     * @param encrypted 要解密的字符串.
     * @param rawKey    密钥字符串, 可以为任意字符, 但最长不得超过8个字符(如最超过，后面的字符会被丢弃).
     * @return 解密之后的字符串.
     */
    public static String DESDecrypt(String encrypted, String rawKey) {
        byte[] arrBTmp = rawKey.getBytes();
        byte[] arrB = new byte[8]; // 创建一个空的8位字节数组（默认值为0）
        for (int i = 0; i < arrBTmp.length && i < arrB.length; i++) // 将原始字节数组转换为8位
            arrB[i] = arrBTmp[i];
        try {
            Key key = new SecretKeySpec(arrB, "DES");// 生成密钥
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            return new String(cipher.doFinal(Str2Byte(encrypted)));
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * Turns array of bytes into string
     *
     * @param buf Array of bytes to convert to hex string
     * @return Generated hex string
     */
    private static String byte2Str(byte[] buf) {
        StringBuilder sb = new StringBuilder(buf.length * 2);
        int i;
        for (i = 0; i < buf.length; i++) {
            if (((int) buf[i] & 0xff) < 0x10) sb.append("0");

            sb.append(Long.toString((int) buf[i] & 0xff, 16));
        }
        return sb.toString();
    }

    /**
     * 将表示16进制值的字符串转换为byte数组， 和public static String byte2Str(byte[] buf)互为可逆的转换过程
     *
     * @param src 需要转换的字符串
     * @return 转换后的byte数组
     */
    private static byte[] Str2Byte(String src) {
        if (src.length() < 1) {
            return null;
        }
        byte[] encrypted = new byte[src.length() / 2];
        for (int i = 0; i < src.length() / 2; i++) {
            int high = Integer.parseInt(src.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(src.substring(i * 2 + 1, i * 2 + 2), 16);

            encrypted[i] = (byte) (high * 16 + low);
        }
        return encrypted;
    }


}
