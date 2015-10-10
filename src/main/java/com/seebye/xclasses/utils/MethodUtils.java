package com.seebye.xclasses.utils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Created by nico on 06.10.15.
 */
public class MethodUtils
{
	public static boolean isStatic(Method method)
	{
		return Modifier.isStatic(method.getModifiers());
	}
	public static boolean isPrivate(Method method)
	{
		return Modifier.isPrivate(method.getModifiers());
	}
	public static boolean hasVoidReturnValue(Method method)
	{
		return method.getReturnType() == void.class;
	}
}
