package com.seebye.xclasses.exceptions;

/**
 * Created by nico on 03.10.15.
 */
public class StaticNonStaticException extends Exception
{
	public StaticNonStaticException()
	{
		super("The methods needs to be both static or non-static.");
	}
}
