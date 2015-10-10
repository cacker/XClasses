package com.seebye.xclasses.exceptions;

/**
 * Created by nico on 03.10.15.
 */
public class ParameterManipulationBeforeException extends Exception
{
	public ParameterManipulationBeforeException()
	{
		super("You tried to add variable manipulation to a hook method, but you forgot to add a parameter to the hook method of the type Object[].");
	}
}
