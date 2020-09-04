package com.hotmail.adriansr.core.util.reflection;

import com.hotmail.adriansr.core.util.reflection.bukkit.BukkitReflection;
import com.hotmail.adriansr.core.util.reflection.bukkit.EntityReflection;
import com.hotmail.adriansr.core.util.reflection.general.ClassReflection;
import com.hotmail.adriansr.core.util.reflection.general.ConstructorReflection;
import com.hotmail.adriansr.core.util.reflection.general.EnumReflection;
import com.hotmail.adriansr.core.util.reflection.general.FieldReflection;
import com.hotmail.adriansr.core.util.reflection.general.MethodReflection;

/**
 * Useful class for dealing with reflection in general.
 * <p>
 * This only contains short-cuts to the different classes for dealing with the
 * different reflections.
 * <p>
 * @author AdrianSR / Wednesday 15 April, 2020 / 08:55 AM
 */
public class ReflectionUtil {
	
	/** Classes reflection. */
	public static final class Classes extends ClassReflection {}
	/** Constructors reflection. */
	public static final class Constructors extends ConstructorReflection {}
	/** Fields reflection. */
	public static final class Fields extends FieldReflection {}
	/** Methods reflection. */
	public static final class Methods extends MethodReflection {}
	/** Enumerations reflection. */
	public static final class Enumerations extends EnumReflection {}
	/** Bukkit reflection. */
	public static final class Bukkit extends BukkitReflection {}
	/** Entities reflection. */
	public static final class Entities extends EntityReflection {}
}