package me.ooi.typeconvertor.util;

import java.lang.reflect.Method;

/**
 * @author jun.zhao
 * @since 1.0
 */
public class ClassUtils {
	
	/**
	 * {@link Class#forName(String)}
	 * @param className
	 * @return return null if not exist.
	 */
	public static Class<?> forName(String className){
		try {
			return Class.forName(className, true, Thread.currentThread().getContextClassLoader()) ;
		} catch (ClassNotFoundException e) {
			return null;
		}
	}
	
	/**
	 * Determine whether the given class has a method with the given signature.
	 * <p>Essentially translates <code>NoSuchMethodException</code> to "false".
	 * @param clazz	the clazz to analyze
	 * @param methodName the name of the method
	 * @param paramTypes the parameter types of the method
	 * @return whether the class has a corresponding method
	 * @see java.lang.Class#getMethod
	 */
	public static boolean hasMethod(Class clazz, String methodName, Class[] paramTypes) {
		return (getMethodIfAvailable(clazz, methodName, paramTypes) != null);
	}

	/**
	 * Determine whether the given class has a method with the given signature,
	 * and return it if available (else return <code>null</code>).
	 * <p>Essentially translates <code>NoSuchMethodException</code> to <code>null</code>.
	 * @param clazz	the clazz to analyze
	 * @param methodName the name of the method
	 * @param paramTypes the parameter types of the method
	 * @return the method, or <code>null</code> if not found
	 * @see java.lang.Class#getMethod
	 */
	public static Method getMethodIfAvailable(Class clazz, String methodName, Class[] paramTypes) {
		Assert.notNull(clazz, "Class must not be null");
		Assert.notNull(methodName, "Method name must not be null");
		try {
			return clazz.getMethod(methodName, paramTypes);
		}
		catch (NoSuchMethodException ex) {
			return null;
		}
	}

}
