package com.hotmail.AdrianSR.core.util.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileDeleteStrategy;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.util.FileUtil;

/**
 * Represents a File Utils class.
 * 
 * @author AdrianSR
 */
public final class FileUtils {
	
	/**
	 * Global Class values.
	 */
	private static final Logger log = Logger.getLogger(FileUtil.class.getName());
    private static final Collection<String> allwaysOverwrite = new ArrayList<>();
    private static final Collection<String> neverOverwrite = new ArrayList<>();
    private static final Map<String, YmlConfiguration> configFiles = new ConcurrentHashMap<>();
    private static Locale locale = Locale.getDefault();
    private static File dataFolder;
    
    public static Set<File> getElements(File directory) {
        final Set<File> all_elements = new HashSet<>();
        if (!directory.isDirectory()) {
            return all_elements;
        }

        for (File element : directory.listFiles()) {
            if (element.isDirectory()) {
                getElements(element).forEach(other -> all_elements.add(other));
            } else {
                all_elements.add(element);
            }
        }
        return all_elements;
    }
    
    public static long calculateLength(File directory) {
    	if (!directory.isDirectory()) {
    		return directory.length();
    	}
    	
    	long length = 0L;
    	for (File element : getElements(directory)) {
    		length += element.length();
    	}
		return length;
    }
    
    public static boolean isJar(File file) {
    	return getExtension(file.getName()).equalsIgnoreCase("jar");
    }
    
    public static boolean isZip(File file) {
    	return getExtension(file.getName()).equalsIgnoreCase("zip");
    }
    
    public static boolean isRar(File file) {
    	return getExtension(file.getName()).equalsIgnoreCase("rar");
    }
    
    public static boolean isYml(File file) {
    	return getExtension(file.getName()).equalsIgnoreCase("yml");
    }

    public static void setAllwaysOverwrite(String... configs) {
        for (String s : configs) {
            if (!allwaysOverwrite.contains(s)) {
                allwaysOverwrite.add(s);
            }
        }
    }

    public static void setNeverOverwrite(String... configs) {
        for (String s : configs) {
            if (!neverOverwrite.contains(s)) {
                neverOverwrite.add(s);
            }
        }
    }

    public static YmlConfiguration loadConfig(File file) {
        YmlConfiguration config = new YmlConfiguration();
        readConfig(config, file);
        return config;
    }

