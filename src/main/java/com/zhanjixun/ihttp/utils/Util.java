package com.zhanjixun.ihttp.utils;

import java.util.Collection;
import java.util.Random;

/**
 * @author :zhanjixun
 * @date : 2019/11/21 15:06
 * @contact :zhanjixun@qq.com
 */
public class Util {

	//集合

	public static boolean isEmpty(Collection coll) {
		return coll == null || coll.isEmpty();
	}

	public static boolean isNotEmpty(Collection coll) {
		return !isEmpty(coll);
	}


	//字符串

	public static boolean isNotBlank(String str) {
		return !isBlank(str);
	}

	public static boolean isBlank(String str) {
		return str == null || str.trim().isEmpty();
	}

	public static boolean isEmpty(String coll) {
		return coll == null || coll.isEmpty();
	}

	public static String replace(String value, String target, String replacement) {

		return null;
	}

	public static String randomString(int length, String chars) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < length; i++) {
			builder.append(chars.charAt(new Random().nextInt(chars.length())));
		}
		return builder.toString();
	}

	//对象

	public static <T> T defaultIfNull(T object, T defaultValue) {
		return object != null ? object : defaultValue;
	}


	//数组

	public static boolean isEmpty(Object[] array) {
		return array == null || array.length == 0;
	}

}
