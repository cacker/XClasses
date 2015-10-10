package com.seebye.xclasses.exceptions;

/**
 * Created by nico on 24.09.15.
 */
public class HookThrowException extends Exception
{
	private Exception m_exceptionReal = null;

	public HookThrowException(Exception realException)
	{
		m_exceptionReal = realException;
	}

	public Exception getRealException() {
		return m_exceptionReal;
	}
}
