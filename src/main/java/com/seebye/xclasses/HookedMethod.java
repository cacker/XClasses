package com.seebye.xclasses;

import java.lang.reflect.Method;

/**
 * Created by nico on 06.10.15.
 */
public class HookedMethod
{
	private String m_strMethodName = null;
	private Class<?>[] m_aParameterTypes = null;
	private Method m_methodBefore = null;
	private Method m_methodAfter = null;

	/**
	 * Used to store hook methods.
	 * This class will be used by {@link de.robv.android.xposed.XC_MethodHook}
	 * to call our before- and afert-methods.
	 *
	 * @param strMethodName		 The method name of the original method
	 * @param aParameterTypes	 The parameter types of the original method
	 */
	public HookedMethod(String strMethodName, Class<?>[] aParameterTypes)
	{
		m_strMethodName = strMethodName;
		m_aParameterTypes = aParameterTypes;
	}

	public String getMethodName()
	{
		return m_strMethodName;
	}

	public Class<?>[] getParameterTypes()
	{
		return m_aParameterTypes;
	}

	public void setMethodBefore(Method method)
	{
		m_methodBefore = method;
	}

	public void setMethodAfter(Method method)
	{
		m_methodAfter = method;
	}

	public Method getMethodBefore()
	{
		return m_methodBefore;
	}

	public Method getMethodAfter()
	{
		return m_methodAfter;
	}
}
