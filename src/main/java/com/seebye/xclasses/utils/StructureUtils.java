package com.seebye.xclasses.utils;

import com.seebye.xclasses.HookedMethod;
import com.seebye.xclasses.exceptions.PrivateException;
import com.seebye.xclasses.exceptions.StaticNonStaticException;

import java.lang.reflect.Method;

import de.robv.android.xposed.XposedBridge;

/**
 * Created by nico on 06.10.15.
 */
public class StructureUtils
{
	private static final String[] ALLOWED_METHOD_SUFFIXES = {"_before", "_after"};

	/**
	 * Checks whether both methods are static or not.
	 *
	 * @param methodTarget
	 * @param hookedMethod
	 * @throws StaticNonStaticException if they're not both static/non-static
	 */
	public static void checkStaticMethod(Method methodTarget, HookedMethod hookedMethod) throws StaticNonStaticException {
		boolean bTargetStatic = MethodUtils.isStatic(methodTarget);
		if(
				// both methods must be static or non-static
				// before original
				(hookedMethod.getMethodBefore() != null && bTargetStatic != MethodUtils.isStatic(hookedMethod.getMethodBefore()))
				// after original
				|| (hookedMethod.getMethodAfter() != null && bTargetStatic != MethodUtils.isStatic(hookedMethod.getMethodAfter()))
			)
		{
			throw new StaticNonStaticException();
		}
	}

	/**
	 * Checks whether we declared the constructor as static or not
	 *
	 * @param hookedMethod
	 * @throws StaticNonStaticException if the constructor is a static method
	 */
	public static void checkStaticConstructorMethod(HookedMethod hookedMethod) throws StaticNonStaticException {
		if(
				// before original
				(hookedMethod.getMethodBefore() != null && MethodUtils.isStatic(hookedMethod.getMethodBefore()))
				// after original
				|| (hookedMethod.getMethodAfter() != null && MethodUtils.isStatic(hookedMethod.getMethodAfter()))
			)
		{
			throw new StaticNonStaticException();
		}
	}

	/**
	 * After- and before-methods should only be called by xposed,
	 * so we're going to check whether we declared them as private.
	 *
	 * @param hookedMethod
	 */
	public static void checkPrivate(HookedMethod hookedMethod) throws PrivateException {
		if(
				// before original
				(hookedMethod.getMethodBefore() != null && !MethodUtils.isPrivate(hookedMethod.getMethodBefore()))
				// after original
				|| (hookedMethod.getMethodAfter() != null && !MethodUtils.isPrivate(hookedMethod.getMethodAfter()))
			)
		{
			throw new PrivateException();
		}
	}


	/**
	 * Formats method names of xclass methods to determine the method name in the original class.
	 *
	 * @param strMethodName		The method name of a xclass method
	 * @return
	 */
	public static String formatMethodName(String strMethodName)
	{
		for(String strSuffix : ALLOWED_METHOD_SUFFIXES)
		{
			if(strMethodName.toLowerCase().endsWith(strSuffix))
			{
				strMethodName = strMethodName.substring(0, strMethodName.length()-strSuffix.length());
			}
		}

		return strMethodName;
	}
}