    public static void readConfig(FileConfiguration config, File file) {
        if (file == null) {
            log.log(Level.INFO, "No "  + file + " found, it will be created");
            return;
        }
        File configFile = file;
        File localeFile = new File(configFile.getParentFile(), getLocaleName(file.getName()));
        if (localeFile.exists() && localeFile.canRead()) {
            configFile = localeFile;
        }
        if (!configFile.exists()) {
            log.log(Level.INFO, "No "  + configFile + " found, it will be created");
            return;
        }
        try (Reader rdr = new InputStreamReader(new FileInputStream(configFile), "UTF-8")) {
            config.load(rdr);
        } catch (InvalidConfigurationException e) {
            log.log(Level.SEVERE, "Unable to read config file " + configFile, e);
            if (configFile.exists()) {
                try {
                    Files.copy(Paths.get(configFile.toURI()), Paths.get(configFile.getParent(), configFile.getName() + ".err"), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e1) {
                    // Ignore - we tried...
                }
            }
        } catch (IOException e) {
            log.log(Level.SEVERE, "Unable to read config file " + configFile, e);
        }
    }

    public static void readConfig(FileConfiguration config, InputStream inputStream) {
        if (inputStream == null) {
            return;
        }
        try (Reader rdr = new InputStreamReader(inputStream, "UTF-8")) {
            config.load(rdr);
        } catch (InvalidConfigurationException | IOException e) {
            log.log(Level.SEVERE, "Unable to read configuration", e);
        }
    }

    public static FilenameFilter createYmlFilenameFilter() {
        return new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name != null && name.endsWith(".yml");
            }
        };
    }

    public static String getBasename(File file) {
        return getBasename(file.getName());
    }

    public static String getBasename(String file) {
        if (file != null && file.lastIndexOf('.') != -1) {
            return file.substring(0, file.lastIndexOf('.'));
        }
        return file;
    }
    
    public static String getExtension(File file) {
        if (file.isDirectory()) {
        	return null;
        }
        return getExtension(file);
    }

    public static String getExtension(String fileName) {
        if (fileName != null && !fileName.isEmpty()) {
            return fileName.substring(getBasename(fileName).length() + 1);
        }
        return "";
    }

    private static File getDataFolder() {
        return dataFolder != null ? dataFolder : new File(".");
    }

    /**
     * System-encoding agnostic config-reader
     * Reads and returns the configuration found in:
     * <pre>
     *   a) the datafolder
     *
     *     a.1) if a config named "config_en.yml" exists - that is read.
     *
     *     a.2) otherwise "config.yml" is read (created if need be).
     *
     *   b) if the version differs from the same resource on the classpath
     *
     *      b.1) all nodes in the jar-file-version is merged* into the local-file
     *
     *      b.2) unless the configName is in the allwaysOverwrite - then the jar-version wins
     *
     * *merged: using data-conversion of special nodes.
     * </pre>
     */
    public static YmlConfiguration getYmlConfiguration(String configName) {
        // Caching, for your convenience! (and a bigger memory print!)

        if (!configFiles.containsKey(configName)) {
            YmlConfiguration config = new YmlConfiguration();
            try {
                // read from datafolder!
                File configFile = getConfigFile(configName);
                YmlConfiguration configJar = new YmlConfiguration();
                readConfig(config, configFile);
                readConfig(configJar, getResource(configName));
                if (!configFile.exists() || config.getInt("version", 0) < configJar.getInt("version", 0)) {
                    if (configFile.exists()) {
                        if (neverOverwrite.contains(configName)) {
                            configFiles.put(configName, config);
                            return config;
                        }
                        File backupFolder = new File(getDataFolder(), "backup");
                        backupFolder.mkdirs();
                        String bakFile = String.format("%1$s-%2$tY%2$tm%2$td-%2$tH%2$tM.yml", getBasename(configName), new Date());
                        log.log(Level.INFO, "Moving existing config " + configName + " to backup/" + bakFile);
                        Files.move(Paths.get(configFile.toURI()),
                                Paths.get(new File(backupFolder, bakFile).toURI()),
                                StandardCopyOption.REPLACE_EXISTING);
                        if (allwaysOverwrite.contains(configName)) {
                            FileUtils.copy(getResource(configName), configFile);
                            config = configJar;
                        } else {
                            config = mergeConfig(configJar, config);
                            config.save(configFile);
                            config.load(configFile);
                        }
                    } else {
                        config = mergeConfig(configJar, config);
                        config.save(configFile);
                        config.load(configFile);
                    }
                }
            } catch (Exception e) {
                log.log(Level.SEVERE, "Unable to handle config-file " + configName, e);
            }
            configFiles.put(configName, config);
        }
        return configFiles.get(configName);
    }

    private static InputStream getResource(String configName) {
        String resourceName = getLocaleName(configName);
        ClassLoader loader = FileUtil.class.getClassLoader();
        InputStream resourceAsStream = loader.getResourceAsStream(resourceName);
        if (resourceAsStream != null) {
            return resourceAsStream;
        }
        return loader.getResourceAsStream(configName);
    }

    private static String getLocaleName(String fileName) {
        String baseName = getBasename(fileName);
        return baseName + "_" + locale + fileName.substring(baseName.length());
    }

    public static File getConfigFile(String configName) {
        File file = new File(getDataFolder(), getLocaleName(configName));
        if (file.exists()) {
            return file;
        }
        return new File(getDataFolder(), configName);
    }

    public static void copy(InputStream stream, File file) throws IOException {
        if (stream == null || file == null) {
            throw new IOException("Invalid resource for " + file);
        }
        Files.copy(stream, Paths.get(file.toURI()), StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * Merges the important keys from src to destination.
     * @param src The source (containing the new values).
     * @param dest The destination (containing old-values).
     */
    private static YmlConfiguration mergeConfig(YmlConfiguration src, YmlConfiguration dest) {
        int existing = dest.getInt("version");
        int version = src.getInt("version", existing);
        dest.setDefaults(src);
        dest.options().copyDefaults(true);
        dest.addComments(src.getComments());
        dest.set("version", version);
        dest.options().copyHeader(false);
        src.options().copyHeader(false);
        removeExcludes(dest);
        moveNodes(src, dest);
        replaceDefaults(src, dest);
        return dest;
    }

    /**
     * Removes nodes from dest.defaults, that are specifically excluded in the config
     */
    private static void removeExcludes(YmlConfiguration dest) {
        List<String> keys = dest.getStringList("merge-ignore");
        for (String key : keys) {
            dest.getDefaults().set(key, null);
        }
    }

    private static void replaceDefaults(YmlConfiguration src, YmlConfiguration dest) {
        ConfigurationSection forceSection = src.getConfigurationSection("force-replace");
        if (forceSection != null) {
            for (String key : forceSection.getKeys(true)) {
                Object def = forceSection.get(key, null);
                Object value = dest.get(key, def);
                Object newDef = src.get(key, null);
                if (def != null && def.equals(value)) {
                    dest.set(key, newDef);
                }
            }
        }
        dest.set("force-replace", null);
        dest.getDefaults().set("force-replace", null);
    }

    private static void moveNodes(YmlConfiguration src, YmlConfiguration dest) {
        ConfigurationSection moveSection = src.getConfigurationSection("move-nodes");
        if (moveSection != null) {
            List<String> keys = new ArrayList<>(moveSection.getKeys(true));
            Collections.reverse(keys); // Depth first
            for (String key : keys) {
                if (moveSection.isString(key)) {
                    String srcPath = key;
                    String tgtPath = moveSection.getString(key, key);
                    Object value = dest.get(srcPath);
                    if (value != null) {
                        dest.set(tgtPath, value);
                        dest.set(srcPath, null);
                    }
                } else if (moveSection.isConfigurationSection(key)) {
                    // Check to see if dest section should be nuked...
                    if (dest.isConfigurationSection(key) && dest.getConfigurationSection(key).getKeys(false).isEmpty()) {
                        dest.set(key, null);
                    }
                }
            }
        }
        dest.set("move-nodes", null);
        dest.getDefaults().set("move-nodes", null);
    }

    public static void setDataFolder(File dataFolder) {
        FileUtils.dataFolder = dataFolder;
        configFiles.clear();
    }

    public static void setLocale(Locale loc) {
        locale = loc != null ? loc : locale;
    }

    public static void reload() {
        for (Map.Entry<String, YmlConfiguration> e : configFiles.entrySet()) {
            File configFile = new File(getDataFolder(), e.getKey());
            readConfig(e.getValue(), configFile);
        }
    }

    public static Properties readProperties(String fileName) {
        File configFile = getConfigFile(fileName);
        if (configFile != null && configFile.exists() && configFile.canRead()) {
            Properties prop = new Properties();
            try (InputStreamReader in = new InputStreamReader(new FileInputStream(configFile), "UTF-8")) {
                prop.load(in);
                return prop;
            } catch (IOException e) {
                log.log(Level.WARNING, "Error reading " + fileName, e);
            }
        }
        return null;
    }
    
	/**
	 * Copy folder to other.
	 * 
	 * @param source the source folder.
	 * @param destination the destination folder.
	 * @throws IOException
	 */
	public static void copy(File source, File destination) throws IOException {
		if (source.isDirectory()) {

			// if directory not exists, create it
			if (!destination.exists()) {
				destination.mkdir();
			}

			// list all the directory contents
			String files[] = source.list();

			for (String file : files) {
				// construct the src and dest file structure
				File srcFile = new File(source, file);
				File destFile = new File(destination, file);
				// recursive copy
				copy(srcFile, destFile);
			}

		} else {
			// if file, then copy it
			// Use bytes stream to support all file types
			InputStream in = new FileInputStream(source);
			OutputStream out = new FileOutputStream(destination);

			byte[] buffer = new byte[1024];

			int length;
			// copy the file content in bytes
			while ((length = in.read(buffer)) > 0) {
				out.write(buffer, 0, length);
			}

			in.close();
			out.close();
		}
	}

	/**
	 * Force file remove.
	 * 
	 * @param destFile the file to remove.
	 * @return true if is sucesfully removed.
	 */
	public static boolean forcedRemoveFile(File destFile) {
		try {
			if (destFile.isDirectory()) {
				return forcedRemoveDir(destFile);
			}

			File dir = destFile;
			System.gc();
			Thread.sleep(1000); // 1000
			FileDeleteStrategy.FORCE.delete(dir);
			dir.delete();
		} catch (Exception e) {
			// ignore.
		}
		return true;
	}

	/**
	 * Force directory remove.
	 * 
	 * @param destFile the directory to remove.
	 * @return true if is sucesfully removed.
	 */
	public static boolean forcedRemoveDir(File destFile) {
		try {
			// destFile = new File((System.getProperty("user.dir")+"/FileName"))
			// checks if the directory has any file
			File dir = destFile;
			if (dir.isDirectory()) {
				File[] files = dir.listFiles();
				if (files != null && files.length > 0) {
					for (File aFile : files) {
						System.gc();
						// Thread.sleep(delay); // 1000
						FileDeleteStrategy.FORCE.delete(aFile);
						// System.out.println("deleting file " + aFile);
					}
				}
				dir.delete();
			} else {
				dir.delete();
			}
		} catch (Exception e) {
			// ignore.
		}
		return true;
	}
}