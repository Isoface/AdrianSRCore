package es.outlook.adriansrj.core.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;

/**
 * An implementation of {@link StringUtils} that implements some new methods.
 * <p>
 * @author AdrianSR / Monday 13 April, 2020 / 02:25 PM
 */
public class StringUtil extends StringUtils {
	
	public static final String LINE_SEPARATOR = System.lineSeparator ( );

	/**
	 * Convert an integer to an HTML RGB value. The result is of the form
	 * #hhhhhh. The input rgb integer value will be clipped into the range 0 ~
	 * 0xFFFFFF
	 * 
	 * @param rgb
	 *            the integer RGB value
	 * @return the value as an HTML RGB string
	 */
	public static String toRgbText( int rgb )
	{
		// clip input value.

		if ( rgb > 0xFFFFFF )
			rgb = 0xFFFFFF;
		if ( rgb < 0 )
			rgb = 0;

		String str = "000000" + Integer.toHexString( rgb ); //$NON-NLS-1$ 
		return "#" + str.substring( str.length( ) - 6 ); //$NON-NLS-1$ 
	}
	
	/**
	 * Limits the provided string to have a maximum of characters.
	 * <p>
	 * @param string the string to limit.
	 * @param max_length the maximum length ( the maximum number of characters ).
	 * @return the limited string.
	 */
	public static String limit ( String string , int max_length ) {
		return string.length() > max_length ? ( string.substring ( 0 , max_length ) ) : string;
	}
	
	/**
	 * Concatenates the provided {@link ChatColor} array, making it useful
	 * to be concatenated to any string.
	 * <br>
	 * @param colors the colors.
	 * @return single color line.
	 */
	public static String concatenate ( ChatColor ... colors ) {
		StringBuilder builder = new StringBuilder ( );
		
		// we're applying colors first
		for ( int x = 0 ; x < 2 ; x ++ ) {
			boolean apply_colors = x == 0;
			
			for ( int i = 0 ; i < colors.length ; i ++ ) {
				ChatColor color = colors [ i ];
				
				if ( apply_colors ? color.isColor ( ) : color.isFormat ( ) ) {
					builder.append ( color );
				}
			}
		}
		return builder.toString ( );
	}
	
	/**
	 * Colorizes the provided {@link String}.
	 * <br>
	 * @param colors the {@link ChatColor}s to apply.
	 * @param string the string to colorize.
	 * @return the colorized string.
	 */
	public static String colorize ( ChatColor [ ] colors , String string ) {
		return concatenate ( colors ) + string;
	}
	
	/**
	 * Colorizes the provided {@link String}.
	 * <br>
	 * @param string the string to colorize.
	 * @param colors the {@link ChatColor}s to apply.
	 * @return the colorized string.
	 */
	public static String colorize ( String string , ChatColor ... colors ) {
		return colorize ( colors , string );
	}
	
	/**
	 * Translates a string using an alternate color code character into a string
	 * that uses the internal {@link ChatColor#COLOR_CHAR} color code character. The
	 * alternate color code character will only be replaced if it is immediately
	 * followed by 0-9, A-F, a-f, K-O, k-o, R or r.
	 * <p>
	 * @param alt_char The alternate color code character to replace. Ex:
	 *                 {@literal &}
	 * @param string   Text containing the alternate color code character.
	 * @return Text containing the ChatColor.COLOR_CODE color code character.
	 * @see ChatColor#translateAlternateColorCodes(char, String)
	 */
	public static String translateAlternateColorCodes ( char alt_char , String string ) {
		return ChatColor.translateAlternateColorCodes ( alt_char , string );
	}
	
	/**
	 * Translates a string using the alternate color code character '{@literal &}'
	 * into a string that uses the internal {@link ChatColor#COLOR_CHAR} color code
	 * character. The alternate color code character will only be replaced if it is
	 * immediately followed by 0-9, A-F, a-f, K-O, k-o, R or r.
	 * <p>
	 * @param string Text containing the alternate color code character.
	 * @return Text containing the ChatColor.COLOR_CODE color code character.
	 * @see #translateAlternateColorCodes(char, String)
	 */
	public static String translateAlternateColorCodes ( String string ) {
		return translateAlternateColorCodes ( '&' , string);
	}
	
