package com.seebye.xclasses.utils;

/**
 * Created by nico on 06.10.15.
 */
public class ParameterUtils
{
	/**
	 * Returns the class objects of the objects
	 *
	 * @param aParameters
	 * @return
	 */
	public static Class<?>[] getParameterTypes(Object... aParameters)
	{
		Class<?>[] aClasses = new Class<?>[aParameters.length];

		for(int i = 0
			; i < aParameters.length
				; i++)
		{
			aClasses[i] = aParameters[i].getClass();
		}

		return aClasses;
	}

	public static boolean equalsParamterTypes(Class<?>[] aParameterTypes1, Class<?>[] aParameterTypes2)
	{
		boolean bRet = aParameterTypes1.length == aParameterTypes2.length;

		for(int i = 0
			; i < aParameterTypes1.length
					&& bRet
				; i++)
		{
			bRet = aParameterTypes1[i] == aParameterTypes2[i];
		}

		return bRet;
	}
}
