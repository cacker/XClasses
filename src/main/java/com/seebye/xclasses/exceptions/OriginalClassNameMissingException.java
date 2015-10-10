package com.seebye.xclasses.exceptions;

/**
 * Created by nico on 03.10.15.
 */
public class OriginalClassNameMissingException extends Exception {
	public OriginalClassNameMissingException() {
		super("You forgot to mention which class we want to hook..");
	}
}
