package com.seebye.xclasses.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by nico on 03.10.15.<br>
 * <br>
 * These methods have to be the same as the original methods.
 * -> Same return value, same arguments
 * Exception: The body of these methods needs to be empty as the will be hooked/replaced via xposed to call the original method.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface OriginalMethod {
}
