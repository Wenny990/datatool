package com.wnhuang.common.utils;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.symmetric.DES;

/**
 * @author wnhuang
 * @Package com.base.utils
 * @date 2023-01-13 10:56
 */
public class DesUtil {

    private final static String DES_KEY = "0CoJUm6ywQ8W8Hwn";

    private final static DES MY_DES = new DES(Mode.ECB, Padding.ZeroPadding, DES_KEY.getBytes());

    /**
     * 解密
     * @param str
     * @return
     */
    public static String decrypt(String str){
        return MY_DES.decryptStr(str, CharsetUtil.CHARSET_UTF_8);
    }

    /**
     * 加密
     * @param str
     * @return
     */
    public static String encrypt(String str){
        return new String(MY_DES.encrypt(str),CharsetUtil.CHARSET_UTF_8);
    }

}
