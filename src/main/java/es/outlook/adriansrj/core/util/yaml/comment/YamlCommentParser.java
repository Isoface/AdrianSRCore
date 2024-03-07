package es.outlook.adriansrj.core.util.yaml.comment;

import java.io.IOException;
import java.io.Reader;
import java.util.regex.Pattern;

public class YamlCommentParser extends YamlCommentReader {
	
	private static final Pattern SIDE_COMMENT_REGEX = Pattern.compile ( "^[ \\t]*[^#\\s].*?([ \\t]*#.*)" );
	
	private StringBuilder currentComment; // block comment
	
	public YamlCommentParser ( final YamlConfigurationOptionsComments options , final Reader reader ) {
		super ( options , reader );
	}
	
	public void parse ( ) throws IOException {
		while ( this.nextLine ( ) ) {
			if ( this.isBlank ( ) || this.isComment ( ) ) {
				this.appendLine ( );
			} else {
				this.trackComment ( );
			}
		}
		
		// Last comment
		this.trackComment ( );
		
		this.reader.close ( );
	}
	
	private void appendLine ( ) {
		if ( this.currentComment == null ) {
			this.currentComment = new StringBuilder ( this.currentLine );
		} else {
			this.currentComment.append ( this.currentLine );
		}
		this.currentComment.append ( '\n' );
	}
	
	private void trackComment ( ) {
		final KeyTree.Node node = this.track ( );
		
		if ( this.currentComment != null ) {
			node.setComment ( this.currentComment.toString ( ) );
			this.currentComment = null;
		}
	}
}