package com.seebye.xclasses.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by nico on 03.10.15.<br>
 * <br>
 * Use this annotation if you want to manipulate the values passed to the original method<br>
 * By using this annotation an additional parameter will be added in front of the other parameters.<br>
 * <br>
 * E.g.:<br>
 * Before: ( Object[] aParameters, / *other parameters* / )<br>
 * Unsupported as it's useless.. After: ( T resultUntilNow, Object[] aParameters, / *other parameters* / )<br>
 * <br>
 * To change the values simply change the array.<br>
 * E.g.<br>
 * aParameters[0] = "new value";
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ParameterManipulation {
}
