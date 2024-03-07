package es.outlook.adriansrj.core.util.permission;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;

public class Permissions {

	private static final Map<String, Permission> PERMISSIONS = new HashMap<>();
	
	public static Permission of(String name) {
		name = name.toLowerCase(Locale.ENGLISH);
		if (!Permissions.PERMISSIONS.containsKey(name)) {
			Permissions.PERMISSIONS.put(name, new Permission(name));
		}
		return Permissions.PERMISSIONS.get(name);
	}
	
	public static void register(Permission permission) {
		if (!Permissions.isAlreadyDefined(permission)) {
			Bukkit.getPluginManager().addPermission(permission);
			permission.recalculatePermissibles();
		}
	}
	
	public static boolean isAlreadyDefined(String permission) {
		return Bukkit.getPluginManager().getPermission(permission) != null;
	}
	
	public static boolean isAlreadyDefined(Permission permission) {
		return Permissions.isAlreadyDefined(permission.getName());
	}
}