package com.hotmail.AdrianSR.core.util.classes;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.hotmail.AdrianSR.core.util.version.ServerVersion;

/**
 * <b>ReflectionUtils</b>
 * <p>
 * This class provides useful methods which makes dealing with reflection much
 * easier, especially when working with Bukkit
 * <p>
 * You are welcome to use it, modify it and redistribute it under the following
 * conditions:
 * <ul>
 * <li>Don't claim this class as your own
 * <li>Don't remove this disclaimer
 * </ul>
 * <p>
 * <i>It would be nice if you provide credit to me if you use this class in a
 * published project</i>
 * @author DarkBlade12
 * <p>
 * @version 1.1
 */
public final class ReflectionUtils {
	
	// Prevent accidental construction
	private ReflectionUtils() {
		/* empty */
	}
	
	/**
	 * Returns the sub class with the given simple name that is in the given father class.
	 * <p>
	 * @param father the father class.
	 * @param name the simple name of the sub class.
	 * @return the sub class with the given name.
	 * @throws ClassNotFoundException throwed if the class was not found.
	 */
	public static Class<?> getSubClass(Class<?> father, String name) throws ClassNotFoundException {
		for (Class<?> sub_class : father.getClasses()) {
			if (sub_class.getSimpleName().equals(name)) {
				return sub_class;
			}
		}
		
		/* search in the declared classes */
		for (Class<?> sub_class : father.getDeclaredClasses()) {
			if (sub_class.getSimpleName().equals(name)) {
				return sub_class;
			}
		}
		throw new ClassNotFoundException("There is not a sub class called '" + name + "' in the specified class!");
	}

	/**
	 * Returns the constructor of a given class with the given parameter types
	 * 
	 * @param clazz
	 *            Target class
	 * @param parameterTypes
	 *            Parameter types of the desired constructor
	 * @return The constructor of the target class with the specified parameter
	 *         types
	 * @throws NoSuchMethodException
	 *             If the desired constructor with the specified parameter types
	 *             cannot be found
	 * @see DataType
	 * @see DataType#getPrimitive(Class[])
	 * @see DataType#compare(Class[], Class[])
	 */
	public static Constructor<?> getConstructor(Class<?> clazz, Class<?>... parameterTypes)
			throws NoSuchMethodException {
		Class<?>[] primitiveTypes = DataType.getPrimitive(parameterTypes);
		for (Constructor<?> constructor : clazz.getConstructors()) {
			if (!DataType.compare(DataType.getPrimitive(constructor.getParameterTypes()), primitiveTypes)) {
				continue;
			}
			return constructor;
		}
		throw new NoSuchMethodException(
				"There is no such constructor in this class with the specified parameter types");
	}

	/**
	 * Returns the constructor of a desired class with the given parameter types
	 * 
	 * @param className
	 *            Name of the desired target class
	 * @param packageType
	 *            Package where the desired target class is located
	 * @param parameterTypes
	 *            Parameter types of the desired constructor
	 * @return The constructor of the desired target class with the specified
	 *         parameter types
	 * @throws NoSuchMethodException
	 *             If the desired constructor with the specified parameter types
	 *             cannot be found
	 * @throws ClassNotFoundException
	 *             ClassNotFoundException If the desired target class with the
	 *             specified name and package cannot be found
	 * @see #getClass(String, PackageType)
	 * @see #getConstructor(Class, Class...)
	 */
	public static Constructor<?> getConstructor(String className, PackageType packageType, Class<?>... parameterTypes)
			throws NoSuchMethodException, ClassNotFoundException {
		return getConstructor(packageType.getClass(className), parameterTypes);
	}

