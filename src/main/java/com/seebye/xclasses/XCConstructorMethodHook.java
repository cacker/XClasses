package com.seebye.xclasses;

import com.seebye.xclasses.utils.ParameterUtils;
import com.seebye.xclasses.utils.XCMethodUtils;

import java.util.ArrayList;

import de.robv.android.xposed.XC_MethodHook;

/**
 * Created by nico on 06.10.15.
 */
public class XCConstructorMethodHook extends XC_MethodHook
{
	private ArrayList<HookedMethod> m_aMethods = null;
	private Class m_clzOurClass = null;

	public XCConstructorMethodHook(Class clzOurClass, final ArrayList<HookedMethod> aMethods)
	{
		m_aMethods = aMethods;
		m_clzOurClass = clzOurClass;
	}

	@Override
	protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
		Object objectOurInstance = null;

		for(HookedMethod method : m_aMethods)
		{
			if(objectOurInstance == null)
			{
				objectOurInstance = XCMethodUtils.getXposedObjectFromOriginalObject(param.thisObject, m_clzOurClass);
			}

			if(method.getMethodBefore() != null
					&& ParameterUtils.equalsParamterTypes(ParameterUtils.getParameterTypes(param.args), method.getParameterTypes()))
			{
				XCMethodUtils.invokeBefore(param, method.getMethodBefore(), objectOurInstance);
			}
		}
	}

	@Override
	protected void afterHookedMethod(MethodHookParam param) throws Throwable {
		Object objectOurInstance = null;

		for(HookedMethod method : m_aMethods)
		{
			if(objectOurInstance == null)
			{
				objectOurInstance = XCMethodUtils.getXposedObjectFromOriginalObject(param.thisObject, m_clzOurClass);
			}

			if(method.getMethodAfter() != null
					&& ParameterUtils.equalsParamterTypes(ParameterUtils.getParameterTypes(param.args), method.getParameterTypes()))
			{
				XCMethodUtils.invokeAfter(param, method.getMethodAfter(), objectOurInstance, param.getResult());
			}
		}
	}
}