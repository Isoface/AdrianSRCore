package es.outlook.adriansrj.core.dependency;

import org.eclipse.aether.repository.RemoteRepository;

/**
 * TODO: Description
 * </p>
 *
 * @author AdrianSR / 17/08/2021 / Time: 03:41 p. m.
 */
public class MavenDependencyRepository {
	
	protected final RemoteRepository handle;
	
	public MavenDependencyRepository ( String id , String type , String url ) {
		this.handle = new RemoteRepository.Builder ( id , type , url ).build ( );
	}
	
	public MavenDependencyRepository ( String id , String url ) {
		this ( id , "default" , url );
	}
	
	public MavenDependencyRepository ( RemoteRepository prototype ) {
		this.handle = new RemoteRepository.Builder ( prototype ).build ( );
	}
	
	public RemoteRepository getHandle ( ) {
		return handle;
	}
	
	public String getId ( ) {
		return handle.getId ( );
	}
	
	public String getContentType ( ) {
		return handle.getContentType ( );
	}
	
	public String getUrl ( ) {
		return handle.getUrl ( );
	}
}
