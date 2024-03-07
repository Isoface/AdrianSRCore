package es.outlook.adriansrj.core.scoreboard;

import es.outlook.adriansrj.core.util.StringUtil;
import es.outlook.adriansrj.core.util.entity.UUIDPlayer;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.*;
import java.util.stream.Collectors;

/**
 * {@link Score} scoreboard.
 * <p>
 * @author AdrianSR / Tuesday 18 May, 2021 / 05:40 PM
 */
public class ScoreScoreboard {
	
	public static final int    MAX_NAME_LENGTH        = 16;
	public static final int    MAX_DISPLAYNAME_LENGTH = 32;
	public static final int    MAX_ELEMENTS           = 15;
	public static final int    MAX_ELEMENTS_LENGTH    = 40;
	public static final String WHITESPACE_INDICATOR   = "{//}";
	
	private final Map < String, Score > scores       = new HashMap <> ( );
	private final Set < UUIDPlayer >    viewers      = new HashSet <> ( );
	private final String[]              elements     = new String[ MAX_ELEMENTS ];
	private       Score[]               current_view = null;
	
	private final Scoreboard handle;
	private final Objective  objective;
	
	public ScoreScoreboard ( String title ) {
		Validate.notNull ( title , "The title cannot be null!" );
		
		this.handle    = Bukkit.getScoreboardManager ( ).getNewScoreboard ( );
		this.objective = handle.registerNewObjective (
				StringUtil.limit ( UUID.randomUUID ( ).toString ( ) , MAX_NAME_LENGTH ) , "milo" );
		this.objective.setDisplayName (
				StringUtil.limit ( StringUtil.translateAlternateColorCodes ( title ) , MAX_DISPLAYNAME_LENGTH ) );
		this.objective.setDisplaySlot ( DisplaySlot.SIDEBAR );
	}
	
	/**
	 * Gets the scoreboard name.
	 * <p>
	 * @return the scoreboard name.
	 */
	public String getName ( ) {
		return objective.getDisplayName ( );
	}
	
	/**
	 * Sets the scoreboard title.
	 * <p>
	 * @param title the scoreboard title.
	 */
	public void setTitle ( String title ) {
		objective.setDisplayName ( StringUtil.limit (
				StringUtil.translateAlternateColorCodes ( StringUtil.defaultIfBlank ( title , "" ) ) ,
				MAX_DISPLAYNAME_LENGTH ) );
	}
	
	/**
	 * Performs an updates to the view of the scoreboard.
	 * <p>
	 * This should be called after any change in the elements, so viewers will
	 * actually see changes.
	 */
	public void update ( ) {
		String[] result = new String[ 0 ];
		
		// finding actual elements
		for ( String element : elements ) {
			if ( element != null ) {
				StringBuilder content = new StringBuilder ( element );
				
				// duplicated element, let's try to fix this
				while ( ArrayUtils.indexOf ( result , content.toString ( ) ) != -1 ) {
					content.append ( ' ' );
				}
				
				// then including
				result = ( String[] ) ArrayUtils.add ( result , limit ( content.toString ( ) ) );
			}
		}
		
		// then updating the view
		if ( current_view == null ) {
			// this is the first update, so let's generate the first view
			current_view = new Score[ result.length ];
			
			for ( int i = 0 ; i < result.length ; i++ ) {
				String element = result[ i ];
				
				if ( WHITESPACE_INDICATOR.equals ( element.trim ( ) ) ) {
					element     = whitespaceStringEquivalent ( nextWhitespace ( result ) );
					result[ i ] = element;
				}
				
				current_view[ i ] = setScore ( element , MAX_ELEMENTS - i );
			}
		} else {
			Score[] updated_view = new Score[ result.length ];
			
			if ( updated_view.length > 0 ) {
				Map < Integer, Score > whitespaces = new HashMap <> ( );
				
				// removing old scores that are not going to be used anymore and
				// find whitespaces that can be recycled
				for ( Score score : current_view ) {
					if ( score == null ) { continue; }
					
					boolean remove;
					
					if ( StringUtil.isBlank ( score.getEntry ( ) ) ) {
						int index = MAX_ELEMENTS - score.getScore ( );
						
						if ( index < result.length && WHITESPACE_INDICATOR.equals ( result[ index ].trim ( ) ) ) {
							whitespaces.put ( index , score );
							
							remove = false;
						} else {
							remove = true;
						}
					} else {
						remove = ArrayUtils.indexOf ( result , score.getEntry ( ) ) == -1;
					}
					
					if ( remove ) {
						removeScore ( score.getEntry ( ) );
					}
				}
				
				// performing changes
				for ( int i = 0 ; i < result.length ; i++ ) {
					String element = result[ i ];
					int    index   = MAX_ELEMENTS - i;
					
					if ( WHITESPACE_INDICATOR.equals ( element.trim ( ) ) ) {
						// reclying whitespace
						if ( whitespaces.containsKey ( i ) ) {
							updated_view[ i ] = whitespaces.get ( i );
							
							whitespaces.remove ( i );
							continue;
						}
						
						element     = whitespaceStringEquivalent ( nextWhitespace ( result ) );
						result[ i ] = element;
					}
					
					updated_view[ i ] = setScore ( element , index );
				}
				
				// clearing old whitespaces
				for ( Score whitespace : whitespaces.values ( ) ) {
					removeScore ( whitespace.getEntry ( ) );
				}
			} else {
				// empty scoreboard
				Iterator < String > iterator = scores.keySet ( ).iterator ( );
				
				while ( iterator.hasNext ( ) ) {
					handle.resetScores ( iterator.next ( ) );
					
					iterator.remove ( );
				}
			}
			
			current_view = updated_view;
		}
		
		/* check viewers are viewing this */
		getViewers ( ).stream ( ).filter ( Objects :: nonNull ).forEach ( viewer -> {
			viewer.setScoreboard ( handle );
		} );
	}
	
