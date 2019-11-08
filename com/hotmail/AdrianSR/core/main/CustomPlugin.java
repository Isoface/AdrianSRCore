package com.hotmail.AdrianSR.core.main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarFile;
import java.util.logging.Level;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.hotmail.AdrianSR.core.listener.CustomPluginListener;
import com.hotmail.AdrianSR.core.logger.combo.ComboLogger;
import com.hotmail.AdrianSR.core.logger.file.FileLogger;
import com.hotmail.AdrianSR.core.util.PrintUtils;
import com.hotmail.AdrianSR.core.util.TextUtils;
import com.hotmail.AdrianSR.core.util.classes.ReflectionUtils;
import com.hotmail.AdrianSR.core.util.dependence.PluginDependence;
import com.hotmail.AdrianSR.core.util.lang.CustomPluginLanguageEnumContainer;
import com.hotmail.AdrianSR.core.util.lang.loader.CustomPluginLanguageLoader;
import com.hotmail.AdrianSR.core.version.CoreVersion;

/**
 * Represents a CustomPlugin from a JavaPlugin.
 */
public abstract class CustomPlugin extends JavaPlugin {
	
	private static final Map<Class<? extends CustomPlugin>, CustomPlugin> PLUGIN_INSTANCES0 = new HashMap<>();
	
    /**
     * Returns the {@link CustomPlugin} that was initialized
     * with the given class.
     * <p>
     * @param <T> the class type of the custom plugin to get.
     * @param plugin_class the class that extends {@link CustomPlugin}.
     * @return the plugin or null if not registered.
     */
	public static <T extends CustomPlugin> T getCustomPlugin(Class<T> plugin_class) {
		return PLUGIN_INSTANCES0.containsKey(plugin_class) ? plugin_class.cast(PLUGIN_INSTANCES0.get(plugin_class)) : null;
	}
	
	/**
	 * Plugin file logger.
	 */
	private FileLogger   file_logger = null;
	private ComboLogger combo_logger = null;
	private boolean        info_done = true;
	private boolean      initialized = false;
	
