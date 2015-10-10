package com.seebye.xclasses.exceptions;

/**
 * Created by nico on 06.10.15.
 */
public class HookOverflowException extends Exception
{
	public HookOverflowException()
	{
		super("You tried setup multiple before- or after-methods for an original method in the same xclass.\n"
			+"If you want to setup multiple before- or after- methods for the same method you need to use multiple xclasses.\n"
			+"Note: A method is identified by the method name AND the parameter types.");
	}
}
