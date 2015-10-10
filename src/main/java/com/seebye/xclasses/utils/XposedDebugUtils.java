package com.seebye.xclasses.utils;

/**
 * Created by nico on 24.09.15.
 */
public class XposedDebugUtils
{
	public static String getDumpObjectArray(Object[] aObjects) {
		StringBuilder stringBuilder = new StringBuilder();

		if(aObjects != null)
		{
			for (Object object : aObjects)
			{
				if(stringBuilder.length() != 0)
				{
					stringBuilder.append(", ");
				}

				stringBuilder.append(object);
			}
		}

		return stringBuilder.toString();
	}
}
