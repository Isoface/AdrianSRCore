package com.hotmail.AdrianSR.core.util.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.logging.Level;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

/**
 * Represents a UTF_8 {@link YamlConfiguration}.
 * 
 * @author AdrianSR
 */
public class UTF8YamlConfiguration extends YamlConfiguration {
	
	@Override
    public void save(File file) throws IOException { //
        Validate.notNull(file, "File cannot be null");
        Files.createParentDirs(file);

        String   data = saveToString();
        Writer writer = new OutputStreamWriter(new FileOutputStream(file), Charsets.UTF_8);

        try {
            writer.write(data);
        } finally {
            writer.close();
        }
    }

	@Override
	public void load(File file) throws FileNotFoundException, IOException, InvalidConfigurationException {
		Validate.notNull(file, "File cannot be null");
		this.load(new InputStreamReader(new FileInputStream(file), Charsets.UTF_8));
	}

	@Deprecated
	public void load(InputStream stream) throws IOException, InvalidConfigurationException {
		Validate.notNull(stream, "Stream cannot be null");
		this.load(new InputStreamReader(stream, Charsets.UTF_8));
	}

	/**
	 * Load UFT_8 Yaml Configuration from a {@link File}.
	 * 
	 * @param file the File.
	 * @return a new Yaml Configuration or null if the file null or does not exists.
	 */
	public static UTF8YamlConfiguration loadConfiguration(File file) {
		Validate.notNull(file, "File cannot be null");
		UTF8YamlConfiguration config = new UTF8YamlConfiguration();

		try {
			config.load(file);
		} catch (FileNotFoundException localFileNotFoundException) {
			// ignore.
		} catch (IOException ex) {
			Bukkit.getLogger().log(Level.SEVERE, "Cannot load " + file, ex);
		} catch (InvalidConfigurationException ex) {
			Bukkit.getLogger().log(Level.SEVERE, "Cannot load " + file, ex);
		}
		return config;
	}
	
	@Deprecated
    public static UTF8YamlConfiguration loadConfiguration(InputStream stream) {
        Validate.notNull(stream, "Stream cannot be null");
        UTF8YamlConfiguration config = new UTF8YamlConfiguration();

        try {
            config.load(stream);
        } catch (IOException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Cannot load configuration from stream", ex);
        } catch (InvalidConfigurationException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Cannot load configuration from stream", ex);
        }

        return config;
    }
	
	/**
	 * Returns object item to class.
	 * <p>
	 * @param class_assignable the Class to.
	 * @return an instance of the given class or null if is not assigable.
	 */
	public <T> T of(String key, Class<T> instance_of) {
		Object object = get(key);
		if (instance_of.isAssignableFrom(object.getClass())) {
			// return (T) object; OLD: @SuppressWarnings
			return instance_of.cast(object);
		}
		return null;
	}
}
