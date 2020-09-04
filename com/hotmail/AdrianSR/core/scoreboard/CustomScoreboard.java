package com.hotmail.AdrianSR.core.scoreboard;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import com.hotmail.AdrianSR.core.util.PlayerUtils;
import com.hotmail.AdrianSR.core.util.TextUtils;

/**
 * Represents a Simple Scoreboard that allows developers to add/insert/clear
 * elements to/from its view.
 * <p>
 * @author AdrianSR / Monday 04 November, 2019 / 08:10 PM
 */
public class CustomScoreboard {
	
	public static final int         MAX_NAME_LENGTH = 16;
	public static final int  MAX_DISPLAYNAME_LENGTH = 32;
	public static final int            MAX_ELEMENTS = 15;
	public static final int     MAX_ELEMENTS_LENGTH = 40;
	public static final String WHITESPACE_INDICATOR = "{//}";
	
	private final Set<Player> viewers = new HashSet<>();
	private final String[]   elements = new String[MAX_ELEMENTS];
	
	private final Scoreboard   handle;
	private final Objective objective;
	
	@SuppressWarnings("deprecation")
	public CustomScoreboard ( String name , String... initial_elements ) {
		this.handle    = Bukkit.getScoreboardManager().getNewScoreboard();
		this.objective = handle.registerNewObjective(TextUtils.getShortenString(
				TextUtils.stripColors(TextUtils.getNotNull( UUID.randomUUID().toString() , "" )), MAX_NAME_LENGTH), "milo");
		this.objective.setDisplayName(TextUtils.getShortenString(TextUtils.translateColors ( 
				( name == null || name.isEmpty ( ) ) ? "" + " " : name ), MAX_DISPLAYNAME_LENGTH));
		this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		
		if (initial_elements != null) {
			this.addAll(initial_elements);
			this.update();
		}
	}
	
	/**
	 * Update view.
	 */
	public void update() {
		this.addAll(elements); // re-insert for avoid null/longer elements  
		
		int    whitespace_count = 0;
		String[] final_elements = new String[MAX_ELEMENTS]; 
		for (int i = 0; i < MAX_ELEMENTS; i++) { // translating white spaces
			String element = this.elements[i];
			if (element == null) {
				continue;
			}
			
			if (element.equals(WHITESPACE_INDICATOR)) { // whitespace found!
				final_elements[i] = this.whitespaceStringEquivalent(whitespace_count ++);
				continue;
			}
			
			final_elements[i] = this.elements[i];
		}
		
		/* append elements */
		for (int i = 0; i < MAX_ELEMENTS; i++) {
			int   score_score = ( MAX_ELEMENTS - i );
			String score_name = final_elements[i];
			if (score_name == null) {
				continue;
			}
			
			/* remove old elements with the same score_score but with other name */
			for (String entry : handle.getEntries()) {
				Score old_score = objective.getScore(entry);
				if ( old_score.getScore() == score_score && !score_name.equals(entry) ) {
					handle.resetScores(entry);
				}
			}
			
			Score score = objective.getScore(score_name);
			if (score.getScore() != score_score) { // update element score
				score.setScore(score_score);
			}
		}
		
		/* removing old displayed elements */
		List<String> elements_list = Arrays.asList(final_elements);
		for (String entry : handle.getEntries()) {
			Score           score = objective.getScore(entry);
			int right_score_score = ( MAX_ELEMENTS - elements_list.indexOf(entry) );
			if ( !elements_list.contains(entry) || score.getScore() != right_score_score ) {
				handle.resetScores(entry);
			}
		}
		
		/* check viewers are viewing this */
		getViewers().stream().filter(viewer -> PlayerUtils.isValid(viewer)).forEach(viewer -> {
			viewer.setScoreboard(handle);
		});
	}
	
	protected String whitespaceStringEquivalent(int count) {
		String equivalent = "";
		for (int i = 0; i < count; i++) {
			equivalent += ' ';
		}
		return equivalent;
	}
	
	/**
	 * A {@link Set} of {@link Player}s that are viewing
	 * this Scoreboard.
	 * <p>
	 * @return
	 */
	public Set<Player> getViewers() {
		return Collections.unmodifiableSet(viewers);
	}
	
	/**
	 * Gets the elements displayed.
	 * <p>
	 * Recommended: Call the method {@link #update()} before calling this. 
	 * <p>
	 * @return the elements displayed.
	 */
	public String[] getEntries() {
		return Arrays.copyOf(this.elements, MAX_ELEMENTS); // return cloned
	}
	
	/**
	 * Gets the element displayed at the given index.
	 * Note that the returns element can be and whitespace.
	 * <p>
	 * @param index the index of the element.
	 * @return the element displayed at the given index.
	 */
	public String get(int index) {
		this.rangeCheck(index);
		return this.getEntries()[index];
	}
	
	/**
	 * Display this for the given players. Note that always
	 * that the method {@link #update()} is called, all the viewers
	 * registered will automatically see the changes on the Scoreboard.
	 * <p>
	 * @param viewer the {@link Collection} of {@link Player} viewers.
	 */
	public void addViewers(Collection<Player> viewers) {
		Validate.notNull(viewers, "The player viewers list cannot be null!");
		this.viewers.addAll(viewers
				.stream()
				.filter(other -> other != null) // avoid nulls
				.collect(Collectors.toList()));
		this.update();
	}
	
