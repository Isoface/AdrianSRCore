package es.outlook.adriansrj.core.util.file.filter;

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Pattern;

/**
 * A filter for abstract pathnames.
 * <p>
 * Instances of this abstract class may be passed to the <code>{@link File#listFiles(java.io.FileFilter)
 * listFiles(FileFilter)}</code> method of the <code>{@link java.io.File}</code> class.
 * <p>
 *
 * @author AdrianSR
 */
public abstract class FileExtensionFilter implements FileFilter {
	
	/**
	 * Returns a new {@link FileExtensionFilter} for the given file extension.
	 * <p>
	 *
	 * @param file_extension the file extension. Example: '.jar'
	 *
	 * @return the {@link FileExtensionFilter} for the given file extension.
	 */
	public static FileExtensionFilter of ( final String file_extension ) {
		return new FileExtensionFilter ( ) {
			
			@Override
			public boolean accept ( File file ) {
				return Pattern.compile ( "\\" + ( file_extension.startsWith ( "." ) ? file_extension.toLowerCase ( )
						: ( "." + file_extension.toLowerCase ( ) ) ) + "$" ).matcher ( file.getName ( ) ).find ( );
			}
		};
	}
	
	/**
	 * A collection of filters for abstract pathnames.<br> {@link #accept(File)} returns true if one of the filters
	 * accepts the provided pathname.
	 *
	 * @author AdrianSR / 30/08/2021 / 04:31 p. m.
	 */
	public static class Multiplexer extends FileExtensionFilter {
		
		/**
		 * Returns a new {@link Multiplexer} for the extensions covered by the provided filters.
		 *
		 * @param filters the filters by extension.
		 *
		 * @return the {@link Multiplexer} that will cover the extensions.
		 */
		public static Multiplexer of ( FileExtensionFilter... filters ) {
			return new Multiplexer ( filters );
		}
		
		/**
		 * Returns a new {@link Multiplexer} for the provided extensions.
		 *
		 * @param extensions the accepted extensions.
		 *
		 * @return the {@link Multiplexer} that will cover the extensions.
		 */
		public static Multiplexer of ( String... extensions ) {
			FileExtensionFilter[] filters = new FileExtensionFilter[ extensions.length ];
			
			for ( int i = 0; i < filters.length; i++ ) {
				filters[ i ] = FileExtensionFilter.of ( extensions[ i ] );
			}
			
			return new Multiplexer ( filters );
		}
		
		protected final FileExtensionFilter[] filters;
		
		public Multiplexer ( FileExtensionFilter... filters ) {
			this.filters = filters;
		}
		
		@Override
		public boolean accept ( File pathname ) {
			for ( FileExtensionFilter filter : filters ) {
				if ( filter.accept ( pathname ) ) {
					return true;
				}
			}
			
			return false;
		}
	}
}
