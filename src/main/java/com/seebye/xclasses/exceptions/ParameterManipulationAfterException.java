package com.seebye.xclasses.exceptions;

/**
 * Created by nico on 03.10.15.
 */
public class ParameterManipulationAfterException extends Exception
{
	public ParameterManipulationAfterException()
	{
		super("You tried to add variable manipulation to a hook method which is executed after the original method is executed.\n"
			+"It's very likely you wanted to use a hook method which is executed BEFORE the original method.\n"
			+"(It's useless to change the variables after the original method has been called.)");
	}
}
