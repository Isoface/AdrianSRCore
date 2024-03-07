package es.outlook.adriansrj.core.dependency;

import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;

import java.io.File;
import java.util.Collections;
import java.util.Map;

/**
 * TODO: Description
 * </p>
 *
 * @author AdrianSR / 17/08/2021 / Time: 03:38 p. m.
 */
public class MavenDependency {
	
	protected final Artifact   artifact;
	protected final Dependency dependency;
	
	public MavenDependency ( Artifact artifact ) {
		this.artifact   = artifact;
		this.dependency = new Dependency ( artifact , null );
	}
	
	public MavenDependency ( String coordinates , Map < String, String > properties ) {
		this ( new DefaultArtifact ( coordinates , properties ) );
	}
	
	public MavenDependency ( String coordinates ) {
		this ( coordinates , Collections.emptyMap ( ) );
	}
	
	public Artifact getArtifact ( ) {
		return artifact;
	}
	
	public Dependency getHandle ( ) {
		return dependency;
	}
	
	public File getResult ( ) {
		return artifact.getFile ( );
	}
}