	private int nextWhitespace ( String[] elements ) {
		int size = 0;
		
		while ( scores.containsKey ( whitespaceStringEquivalent ( size ) ) ) {
			size++;
		}
		
		return size;
	}
	
	private String whitespaceStringEquivalent ( int count ) {
		StringBuilder equivalent = new StringBuilder ( );
		
		for ( int i = 0 ; i < count ; i++ ) {
			equivalent.append ( ' ' );
		}
		return equivalent.toString ( );
	}
	
	private Score createScore ( String name , int value ) {
		if ( scores.containsKey ( name ) ) {
			throw new IllegalStateException ( "a score with that name already exists" );
		}
		
		Score score = objective.getScore ( name );
		score.setScore ( value );
		
		scores.put ( name , score );
		return score;
	}
	
	private Score setScore ( String name , int value ) {
		Score score = null;
		
		if ( scores.containsKey ( name ) ) {
			score = scores.get ( name );
			score.setScore ( value );
		} else {
			score = createScore ( name , value );
		}
		
		return score;
	}
	
	private boolean removeScore ( String name ) {
		if ( scores.containsKey ( name ) ) {
			handle.resetScores ( name );
			scores.remove ( name );
			
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Gets the players who are viewing this scoreboard.
	 * <p>
	 * @return the players who are viewing this scoreboard.
	 */
	public Set < Player > getViewers ( ) {
		return Collections.unmodifiableSet (
				viewers.stream ( ).map ( UUIDPlayer :: get )
						.filter ( Objects :: nonNull ).collect ( Collectors.toSet ( ) ) );
	}
	
	/**
	 * Gets whether the provided player is a viewer
	 * of this scoreboard.
	 *
	 * @param player the player to check.
	 * @return whether the provided player is a viewer of this scoreboard.
	 */
	public boolean isViewer ( Player player ) {
		return viewers.stream ( ).anyMatch (
				uuid -> Objects.equals ( uuid.getUniqueId ( ) , player.getUniqueId ( ) ) );
	}
	
	/**
	 * Gets whether the provided player (represented by the unique id) is a viewer
	 * of this scoreboard.
	 *
	 * @param unique_id the {@link UUID} of the player to check.
	 * @return whether the provided player is a viewer of this scoreboard.
	 */
	public boolean isViewer ( UUID unique_id ) {
		return viewers.stream ( ).anyMatch (
				uuid -> Objects.equals ( uuid.getUniqueId ( ) , unique_id ) );
	}
	
	/**
	 * Clears the list of viewers.
	 */
	public void clearViewers ( ) {
		viewers.clear ( );
	}
	
	/**
	 * Gets the elements this scoreboard is displaying
	 * <p>
	 * @return the elements this scoreboard is displaying
	 */
	public String[] getEntries ( ) {
		return Arrays.copyOf ( this.elements , MAX_ELEMENTS );
	}
	
	/**
	 * Gets the element displayed at the given index.
	 * <p>
	 * Note that the returned value might be empty or
	 * {@value #WHITESPACE_INDICATOR}, which means that there is a whitespace at
	 * the provided {@code index}.
	 * <p>
	 * Note that the returned value might be <strong><code>null</code></strong>,
	 * which means that the element at the provided {@code index} is not valid.
	 * <p>
	 * @param index the index of the element.
	 * @return the element displayed at the given index.
	 */
	public String get ( int index ) {
		this.rangeCheck ( index );
		
		return elements[ index ];
	}
	
	/**
	 * Displays this scoreboard to the specified viewers.
	 * <p>
	 * <strong>Note that when the method {@link #update()} is called, all
	 * the viewers will automatically see the changes on the
	 * Scoreboard.</strong>
	 * <p>
	 * @param viewers the {@link Collection} of {@link Player} viewers.
	 */
	public void addViewers ( Collection < Player > viewers ) {
		Validate.notNull ( viewers , "viewers cannot be null!" );
		
		viewers.stream ( ).filter ( Objects :: nonNull ).map ( UUIDPlayer :: new )
				.forEach ( this.viewers :: add );
	}
	
	/**
	 * Displays this scoreboard to the specified viewers represented
	 * by the provided unique ids.
	 * <p>
	 * <strong>Note that when the method {@link #update()} is called, all
	 * the viewers will automatically see the changes on the
	 * Scoreboard.</strong>
	 * <p>
	 * @param viewers the {@link Collection} of viewers {@link UUID}s.
	 */
	public void addViewersByUniqueId ( Collection < UUID > viewers ) {
		Validate.notNull ( viewers , "viewers cannot be null!" );
		
		viewers.stream ( ).filter ( Objects :: nonNull ).map ( UUIDPlayer :: new )
				.forEach ( this.viewers :: add );
	}
	
	/**
	 * Displays this scoreboard to the specified viewers.
	 * <p>
	 * <strong>Note that when the method {@link #update()} is called, all
	 * the viewers will automatically see the changes on the
	 * Scoreboard.</strong>
	 * <p>
	 * @param viewers the {@link Player} viewers.
	 */
	public void addViewer ( Player... viewers ) {
		this.addViewers ( Arrays.asList ( viewers ) );
	}
	
	/**
	 * Displays this scoreboard to the specified viewers represented
	 * by the provided unique ids.
	 * <p>
	 * <strong>Note that when the method {@link #update()} is called, all
	 * the viewers will automatically see the changes on the
	 * Scoreboard.</strong>
	 * <p>
	 * @param viewers the viewers {@link UUID}s.
	 */
	public void addViewersByUniqueId ( UUID... viewers ) {
		addViewersByUniqueId ( Arrays.asList ( viewers ) );
	}
	
	/**
	 * Unregister the given players from the viewers.
	 * <p>
	 * @param viewers the {@link Collection} of {@link Player} viewers to unregister.
	 */
	public void removeViewers ( Collection < Player > viewers ) {
		Validate.notNull ( viewers , "the player viewers list cannot be null!" );
		
		this.viewers.removeIf ( uuid -> viewers.stream ( )
				.anyMatch ( player -> Objects.equals ( uuid.getUniqueId ( ) , player.getUniqueId ( ) ) ) );
		
		// here we set the viewing scoreboard to the main
		viewers.forEach ( viewer -> {
			if ( Objects.equals ( viewer.getScoreboard ( ) , handle ) ) {
				viewer.setScoreboard ( Bukkit.getScoreboardManager ( ).getMainScoreboard ( ) );
			}
		} );
	}
	
	/**
	 * Unregister the given players (represented by the an {@link UUID}) from the viewers.
	 * <p>
	 * @param viewers the viewers {@link UUID}s to unregister.
	 */
	public void removeViewersByUniqueId ( Collection < UUID > viewers ) {
		removeViewers ( viewers.stream ( ).map ( Bukkit :: getPlayer ).collect ( Collectors.toSet ( ) ) );
	}
	
	/**
	 * Unregister the given players from the viewers.
	 * <p>
	 * @param viewers the {@link Player} viewers to unregister.
	 */
	public void removeViewer ( Player... viewers ) {
		this.removeViewers ( Arrays.asList ( viewers ) );
	}
	
	/**
	 * Unregister the given players (represented by the an {@link UUID}) from the viewers.
	 * <p>
	 * @param viewers the viewers {@link UUID}s to unregister.
	 */
	public void removeViewerByUniqueId ( UUID... viewers ) {
		removeViewers ( Arrays.stream ( viewers ).map ( Bukkit :: getPlayer ).collect ( Collectors.toSet ( ) ) );
	}
	
	/**
	 * Sets the element displayed at the given {@code index} or null if to remove
	 * the text at the given index.
	 * <p>
	 * The method {@link #update()} should be called after this, so viewers will
	 * actually see changes.
	 * <p>
	 * @param index the index where to display the text.
	 * @param element the element to display at the specified index.
	 */
	public void set ( int index , String element ) {
		rangeCheck ( index );
		
		if ( element == null ) {
			this.elements[ index ] = null;
		} else if ( StringUtil.isBlank ( element ) || WHITESPACE_INDICATOR.equals ( element.trim ( ) ) ) {
			this.elements[ index ] = WHITESPACE_INDICATOR;
		} else {
			this.elements[ index ] = limit ( element );
		}
	}
	
	protected String limit ( String element ) {
		return StringUtil.limit ( StringUtil.translateAlternateColorCodes ( element ) , MAX_ELEMENTS_LENGTH );
	}
	
	/**
	 * Appends all the given elements starting from the specified index.
	 * <p>
	 * The method {@link #update()} should be called after this, so viewers will
	 * actually see changes.
	 * <p>
	 * @param begin_index the index where start appending the elements.
	 * @param elements the elements to add.
	 */
	public void addAll ( int begin_index , String... elements ) {
		Validate.notNull ( elements , "the elements cannot be null!" );
		
		rangeCheck ( begin_index );
		
		if ( elements.length > 0 ) {
			int element_index = 0;
			
			for ( int i = begin_index ; i < MAX_ELEMENTS ; i++ ) {
				if ( element_index < elements.length ) {
					String element = elements[ element_index++ ];
					
					if ( element != null ) {
						set ( i , element );
					}
				} else {
					break;
				}
			}
		}
	}
	
	/**
	 * Appends all the specified elements.
	 * <p>
	 * The method {@link #update()} should be called after this, so viewers will
	 * actually see changes.
	 * <p>
	 * @param elements the elements to add.
	 */
	public void addAll ( String... elements ) {
		this.addAll ( 0 , elements );
	}
	
	/**
	 * Appends the specified element at the next available index <strong>if
	 * any</strong>.
	 * <p>
	 * <strong>Note that this method will look for the next available index
	 * starting from the specified index.</strong>
	 * <p>
	 * The method {@link #update()} should be called after this, so viewers will
	 * actually see changes.
	 * <p>
	 * @param index   the index where start looking for the next available
	 *                index.
	 * @param element the element to add.
	 */
	public void add ( int index , String element ) {
		int empty = getNextEmpty ( index );
		
		if ( empty != -1 ) {
			this.set ( empty , element );
		}
	}
	
	/**
	 * Iterates looking for the first available index and returns it (<strong>if
	 * any</strong>), starting from the specified {@code index}.
	 * <p>
	 * By available index we mean an index in which a valid element has not yet
	 * been set.
	 * <p>
	 * @param index the index where start looking for a available indexes.
	 * @return the next available index, or {@code -1} if no available indexes
	 *         were found.
	 */
	public int getNextEmpty ( int index ) {
		this.rangeCheck ( index );
		
		for ( int i = index ; i < MAX_ELEMENTS ; i++ ) {
			if ( elements[ i ] == null ) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * Iterates looking for the first available index and returns it (<strong>if
	 * any</strong>).
	 * <p>
	 * By available index we mean an index in which a valid element has not yet
	 * been set.
	 * <p>
	 * @return the next available index, or {@code -1} if no available indexes
	 *         were found.
	 */
	public int getNextEmpty ( ) {
		return getNextEmpty ( 0 );
	}
	
	/**
	 * Removes the element at the given index.
	 * <p>
	 * This method is the equivalent of using {@code set(index, null)}.
	 * <p>
	 * The method {@link #update()} should be called after this, so viewers will
	 * actually see changes.
	 * <p>
	 * @param index the index of the element to remove.
	 */
	public void remove ( int index ) {
		rangeCheck ( index );
		
		this.set ( index , null );
	}
	
	/**
	 * Removes all the elements on the scoreboard.
	 * <p>
	 * The method {@link #update()} should be called after this, so viewers will
	 * actually see changes.
	 */
	public void clear ( ) {
		for ( int i = 0 ; i < MAX_ELEMENTS ; i++ ) {
			remove ( i );
		}
	}
	
	protected void rangeCheck ( int index ) {
		Validate.isTrue ( ( index < MAX_ELEMENTS ) , "the index must be < " + MAX_ELEMENTS );
	}
}