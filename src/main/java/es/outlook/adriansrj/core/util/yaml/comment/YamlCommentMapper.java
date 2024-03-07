package es.outlook.adriansrj.core.util.yaml.comment;

/**
 *
 */
public class YamlCommentMapper {

	public static final String COMMENT_PREFIX = "# ";

	protected KeyTree keyTree;

	public YamlCommentMapper ( final YamlConfigurationOptionsComments options ) {
		this.keyTree = new KeyTree ( options );
	}

	public void setComment ( final String path , String comment ) {
		KeyTree.Node node = this.getNode ( path );
		
		if ( node == null ) {
			node = this.keyTree.add ( path );
		}
		
		if ( comment == null || comment.isEmpty ( ) ) {
			this.setComment ( node , null );
		} else if ( comment.matches ( "\n+" ) ) {
			this.setComment ( node , comment );
		} else {
			comment = COMMENT_PREFIX + comment;
			comment = comment.replaceAll ( "[ \\t]*\n" , "\n" + COMMENT_PREFIX );
			
			node.setComment ( this.indent ( comment , node.getIndentation ( ) ) );
		}
	}

	public String getComment ( final String path ) {
		final KeyTree.Node node = this.getNode ( path );
		if ( node == null ) {
			return null;
		}
		String comment = node.getComment ( );
		
		if ( comment != null ) {
			comment = comment.replaceAll ( "[ \\t]*#+[ \\t]*" , "" ).trim ( );
		}
		return comment;
	}

	protected KeyTree.Node getNode ( final String path ) {
		return this.keyTree.get ( path );
	}

	private void setComment ( final KeyTree.Node node , final String comment ) {
		node.setComment ( comment );
	}

	private String indent ( final String s , final int n ) {
		final String padding = this.padding ( n );
		final String[] lines = s.split ( "\n" );
		final StringBuilder builder = new StringBuilder ( s.length ( ) + n * lines.length );
		for ( final String line : lines ) {
			builder.append ( padding ).append ( line ).append ( '\n' );
		}
		return builder.toString ( );
	}

	private String padding ( final int n ) {
		final StringBuilder builder = new StringBuilder ( n );
		for ( int i = 0; i < n; i++ ) {
			builder.append ( ' ' );
		}
		return builder.toString ( );
	}
}
