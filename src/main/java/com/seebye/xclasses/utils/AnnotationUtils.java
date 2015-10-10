package com.seebye.xclasses.utils;

import com.seebye.xclasses.annotations.AfterOriginalMethod;
import com.seebye.xclasses.annotations.BeforeOriginalMethod;
import com.seebye.xclasses.annotations.OriginalConstructor;
import com.seebye.xclasses.annotations.ParameterManipulation;
import com.seebye.xclasses.annotations.OriginalMethod;

import java.lang.reflect.Method;

/**
 * Created by nico on 06.10.15.
 */
public class AnnotationUtils
{
	public static boolean isBeforeMethodHook(Method method)
	{
		return method.isAnnotationPresent(BeforeOriginalMethod.class);
	}
	public static boolean isAfterMethodHook(Method method)
	{
		return method.isAnnotationPresent(AfterOriginalMethod.class);
	}
	public static boolean isOriginalMethod(Method method)
	{
		return method.isAnnotationPresent(OriginalMethod.class);
	}
	public static boolean isConstructorMethodHook(Method method)
	{
		return method.isAnnotationPresent(OriginalConstructor.class);
	}
	public static boolean hasParametersAsArrayAnnotation(Method method)
	{
		return method.isAnnotationPresent(ParameterManipulation.class);
	}
}
