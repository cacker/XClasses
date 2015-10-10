package com.seebye.xclasses.utils;

import com.seebye.xclasses.CustomType;

/**
 * Created by nico on 06.10.15.
 */
public class ClassUtils
{

	public static boolean isCustomType(Class clz)
	{
		return CustomType.class.isAssignableFrom(clz);
	}

}
