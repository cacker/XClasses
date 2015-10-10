package com.seebye.xclasses.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by nico on 03.10.15.<br>
 * <br>
 * Use this annotation if you want to hook a constructor in combination<br>
 * with {@link AfterOriginalMethod} or {@link BeforeOriginalMethod}.<br>
 * The name of this method isn't important.<br>
 * (The parameter rules still needs to be followed.)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface OriginalConstructor {
}
