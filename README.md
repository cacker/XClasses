# XClasses

XClasses is a library which aims to improve the structure and the readability of Xposed Modules.

## 1. XposedMain.java 

XposedMain is the class which you need to name in your 'xposed_init' file.  
It needs to implement IXposedHookLoadPackage.  
Also you need to call XposedUtils.setup(ClassLoader) inside of the method handleLoadPackage.  
To initialize a hook simply call XposedUtils.hook(Class).
Take as example the following code:

```java
package com.seebye.xclassesclockdemo;

import static com.seebye.xclasses.utils.XposedUtils.hook;
import static com.seebye.xclasses.utils.XposedUtils.setup;
// some more imports..

public class XposedMain
        implements IXposedHookLoadPackage
{
    @Override
	public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam)
			throws Throwable {
		setup(lpparam.classLoader);

		try
		{
			switch (lpparam.packageName)
			{
				case "com.android.systemui":
					handleSystemUI();
					break;
			}
		}
		catch (OriginalClassNameMissingException e) {
			e.printStackTrace();
		} catch (ParameterManipulationAfterException e) {
			e.printStackTrace();
		} catch (StaticNonStaticException e) {
			e.printStackTrace();
		} catch (ParameterManipulationBeforeException e) {
			e.printStackTrace();
		} catch (PrivateException e) {
			e.printStackTrace();
		} catch (HookOverflowException e) {
			e.printStackTrace();
		}
	}

	private void handleSystemUI()
			throws PrivateException, ParameterManipulationAfterException, StaticNonStaticException,
				   OriginalClassNameMissingException, HookOverflowException,
				   ParameterManipulationBeforeException {
		hook(XClock.class);
	}
}
```


## 2. XClasses

XClasses are thought as extensions of the original classes.  
They're generic classes and instantiated with a call of a hooked method.  
After their instantiation a reference is create which exists as long as the instance of the original class.  

### 2.1. Subclasses

The generic type of a subclass should be the class of the original class or a superclass of the original class or the Object class.  
E.g.  
```java
public class XClock
    extends AbstractXClass<TextView>
```

Also we need to define a static method in order to know which class we want to hook.  
E.g.  
```java
    public static String getOriginalClassName()	{ return "com.android.systemui.statusbar.policy.Clock";	}
```

### 2.2. Methods

The following methods helps you to write a readable code.  

**T getThis() throws TargetDestroyedException**
> Returns the instance of the original class 

**T self() throws TargetDestroyedException**
> Calls getThis()

**Object get(String strFieldName) throws TargetDestroyedException**  
> Returns the content of the field with the name strFieldName.  
> Use this method to create submethods.  
> E.g.  
> public String getName() {  
> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return get("mName");  
> }  

**void set(String strFieldName, Object object) throws TargetDestroyedException**  
> Setter for variables of the instance of the original class.  
> Use this method to create submethods.

### 2.3. Hooks  

Hooks are marked with annotations.  
Hook methods need to have the same parameters as the original methods.  
Also they need to have the same name as the original method.  
In ordner to use before- & after-hooks you can add the suffix '_Before' or '_After' to the method name.  
As hook methods should be called by Xposed only they need to be declared as private.  

To change the return value simply return a object in the hook method.  
To throw an exception throw a HookThrowException which holds the exception you want to throw.  
E.g.  
```java
throw new HookThrowException(new NullPointerException());
```

#### 2.3.1. Before method hooks  
Returning anything except CONTINUE_EXECUTION will lead to blocked execution of the original method.  
Also it will change the return value for non void methods.  
To increase the readability I suggest to use STOP_EXECUTION in order to increase the readability.  
Note: This works only for void methods and methods which are able to return null.  
Otherwise it could lead to a crash of the app which holds the original class.
```java
    @BeforeOriginalMethod
    private String getHint_Before() throws Throwable {
    	return "changed ;)";
    }  
```  

#### 2.3.2. After method hooks  
The result of the original method will be passed to the after-method as first parameter in addition to the parameters of the original method.
```java
    @AfterOriginalMethod
    private String getHint_After(String strResult) throws Throwable {
    	return strResult + " changed ;)";
    }
```


#### 2.3.3. Parameter Manipulation  
In order to change the passed parameter you need to an annotation.  
Methods with this annotation will receive all arguments as an object array as first parameter.  
Note: Only before-methods can use this annotation.  
```java
    @ParameterManipulation
	@BeforeOriginalMethod
	private void setTextColor(Object[] aParams, int color) throws Throwable {
		aParams[0] = Color.GREEN; // color changed to green
        // if you want to do something after the manipulation
        // assign it like the following code
        // aParams[0] = color = Color.GREEN;
	}
```


#### 2.3.4. Hooking constructors  
Constructors are methods which are called on the instantiation of the object.  
So.. you just need to mark a before- or after-method as constructor by using an annotation.  
In this case we don't care about the name of the method.  
```java
    @OriginalConstructor
    @BeforeOriginalMethod
    public void TextView(Context context) throws Throwable {
        getThis().setText("constructor hooked");
    }
```

#### 2.3.5. Calling original methods
Like in the other times we need to add an annotation.  
Also our method needs to have the same parameters and the same name as the original method like the hook.  
Methods with this annotation will be hooked and redirected to the original method.
```java
    // this is a hidden method of the class TextView 
    // you also need this annotation to be able to call private or protected methods
    @OriginalMethod
    private float getScaledTextSize() { return 0f; }
```
