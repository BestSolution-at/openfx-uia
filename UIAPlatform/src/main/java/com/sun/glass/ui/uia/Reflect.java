package com.sun.glass.ui.uia;

import java.lang.reflect.Method;

// a generic reflection utility
public class Reflect {

	private final Object target;
	
	public Reflect(Object target) {
		this.target = target;
	}
	

	public void invokeVoid0(String name) {
		try {
			Method method = target.getClass().getDeclaredMethod(name);
			method.setAccessible(true);
			method.invoke(target);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	public void invokeVoid1(String name, Class<?> argType, Object arg) {
		try {
			Method method = target.getClass().getDeclaredMethod(name, argType);
			method.setAccessible(true);
			method.invoke(target, arg);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	public Object invokeObject0(String name) {
		try {
			Method method = target.getClass().getDeclaredMethod(name);
			method.setAccessible(true);
			return method.invoke(target);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	public Object invokeObject1(String name, Class<?> argType, Object arg) {
		try {
			Method method = target.getClass().getDeclaredMethod(name, argType);
			method.setAccessible(true);
			return method.invoke(target, arg);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	public Object invokeObject2(String name, Class<?> arg1Type, Class<?> arg2Type, Object arg1, Object arg2) {
		try {
			Method method = target.getClass().getDeclaredMethod(name, arg1Type, arg2Type);
			method.setAccessible(true);
			return method.invoke(target, arg1, arg2);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	public Object invokeObject3(String name, 
			Class<?> arg1Type, Class<?> arg2Type, Class<?> arg3Type,
			Object arg1, Object arg2, Object arg3) {
		try {
			Method method = target.getClass().getDeclaredMethod(name, arg1Type, arg2Type, arg3Type);
			method.setAccessible(true);
			return method.invoke(target, arg1, arg2, arg3);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	public Object invokeObject8(String name, 
			Class<?> arg1Type, Class<?> arg2Type, Class<?> arg3Type, Class<?> arg4Type, Class<?> arg5Type, Class<?> arg6Type, Class<?> arg7Type, Class<?> arg8Type,
			Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6, Object arg7, Object arg8) {
		try {
			Method method = target.getClass().getDeclaredMethod(name, arg1Type, arg2Type, arg3Type, arg4Type, arg5Type, arg6Type, arg7Type, arg8Type);
			method.setAccessible(true);
			return method.invoke(target, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

}
