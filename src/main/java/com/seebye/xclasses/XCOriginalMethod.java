package com.seebye.xclasses;

import java.lang.reflect.Member;
import java.lang.reflect.Modifier;

import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;

/**
 * Created by nico on 06.10.15.
 */
public class XCOriginalMethod extends XC_MethodReplacement
{
	private Member m_member = null; // the original method

	public XCOriginalMethod(Member member)
	{
		m_member = member;
	}

	@Override
	protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
		Object objectThis = Modifier.isStatic(m_member.getModifiers()) ? null : ((AbstractXClass)param.thisObject).getThis();
		return XposedBridge.invokeOriginalMethod(m_member, objectThis, param.args);
	}
}