	/**
	 * Display this for the given players. Note that always
	 * that the method {@link #update()} is called, all the viewers
	 * registered will automatically see the changes on the Scoreboard.
	 * <p>
	 * @param viewer the {@link Player} viewer.
	 */
	public void addViewer(Player... viewer) {
		this.addViewers(Arrays.asList(viewer));
	}

	/**
	 * Unregister the given players from the viewers.
	 * <p>
	 * @param viewer the {@link Collection} of {@link Player} viewers.
	 */
	public void removeViewers(Collection<Player> viewers) {
		Validate.notNull(viewers, "The player viewers list cannot be null!");
		this.viewers.removeAll(viewers);
		viewers.forEach(viewer -> { // set the viewing Scoreboard to the main
			viewer.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard()); 
		});
	}
	
	/**
	 * Unregister the given players from the viewers.
	 * <p>
	 * @param viewer the {@link Player} viewers.
	 */
	public void removeViewer(Player... viewer) {
		this.removeViewers(Arrays.asList(viewer));
	}
	
	/**
	 * Sets the text displayed at the given index, or null
	 * if to remove the text at the given index.
	 * <p>
	 * You should call the method {@link #update()} after calling this,
	 * to see the changes.
	 * <p>
	 * @param index the index where to display the text.
	 * @param text text to display.
	 */
	public void set(int index, String text) {
		rangeCheck(index);
		
		if (text == null) {
			this.elements[index] = null;
		} else if ( text != null && StringUtils.isBlank(text) ) {
			this.elements[index] = WHITESPACE_INDICATOR;
		} else {
			this.elements[index] = TextUtils.getShortenString(TextUtils.translateColors(text), MAX_ELEMENTS_LENGTH);
		}
	}
	
	/**
	 * Appends all the given elements starting from the given 'begin_index'.
	 * <p>
	 * You should call the method {@link #update()} after calling this,
	 * to see the changes.
	 * <p>
	 * @param begin_index the index where start appending the elements.
	 * @param elements
	 */
	public void addAll(int begin_index, String... elements) {
		Validate.notNull(elements, "The elements cannot be null!");
		rangeCheck(begin_index);
		if (elements != null && elements.length > 0) {
			int element_index = 0;
			for (int index = begin_index; index < MAX_ELEMENTS; index++) {
				if (index >= elements.length || element_index >= elements.length) {
					break;
				}
				
				set(index, elements[element_index ++]);
			}
		}
	}
	
	public void addAll(String... elements) {
		this.addAll(0, elements);
	}
	
	/**
	 * Returns the index of the next null/whitespace element, or -1 if no
	 * null/whitespace elements was found. Note that this method will look for the next null/whitespace
	 * starting from the given 'begin_index'.
	 * <p>
	 * You should call the method {@link #update()} before calling this.
	 * <p>
	 * @param begin_index the index where start looking for a null/whitespace element.
	 * @return the index of the next null/whitespace element, or -1 if no
	 *         null/whitespace elements was found.
	 */
	public int getNextEmpty(int begin_index) {
		this.rangeCheck(begin_index);
		for (int i = begin_index; i < MAX_ELEMENTS; i++) {
			if (StringUtils.isBlank(getEntries()[i])) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Returns the index of the next null/whitespace element, or -1 if no
	 * null/whitespace elements was found.
	 * <p>
	 * You should call the method {@link #update()} before calling this.
	 * <p>
	 * @return the index of the next null/whitespace element, or -1 if no
	 *         null/whitespace elements was found.
	 */
	public int getNextEmpty() {
		return getNextEmpty(0);
	}
	
	/**
	 * Appends the given text to the entries of this at the index of the next
	 * null/whitespace element. Note that this method will look for the next null/whitespace
	 * starting from the given 'begin_index'.
	 * <p>
	 * You should call the method {@link #update()} after calling this,
	 * to see the changes.
	 * <p>
	 * @param begin_index the index where start looking for a null/whitespace element.
	 * @param text the text to append.
	 */
	public void add(int begin_index, String text) {
		int empty = getNextEmpty(begin_index);
		if (empty != -1) {
			this.set(empty, text);
		}
	}

	/**
	 * Appends the given text to the entries of this at the index of the next
	 * null/whitespace element.
	 * <p>
	 * You should call the method {@link #update()} after calling this,
	 * to see the changes.
	 * <p>
	 * @param text the text to append.
	 */
	public void add(String text) {
		this.add(0, text);
	}
	
	/**
	 * Removes the element at the given index.
	 * <p>
	 * This method is the equivalent of using {@code set(index, null)}.
	 * <p>
	 * You should call the method {@link #update()} after calling this,
	 * to see the changes.
	 * <p>
	 * @param index the index of the element.
	 */
	public void clear(int index) {
		rangeCheck(index);
		this.set(index, null);
	}
	
	/**
	 * Clears all the entries.
	 * <p>
	 * You should call the method {@link #update()} after calling this,
	 * to see the changes.
	 * <p>
	 * This method is the equivalent of using {@code addAll(0, new String[MAX_ELEMENTS])}.
	 */
	public void clearAll() {
		this.addAll(0, new String[MAX_ELEMENTS]);
	}
	
	protected void rangeCheck(int index) {
		Validate.isTrue( ( index < MAX_ELEMENTS ) , "The index must be < " + MAX_ELEMENTS);
	}
}