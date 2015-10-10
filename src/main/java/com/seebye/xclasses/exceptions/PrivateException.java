package com.seebye.xclasses.exceptions;

/**
 * Created by nico on 06.10.15.
 */
public class PrivateException extends Exception
{
	public PrivateException()
	{
		super("After- and before-methods should be private as they should be called by xposed only.");
	}
}
