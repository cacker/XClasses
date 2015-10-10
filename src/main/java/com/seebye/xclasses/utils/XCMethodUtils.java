package com.seebye.xclasses.utils;

import android.util.Log;

import com.seebye.xclasses.AbstractXClass;
import com.seebye.xclasses.exceptions.HookThrowException;
import com.seebye.xclasses.exceptions.TargetDestroyedException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

/**
 * Created by nico on 06.10.15.
 *
 * These methods are use by multiple XC-classes,
 * so we need to outsource them.
 */
public class XCMethodUtils
{
	/**
	 * Adds the parameters we received from xposed to our parameter list.
	 *
	 * @param method				the before- or after-method
	 * @param aParameterList		the parameter list which will be passed to our method
	 * @param aParameters			the parameters we received from xposed
	 */
	private static void addParametersToList(Method method, ArrayList<Object> aParameterList, Object[] aParameters)
	{
		Class<?>[] aParameterTypes = method.getParameterTypes();
		int nInitialLength = aParameterList.size();

		// we need to wrap instances from unknown classes in their CustomTypes-class-instances
		for(int i = 0
			; i < aParameters.length
				; i++)
		{
			Object object = aParameters[i];

			if(ClassUtils.isCustomType(aParameterTypes[i + nInitialLength]))
			{
				try {
					object = aParameterTypes[i + nInitialLength].getConstructor(Object.class).newInstance(aParameters[i]);
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				}
			}

			aParameterList.add(object);
		}
	}


	/**
	 * Handles the results returned by before- and after-methods
	 *
	 * @param param
	 * @param objectResult
	 */
	private static void handleResultOfHookedMethod(XC_MethodHook.MethodHookParam param, Object objectResult)
	{
		if (objectResult != AbstractXClass.CONTINUE_EXECUTION)
		{
			param.setResult(objectResult);
		}
	}

	/**
	 * Handles exceptions thrown by before- and after-methods
	 *
	 * @param param
	 * @param methodOur
	 * @param e
	 */
	private static void handleExceptionOfHookedMethod(XC_MethodHook.MethodHookParam param, Method methodOur, Exception e)
	{
		if(e instanceof HookThrowException)
		{
			param.setThrowable(((HookThrowException) e).getRealException());
		}
		else if(e instanceof TargetDestroyedException)
		{
			param.setResult(null);
		}
		else
		{
			XposedBridge.log("looks like we fucked something up..."
					+ "\nOur method: " + methodOur.getDeclaringClass().getCanonicalName() + "#" + methodOur.getName()
					+ "\nHooked method: " + param.method.getName()
					+ "\nParameters: " + XposedDebugUtils.getDumpObjectArray(param.args)
					+ "\nResult: " + param.getResult()
					+ "\n"
					+ Log.getStackTraceString(e));
		}
	}


	/**
	 * Creates a new instance of xposed class.
	 *
	 * @param clzOurClass			the xposed class
	 * @param objectOriginalClass	the instance of the original class
	 * @return
	 */
	private static Object createInstanceFromOurClass(Class clzOurClass, Object objectOriginalClass)
	{
		Object objectReturn = null;

		try {
			objectReturn = clzOurClass.getConstructors()[0].newInstance(objectOriginalClass);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		return objectReturn;
	}

	/**
	 * Returns the additional xposed class object which is associated to the passed object.
	 * If there's no object available it will create a new instance.
	 *
	 * @param objectOriginal
	 * @param clzOurClass
	 * @return
	 */
	public static Object getXposedObjectFromOriginalObject(Object objectOriginal, Class clzOurClass)
	{
		synchronized (objectOriginal)
		{
			Object objectOurInstance = AbstractXClass.getXposedObject(objectOriginal, clzOurClass);

			if (objectOurInstance == null)
			{
				objectOurInstance = createInstanceFromOurClass(clzOurClass, objectOriginal);
				XposedHelpers.setAdditionalInstanceField(objectOriginal, AbstractXClass.getAdditionalXposedObjectVariableName(clzOurClass), objectOurInstance);
			}

			return objectOurInstance;
		}
	}

	/**
	 * Invokes the before-method
	 *
	 * @param param					the arguments we got from xposed
	 * @param method				the before-method
	 * @param objectOurInstance		the instance of our xposed class
	 */
	public static void invokeBefore(XC_MethodHook.MethodHookParam param, Method method, Object objectOurInstance)
	{
		Object objectResultHook = null;
		ArrayList<Object> aArgs = new ArrayList<>();

		if(AnnotationUtils.hasParametersAsArrayAnnotation(method))
		{
			aArgs.add(param.args);
		}

		//aArgs.addAll(Arrays.asList(param.args));
		addParametersToList(method, aArgs, param.args);


		method.setAccessible(true);

		try
		{
			objectResultHook = method.invoke(objectOurInstance, aArgs.toArray());

			if(MethodUtils.hasVoidReturnValue(method))
			{
				objectResultHook = AbstractXClass.CONTINUE_EXECUTION;
			}

			handleResultOfHookedMethod(param, objectResultHook);
		}
		catch(Exception e)
		{
			handleExceptionOfHookedMethod(param, method, e);
		}
	}

	/**
	 * Invokes the after-method
	 *
	 * @param param					the arguments we got from xposed
	 * @param method				the before-method
	 * @param objectOurInstance		the instance of our xposed class
	 * @param objectResult 			the result (of the original method) we got from xposed
	 */
	public static void invokeAfter(XC_MethodHook.MethodHookParam param, Method method, Object objectOurInstance, Object objectResult)
	{
		Object objectResultHook = null;
		ArrayList<Object> aArgs = new ArrayList<>();

		aArgs.add(objectResult);

	/*	if(hasParametersAsArrayAnnotation(method))
		{
			aArgs.add(param.args);
		}*/

		//aArgs.addAll(Arrays.asList(param.args));
		addParametersToList(method, aArgs, param.args);

		method.setAccessible(true);

		try
		{
			objectResultHook = method.invoke(objectOurInstance, aArgs.toArray());

			if(MethodUtils.hasVoidReturnValue(method))
			{
				objectResultHook = AbstractXClass.CONTINUE_EXECUTION;
			}

			handleResultOfHookedMethod(param, objectResultHook);
		}
		catch(Exception e)
		{
			handleExceptionOfHookedMethod(param, method, e);
		}
	}

}