	/**
	 * Translates the strings of the provided array using an alternate color code
	 * character into a string that uses the internal {@link ChatColor#COLOR_CHAR} color
	 * code character. The alternate color code character will only be replaced if
	 * it is immediately followed by 0-9, A-F, a-f, K-O, k-o, R or r.
	 * <p>
	 * @param alt_char The alternate color code character to replace. Ex:
	 *                 {@literal &}
	 * @param array    the strings array to translate.
	 * @return a new strings array containing the translated strings.
	 */
	public static String [ ] translateAlternateColorCodes ( char alt_char , String [ ] array ) {
		String [ ] copy = new String [ array.length ];
		for ( int i = 0 ; i < copy.length ; i ++ ) {
			copy [ i ] = translateAlternateColorCodes ( alt_char , array [ i ] );
		}
		return array;
	}
	
	/**
	 * Translates the strings of the provided array using the alternate color code
	 * character '{@literal &}' into a string that uses the internal
	 * {@link ChatColor#COLOR_CHAR} color code character. The alternate color code character
	 * will only be replaced if it is immediately followed by 0-9, A-F, a-f, K-O,
	 * k-o, R or r.
	 * <p>
	 * @param array the strings collection to translate.
	 * @return a new strings array containing the translated strings.
	 */
	public static String [ ] translateAlternateColorCodes ( String [ ] array ) {
		return translateAlternateColorCodes ( '&' , array );
	}
	
	/**
	 * Translates the strings of the provided collection using an alternate color
	 * code character into a string that uses the internal {@link ChatColor#COLOR_CHAR}
	 * color code character. The alternate color code character will only be
	 * replaced if it is immediately followed by 0-9, A-F, a-f, K-O, k-o, R or r.
	 * <p>
	 * @param alt_char   The alternate color code character to replace. Ex:
	 *                   {@literal &}
	 * @param collection the collection of strings to translate.
	 * @return a new {@link List} of string containing the translated strings.
	 */
	public static List < String > translateAlternateColorCodes ( char alt_char , Collection < String > collection ) {
		final List < String > list = new ArrayList < String > ( );
		for ( String string : collection ) {
			list.add ( translateAlternateColorCodes ( alt_char , string ) );
		}
		return list;
	}
	
	/**
	 * Translates the strings of the provided collection using the alternate color
	 * code character '{@literal &}' into a string that uses the internal
	 * {@link ChatColor#COLOR_CHAR} color code character. The alternate color code character
	 * will only be replaced if it is immediately followed by 0-9, A-F, a-f, K-O,
	 * k-o, R or r.
	 * <p>
	 * @param collection the collection of strings to translate.
	 * @return a new {@link List} of string containing the translated strings.
	 */
	public static List < String > translateAlternateColorCodes ( Collection < String > collection ) {
		return translateAlternateColorCodes ( '&' , collection );
	}
	
	/**
	 * Translates the strings of the provided list using an alternate color code
	 * character into a string that uses the internal {@link ChatColor#COLOR_CHAR} color
	 * code character. The alternate color code character will only be replaced if
	 * it is immediately followed by 0-9, A-F, a-f, K-O, k-o, R or r.
	 * <p>
	 * @param alt_char The alternate color code character to replace. Ex:
	 *                 {@literal &}
	 * @param list     the list of strings to translate.
	 * @return the same {@link List} containing the translated strings.
	 */
	public static List < String > translateAlternateColorCodes ( char alt_char , List < String > list ) {
		for ( int i = 0 ; i < list.size() ; i++ ) {
			list.set ( i , translateAlternateColorCodes ( alt_char , list.get ( i ) ) );
		}
		return list;
	}
	
	/**
	 * Translates the strings of the provided list using the alternate color code
	 * character '{@literal &}' into a string that uses the internal
	 * {@link ChatColor#COLOR_CHAR} color code character. The alternate color code character
	 * will only be replaced if it is immediately followed by 0-9, A-F, a-f, K-O,
	 * k-o, R or r.
	 * <p>
	 * @param list the list of strings to translate.
	 * @return the same {@link List} containing the translated strings.
	 */
	public static List < String > translateAlternateColorCodes ( List < String > list ) {
		return translateAlternateColorCodes ( '&' , list );
	}
	
	/**
	 * Replaces the Bukkit internal color character {@link ChatColor#COLOR_CHAR} by the
	 * provided character.
	 * <p>
	 * @param alt_char the character replacer.
	 * @param string   the target string.
	 * @return the result.
	 */
	public static String untranslateAlternateColorCodes ( char alt_char , String string ) {
		char [ ] contents = string.toCharArray ( );
		for ( int i = 0 ; i < contents.length ; i ++ ) {
			if ( contents [ i ] == ChatColor.COLOR_CHAR ) {
				contents [ i ] = alt_char;
			}
		}
		return new String ( contents );
	}
	