	/**
	 * On plugin enable.
	 */
	@Override
	public final void onEnable() {
		final long begin = System.currentTimeMillis(); /* begin time mills */
		
		/* checking required core version */
		if (getRequiredCoreVersion() != null && CoreVersion.getVersion().isOlderThan(getRequiredCoreVersion())) {
			PrintUtils.print(ChatColor.RED, TextUtils.getNotNull(getRequiredCoreVersionMessage(), 
					"getRequiredCoreVersionMessage() == null!"), this);
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		
		/* checking dependencies */
		if (getDependencies() != null) {
			boolean disable = false;
			for (PluginDependence depended : getDependencies()) {
				if (!Bukkit.getPluginManager().isPluginEnabled(depended.getDependedPlugin())) {
					PrintUtils.print(ChatColor.RED, depended.getNotFoundErrorMessage(), this);
					disable = true;
					break;
				}
				
				Plugin dependence = Bukkit.getPluginManager().getPlugin(depended.getDependedPlugin());
				if (StringUtils.isBlank(depended.getRequiredMinimumVersion())) {
					continue;
				}
				
				try {
					String  current = dependence.getDescription().getVersion();
					String required = depended.getRequiredMinimumVersion();
					if (Integer.parseInt(current.replace(".", "").trim()) < Integer.parseInt(required.replace(".", "").trim())) {
						PrintUtils.print(ChatColor.RED, TextUtils.getNotNull(
								depended.getRequiredMinimumVersionErrorMessage(),
								String.format("The version '%s' of '%s' is obsolote!", current, dependence.getName())),
								this);
						disable = true;
					}
				} catch(Throwable t) {
					PrintUtils.print(ChatColor.RED,
							String.format(
									"Couldn't check the minimum required version of the depended plugin '%s'!",
									dependence.getName()),
							this);
					disable = true; break;
				}
			}
			
			if (disable) {
				Bukkit.getPluginManager().disablePlugin(this); return;
			}
		}
		
		/* load language container */
		if (getLanguageContainer() != null) {
			if (getLanguageResourcesPackage() == null) {
				throw new IllegalArgumentException("getLanguageResourcesPackage() == null!");
			}
			
			try {
				if (!new CustomPluginLanguageLoader(this, new JarFile(getFile())).load()) {
					PrintUtils.print(ChatColor.RED, "The internal language of the plugin couldn't be loaded!", this);
					Bukkit.getPluginManager().disablePlugin(this);
					return;
				}
			} catch (IOException e) {
				PrintUtils.print(ChatColor.RED, "The internal language of the plugin couldn't be loaded: ", this);
				e.printStackTrace();
				Bukkit.getPluginManager().disablePlugin(this);
				return;
			}
		}
		
		/* register */
		PLUGIN_INSTANCES0.put(getClass(), this);
		
		/* if failed initialization */
		if (!setUp() || !isEnabled()) {
			PLUGIN_INSTANCES0.remove(getClass()); // unregister
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		
		/* initializing */
		initConfig();
		initManagers();
		initCommands();
		initListeners();
		this.initialized = true;
		
		/* time enabling token by the plugin */
		if (isInfoDoneEnabled()) {
			getFileLogger().log("Done (" + TimeUnit.MILLISECONDS.toSeconds( System.currentTimeMillis() - begin ) + " seconds)!");
		}
	}
	
	/**
	 * On plugin enable.
	 * <p>
	 * @return true if was enabled successfully.
	 */
	public abstract boolean setUp();
	
	/**
	 * Returns the minimum version of the AdrianSRCore required by this
	 * {@link CustomPlugin}, or null if no minimum version is required.
	 * <p>
	 * @return required minimum AdrianSRCore version, or null if no minimum
	 *         version is required.
	 */
	public abstract CoreVersion getRequiredCoreVersion();
	
	/**
	 * Returns the error message to print if the version of the AdrianSRCore is
	 * older than the required version, or null if no minimum version is required.
	 * <p>
	 * @return required minimum version of AdrianSRCore error message, or null if no
	 *         minimum version is required.
	 */
	public abstract String getRequiredCoreVersionMessage();
	
	/**
	 * Returns the dependencies of this {@link CustomPlugin}, or null if it has no
	 * dependencies.
	 * <p>
	 * @return the dependencies of this {@link CustomPlugin}, or null if it has no
	 *         dependencies.
	 */
	public abstract PluginDependence[] getDependencies();
	
	/**
	 * Returns the enum language container of this {@link CustomPlugin}, or null if it not required.
	 * <p>
	 * The items of the given container will be loaded from the .lang resources
	 * that should be found at the given resources package. {@link #getLanguageResourcesPackage()}.
	 * <p>
	 * @return the language container of this {@link CustomPlugin}, or null if it not required.
	 */
	public abstract Class<? extends Enum<? extends CustomPluginLanguageEnumContainer>> getLanguageContainer();
	
	/**
	 * Returns the package where the .lang resources of this {@link CustomPlugin}
	 * are stored, or null if not required.
	 * <p>
	 * @return the package where the .lang resources of this {@link CustomPlugin}
	 *         are stored, or null if not required.
	 */
	public abstract String getLanguageResourcesPackage();
	
	/**
	 * Get the commands in the plugin.yml file.
	 *
	 * @return a plugin.yml command list.
	 */
	public final List<String> descriptionCommands() {
		return getDescription().getCommands() != null ? new ArrayList<String>(getDescription().getCommands().keySet())
				: new ArrayList<String>();
	}

	/**
	 * Check/Load Config.
	 */
	public void initConfig() {
		// nothing by default.
	}

	/**
	 * Initialize Manager Classes.
	 */
	public void initManagers() {
		// nothing by default.
	}

	/**
	 * Set Command Executors.
	 */
	public void initCommands() {
		// nothing by default.
	}

	/**
	 * Register Listener Classes.
	 */
	public void initListeners() {
		// nothing by default.
	}
	
	/**
	 * Returns true if this custom plugin has
	 * already been initialized.
	 * <p>
	 * @return true if initialized.
	 */
	public boolean isAlreadyInitialized() {
		return this.initialized;
	}
	
	/**
	 * Initialize all the listener class
	 * that extends the class {@link CustomPluginListener}
	 * and are within the given package.
	 * <p>
	 * @apiNote Example of use: By giving the package 'a.b.c.listeners', will the following classes be initialized?
	 * 			- a.b.c.listeners.ListenerA.class           = true
	 * 			- a.b.c.listeners.ListenerB.class           = true
	 * 			- a.b.c.listeners.apple.AppleListener.class = true
	 * 			- a.b.c.OtherListener.class                 = false
	 * <p>
	 * @param listeners_package the name of the listeners package.
	 * @param ignore_exceptions ignore any exception when initializing any listener.
	 */
	protected void initListenersPackage(String listeners_package, boolean ignore_exceptions) {
		/* register classes within the listeners package */
		for (Class<?> clazz : ReflectionUtils.getClasses(getFile(), listeners_package)) {
			if (Modifier.isAbstract( clazz.getModifiers() )) {
				continue;
			}
			
			/* check superclass of the listener class */
			if (!CustomPluginListener.class.isAssignableFrom(clazz)) {
				continue;
			}
			
			try {
				for (Method method : clazz.getMethods()) { /* check the class has at least one event handler */
					if (method.isAnnotationPresent(EventHandler.class)) {
						Constructor<?> constructor = clazz.getConstructor(getClass());
						if (constructor != null) {
							constructor.newInstance(this); /* make instance */
						}
						break;
					}
				}
			} catch (Throwable t) {
				if (!ignore_exceptions) {
					PrintUtils.print(ChatColor.RED, "The listener '" + clazz.getName() + "' couldn't be initialized: " , this);
					t.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Initialize all the listener class
	 * that extends the class {@link CustomPluginListener}
	 * and are within the given package.
	 * <p>
	 * @apiNote Example of use: By giving the package 'a.b.c.listeners', will the following classes be initialized?
	 * 			- a.b.c.listeners.ListenerA.class           = true
	 * 			- a.b.c.listeners.ListenerB.class           = true
	 * 			- a.b.c.listeners.apple.AppleListener.class = true
	 * 			- a.b.c.OtherListener.class                 = false
	 * <p>
	 * @param listeners_package the name of the listeners package.
	 */
	protected void initListenersPackage(String listeners_package) {
		this.initListenersPackage(listeners_package, false);
	}
	
	/**
	 * Returns the plugin file
	 * logger.
	 * <p>
	 * @return hidden logger.
	 */
	public final FileLogger getFileLogger() {
		return ( file_logger = ( file_logger == null ? new FileLogger(this) : file_logger ) );
	}
	
	/**
	 * Returns the plugin
	 * combo logger.
	 * <p>
	 * @return combo logger.
	 */
	public final ComboLogger getComboLogger() {
		return ( combo_logger = ( combo_logger == null ? new ComboLogger(this) : combo_logger ));
	}
	
	/**
	 * @return true if the done info message
	 * is enabled.
	 */
	public boolean isInfoDoneEnabled() {
		return info_done;
	}
	
	/**
	 * Enable/Disable the done info
	 * after loading this.
	 * <p>
	 * @param enabled enable?
	 */
	public void setInfoDone(boolean enabled) {
		this.info_done = enabled;
	}
	
	public void saveResource(String resource, File out_directory, boolean replace) {
		if (StringUtils.isBlank(resource)) {
			throw new IllegalArgumentException("resource cannot be null or empty");
		}

		resource = resource.replace('\\', '/');
		if (out_directory == null) {
			out_directory = getDataFolder();
		}

		if (!out_directory.exists() || !out_directory.isDirectory()) {
			out_directory.mkdir();
		}

		InputStream in = getResource(resource);
		if (in == null) {
			throw new IllegalArgumentException(
					"The embedded resource '" + resource + "' cannot be found in " + getFile());
		}

		File  out_file = new File(out_directory, resource);
		int last_index = resource.lastIndexOf('/');
		File   out_dir = new File(out_directory, resource.substring(0, last_index >= 0 ? last_index : 0));

		if (!out_dir.exists()) {
			out_dir.mkdirs();
		}

		try {
			if (!out_file.exists() || replace) {
				OutputStream out = new FileOutputStream(out_file);
				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}

				out.close();
				in.close();
			}
		} catch (IOException ex) {
			getLogger().log(Level.SEVERE, "Could not save " + out_file.getName() + " to " + out_file, ex);
		}
	}

	// public boolean saveResource(String resourcePath, boolean replace, boolean
	// check_yml_entries) throws IOException {
	// // get out resource path file.
	// final File out_file = new File(getDataFolder(), resourcePath);
	//
	// // check is not already exists and save.
	// if (!out_file.exists()) {
	// saveResource(resourcePath, replace);
	// }
	//
	// if (check_yml_entries) {
	// // check is yml.
	// if (!getFileExtension(out_file).toLowerCase().equalsIgnoreCase("yml")) {
	// return false;
	// }
	//
	// // get out file yml configuration.
	// final UTF8YamlConfiguration out_yml =
	// UTF8YamlConfiguration.loadConfiguration(out_file);
	//
	// // get resource yml configuration.
	// final InputStream resource = getResource(resourcePath);
	// final UTF8YamlConfiguration yml =
	// UTF8YamlConfiguration.loadConfiguration(resource);
	//
	// // check out file entries.
	// for (String key : yml.getKeys(true)) {
	// if (!out_yml.isSet(key)) {
	//
	// }
	// }
	//// final BufferedReader reader = new BufferedReader(new
	// InputStreamReader(resource));
	////
	//// // make temp file.
	//// final File temp_file =
	// File.createTempFile(FilenameUtils.removeExtension(out_file.getName()),
	// ".yml");
	//// final List<String> lines = new ArrayList<String>();
	//// String line;
	//// while ((line = reader.readLine()) != null) {
	//// lines.add(line);
	//// }
	//// Files.write(Paths.get(temp_file.getPath()), lines,
	// Charset.forName("UTF-8"));
	//// resource.close();
	//// reader.close();
	//
	// // get yaml configuration.
	//
	//// tempFile.deleteOnExit();
	//
	// // get file from resource imput stream.
	// // getResource(resourcePath)
	//
	// // check entries.
	// }
	// return true;
	// }

//	private static String getFileExtension(final File file) {
//		final String fileName = file.getName();
//		if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
//			return fileName.substring(fileName.lastIndexOf(".") + 1);
//		}
//		return null;
//	}

	public File getFileInDataFolder(final String file_name) {
		return new File(getDataFolder(), file_name);
	}
}