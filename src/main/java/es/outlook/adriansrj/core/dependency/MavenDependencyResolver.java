package es.outlook.adriansrj.core.dependency;

import es.outlook.adriansrj.core.dependency.classloader.IsolatedClassLoader;
import es.outlook.adriansrj.core.util.reflection.general.MethodReflection;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.bukkit.plugin.Plugin;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.repository.RepositoryPolicy;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.resolution.DependencyResolutionException;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transport.http.HttpTransporterFactory;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * TODO: Description
 * </p>
 *
 * @author AdrianSR / 17/08/2021 / Time: 03:37 p. m.
 */
public class MavenDependencyResolver {
	
	protected static final DefaultServiceLocator     LOCATOR;
	protected static final RepositorySystem          REPOSITORY;
	protected static final MavenDependencyRepository MAVEN2_REPOSITORY;
	
	static {
		LOCATOR = MavenRepositorySystemUtils.newServiceLocator ( );
		LOCATOR.addService ( RepositoryConnectorFactory.class , BasicRepositoryConnectorFactory.class );
		LOCATOR.addService ( TransporterFactory.class , HttpTransporterFactory.class );
		
		REPOSITORY        = LOCATOR.getService ( RepositorySystem.class );
		MAVEN2_REPOSITORY = new MavenDependencyRepository ( "central" , "default" ,
															"https://repo.maven.apache.org/maven2" );
	}
	
	protected final List < RemoteRepository >      repositories_raw = new ArrayList <> ( );
	protected final List < RemoteRepository >      repositories     = new ArrayList <> ( );
	protected final DefaultRepositorySystemSession session;
	
	public MavenDependencyResolver ( File directory , Collection < MavenDependencyRepository > repositories ) {
		session = MavenRepositorySystemUtils.newSession ( );
		
		session.setChecksumPolicy ( RepositoryPolicy.CHECKSUM_POLICY_FAIL );
		session.setLocalRepositoryManager (
				REPOSITORY.newLocalRepositoryManager ( session , new LocalRepository ( directory ) ) );
		session.setReadOnly ( );
		
		// adding repositories
		for ( MavenDependencyRepository repository : repositories ) {
			addRepository ( repository );
		}
		
		// we include the maven2 repository by default
		addRepository ( MAVEN2_REPOSITORY );
	}
	
	public MavenDependencyResolver ( File directory , MavenDependencyRepository... repositories ) {
		this ( directory , Arrays.asList ( repositories ) );
	}
	
	public Map < Artifact, File > resolveDependencies ( Collection < MavenDependency > dependencies ) {
		Map < Artifact, File > result = new HashMap <> ( );
		
		try {
			for ( ArtifactResult artifact_result : REPOSITORY.resolveDependencies ( session , new DependencyRequest (
					new CollectRequest ( ( Dependency ) null ,
										 dependencies.stream ( ).map ( MavenDependency :: getHandle )
												 .collect ( Collectors.toList ( ) ) ,
										 repositories ) , null ) ).getArtifactResults ( ) ) {
				
				result.put ( artifact_result.getArtifact ( ) ,
							 artifact_result.getArtifact ( ).getFile ( ) );
			}
		} catch ( DependencyResolutionException ex ) {
			throw new RuntimeException ( "error resolving dependencies" , ex );
		}
		
		return result;
	}
	
	public Map < Artifact, File > resolveDependencies ( MavenDependency... dependencies ) {
		return resolveDependencies ( Arrays.asList ( dependencies ) );
	}
	
	public Map < Artifact, File > resolveDependency ( MavenDependency dependency ) {
		return resolveDependencies ( dependency );
	}
	
	public URL[] prepare ( Collection < MavenDependency > dependencies ) {
		return resolveDependencies ( dependencies ).values ( ).stream ( ).map ( file -> {
			try {
				return file.toURI ( ).toURL ( );
			} catch ( MalformedURLException e ) {
				throw new RuntimeException ( e );
			}
		} ).toArray ( URL[] :: new );
	}
	
	public URL[] prepare ( MavenDependency... dependencies ) {
		return prepare ( Arrays.asList ( dependencies ) );
	}
	
	public URLClassLoader createLoader ( boolean isolate , Collection < MavenDependency > dependencies ) {
		URL[] urls = prepare ( dependencies );
		
		return isolate ? new IsolatedClassLoader ( urls ) :
				new URLClassLoader ( urls , getClass ( ).getClassLoader ( ) );
	}
	
	public URLClassLoader createLoader ( Collection < MavenDependency > dependencies ) {
		return createLoader ( false , dependencies );
	}
	
	public URLClassLoader createLoader ( boolean isolate , MavenDependency... dependencies ) {
		return createLoader ( isolate , Arrays.asList ( dependencies ) );
	}
	
	public URLClassLoader createLoader ( MavenDependency... dependencies ) {
		return createLoader ( false , dependencies );
	}
	
	/**
	 * @param plugin
	 * @param dependencies
	 *
	 * @throws UnsupportedOperationException if the class loader of the provided plugin doesn't extend from {@link
	 *                                       URLClassLoader}.
	 */
	public void inject ( Plugin plugin , Collection < MavenDependency > dependencies )
			throws UnsupportedOperationException {
		ClassLoader class_loader = plugin.getClass ( ).getClassLoader ( );
		
		if ( class_loader instanceof URLClassLoader ) {
			try {
				Method add_method = MethodReflection.getAccessible (
						URLClassLoader.class , "addURL" , URL.class );
				
				for ( URL url : prepare ( dependencies ) ) {
					add_method.invoke ( class_loader , url );
				}
			} catch ( NoSuchMethodException | InvocationTargetException | IllegalAccessException ex ) {
				ex.printStackTrace ( );
			}
		} else {
			throw new UnsupportedOperationException ( "can only inject plugins loaded from "
															  + URLClassLoader.class.getName ( ) + " loaders" );
		}
	}
	
	/**
	 * @param plugin
	 * @param dependencies
	 *
	 * @throws UnsupportedOperationException if the class loader of the provided plugin doesn't extend from {@link
	 *                                       URLClassLoader}.
	 */
	public void inject ( Plugin plugin , MavenDependency... dependencies )
			throws UnsupportedOperationException {
		inject ( plugin , Arrays.asList ( dependencies ) );
	}
	
	public void addRepository ( MavenDependencyRepository repository ) {
		this.repositories_raw.add ( repository.getHandle ( ) );
		this.updateRepositories ( );
	}
	
	public void removeRepository ( MavenDependencyRepository repository ) {
		this.repositories_raw.remove ( repository.getHandle ( ) );
		this.updateRepositories ( );
	}
	
	protected void updateRepositories ( ) {
		this.repositories.clear ( );
		this.repositories.addAll (
				REPOSITORY.newResolutionRepositories ( session , new ArrayList <> ( repositories_raw ) ) );
	}
}