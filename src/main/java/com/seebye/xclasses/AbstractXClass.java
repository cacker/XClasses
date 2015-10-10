package com.seebye.xclasses;

import com.seebye.xclasses.exceptions.OriginalClassNameMissingException;
import com.seebye.xclasses.exceptions.TargetDestroyedException;

import java.lang.ref.WeakReference;

import de.robv.android.xposed.XposedHelpers;

/**
 * Created by nico on 24.09.15.<br>
 * <br>
 * Note:<br>
 *     Every subclass needs to declare a public static method with the name "getOriginalClassName".<br>
 *     It needs to return the name of the class we want to hook.<br>
 *     If the variable isn't declared a {@link OriginalClassNameMissingException} will be thrown.<br>
 * <br>
 * Note2: This class allows only one constructor.
 */
public abstract class AbstractXClass<T>
{
	/**
	 * Stops the execution by returning null.<br>
	 * Warning: The app could crash if null as return value isn't supported.
	 */
	public static final Object STOP_EXECUTION = null;
	// child classes doesn't recreate static final variables
	// see my test code for a proof: https://ideone.com/UO1xDt
	// so we can use this variable to continue the execution of hooked methods
	/**
	 * Continues the execution by returning a static final object.
	 */
	public static final Object CONTINUE_EXECUTION = new Object();
	// we can replace them by redefining them
	// the same goes for methods
	// (this is the best solution I could think of.. as static abstract methods doesn't exist..)
	public static String getOriginalClassName() throws OriginalClassNameMissingException {
		throw new OriginalClassNameMissingException();
	}
	public static final String ORIGINAL_CLASS_NAME_METHODNAME = "getOriginalClassName";
	// special methods
	public static final String CONSTRUCTOR = "CONSTRUCTOR";
	// additional variables which will be injected into the original class
	/** variable which will hold the reference to the subclass of {@link AbstractXClass} */
	public static final String ADDITIONAL_VAR_XPOSED_OBJECT = "m_xposedObject";

	// we need to add for each class a own variable to the original class
	public static String getAdditionalXposedObjectVariableName(Class clzOurClass)
	{
		return AbstractXClass.ADDITIONAL_VAR_XPOSED_OBJECT + clzOurClass.getCanonicalName().replaceAll("[^a-zA-Z0-9_]", "");
	}
	// sometimes we need to get the instance from our class via the object of the original class
	public static <C> C getXposedObject(Object objectOriginalInstance, Class<C> clzOurClass)
	{
		return (C) XposedHelpers.getAdditionalInstanceField(objectOriginalInstance, AbstractXClass.getAdditionalXposedObjectVariableName(clzOurClass));
	}



	private WeakReference<T> m_objectThis = null;

	public AbstractXClass(T objectThis)
	{
		m_objectThis = new WeakReference<T>(objectThis);
	}




	/**
	 * Returns the original class object<br>
	 * If the original object is destroyed by the gc the method will throw a {@link TargetDestroyedException} exception.<br>
	 * This will lead to stop of the execution of the method.<br>
	 * -> Nor the execution of the original method neither the execution of the hook method will be continued.
	 *
	 * @return
	 * @throws TargetDestroyedException
	 */
	protected final T getThis() throws TargetDestroyedException {
		T objectReturn = m_objectThis.get();

		if(objectReturn == null)
		{
			// looks like the hooked object is a victim of the gc
			// let's stop the execution
			throw new TargetDestroyedException();
		}

		return objectReturn;
	}

	/**
	 * Calls {@link #getThis()}
	 * @see #getThis()
	 * @return
	 * @throws TargetDestroyedException
	 */
	protected final T self() throws TargetDestroyedException {
		return getThis();
	}

	/**
	 * Getter for variables of the original class
	 *
	 * @param strFieldName
	 * @return
	 * @throws TargetDestroyedException
	 */
	protected Object get(String strFieldName) throws TargetDestroyedException
	{
		return XposedHelpers.getObjectField(getThis(), strFieldName);
	}

	/**
	 * Setter for variables of the original class
	 *
	 * @param strFieldName
	 * @return
	 * @throws TargetDestroyedException
	 */
	protected void set(String strFieldName, Object object) throws TargetDestroyedException
	{
		XposedHelpers.setObjectField(getThis(), strFieldName, object);
	}
}