	/**
	 * Returns an instance of a class with the given arguments
	 * 
	 * @param clazz
	 *            Target class
	 * @param arguments
	 *            Arguments which are used to construct an object of the target
	 *            class
	 * @return The instance of the target class with the specified arguments
	 * @throws InstantiationException
	 *             If you cannot create an instance of the target class due to
	 *             certain circumstances
	 * @throws IllegalAccessException
	 *             If the desired constructor cannot be accessed due to certain
	 *             circumstances
	 * @throws IllegalArgumentException
	 *             If the types of the arguments do not match the parameter types of
	 *             the constructor (this should not occur since it searches for a
	 *             constructor with the types of the arguments)
	 * @throws InvocationTargetException
	 *             If the desired constructor cannot be invoked
	 * @throws NoSuchMethodException
	 *             If the desired constructor with the specified arguments cannot be
	 *             found
	 */
	public static Object instantiateObject(Class<?> clazz, Object... arguments) throws InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
		return getConstructor(clazz, DataType.getPrimitive(arguments)).newInstance(arguments);
	}

	/**
	 * Returns an instance of a desired class with the given arguments
	 * 
	 * @param className
	 *            Name of the desired target class
	 * @param packageType
	 *            Package where the desired target class is located
	 * @param arguments
	 *            Arguments which are used to construct an object of the desired
	 *            target class
	 * @return The instance of the desired target class with the specified arguments
	 * @throws InstantiationException
	 *             If you cannot create an instance of the desired target class due
	 *             to certain circumstances
	 * @throws IllegalAccessException
	 *             If the desired constructor cannot be accessed due to certain
	 *             circumstances
	 * @throws IllegalArgumentException
	 *             If the types of the arguments do not match the parameter types of
	 *             the constructor (this should not occur since it searches for a
	 *             constructor with the types of the arguments)
	 * @throws InvocationTargetException
	 *             If the desired constructor cannot be invoked
	 * @throws NoSuchMethodException
	 *             If the desired constructor with the specified arguments cannot be
	 *             found
	 * @throws ClassNotFoundException
	 *             If the desired target class with the specified name and package
	 *             cannot be found
	 * @see #getClass(String, PackageType)
	 * @see #instantiateObject(Class, Object...)
	 */
	public static Object instantiateObject(String className, PackageType packageType, Object... arguments)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, ClassNotFoundException {
		return instantiateObject(packageType.getClass(className), arguments);
	}

	/**
	 * Returns a method of a class with the given parameter types
	 * 
	 * @param clazz
	 *            Target class
	 * @param methodName
	 *            Name of the desired method
	 * @param parameterTypes
	 *            Parameter types of the desired method
	 * @return The method of the target class with the specified name and parameter
	 *         types
	 * @throws NoSuchMethodException
	 *             If the desired method of the target class with the specified name
	 *             and parameter types cannot be found
	 * @see DataType#getPrimitive(Class[])
	 * @see DataType#compare(Class[], Class[])
	 */
	public static Method getMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) throws NoSuchMethodException {
		Class<?>[] primitiveTypes = DataType.getPrimitive(parameterTypes);
		for (Method method : clazz.getMethods()) {
			if (!method.getName().equals(methodName) || !DataType.compare(DataType.getPrimitive(method.getParameterTypes()), primitiveTypes)) {
				continue;
			}
			return method;
		}
		throw new NoSuchMethodException("There is no such method in this class with the specified name and parameter types");
	}

	/**
	 * Returns a method of a desired class with the given parameter types
	 * 
	 * @param className
	 *            Name of the desired target class
	 * @param packageType
	 *            Package where the desired target class is located
	 * @param methodName
	 *            Name of the desired method
	 * @param parameterTypes
	 *            Parameter types of the desired method
	 * @return The method of the desired target class with the specified name and
	 *         parameter types
	 * @throws NoSuchMethodException
	 *             If the desired method of the desired target class with the
	 *             specified name and parameter types cannot be found
	 * @throws ClassNotFoundException
	 *             If the desired target class with the specified name and package
	 *             cannot be found
	 * @see #getClass(String, PackageType)
	 * @see #getMethod(Class, String, Class...)
	 */
	public static Method getMethod(String className, PackageType packageType, String methodName,
			Class<?>... parameterTypes) throws NoSuchMethodException, ClassNotFoundException {
		return getMethod(packageType.getClass(className), methodName, parameterTypes);
	}

	/**
	 * Invokes a method on an object with the given arguments
	 * 
	 * @param instance
	 *            Target object
	 * @param methodName
	 *            Name of the desired method
	 * @param arguments
	 *            Arguments which are used to invoke the desired method
	 * @return The result of invoking the desired method on the target object
	 * @throws IllegalAccessException
	 *             If the desired method cannot be accessed due to certain
	 *             circumstances
	 * @throws IllegalArgumentException
	 *             If the types of the arguments do not match the parameter types of
	 *             the method (this should not occur since it searches for a method
	 *             with the types of the arguments)
	 * @throws InvocationTargetException
	 *             If the desired method cannot be invoked on the target object
	 * @throws NoSuchMethodException
	 *             If the desired method of the class of the target object with the
	 *             specified name and arguments cannot be found
	 * @see #getMethod(Class, String, Class...)
	 * @see DataType#getPrimitive(Object[])
	 */
	public static Object invokeMethod(Object instance, String methodName, Object... arguments)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
		return getMethod(instance.getClass(), methodName, DataType.getPrimitive(arguments)).invoke(instance, arguments);
	}

	/**
	 * Invokes a method of the target class on an object with the given arguments
	 * 
	 * @param instance
	 *            Target object
	 * @param clazz
	 *            Target class
	 * @param methodName
	 *            Name of the desired method
	 * @param arguments
	 *            Arguments which are used to invoke the desired method
	 * @return The result of invoking the desired method on the target object
	 * @throws IllegalAccessException
	 *             If the desired method cannot be accessed due to certain
	 *             circumstances
	 * @throws IllegalArgumentException
	 *             If the types of the arguments do not match the parameter types of
	 *             the method (this should not occur since it searches for a method
	 *             with the types of the arguments)
	 * @throws InvocationTargetException
	 *             If the desired method cannot be invoked on the target object
	 * @throws NoSuchMethodException
	 *             If the desired method of the target class with the specified name
	 *             and arguments cannot be found
	 * @see #getMethod(Class, String, Class...)
	 * @see DataType#getPrimitive(Object[])
	 */
	public static Object invokeMethod(Object instance, Class<?> clazz, String methodName, Object... arguments)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
		return getMethod(clazz, methodName, DataType.getPrimitive(arguments)).invoke(instance, arguments);
	}

	/**
	 * Invokes a method of a desired class on an object with the given arguments
	 * 
	 * @param instance
	 *            Target object
	 * @param className
	 *            Name of the desired target class
	 * @param packageType
	 *            Package where the desired target class is located
	 * @param methodName
	 *            Name of the desired method
	 * @param arguments
	 *            Arguments which are used to invoke the desired method
	 * @return The result of invoking the desired method on the target object
	 * @throws IllegalAccessException
	 *             If the desired method cannot be accessed due to certain
	 *             circumstances
	 * @throws IllegalArgumentException
	 *             If the types of the arguments do not match the parameter types of
	 *             the method (this should not occur since it searches for a method
	 *             with the types of the arguments)
	 * @throws InvocationTargetException
	 *             If the desired method cannot be invoked on the target object
	 * @throws NoSuchMethodException
	 *             If the desired method of the desired target class with the
	 *             specified name and arguments cannot be found
	 * @throws ClassNotFoundException
	 *             If the desired target class with the specified name and package
	 *             cannot be found
	 * @see #getClass(String, PackageType)
	 * @see #invokeMethod(Object, Class, String, Object...)
	 */
	public static Object invokeMethod(Object instance, String className, PackageType packageType, String methodName,
			Object... arguments) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, ClassNotFoundException {
		return invokeMethod(instance, packageType.getClass(className), methodName, arguments);
	}

	/**
	 * Returns a field of the target class with the given name
	 * 
	 * @param clazz
	 *            Target class
	 * @param declared
	 *            Whether the desired field is declared or not
	 * @param fieldName
	 *            Name of the desired field
	 * @return The field of the target class with the specified name
	 * @throws NoSuchFieldException
	 *             If the desired field of the given class cannot be found
	 * @throws SecurityException
	 *             If the desired field cannot be made accessible
	 */
	public static Field getField(Class<?> clazz, boolean declared, String fieldName)
			throws NoSuchFieldException, SecurityException {
		Field field = declared ? clazz.getDeclaredField(fieldName) : clazz.getField(fieldName);
		field.setAccessible(true);
		return field;
	}

	/**
	 * Returns a field of a desired class with the given name
	 * 
	 * @param className
	 *            Name of the desired target class
	 * @param packageType
	 *            Package where the desired target class is located
	 * @param declared
	 *            Whether the desired field is declared or not
	 * @param fieldName
	 *            Name of the desired field
	 * @return The field of the desired target class with the specified name
	 * @throws NoSuchFieldException
	 *             If the desired field of the desired class cannot be found
	 * @throws SecurityException
	 *             If the desired field cannot be made accessible
	 * @throws ClassNotFoundException
	 *             If the desired target class with the specified name and package
	 *             cannot be found
	 * @see #getField(Class, boolean, String)
	 */
	public static Field getField(String className, PackageType packageType, boolean declared, String fieldName)
			throws NoSuchFieldException, SecurityException, ClassNotFoundException {
		return getField(packageType.getClass(className), declared, fieldName);
	}

	/**
	 * Returns the value of a field of the given class of an object
	 * 
	 * @param instance
	 *            Target object
	 * @param clazz
	 *            Target class
	 * @param declared
	 *            Whether the desired field is declared or not
	 * @param fieldName
	 *            Name of the desired field
	 * @return The value of field of the target object
	 * @throws IllegalArgumentException
	 *             If the target object does not feature the desired field
	 * @throws IllegalAccessException
	 *             If the desired field cannot be accessed
	 * @throws NoSuchFieldException
	 *             If the desired field of the target class cannot be found
	 * @throws SecurityException
	 *             If the desired field cannot be made accessible
	 * @see #getField(Class, boolean, String)
	 */
	public static Object getValue(Object instance, Class<?> clazz, boolean declared, String fieldName)
			throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		return getField(clazz, declared, fieldName).get(instance);
	}

	/**
	 * Returns the value of a field of a desired class of an object
	 * 
	 * @param instance
	 *            Target object
	 * @param className
	 *            Name of the desired target class
	 * @param packageType
	 *            Package where the desired target class is located
	 * @param declared
	 *            Whether the desired field is declared or not
	 * @param fieldName
	 *            Name of the desired field
	 * @return The value of field of the target object
	 * @throws IllegalArgumentException
	 *             If the target object does not feature the desired field
	 * @throws IllegalAccessException
	 *             If the desired field cannot be accessed
	 * @throws NoSuchFieldException
	 *             If the desired field of the desired class cannot be found
	 * @throws SecurityException
	 *             If the desired field cannot be made accessible
	 * @throws ClassNotFoundException
	 *             If the desired target class with the specified name and package
	 *             cannot be found
	 * @see #getValue(Object, Class, boolean, String)
	 */
	public static Object getValue(Object instance, String className, PackageType packageType, boolean declared,
			String fieldName) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException,
			SecurityException, ClassNotFoundException {
		return getValue(instance, packageType.getClass(className), declared, fieldName);
	}

	/**
	 * Returns the value of a field with the given name of an object
	 * 
	 * @param instance
	 *            Target object
	 * @param declared
	 *            Whether the desired field is declared or not
	 * @param fieldName
	 *            Name of the desired field
	 * @return The value of field of the target object
	 * @throws IllegalArgumentException
	 *             If the target object does not feature the desired field (should
	 *             not occur since it searches for a field with the given name in
	 *             the class of the object)
	 * @throws IllegalAccessException
	 *             If the desired field cannot be accessed
	 * @throws NoSuchFieldException
	 *             If the desired field of the target object cannot be found
	 * @throws SecurityException
	 *             If the desired field cannot be made accessible
	 * @see #getValue(Object, Class, boolean, String)
	 */
	public static Object getValue(Object instance, boolean declared, String fieldName)
			throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		return getValue(instance, instance.getClass(), declared, fieldName);
	}

	/**
	 * Sets the value of a field of the given class of an object
	 * 
	 * @param instance
	 *            Target object
	 * @param clazz
	 *            Target class
	 * @param declared
	 *            Whether the desired field is declared or not
	 * @param fieldName
	 *            Name of the desired field
	 * @param value
	 *            New value
	 * @throws IllegalArgumentException
	 *             If the type of the value does not match the type of the desired
	 *             field
	 * @throws IllegalAccessException
	 *             If the desired field cannot be accessed
	 * @throws NoSuchFieldException
	 *             If the desired field of the target class cannot be found
	 * @throws SecurityException
	 *             If the desired field cannot be made accessible
	 * @see #getField(Class, boolean, String)
	 */
	public static void setValue(Object instance, Class<?> clazz, boolean declared, String fieldName, Object value)
			throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		getField(clazz, declared, fieldName).set(instance, value);
	}

	/**
	 * Sets the value of a field of a desired class of an object
	 * 
	 * @param instance
	 *            Target object
	 * @param className
	 *            Name of the desired target class
	 * @param packageType
	 *            Package where the desired target class is located
	 * @param declared
	 *            Whether the desired field is declared or not
	 * @param fieldName
	 *            Name of the desired field
	 * @param value
	 *            New value
	 * @throws IllegalArgumentException
	 *             If the type of the value does not match the type of the desired
	 *             field
	 * @throws IllegalAccessException
	 *             If the desired field cannot be accessed
	 * @throws NoSuchFieldException
	 *             If the desired field of the desired class cannot be found
	 * @throws SecurityException
	 *             If the desired field cannot be made accessible
	 * @throws ClassNotFoundException
	 *             If the desired target class with the specified name and package
	 *             cannot be found
	 * @see #setValue(Object, Class, boolean, String, Object)
	 */
	public static void setValue(Object instance, String className, PackageType packageType, boolean declared,
			String fieldName, Object value) throws IllegalArgumentException, IllegalAccessException,
			NoSuchFieldException, SecurityException, ClassNotFoundException {
		setValue(instance, packageType.getClass(className), declared, fieldName, value);
	}

	/**
	 * Sets the value of a field with the given name of an object
	 * 
	 * @param instance
	 *            Target object
	 * @param declared
	 *            Whether the desired field is declared or not
	 * @param fieldName
	 *            Name of the desired field
	 * @param value
	 *            New value
	 * @throws IllegalArgumentException
	 *             If the type of the value does not match the type of the desired
	 *             field
	 * @throws IllegalAccessException
	 *             If the desired field cannot be accessed
	 * @throws NoSuchFieldException
	 *             If the desired field of the target object cannot be found
	 * @throws SecurityException
	 *             If the desired field cannot be made accessible
	 * @see #setValue(Object, Class, boolean, String, Object)
	 */
	public static void setValue(Object instance, boolean declared, String fieldName, Object value)
			throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		setValue(instance, instance.getClass(), declared, fieldName, value);
	}

	/**
	 * Represents an enumeration of dynamic packages of NMS and CraftBukkit
	 * <p>
	 * This class is part of the <b>ReflectionUtils</b> and follows the same usage
	 * conditions
	 * 
	 * @author DarkBlade12
	 * @since 1.0
	 */
	public enum PackageType {
		MINECRAFT_SERVER("net.minecraft.server." + getServerVersion()), CRAFTBUKKIT("org.bukkit.craftbukkit."
				+ getServerVersion()), CRAFTBUKKIT_BLOCK(CRAFTBUKKIT, "block"), CRAFTBUKKIT_CHUNKIO(CRAFTBUKKIT,
						"chunkio"), CRAFTBUKKIT_COMMAND(CRAFTBUKKIT, "command"), CRAFTBUKKIT_CONVERSATIONS(CRAFTBUKKIT,
								"conversations"), CRAFTBUKKIT_ENCHANTMENS(CRAFTBUKKIT,
										"enchantments"), CRAFTBUKKIT_ENTITY(CRAFTBUKKIT, "entity"), CRAFTBUKKIT_EVENT(
												CRAFTBUKKIT, "event"), CRAFTBUKKIT_GENERATOR(CRAFTBUKKIT,
														"generator"), CRAFTBUKKIT_HELP(CRAFTBUKKIT,
																"help"), CRAFTBUKKIT_INVENTORY(CRAFTBUKKIT,
																		"inventory"), CRAFTBUKKIT_MAP(CRAFTBUKKIT,
																				"map"), CRAFTBUKKIT_METADATA(
																						CRAFTBUKKIT,
																						"metadata"), CRAFTBUKKIT_POTION(
																								CRAFTBUKKIT,
																								"potion"), CRAFTBUKKIT_PROJECTILES(
																										CRAFTBUKKIT,
																										"projectiles"), CRAFTBUKKIT_SCHEDULER(
																												CRAFTBUKKIT,
																												"scheduler"), CRAFTBUKKIT_SCOREBOARD(
																														CRAFTBUKKIT,
																														"scoreboard"), CRAFTBUKKIT_UPDATER(
																																CRAFTBUKKIT,
																																"updater"), CRAFTBUKKIT_UTIL(
																																		CRAFTBUKKIT,
																																		"util");

		private final String path;

		/**
		 * Construct a new package type
		 * 
		 * @param path
		 *            Path of the package
		 */
		private PackageType(String path) {
			this.path = path;
		}

		/**
		 * Construct a new package type
		 * 
		 * @param parent
		 *            Parent package of the package
		 * @param path
		 *            Path of the package
		 */
		private PackageType(PackageType parent, String path) {
			this(parent + "." + path);
		}

		/**
		 * Returns the path of this package type
		 * 
		 * @return The path
		 */
		public String getPath() {
			return path;
		}

		/**
		 * Returns the class with the given name
		 * 
		 * @param className
		 *            Name of the desired class
		 * @return The class with the specified name
		 * @throws ClassNotFoundException
		 *             If the desired class with the specified name and package cannot
		 *             be found
		 */
		public Class<?> getClass(String className) throws ClassNotFoundException {
			return Class.forName(this + "." + className);
		}

		// Override for convenience
		@Override
		public String toString() {
			return path;
		}

		/**
		 * Returns the version of your server
		 * 
		 * @return The server version
		 */
		public static String getServerVersion() {
			return Bukkit.getServer().getClass().getPackage().getName().substring(23);
		}
	}

    /**
     * Returns the current version of the Bukkit implementation
     *
     * @return the current version of the Bukkit implementation
     * @since 1.8
     */
	public static String getCraftBukkitVersion() {
		return cb().split("\\.")[3];
	}
    
    /**
     * Returns the real packagename for the org.bukkit.craftbukkit package
     * @return the real packagename for the org.bukkit.craftbukkit package
     * @since 1.9
     */
	public static String cb() {
		return Bukkit.getServer().getClass().getPackage().getName();
	}
    
    /**
     * Returns the corresponding Bukkit class, given a CraftBukkit implementation object.
     *
     * @param craftObject A CraftBukkit implementation of a Bukkit class.
     * @return the corresponding Bukkit class, given a CraftBukkit implementation object.
     * @since 1.8
     */
	public static Class<?> getBukkitClass(Object craftObject) {
		Class<?> clazz = craftObject != null ? craftObject.getClass() : null;
		while (clazz != null && clazz.getCanonicalName().contains(".craftbukkit.")) {
			clazz = clazz.getSuperclass();
		}
		return clazz;
	}
    
    /**
     * Returns the value of a field on the object.
     * @param obj           The object
     * @param fieldName     The name of the field
     * @param <T>           The type of field
     * @return the value or <code>null</code>
     * @since 1.9
     */
	@SuppressWarnings("unchecked")
	public static <T> T getField(Object obj, String fieldName) {
		try {
			Field field = getFieldInternal(obj, fieldName);
			boolean wasAccessible = field.isAccessible();
			field.setAccessible(true);
			try {
				return (T) field.get(obj);
			} finally {
				field.setAccessible(wasAccessible);
			}
		} catch (NoSuchFieldException | IllegalAccessException e) {
			System.out.println("Unable to find field " + fieldName + " on " + obj);
		}
		return null;
	}

	private static Field getFieldInternal(Object obj, String fieldName) throws NoSuchFieldException {
		return getFieldFromClass(obj.getClass(), fieldName);
	}

	private static Field getFieldFromClass(Class<?> aClass, String fieldName) throws NoSuchFieldException {
		if (aClass == null) {
			throw new NoSuchFieldException("Unable to locate field " + fieldName);
		}
		try {
			return aClass.getDeclaredField(fieldName);
		} catch (NoSuchFieldException e) {
			// Ignored
		}
		try {
			return aClass.getField(fieldName);
		} catch (NoSuchFieldException e) {
			// Ignore
		}
		return getFieldFromClass(aClass.getSuperclass(), fieldName);
	}
    
    /**
     * Sets the value of a field on the object.
     * 
     * @param obj           The object
     * @param fieldName     The name of the field
     * @param field         The value to set
     * @param <T>           The type of field
     * @since 1.9
     */
	public static <T> void setField(Object obj, String fieldName, T field) {
		try {
			Field declaredField = getFieldInternal(obj, fieldName);
			boolean wasAccessible = declaredField.isAccessible();
			declaredField.setAccessible(true);
			try {
				declaredField.set(obj, field);
			} finally {
				declaredField.setAccessible(wasAccessible);
			}
		} catch (NoSuchFieldException | IllegalAccessException e) {
			System.out.println("Unable to find field " + fieldName + " on " + obj);
		}
	}
	
	/**
     * Instantiates an object.
     * <p>
     * @param clazz The type of object to instantiate.
     * @param arg_types  An array of argument-types
     * @param args      An array of arguments
     * @param <T>       Return-type
     * @return the object, or <code>null</code>.
     * @since 1.9
     */
	@SuppressWarnings("unchecked")
	public static <T> T newInstance(Class<?> clazz, Class<?>[] arg_types, Object... args) {
		try {
			Constructor<?> constructor = clazz.getDeclaredConstructor(arg_types);
			return (T) constructor.newInstance(args);
		} catch (InstantiationException | IllegalAccessException | NoSuchMethodException
				| InvocationTargetException e) {
			System.out.println("Unable to instantiate object of type " + clazz.getSimpleName() + ":" + e);
		}
		return null;
	}
    
    /**
     * Instantiates an object.
     * 
     * @param className The name of the class
     * @param argTypes  An array of argument-types
     * @param args      An array of arguments
     * @param <T>       Return-type
     * @return the object, or <code>null</code>.
     * @since 1.9
     */
	@SuppressWarnings("unchecked")
	public static <T> T newInstance(String className, Class<?>[] argTypes, Object... args) {
		try {
			Class<?> aClass = Class.forName(className);
			Constructor<?> constructor = aClass.getDeclaredConstructor(argTypes);
			return (T) constructor.newInstance(args);
		} catch (InstantiationException | ClassNotFoundException | IllegalAccessException | NoSuchMethodException
				| InvocationTargetException e) {
			System.out.println("Unable to instantiate object of type " + className + ":" + e);
		}
		return null;
	}
	
    /**
     * Instantiates an object.
     * @param className The name of the class
     * @param args      An array of arguments
     * @param <T>       Return-type
     * @return the object, or <code>null</code>.
     * @since 1.9
     */
	public static <T> T newInstance(String className, Object... args) {
		Class<?>[] argTypes = new Class[args.length];
		int ix = 0;
		for (Object arg : args) {
			argTypes[ix++] = arg != null ? arg.getClass() : null;
		}
		return newInstance(className, argTypes, args);
	}
	
	public static void setServerMotd(String motd) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		Class<?> minecraft_server_class = ReflectionUtils.getCraftClass("MinecraftServer");
		Object         minecraft_server = minecraft_server_class.getMethod("getServer").invoke(minecraft_server_class);
		Method                   method = minecraft_server.getClass().getMethod("setMotd", String.class);
		method.invoke(minecraft_server, motd);
	}

	/**
	 * Send packet to a player.
	 * 
	 * @param p the target player.
	 * @param packet the packet to send.
	 * @throws NoSuchMethodException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchFieldException
	 * @throws ClassNotFoundException
	 */
	public static void sendPacket(Player p, Object packet) throws NoSuchMethodException, IllegalAccessException,
			InvocationTargetException, NoSuchFieldException, ClassNotFoundException {
		Object     nmsPlayer = p.getClass().getMethod("getHandle").invoke(p);
		Object plrConnection = nmsPlayer.getClass().getField("playerConnection").get(nmsPlayer);
		plrConnection.getClass().getMethod("sendPacket", getCraftClass("Packet")).invoke(plrConnection, packet);
	}
	
	/**
	 * Get a Craft Class from name.
	 * 
	 * @param className the Class Name.
	 * @return a class.
	 */
	public static Class<?> getCraftClass(String className) {
		try {
			return Class.forName("net.minecraft.server." + ServerVersion.getVersion().toString() + "." + className);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
	

	/**
	 * Get a Craft bukkit Class from name.
	 * 
	 * @param packageName the name of the package in which the class is located.
	 * @param className the Class Name.
	 * @return a class.
	 */
	public static Class<?> getCraftBukkitClass(String packageName, String className) {
		try {
			return Class.forName("org.bukkit.craftbukkit." + ServerVersion.getVersion().toString() + "."
					+ (packageName == null ? "" : packageName.toLowerCase()) + "." + className);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * A common method for all enums since they can't have another base class
	 * <p>
	 * @param <T> Enum type
	 * @param enum_class the enum type.
	 * @param constant_name the name of the constant to get.
	 * @return corresponding enum constant, or null if could not be found.
	 */
	public static <T extends Enum<T>> T getEnumConstant(Class<T> enum_class, String constant_name) {
		try {
			return Enum.valueOf(enum_class, constant_name);
		} catch (IllegalArgumentException ex) {
			return null;
		}
	}

	 /**
	 * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
	 *
	 * @param packageName The base package
	 * @return The classes
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public static Class<?>[] getClasses(String packageName)
	        throws ClassNotFoundException, IOException {
	    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
	    assert classLoader != null;
	    String path = packageName.replace('.', '/');
	    Enumeration<URL> resources = classLoader.getResources(path);
	    List<File> dirs = new ArrayList<File>();
	    while (resources.hasMoreElements()) {
	        URL resource = resources.nextElement();
	        dirs.add(new File(resource.getFile()));
	    }
	    ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
	    for (File directory : dirs) {
	        classes.addAll(findClasses(directory, packageName));
	    }
	    return classes.toArray(new Class[classes.size()]);
	}

	/**
	 * Recursive method used to find all classes in a given directory and subdirs.
	 *
	 * @param directory   The base directory
	 * @param packageName The package name for classes found inside the base directory
	 * @return The classes
	 * @throws ClassNotFoundException
	 */
	public static List<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException {
	    List<Class<?>> classes = new ArrayList<Class<?>>();
	    if (!directory.exists()) {
	        return classes;
	    }
	    File[] files = directory.listFiles();
	    for (File file : files) {
	        if (file.isDirectory()) {
	            assert !file.getName().contains(".");
	            classes.addAll(findClasses(file, packageName + "." + file.getName()));
	        } else if (file.getName().endsWith(".class")) {
	            classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
	        }
	    }
	    return classes;
	}
	
	public static Set<String> getClassNames(File jarFile, String packageName) {
		Set<String> names = new HashSet<>();
		try {
			JarFile file = new JarFile(jarFile);
			for (Enumeration<JarEntry> entry = file.entries(); entry.hasMoreElements();) {
				JarEntry jarEntry = entry.nextElement();
				String       name = jarEntry.getName().replace("/", ".");
				if ((packageName == null || packageName.trim().isEmpty() || name.startsWith(packageName.trim()))
						&& name.endsWith(".class")) {
					names.add(name.substring(0, name.lastIndexOf(".class")));
				}
			}
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return names;
	}
	
	public static Set<Class<?>> getClasses(File jarFile, String packageName) {
		Set<Class<?>> classes = new HashSet<Class<?>>();
		getClassNames(jarFile, packageName).forEach(class_name -> {
			try {
				classes.add(Class.forName(class_name));
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		});
		return classes;
	}
	
	public static Field setAccessible(Field field) {
		return setAccessible(field, true);
	}
	
	public static Field setAccessible(Field field, boolean accessible) {
		try {
			field.setAccessible(accessible);
			if (accessible) {
				Field modifiersField = Field.class.getDeclaredField("modifiers");
				modifiersField.setAccessible(true);
				modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
				modifiersField.setAccessible(false);
			}
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		return field;
	}
	
	public static Object getObject(Object object, String fieldName) throws IllegalAccessException, NoSuchFieldException {
		Field field;
		try {
			field = object.getClass().getDeclaredField(fieldName);
		} catch (Exception ex) {
			field = object.getClass().getField(fieldName);
		}
		boolean accessible = field.isAccessible();
		if (!accessible) field.setAccessible(true);
		Object obj = field.get(object);
		if (!accessible) field.setAccessible(false);
		return obj;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getFirstObject(Class<?> clazz, Class<T> objClass, Object instance) throws Exception {
		Field f = null;
		for (Field fi : clazz.getDeclaredFields()) {
			if (fi.getType().equals(objClass)) {
				f = fi;
				break;
			}
		}

		if (f == null) {
			for (Field fi : clazz.getFields()) {
				if (fi.getType().equals(objClass)) {
					f = fi;
					break;
				}
			}
		}

		f.setAccessible(true);
		return (T) f.get(instance);
	}
	
	/**
	 * Returns the classes of the types of a parameterized
	 * field, like {@link Collection}s or {@link Map}s.
	 * <p>
	 * Examples:
	 * - getParameterizedTypesClasses(Collection<Integer>) (of a collection field in this case) will return: [ java.lang.Integer ]
	 * - getParameterizedTypesClasses(Map<Integer, String>) (of a map field in this case) will return: [ java.lang.Integer, java.lang.String ]
	 * <p>
	 * @param field the parameterized field.
	 * @return the classes of the types of a parameterized field.
	 */
	public static Class<?>[] getParameterizedTypesClasses(Field field) {
		if (!(field.getGenericType() instanceof ParameterizedType)) {
			throw new IllegalArgumentException("The field is not a parameterized type!");
		}
		
		ParameterizedType parameterized_type = (ParameterizedType) field.getGenericType();
		Type[]               types_arguments = parameterized_type.getActualTypeArguments();
		Class<?>[]                   classes = new Class<?>[types_arguments.length];
		for (int i = 0; i < classes.length; i++) {
			classes[i] = (Class<?>) types_arguments[i];
		}
		return classes;
	}
	
	public static void clearWorldBorder(final World world) {
		try {
			/* reset world border */
			world.getWorldBorder().reset();
			
			/* set null world border */
			final Field field = world.getClass().getDeclaredField("worldBorder");
			field.setAccessible(true);
			field.set(world, null);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
	/**
	 * Get entity by his class.
	 * 
	 * @param clazz the class.
	 * @param w the world.
	 * @param id the Unique {@link UUID}
	 * @return return the entity or null if is not found.
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Entity> T getEntityByClass(Class<T> clazz, World w, UUID id) {
		for (Entity ent : w.getEntitiesByClass(clazz)) {
			if (ent == null) {
				continue;
			}

			Class<?> bukkitClass = ent.getClass();
			if (ent.getUniqueId().equals(id)) {
				if (ent.getClass() != null) {
					if (clazz.isAssignableFrom(bukkitClass)) {
						return (T) ent;
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * Make entity invisible for all players.
	 * 
	 * @param ent the entity to make invisible.
	 */
	public static void setInvisible(final Entity ent) {
		final Collection<? extends Player> opls = Bukkit.getOnlinePlayers();
		setInvisibleFor(ent, opls.toArray(new Player[opls.size()]));
	}
	
	/**
	 * Set entity invisible for a some players.
	 * 
	 * @param ent the entity to make invisible.
	 * @param pls the players.
	 */
	public static void setInvisibleFor(final Entity ent, final Player... pls) {
		if (ent == null || pls == null || pls.length <= 0) {
			return;
		}

		try {
			Object packet = ReflectionUtils.getCraftClass("PacketPlayOutEntityDestroy").getConstructor(int[].class)
					.newInstance(new int[] { ent.getEntityId() });
			for (Player p : pls) {
				if (p == null || !p.isOnline()) {
					continue;
				}

				ReflectionUtils.sendPacket(p, packet);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Make entity No AI.
	 * 
	 * @param ent the entity to make No AI.
	 */
	public static void setNoAI(LivingEntity ent, boolean tag) {
		if (!(ent instanceof LivingEntity)) {
			throw new IllegalArgumentException("Invalid LivingEntity, This entity not is a LivingEntity");
		}

		Object localObject1;
		Object localObject2 = null;
		try {
			localObject1 = ent.getClass().getDeclaredMethod("getHandle", new Class[0]).invoke(ent, new Object[0]);
			if (localObject2 == null) {
				localObject2 = getCraftClass("NBTTagCompound").getConstructor(new Class[0]).newInstance(new Object[0]);
			}
			localObject1.getClass().getMethod("c", new Class[] { localObject2.getClass() }).invoke(localObject1,
					new Object[] { localObject2 });
			localObject2.getClass().getMethod("setInt", new Class[] { String.class, Integer.TYPE }).invoke(localObject2,
					new Object[] { "NoAI", Integer.valueOf(tag ? 1 : 0) });
			localObject1.getClass().getMethod("f", new Class[] { localObject2.getClass() }).invoke(localObject1,
					new Object[] { localObject2 });
		} catch (IllegalAccessException ParamException) {
			ParamException.printStackTrace();
		} catch (IllegalArgumentException ParamException) {
			ParamException.printStackTrace();
		} catch (InvocationTargetException ParamException) {
			ParamException.printStackTrace();
		} catch (NoSuchMethodException ParamException) {
			ParamException.printStackTrace();
		} catch (SecurityException ParamException) {
			ParamException.printStackTrace();
		} catch (InstantiationException ParamException) {
			ParamException.printStackTrace();
		}
	}
	
	/**
	 * Class reflection utils.
	 */
	// CRAFT:
	public static final String CRAFT_PREFIX = "org.bukkit.craftbukkit.";
	public static Class<?> CRAFT_ENTITY;      // 'CraftEntity' class.
	public static Method CRAFT_ENTITY_HANDLE; // 'getHandle()', 'CraftEntity' class, method.
	// NMS:
	public static final String NMS_PREFIX = "net.minecraft.server.";
	public static Class<?> NMS_ENTITY;          // nms 'Entity' class.
	public static Field NMS_ENTITY_NOCLIP;      // nms 'Entity' class, 'noclip' field.
	public static Method NMS_ENTITY_SILENT;     // nms 'Entity' class, set silent method.
	public static Method NMS_ENTITY_IS_SILENT;  // nms 'Entity' class, is silent method.
	public static Field NMS_ENTITY_INVULNERABLE;     // nms 'Entity' class, invulnerable field.
	public static Method NMS_ENTITY_IS_INVULNERABLE; // nms 'Entity' class, is invulnerable method.
	public static Class<?> NMS_DAMAGE_SOURCE;
	public static Class<?> NMS_ARMORSTAND;      // nms 'EntityAmorStand' class.
	public static Field NMS_ARMORSTAND_FIELD_H; // nms 'EntityAmorStand' class, 'h' field.
	// SPIGOT VERSION
	public static String VERSION;
	
	/**
	 * Check reflection is initialized.
	 */
	public static void checkIsInitializeReflection() {
		// check is not already initialized.
		if (NMS_ARMORSTAND != null) {
			return;
		}

		try {
			// get version.
			VERSION = ServerVersion.getVersion().toString();

			// CRAFT:
			// get 'CraftEntity' class.
			CRAFT_ENTITY = Class.forName(CRAFT_PREFIX + VERSION + ".entity.CraftEntity");

			// get 'getHandle' Method getter.
			CRAFT_ENTITY_HANDLE = CRAFT_ENTITY.getMethod("getHandle");
			
			// NMS: 
			// get 'Entity' class.
			NMS_ENTITY = Class.forName(NMS_PREFIX + VERSION + ".Entity");
			
			// get Field 'noclip'
			NMS_ENTITY_NOCLIP = NMS_ENTITY.getField("noclip");
			
			// get methods 'NMS_ENTITY_SILENT' and 'NMS_ENTITY_IS_SILENT'.
			{
				// get method dependig of spigot version.
				switch (VERSION) {
				case "v1_8_R1":
				case "v1_8_R2":
				case "v1_8_R3":
					// get a method called 'b'
					NMS_ENTITY_SILENT = NMS_ENTITY.getMethod("b", boolean.class);
					
					// get a method called 'R'
					NMS_ENTITY_IS_SILENT = NMS_ENTITY.getMethod("R");
					break;
					
				case "v1_9_R1":
				case "v1_9_R2":
					// get a method called 'c'
					NMS_ENTITY_SILENT = NMS_ENTITY.getMethod("b", boolean.class);
					
					// get a method called 'ad'
					NMS_ENTITY_IS_SILENT = NMS_ENTITY.getMethod("ad");
					break;
					
				default:
					// set null.
					NMS_ENTITY_SILENT    = null;
					NMS_ENTITY_IS_SILENT = null;
					break;
				}
			}
			
			// get 'EntityArmorStand' class.
			NMS_ARMORSTAND = Class.forName(NMS_PREFIX + VERSION + ".EntityArmorStand");

			// get Field 'h' in the 'EntityArmorStand' class.
			{
				// get field name.
				String h_field_name = "h";

				// get field name depeding of spigot version.
				switch (VERSION) {
				case "v1_9_R1":
					h_field_name = "by";
					break;

				case "v1_9_R2":
					h_field_name = "bz";
					break;

				case "v1_10_R1":
					h_field_name = "bA";
					break;

				case "v1_11_R1":
					h_field_name = "bz";
					break;

				case "v1_12_R1":
					h_field_name = "bA";
					break;
					
				case "v1_13_R1":
				case "v1_13_R2":
					h_field_name = "bG";
					break;
					
				case "v1_14_R1":
					h_field_name = "bD";
					break;
				}

				// get field.
				NMS_ARMORSTAND_FIELD_H = NMS_ARMORSTAND.getDeclaredField(h_field_name);
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
		
		/* initialize invulnerable field and method */
		try {
			ReflectionUtils.NMS_DAMAGE_SOURCE          = ReflectionUtils.getCraftClass("DamageSource");
			ReflectionUtils.NMS_ENTITY_INVULNERABLE    = NMS_ENTITY.getDeclaredField("invulnerable");
			ReflectionUtils.NMS_ENTITY_IS_INVULNERABLE = NMS_ENTITY.getMethod("isInvulnerable", NMS_DAMAGE_SOURCE);
		} catch(Throwable t) {
			t.printStackTrace();
		}
	}
	
	/**
	 * Returns the handle
	 * of some craft objects.
	 * <p>
	 * @param craft_object the object whose its class has the method 'getHandle'.
	 * @return the handle of the given object
	 */
	public static Object getHandle(Object craft_object) {
		try {
			return craft_object.getClass().getMethod("getHandle").invoke(craft_object);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Set an Entity location using the NMS method.
	 * <p>
	 * @param entity the {@link Entity} to set.
	 * @param location the location.
	 */
	public static void setLocation(final Entity ent, final Location location) {
		setLocation(ent, location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
	}
	
	/**
	 * Set an Entity location using the NMS method.
	 * <p>
	 * @param entity the {@link Entity} to set.
	 * @param x The new x-coordinate.
	 * @param y The new y-coordinate.
	 * @param z The new z-coordinate.
	 * @param yaw The absolute rotation on the x-plane, in degrees.
	 * @param pitch pitch The absolute rotation on the y-plane, in degrees.
	 */
	public static void setLocation(final Entity ent, double x, double y, double z, float yaw, float pitch) {
		// check reflection.
		checkIsInitializeReflection();
		
		// set location.
		try {
			// get set location method.
			Method NMS_ENTITY_SET_LOCATION = NMS_ENTITY.getMethod("setLocation",
					new Class[] { double.class, double.class, double.class, float.class, float.class });

			// get handle
			Object handle = NMS_ENTITY.cast(CRAFT_ENTITY_HANDLE.invoke(CRAFT_ENTITY.cast(ent)));

			// invoke method.
			NMS_ENTITY_SET_LOCATION.invoke(handle, x, y, z, yaw, pitch);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
	/**
	 * Sets whether the {@link Entity}
	 * should be visible or not.
	 * <p>
	 * @param entity The entity to set.
	 * @param tag - whether the entity is visible or not.
	 */
	public static void setVisible(final Entity entity, boolean tag) {
		// check reflection.
		checkIsInitializeReflection();
		
		// change visibility.
		try {
			// get set invisible method.
			Method NMS_ENTITY_SET_INVISIBLE = NMS_ENTITY.getMethod("setInvisible", boolean.class);

			// get handle
			Object handle = NMS_ENTITY
					.cast(CRAFT_ENTITY_HANDLE.invoke(CRAFT_ENTITY.cast(entity)));

			// invoke method.
			NMS_ENTITY_SET_INVISIBLE.invoke(handle, !tag);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
	/**
	 * Returns whether the {@link Entity}
	 * should be visible or not.
	 * <p>
	 * @param entity The entity to check.
	 * @return - whether the entity is visible or not.
	 */
	public static boolean isVisible(final Entity entity) {
		// check reflection.
		checkIsInitializeReflection();
		
		// get visibility.
		try {
			// get set location method.
			Method NMS_ENTITY_IS_INVISIBLE = NMS_ENTITY.getMethod("isInvisible");

			// get handle
			Object handle = NMS_ENTITY
					.cast(CRAFT_ENTITY_HANDLE.invoke(CRAFT_ENTITY.cast(entity)));

			// invoke method.
			return !((boolean)NMS_ENTITY_IS_INVISIBLE.invoke(handle));
		} catch (Throwable t) {
			t.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Sets whether the {@link Entity} 
	 * is silent or not.
	 * When an entity is silent it 
	 * will not produce any sound.
	 * <p>
	 * @param entity The entity to set.
	 * @param silent if the entity is silent.
	 */
	public static void setSilent(final Entity entity, final boolean silent) {
		// check reflection.
		checkIsInitializeReflection();
		
		// invoke method.
		if (NMS_ENTITY_SILENT == null) { // invoke methdo in normal API.
			// set.
			entity.setSilent(silent);
		} else { // invoke method in the entity handle.
			try {
				// get handle
				Object handle = NMS_ENTITY.cast(CRAFT_ENTITY_HANDLE.invoke(CRAFT_ENTITY.cast(entity)));
				
				// invoke method.
				NMS_ENTITY_SILENT.invoke(handle, silent);
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}
	
	/**
	 * Sets whether the entity is invulnerable or not.
	 * <p>
	 * When an entity is invulnerable it can only be damaged by players in creative
	 * mode.
	 *<p>
	 * @param invulnerable if the entity is invulnerable
	 */
	public static void setInvulnerable(final Entity entity, final boolean invulnerable) {
		checkIsInitializeReflection(); /* initialize reflection */
		try {
			ReflectionUtils.NMS_ENTITY_INVULNERABLE.setAccessible(true);
			ReflectionUtils.NMS_ENTITY_INVULNERABLE
			.set(NMS_ENTITY.cast(CRAFT_ENTITY_HANDLE.invoke(CRAFT_ENTITY.cast(entity))), invulnerable);
		} catch(Throwable t) {
			t.printStackTrace();
		}
	}

	/**
	 * Gets whether the entity is invulnerable or not.
	 * <p>
	 * @return whether the entity is
	 */
	public static boolean isInvulnerable(final Entity entity) {
		checkIsInitializeReflection(); /* initialize reflection */
		try {
			return (boolean) ReflectionUtils.NMS_ENTITY_IS_INVULNERABLE.invoke(
					NMS_ENTITY.cast(CRAFT_ENTITY_HANDLE.invoke(CRAFT_ENTITY.cast(entity))),
					ReflectionUtils.NMS_DAMAGE_SOURCE.getField("GENERIC").get(ReflectionUtils.NMS_DAMAGE_SOURCE));
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Sets whether an entity 
	 * will have AI.
	 * <p>
	 * @param ent the LivingEntity to set.
	 * @param tag whether the entity will have AI or not.
	 */
	public static void setAI(final LivingEntity ent, final boolean tag) {
		// check is a LivingEntity.
		if (!(ent instanceof LivingEntity)) {
			throw new IllegalArgumentException("Invalid LivingEntity, This entity not is a LivingEntity");
		}
		
		// check reflection.
		checkIsInitializeReflection();
		
		// set 'AI' as tag in a new NBTTagCompound.
		try {
			// get handle.
			Object handle = ent.getClass().getDeclaredMethod("getHandle").invoke(ent);
			
			// get NBTTagCompound.
			final Object ntb = Class.forName(NMS_PREFIX + VERSION + ".NBTTagCompound").getConstructor().newInstance();
			
			// get and invoke method 'c' in handle.
			Method c = handle.getClass().getMethod("c", ntb.getClass());
			c.invoke(handle, ntb);
			
			// get and invoke method 'setInt' in NBTTagCompound.
			Method setInt = ntb.getClass().getMethod("setInt", String.class, int.class);
			setInt.invoke(ntb, "NoAI", (tag ? 0 : 1));
			
			// get and invoke method 'f' in handle.
			Method f = handle.getClass().getMethod("f", ntb.getClass());
			f.invoke(handle, ntb);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	/**
	 * Get an Entity location from his 
	 * location in his NMS Class.
	 * <p>
	 * @param entity the {@link Entity} to get.
	 * @return the current Entity location.
	 */
	public static Location getLocation(final Entity entity) {
		// check reflection.
		checkIsInitializeReflection();
		
		try {
			// get methods.
			Field LOC_X     = NMS_ENTITY.getField("locX");
			Field LOC_Y     = NMS_ENTITY.getField("locY");
			Field LOC_Z     = NMS_ENTITY.getField("locZ");
			Field LOC_YAW   = NMS_ENTITY.getField("yaw");
			Field LOC_PITCH = NMS_ENTITY.getField("pitch");

			// get handle
			Object handle = NMS_ENTITY
					.cast(CRAFT_ENTITY_HANDLE.invoke(CRAFT_ENTITY.cast(entity)));

			// return location.
			return new Location(entity.getWorld(), LOC_X.getDouble(handle), LOC_Y.getDouble(handle),
					LOC_Z.getDouble(handle), LOC_YAW.getFloat(handle), LOC_PITCH.getFloat(handle));
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return null;
	}

	/**
	 * Set armor stand damagable 
	 * and clickable when its
	 * invisible.
	 * <p>
	 * @param armorStand the {@link ArmorStand} 
	 * to set damagable when is invisible.
	 */
	public static void h(final ArmorStand armorStand) {
		// check reflection.
		checkIsInitializeReflection();
		
		// change the Field 'h' value.
		try {
			// get handle
			Object handle = NMS_ARMORSTAND
					.cast(CRAFT_ENTITY_HANDLE.invoke(CRAFT_ENTITY.cast(armorStand)));

			// set.
			NMS_ARMORSTAND_FIELD_H.setAccessible(true);
			NMS_ARMORSTAND_FIELD_H.setBoolean(handle, false);
			NMS_ARMORSTAND_FIELD_H.setAccessible(false);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
	/**
	 * Gets whether the {@link Entity} 
	 * is silent or not.
	 * <p>
	 * @param entity The Entity to check.
	 * @return whether the entity is silent.
	 */
	public static boolean isSilent(final Entity entity) {
		// check reflection.
		checkIsInitializeReflection();
		
		// get boolean invoking method.
		if (NMS_ENTITY_IS_SILENT == null) { // invoke methdo in normal API.
			return entity.isSilent();
		} else { // invoke method in the entity handle.
			try {
				// get handle
				Object handle = NMS_ENTITY
						.cast(CRAFT_ENTITY_HANDLE.invoke(CRAFT_ENTITY.cast(entity)));
				
				// invoke method.
				return (boolean) NMS_ENTITY_IS_SILENT.invoke(handle);
			} catch(Throwable t) {
				t.printStackTrace();
				return false;
			}
		}
	}
	
	public static void playNamedSound(final Player player, final String sound, final float volume, final float pitch) {
		try {
			// check giving data.
			if (player == null || !player.isOnline() || sound == null || sound.isEmpty()) {
				return;
			}
			
			// get location data.
			final Location loc = player.getEyeLocation();
//			final World  w     = loc.getWorld();
			final double x     = loc.getX();
			final double y     = loc.getY();
			final double z     = loc.getZ();
			
			// get sound category.
			final Class<?> sound_category_enum = getCraftClass("SoundCategory");
			final Object object = sound_category_enum.getMethod("valueOf", String.class).invoke(sound_category_enum,
					"MASTER");
			
			// get packet constructor.
			final Constructor<?> packet_constructor = ReflectionUtils.getCraftClass("PacketPlayOutCustomSoundEffect")
					.getConstructor(String.class, sound_category_enum, double.class, double.class, double.class, float.class, float.class);
			
			// make packet.
			final Object packet = packet_constructor.newInstance(sound, object, x, y, z, volume, pitch);
			
			// send packet.
			sendPacket(player, packet);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
	/**
	 * Play a 3D named sound.
	 * <p>
	 * @param location the location.
	 * @param sound the sound name to play.
	 * @param volume the volume.
	 * @param pitch the pitch.
	 */
	public static void play3DNamedSound(final Location location, final String sound, final float volume, final float pitch) {
		// play 3D sound.
		try {
			// check location and sound.
			if (location == null || sound == null) {
				return;
			}

			// get location data.
			final World  w = location.getWorld();
			final double x = location.getX();
			final double y = location.getY();
			final double z = location.getZ();

			// get world handle.
			final Field wf = w.getClass().getDeclaredField("world");
			wf.setAccessible(true);
			final Object world_server = wf.get(w);
			
			// get sound category.
			final Class<?> sound_category_enum = getCraftClass("SoundCategory");
			final Object object = sound_category_enum.getMethod("valueOf", String.class).invoke(sound_category_enum, "MASTER");
			
			// get packet constructor.
			final Constructor<?> packet_constructor = ReflectionUtils.getCraftClass("PacketPlayOutCustomSoundEffect")
					.getConstructor(String.class, sound_category_enum, double.class, double.class, double.class, float.class, float.class);

			// make packet.
			final Object packet = packet_constructor.newInstance(sound, object, x, y, z, volume, pitch);

			// get minecraft server.
			final Method mmc_server = world_server.getClass().getDeclaredMethod("getMinecraftServer");
			final Object mc_server  = mmc_server.invoke(world_server);

			// get minecraft server player list.
			final Method mp_list = mc_server.getClass().getMethod("getPlayerList");
			final Object pl_list = mp_list.invoke(mc_server);

			// get EntityHuman class.
			final Class<?> entity_human = getCraftClass("EntityHuman");
			
			// get packet class.
			final Class<?> packet_class = getCraftClass("Packet");

			// get dimension.
			final int dimension = world_server.getClass().getField("dimension").getInt(world_server);

			// get and invoke, send packet to nearby players method.
			final Method msend_packet = pl_list.getClass().getMethod("sendPacketNearby", entity_human, double.class, double.class,
					double.class, double.class, int.class, packet_class);
			
			msend_packet.invoke(pl_list, null, x, y, z, (volume > 1.0F ? 16.0F * volume : 16.0D), dimension, packet);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
}