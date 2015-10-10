package com.seebye.xclasses.annotations;

import com.seebye.xclasses.AbstractXClass;
import com.seebye.xclasses.exceptions.HookThrowException;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.robv.android.xposed.XC_MethodHook;

/**
 * Created by nico on 03.10.15.<br>
 * <br>
 * Note:<br>
 * 		 1. These methods needs to return {@link AbstractXClass#CONTINUE_EXECUTION}<br>
 * 		 to continue the execution.<br>
 * 		 -> The return type needs to be an {@link Object}<br>
 * 		 Other return types except void will lead to a blocked execution of the original method.<br>
 * 		 (Also it will lead to a manipulated return value)<br>
 * 		 -> returning anything except {@link AbstractXClass#CONTINUE_EXECUTION} will lead to a call of {@link de.robv.android.xposed.XC_MethodHook.MethodHookParam#setResult(Object)} <br>
 * 		 <br>
 * 		 2. The name of these methods must be the same as the names of the original methods.<br>
 * 		 3. You can append the suffix "_Before" to the method name<br>
 * 		 (2+3): e.g. method name in the original class: Activity#onCreate<br>
 * 		 			 method name in our class:			OurClass#onCreate_Before<br>
 * 		 <br>
 * 		 4. throwing an exception (wrapped in {@link HookThrowException}) will lead to a call of {@link XC_MethodHook.MethodHookParam#setThrowable(Throwable)} <br>
 * 		 <br>
 * 		 5. The arguments of the our method have to match the arguments of the original method.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface BeforeOriginalMethod {
}
