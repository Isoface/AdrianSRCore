package es.outlook.adriansrj.core.util.reflection;

import es.outlook.adriansrj.core.util.reflection.bukkit.BukkitReflection;
import es.outlook.adriansrj.core.util.reflection.bukkit.EntityReflection;
import es.outlook.adriansrj.core.util.reflection.bukkit.PacketReflection;
import es.outlook.adriansrj.core.util.reflection.general.ClassReflection;
import es.outlook.adriansrj.core.util.reflection.general.ConstructorReflection;
import es.outlook.adriansrj.core.util.reflection.general.EnumReflection;
import es.outlook.adriansrj.core.util.reflection.general.FieldReflection;
import es.outlook.adriansrj.core.util.reflection.general.MethodReflection;

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

	/** Packet reflection. */
	public static final class Packets extends PacketReflection {}

	/** Entities reflection. */
	public static final class Entities extends EntityReflection {}
}