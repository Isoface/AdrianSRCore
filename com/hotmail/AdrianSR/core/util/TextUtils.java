package com.hotmail.AdrianSR.core.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.bukkit.ChatColor;

/**
 * Represents a String utils class.
 * <p>
 * @author AdrianSR
 */
public final class TextUtils {
	
	public static final char LINE_SEPARATOR_CHAR = '\n';
	public static final char            TAB_CHAR = '\t';
	
	/**
	 * Class values.
	 */
	public  static final char  COLOR_CHAR = '&';
	private static final String ALL_CODES = "0123456789AaBbCcDdEeFfKkLlMmNnOoRr";
	
	/**
	 * Returns the given default string if the
	 * given string is null.
	 *  <p>
	 * @param str the String to check.
	 * @param def the default String.
	 * @return str, but def if null.
	 */
	public static String getNotNull(String str, String def) {
		return str != null ? str : def;
	}
	
	/**
	 * Get a shorten string.
	 * 
	 * @param the string.
	 * @param characters the max characters.
	 * @return the shorthen string from the string.
	 */
	public static String getShortenString(String string, int characters) {
		return (string.length() <= characters) ? string : string.substring(0, characters);
	}
	
	/**
	 * Converts only the characters sequence of the given target to lower case.
	 * <p>
	 * @param string the {@code String} where the characters to convert are located.
	 * @param target the characters sequence reference.
	 * @return the characters sequence of the given target in the given string converted to lower case.
	 */
	public static String toLowerCase(String string, String target) {
		return toLowerCase(string, target, Locale.getDefault());
	}
	
	/**
	 * Converts only the characters sequence of the given target to lower case,
	 * using the given {@code Locale} rules.
	 * <p>
	 * @param string the {@code String} where the characters to convert are located.
	 * @param target the characters sequence reference.
	 * @return the characters sequence of the given target in the given string converted to lower case.
	 */
	public static String toLowerCase(String string, String target, Locale locale) {
		String        lower_case = string.toLowerCase(locale);
		String target_lower_case = target.toLowerCase(locale);
		if (!lower_case.contains(target_lower_case)) {
			return lower_case;
		}
		
		char[]   chars = string.toCharArray();
		int last_index = 0;
		for (int i = 0; i < lower_case.length(); i++) {
			int current_index = lower_case.indexOf(target_lower_case, last_index);
			if (current_index != -1) {
				int end_index = current_index + target_lower_case.length();
				last_index    = end_index;
				for (int j = current_index; j < end_index; j++) {
					chars[j] = lower_case.charAt(j);
				}
			}
		}
		return new String(chars);
	}
	
	/**
	 * Converts only the characters sequence of the given target to upper case.
	 * <p>
	 * @param string the {@code String} where the characters to convert are located.
	 * @param target the characters sequence reference.
	 * @return the characters sequence of the given target in the given string converted to upper case.
	 */
	public static String toUpperCase(String string, String target) {
		return toUpperCase(string, target, Locale.getDefault());
	}
	
	/**
	 * Converts only the characters sequence of the given target to upper case,
	 * using the given {@code Locale} rules.
	 * <p>
	 * @param string the {@code String} where the characters to convert are located.
	 * @param target the characters sequence reference.
	 * @return the characters sequence of the given target in the given string converted to upper case.
	 */
	public static String toUpperCase(String string, String target, Locale locale) {
		String        upper_case = string.toUpperCase(locale);
		String target_upper_case = target.toUpperCase(locale);
		if (!upper_case.contains(target_upper_case)) {
			return upper_case;
		}
		
		char[]   chars = string.toCharArray();
		int last_index = 0;
		for (int i = 0; i < upper_case.length(); i++) {
			int current_index = upper_case.indexOf(target_upper_case, last_index);
			if (current_index != -1) {
				int end_index = current_index + target_upper_case.length();
				last_index    = end_index;
				for (int j = current_index; j < end_index; j++) {
					chars[j] = upper_case.charAt(j);
				}
			}
		}
		return new String(chars);
	}
	
	/**
	 * Counts the amount of substrings that are equals to the given substring
	 * on the given string.
	 * <p>
	 * @param string the string where the substrings are concatenated.
	 * @param substring the substring to search.
	 * @return count of substrings that are equal to the given substring.
	 */
	public static int getSubStringCount(String string, String substring) {
    	int count = 0, last_index = 0 ,index = 0;
    	while (index != -1) {
    		index      = string.indexOf(substring, last_index);
    		last_index = index + 1;
			count ++;
		}
		return count - 1;
    }
	
	/**
	 * Translate the symbol '&' to colors.
	 * 
	 * @param textToTranslate the String to transalte.
	 * @return a translated colors String.
	 */
	public static String translateColors(final String textToTranslate) {
		return textToTranslate == null ? null : ChatColor.translateAlternateColorCodes('&', textToTranslate);
	}
	
