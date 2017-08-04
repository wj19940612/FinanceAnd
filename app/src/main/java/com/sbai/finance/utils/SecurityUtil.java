package com.sbai.finance.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class SecurityUtil {

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


    /**
     * 使用AES算法解密字符串.
     * AES加密算法（美国国家标准局倡导的AES即将作为新标准取代DES）
     *
     * @param encrypted 要解密的字符串
     * @param rawKey    密钥字符串, 要求为一个32位(或64位，或128位)的16进制数的字符串,否则会出错.
     * @return 解密之后的字符串
     */
    public static String AESDecrypt(String encrypted, String rawKey) {
        byte[] tmp = hexStr2ByteArr(encrypted);
        byte[] key = hexStr2ByteArr(rawKey);
        try {
            SecretKeySpec sks = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, sks);
            byte[] decrypted = cipher.doFinal(tmp);
            return new String(decrypted);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 使用AES算法解密字符串.
     * AES加密算法（美国国家标准局倡导的AES即将作为新标准取代DES）
     *
     * @param encrypted 要解密的字符串
     * @return 解密之后的字符串
     */
    public static String AESDecrypt(String encrypted) {
        return AESDecrypt(encrypted, "7634f7c34c02805afd241dec53b7fa53");
    }


    /**
     * Turns array of bytes into string
     *
     * @param buf Array of bytes to convert to hex string
     * @return Generated hex string
     */
    private static String byteArr2HexStr(byte[] buf) {
        StringBuilder sb = new StringBuilder(buf.length * 2);
        int i;
        for (i = 0; i < buf.length; i++) {
            if (((int) buf[i] & 0xff) < 0x10) sb.append("0");

            sb.append(Long.toString((int) buf[i] & 0xff, 16));
        }
        return sb.toString();
    }

    /**
     * 将表示16进制值的字符串转换为byte数组， 和public static String byteArr2HexStr(byte[] buf)互为可逆的转换过程
     *
     * @param src 需要转换的字符串
     * @return 转换后的byte数组
     */
    private static byte[] hexStr2ByteArr(String src) {
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
