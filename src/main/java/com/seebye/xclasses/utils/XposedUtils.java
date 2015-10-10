package com.seebye.xclasses.utils;

import com.seebye.xclasses.AbstractXClass;
import com.seebye.xclasses.CustomType;
import com.seebye.xclasses.HookedMethod;
import com.seebye.xclasses.XCConstructorMethodHook;
import com.seebye.xclasses.XCMethodHook;
import com.seebye.xclasses.XCOriginalMethod;
import com.seebye.xclasses.exceptions.HookOverflowException;
import com.seebye.xclasses.exceptions.OriginalClassNameMissingException;
import com.seebye.xclasses.exceptions.ParameterManipulationBeforeException;
import com.seebye.xclasses.exceptions.PrivateException;
import com.seebye.xclasses.exceptions.StaticNonStaticException;
import com.seebye.xclasses.exceptions.ParameterManipulationAfterException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

/**
 * Created by nico on 03.10.15.
 */
public class XposedUtils
{
	private static ClassLoader s_classLoader = null;

	public static void setup(ClassLoader classLoader)
	{
		s_classLoader = classLoader;
	}

	public static ClassLoader getClassLoader()
	{
		return s_classLoader;
	}

	private static HookedMethod getMethod(ArrayList<HookedMethod> aList, String strMethodName, Class<?>[] aParameterTypes)
	{
		HookedMethod hookedMethod = null;

		for(int i = 0
				; i < aParameterTypes.length
				; i++)
		{
			// we need to replace the customtype class with the target class
			// (otherwise we wouldn't be able to find the target method)
			if(ClassUtils.isCustomType(aParameterTypes[i]))
			{
				try {
					aParameterTypes[i] = getClassLoader().loadClass((String) aParameterTypes[i].getDeclaredMethod(CustomType.ORIGINAL_CLASS_NAME_METHODNAME).invoke(null));
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				}
			}
		}

		for(int i = 0, nLen = aList.size()
				; i < nLen
					&& hookedMethod == null
				; i++)
		{
			if(aList.get(i).getMethodName().equals(strMethodName)
					&& ParameterUtils.equalsParamterTypes(aList.get(i).getParameterTypes(), aParameterTypes))
			{
				// we already have a before or after method
				hookedMethod = aList.get(i);
			}
		}

		if(hookedMethod == null)
		{
			hookedMethod = new HookedMethod(strMethodName, aParameterTypes);
			aList.add(hookedMethod);
		}

		return hookedMethod;
	}

