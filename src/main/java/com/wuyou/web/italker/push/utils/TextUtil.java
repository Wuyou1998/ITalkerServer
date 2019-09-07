package com.wuyou.web.italker.push.utils;


import com.wuyou.web.italker.push.provider.GsonProvider;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Base64;

/**
 * @author qiujuer Email:qiujuer@live.cn
 * @version 1.0.0
 */
public class TextUtil {
    /**
     * 计算一个字符串的MD5信息
     *
     * @param str 字符串
     * @return MD5值
     */
    public static String getMD5(String str) {
        try {
            // 生成一个MD5加密计算摘要
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 计算md5函数
            md.update(str.getBytes());
            // digest()最后确定返回md5 hash值，返回值为8为字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
            // BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
            return new BigInteger(1, md.digest()).toString(16);
        } catch (Exception e) {
            e.printStackTrace();
            return str;
        }
    }

    /**
     * 对一个字符串进行Base64编码
     *
     * @param str 原始字符串
     * @return 进行Base64编码后的字符串
     */
    public static String encodeBase64(String str) {
        return Base64
                .getEncoder()
                .encodeToString(str.getBytes());
    }

    /**
     * 把任意类的实例转换为Json字符串
     *
     * @param obj Object
     * @return Json字符串
     */
    public static String toJson(Object obj) {
        return GsonProvider.getGson().toJson(obj);
    }
}