	/**
	 * Replaces the Bukkit internal color character {@link ChatColor#COLOR_CHAR} by the well
	 * known color character '{@literal &}'.
	 * <p>
	 * @param string the target string.
	 * @return the result.
	 */
	public static String untranslateAlternateColorCodes ( String string ) {
		return untranslateAlternateColorCodes ( '&' , string );
	}
	
	/**
	 * Replaces from the strings of the provided array the Bukkit internal color
	 * character {@link ChatColor#COLOR_CHAR} by the provided character.
	 * <p>
	 * @param alt_char the character replacer.
	 * @param array    the strings array to replace.
	 * @return a new strings array containing the processed strings.
	 */
	public static String [ ] untranslateAlternateColorCodes ( char alt_char , String [ ] array ) {
		String [ ] copy = new String [ array.length ];
		for ( int i = 0 ; i < copy.length ; i ++ ) {
			copy [ i ] = untranslateAlternateColorCodes ( alt_char , array [ i ] );
		}
		return array;
	}
	
	/**
	 * Replaces from the strings of the provided array the Bukkit internal color
	 * character {@link ChatColor#COLOR_CHAR} by the well known color character
	 * '{@literal &}'.
	 * <p>
	 * @param array the strings array to replace.
	 * @return a new strings array containing the processed strings.
	 */
	public static String [ ] untranslateAlternateColorCodes ( String [ ] array ) {
		return untranslateAlternateColorCodes ( '&' , array );
	}
	
	/**
	 * Replaces from the strings of the provided collection the Bukkit internal
	 * color character {@link ChatColor#COLOR_CHAR} by the provided character.
	 * <p>
	 * @param alt_char   the character replacer.
	 * @param collection the strings collection to replace.
	 * @return a new list containing the processed strings.
	 */
	public static List < String > untranslateAlternateColorCodes ( char alt_char , Collection < String > collection ) {
		final List < String > list = new ArrayList < String > ( );
		for ( String string : collection ) {
			list.add ( untranslateAlternateColorCodes ( alt_char , string ) );
		}
		return list;
	}
	
	/**
	 * Replaces from the strings of the provided list the Bukkit internal color
	 * character {@link ChatColor#COLOR_CHAR} by the well known color character
	 * '{@literal &}'.
	 * <p>
	 * @param collection the strings collection to replace.
	 * @return a new list containing the processed strings.
	 */
	public static List < String > untranslateAlternateColorCodes ( Collection < String > collection ) {
		return untranslateAlternateColorCodes ( '&' , collection );
	}
	
	/**
	 * Replaces from the strings of the provided collection the Bukkit internal
	 * color character {@link ChatColor#COLOR_CHAR} by the provided character.
	 * <p>
	 * @param alt_char the character replacer.
	 * @param list     the list of strings to replace.
	 * @return the same {@link List} containing the processed strings.
	 */
	public static List < String > untranslateAlternateColorCodes ( char alt_char , List < String > list ) {
		for ( int i = 0 ; i < list.size() ; i++ ) {
			list.set ( i , untranslateAlternateColorCodes ( alt_char , list.get ( i ) ) );
		}
		return list;
	}
	
	/**
	 * Replaces from the strings of the provided collection the Bukkit internal
	 * color character {@link ChatColor#COLOR_CHAR} by the well known color
	 * character '{@literal &}'.
	 * <p>
	 * @param list the list of strings to replace.
	 * @return the same {@link List} containing the processed strings.
	 */
	public static List < String > untranslateAlternateColorCodes ( List < String > list ) {
		return untranslateAlternateColorCodes ( '&' , list );
	}
	
	/**
	 * Strips the given string of all colors.
	 * <p>
	 * @param string the string to strip of color.
	 * @return the result.
	 */
	public static String stripColors ( String string ) {
		return ChatColor.stripColor ( string );
	}
	
	/**
	 * Fixes excessive whitespaces replacing it with a single whitespace.
	 * <p>
	 * @param string the string to fix.
	 * @return the fix string.
	 */
	public static String fixExcessiveWhitespaces ( String string ) {
		return string.replaceAll ( "\\s{2,}" , " " );
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
}