	/**
	 * Setups the method hooks.
	 *
	 * @param strClassName					The class name of the original class.
	 * @param clzOurClass					The class object of our {@link AbstractXClass}
	 * @param aOriginalMethods				The methods of our {@link AbstractXClass} which should be redirected to the original methods.
	 * @param aHookedConstructorMethods		The constructor methods we want to hook of the original class.
	 * @param aHookedMethods				The methods we want to hook of the original class.
	 * @throws StaticNonStaticException
	 */
	private static void setupMethodHooks(String strClassName, Class clzOurClass, ArrayList<HookedMethod> aOriginalMethods, ArrayList<HookedMethod> aHookedConstructorMethods, ArrayList<HookedMethod> aHookedMethods)
			throws StaticNonStaticException, PrivateException {
		Class clz = XposedHelpers.findClass(strClassName, getClassLoader());


		for(HookedMethod hookedMethod : aHookedConstructorMethods)
		{
			StructureUtils.checkStaticConstructorMethod(hookedMethod);
			StructureUtils.checkPrivate(hookedMethod);
		}

		XposedBridge.hookAllConstructors(clz, new XCConstructorMethodHook(clzOurClass, aHookedConstructorMethods));

		for(HookedMethod hookedMethod : aHookedMethods)
		{
			Method methodTarget = XposedHelpers.findMethodExact(clz, hookedMethod.getMethodName(), hookedMethod.getParameterTypes());
			ArrayList<Object> aParameterTypesAndCallback = new ArrayList<>(Arrays.asList((Object[])hookedMethod.getParameterTypes()));
			aParameterTypesAndCallback.add(new XCMethodHook(clzOurClass, hookedMethod));

			StructureUtils.checkStaticMethod(methodTarget, hookedMethod);
			StructureUtils.checkPrivate(hookedMethod);

			XposedHelpers.findAndHookMethod(clz, hookedMethod.getMethodName(), aParameterTypesAndCallback.toArray());
		}

		for(HookedMethod hookedMethod : aOriginalMethods)
		{
			try {
				Method methodTarget = XposedHelpers.findMethodExact(clz, hookedMethod.getMethodName(), hookedMethod.getParameterTypes());
				ArrayList<Object> aParameterTypesAndCallback = new ArrayList<>(Arrays.asList((Object[])hookedMethod.getParameterTypes()));
				try {
					aParameterTypesAndCallback.add(new XCOriginalMethod(clz.getDeclaredMethod(hookedMethod.getMethodName(), hookedMethod.getParameterTypes())));
				}
				catch (NoSuchMethodException e2)
				{
					aParameterTypesAndCallback.add(new XCOriginalMethod(clz.getMethod(hookedMethod.getMethodName(), hookedMethod.getParameterTypes())));
				}


				StructureUtils.checkStaticMethod(methodTarget, hookedMethod);


				XposedHelpers.findAndHookMethod(clzOurClass, hookedMethod.getMethodName(), aParameterTypesAndCallback.toArray());
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Searches for methods we want to hook with help of the passed class.
	 * Also it setups the hooks after the methods are determined.
	 *
	 * @param clzOurClass A subclass of {@link AbstractXClass} which holds the methods we want to hook.
	 * @throws OriginalClassNameMissingException
	 * @throws ParameterManipulationAfterException
	 * @throws StaticNonStaticException
	 * @throws ParameterManipulationBeforeException
	 */
	public static <SubClass extends AbstractXClass> void hook(final Class<SubClass> clzOurClass)
			throws OriginalClassNameMissingException, ParameterManipulationAfterException
			, StaticNonStaticException, ParameterManipulationBeforeException, PrivateException, HookOverflowException {
		String strClassName = null;
		Method[] aMethods = clzOurClass.getDeclaredMethods();
		ArrayList<HookedMethod> aOriginalMethods = new ArrayList<>();
		ArrayList<HookedMethod> aHookedMethods = new ArrayList<>();
		ArrayList<HookedMethod> aHookedConstructorMethods = new ArrayList<>();

		try
		{
			/**
			 * look for the class name of the class we want to hook
			 * if the static method isn't redefined a {@link OriginalClassNameMissingException} will be thrown
			 */
			strClassName = (String) clzOurClass.getDeclaredMethod(AbstractXClass.ORIGINAL_CLASS_NAME_METHODNAME).invoke(null);


			// go though the methods of our class and look for methods to hook
			for(Method method : aMethods)
			{
				HookedMethod hookedMethod = null;
				ArrayList<Class<?>> aParameterTypes = null;
				String strMethodName = method.getName();
				ArrayList<HookedMethod> aMethodList = aHookedMethods;

				if(AnnotationUtils.isConstructorMethodHook(method))
				{
					// we want to hook the constructor of the original class
					strMethodName = AbstractXClass.CONSTRUCTOR;
					aMethodList = aHookedConstructorMethods;
				}

				if(AnnotationUtils.isBeforeMethodHook(method))
				{
					// this method should be called before the original method was called
					aParameterTypes = new ArrayList<>(Arrays.asList(method.getParameterTypes()));

					if (AnnotationUtils.hasParametersAsArrayAnnotation(method))
					{
						// we want to manipulate the passed arguments..
						// so we need to pass the parameters as an array as an additional parameter to our own method

						// we need to remove this parameter from our list in order to search for the original method
						// should be the Object-Array
						if(aParameterTypes.remove(0) != Object[].class)
						{
							// looks like we forgot to add this parameter
							throw new ParameterManipulationBeforeException();
						}
					}

					// getMethod will add the method to our list
					hookedMethod = getMethod(aMethodList, StructureUtils.formatMethodName(strMethodName), aParameterTypes.toArray(new Class<?>[aParameterTypes.size()]));

					// only one before-method for a original method (with the same parameters) per xclass
					if(hookedMethod.getMethodBefore() != null)
					{
						throw new HookOverflowException();
					}

					hookedMethod.setMethodBefore(method);
				}
				else if(AnnotationUtils.isAfterMethodHook(method))
				{
					// this method should be called after the original method was called

					// first parameter should be objectResultUntilNow, we need to remove it from our list
					// unfortunately we can't check whether we forgot the parameter or not..
					// (as we want to allow every type for an comfortable usage)
					aParameterTypes = new ArrayList<>(Arrays.asList(method.getParameterTypes()));
					aParameterTypes.remove(0);

					if (AnnotationUtils.hasParametersAsArrayAnnotation(method))
					{
						// parameter manipulation is useless for after-methods
						throw new ParameterManipulationAfterException();
					}

					// getMethod will add the method to our list
					hookedMethod = getMethod(aMethodList, StructureUtils.formatMethodName(strMethodName), aParameterTypes.toArray(new Class<?>[aParameterTypes.size()]));

					// only one after-method for a original method (with the same parameters) per xclass
					if(hookedMethod.getMethodAfter() != null)
					{
						throw new HookOverflowException();
					}

					hookedMethod.setMethodAfter(method);
				}
				else if(AnnotationUtils.isOriginalMethod(method))
				{
					// we want to hook our own method in order to call the original method

					hookedMethod = getMethod(aOriginalMethods, strMethodName, method.getParameterTypes());
					hookedMethod.setMethodBefore(method);
				}
			}

			// setup the hooks in our lists
			setupMethodHooks(strClassName, clzOurClass, aOriginalMethods, aHookedConstructorMethods, aHookedMethods);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
}