	/**
	 * Translate the symbol '&' in all string list 
	 * contents to colors.
	 * <p>
	 * @param list the string list to transalte.
	 * @return translated colors string list.
	 */
	public static List<String> translateColors(final List<String> list) {
		// clone list.
		final List<String> clone = new ArrayList<String>(list);
		
		// translate.
		for (int x = 0; x < clone.size(); x++) {
			clone.set(x, translateColors(clone.get(x)));
		}
		return clone;
	}
	
	public static String[] translateColors(String[] string_array) {
		String[] colored = string_array.clone();
		for (int i = 0; i < colored.length; i++) {
			colored[i] = translateColors(colored[i]);
		}
		return colored;
	}
	
	/**
	 * Untranslate colors to custom symbol.
	 * 
	 * @param altColorChar the colors replacer.
	 * @param textToTranslate the text to untranslate.
	 * @return the translated String.
	 */
	public static String untranslateAlternateColorCodes(char altColorChar, String textToTranslate) {
		char[] b = textToTranslate.toCharArray();
		for (int i = 0; i < b.length - 1; i++) {
			if (b[i] == altColorChar && ALL_CODES.indexOf(b[i + 1]) > -1) {
				b[i] = COLOR_CHAR;
				b[i + 1] = Character.toLowerCase(b[i + 1]);
			}
		}
		return new String(b);
	}

	/**
	 * Untranslate colors to '&' symbol.
	 * 
	 * @param textToTranslate the String to untranslated.
	 * @returna untranslated colors String.
	 */
	public static String untranslateColors(String textToTranslate) {
		return untranslateAlternateColorCodes(ChatColor.COLOR_CHAR, textToTranslate);
	}
	
	/**
	 * Untranslate colors to '&' symbol, in all
	 * string list contents.
	 * <p>
	 * @param list the string list to untranslated.
	 * @returna untranslated colors string list.
	 */
	public static List<String> untranslateColors(List<String> list) {
		List<String> clone = new ArrayList<String>(list);
		for (int x = 0; x < clone.size(); x++) {
			clone.set(x, untranslateColors(clone.get(x)));
		}
		return clone;
	}
	
	public static String[] untranslateColors(String[] string_array) {
		String[] colored = string_array.clone();
		for (int i = 0; i < colored.length; i++) {
			colored[i] = untranslateColors(colored[i]);
		}
		return colored;
	}
	
	public static String stripColors(String input) {
		return ChatColor.stripColor(input);
	}
	
	public String getLastColors(String input) {
		return ChatColor.getLastColors(input);
	}
	
	public static String[] stripColors(String[] string_array) {
		String[] colored = string_array.clone();
		for (int i = 0; i < colored.length; i++) {
			colored[i] = stripColors(colored[i]);
		}
		return colored;
	}
	
	public static List<String> stripColors(List<String> string_list) {
		List<String> colored = new ArrayList<String>(string_list);
		for (int i = 0; i < colored.size(); i++) {
			colored.set(i, colored.get(i));
		}
		return colored;
	}
	
	public static String clearWhitespaces(String string) {
    	String result = "";
    	for (int i = 0; i < string.toCharArray().length; i++) {
			if (!Character.isWhitespace(string.charAt(i))) {
				result += string.charAt(i);
			}
		}
    	return result;
    }
	
	/**
	 * Generated Keys list.
	 */
	private static final List<String> GENERATED_KEYS = new ArrayList<String>();
	
	/**
	 * Getnerated Keys counter.
	 */
	private static int KEY_COUNTER = 0;
	
	/**
	 * Generate Unique Key for
	 * ScoreBoard Teams.
	 * <p>
	 * @return generated key.
	 */
	public static String generateKey() {
		// get colors array.
		final ChatColor[] colors = ChatColor.values();

		// get color
		ChatColor tor = null;

		// check lenght.
		if (KEY_COUNTER < colors.length) {
			tor = colors[KEY_COUNTER];
		} else {
			// getting id
			int id = KEY_COUNTER;

			// decress and break when is min.
			while (true) {
				// check is max.
				if (id < colors.length) {
					break;
				}

				id -= 22; // decress
			}

			// get color.
			tor = colors[id];
		}

		// get string to return.
		String torstring = tor.toString();

		// check contains.
		while (true) {
			if (!GENERATED_KEYS.contains(torstring)) {
				break;
			}

			// add val.
			torstring += ChatColor.RESET.toString();
		}

		// register key.
		GENERATED_KEYS.add(torstring);

		// ++ KEY_COUNTER.
		KEY_COUNTER++;
		return torstring;
	}
}
