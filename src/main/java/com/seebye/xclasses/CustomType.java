package com.seebye.xclasses;

import com.seebye.xclasses.exceptions.OriginalClassNameMissingException;

/**
 * Created by nico on 03.10.15.<br>
 * <br>
 * Create a subclass of this abstract class if you need to use a class which isn't available in your code / the android sdk.<br>
 * Use the created subclass as parameter for the method you want to hook.
 */
public abstract class CustomType
{
	public static final String ORIGINAL_CLASS_NAME_METHODNAME = "getOriginalClassName";

	private Object m_objectOriginal = null;

	public CustomType(Object objectOriginal)
	{
		m_objectOriginal = objectOriginal;
	}

	public static String getOriginalClassName() throws OriginalClassNameMissingException {
		throw new OriginalClassNameMissingException();
	}

	public Object getOriginalObject() {
		return m_objectOriginal;
	}
}
