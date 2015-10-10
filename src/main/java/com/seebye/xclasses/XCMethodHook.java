package com.seebye.xclasses;

import com.seebye.xclasses.utils.XCMethodUtils;

import java.lang.reflect.Modifier;

import de.robv.android.xposed.XC_MethodHook;

/**
 * Created by nico on 06.10.15.
 */
public class XCMethodHook extends XC_MethodHook
{
	private HookedMethod m_method = null;
	private Class m_clzOurClass = null;

	public XCMethodHook(Class clzOurClass, final HookedMethod method)
	{
		m_clzOurClass = clzOurClass;
		m_method = method;
	}

	@Override
	protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
		if(m_method.getMethodBefore() != null)
		{
			Object objectOurInstance = Modifier.isStatic(m_method.getMethodBefore().getModifiers())
					? null : XCMethodUtils.getXposedObjectFromOriginalObject(param.thisObject, m_clzOurClass);
			/*	XposedBridge.log("call before "+m_method.getMethodBefore().getName()+" "+objectOurInstance
								+ "\nstatic: "+Modifier.isStatic(m_method.getMethodBefore().getModifiers())
								+ "\nvar-name: "+AbstractHookedClass.getAdditionalXposedObjectVariableName(m_clzOurClass)
								);*/
			XCMethodUtils.invokeBefore(param, m_method.getMethodBefore(), objectOurInstance);
			//	XposedBridge.log("before called");
		}
	}

	@Override
	protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
		if(m_method.getMethodAfter() != null)
		{
			Object objectOurInstance = Modifier.isStatic(m_method.getMethodAfter().getModifiers()) ? null : XCMethodUtils.getXposedObjectFromOriginalObject(param.thisObject, m_clzOurClass);
			//	XposedBridge.log("call after "+m_method.getMethodAfter().getName()+" "+objectOurInstance);
			XCMethodUtils.invokeAfter(param, m_method.getMethodAfter(), objectOurInstance, param.getResult());
			//	XposedBridge.log("after called");
		}
	}
}
