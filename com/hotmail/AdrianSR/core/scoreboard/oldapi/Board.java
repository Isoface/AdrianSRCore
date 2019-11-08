package com.hotmail.AdrianSR.core.scoreboard.oldapi;

import java.util.Arrays;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import com.hotmail.AdrianSR.core.util.TextUtils;

/**
 * Represents an anti-
 * flicker Scoreboard
 * that allow constantly
 * updates.
 * <p>
 * @author AdrianSR
 */
public class Board {
	
	/**
	 * Global class values.
	 */
	private static final String SCOREBOARDS_OJECTIVES_TARGET = "MILO";
	private static final int    SCOREBOARDS_MAX_LENGTH       = 15;
	private static final int    SCOREBOARDS_MEX_CHARACTERS   = 40;
	
	/**
	 * Class values.
	 */
	private final UUID        id; // the Player Unique id.
	private final String   title; // the title to display.
	private final String[]  data; // the texts to display.
	private final int     length; // the Scoreboard lines number.

	/**
	 * Construct a new
	 * anti-flicker Scoreboard.
	 * <p>
	 * @param p the Player target.
	 * @param title the title to display.
	 * @param data the texts to display.
	 */
	public Board(final Player p, final String title, final String[] data) {
		// load id and title.
		this.id     = p.getUniqueId();
		this.title  = title;
		
		// set data.
		this.data   = new String[SCOREBOARDS_MAX_LENGTH];
		
		// load data.
		for (int x = 0; x < Math.min(data.length, SCOREBOARDS_MAX_LENGTH); x++) {
			// get and check text in slot.
			final String text = data[x];
			if (text == null) {
				continue;
			}
			
			// load text from slot.
			this.data[x] = TextUtils.getShortenString(
					       TextUtils.translateColors(text), SCOREBOARDS_MEX_CHARACTERS); // translate colors, and check length.
		}
		
		// load length.
		this.length  = this.data.length;
	}
	
	/**
	 * Show this Scoreboard
	 * to the target Player.
	 * <p>
	 * @return true if could be displayed.
	 */
	public boolean display() {
		// get and player.
		final Player p = Bukkit.getPlayer(id);
		if (p == null) {
			return false;
		}
		
		// check scoreboard.
		if (p.getScoreboard() == null 
				|| p.getScoreboard().getObjective(SCOREBOARDS_OJECTIVES_TARGET) == null) {
			// get new scoreboard.
			final Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
			
			// set scoreboard.
			p.setScoreboard(board);
			
			// register Objective.
			final Objective obj = board.registerNewObjective(TextUtils.translateColors(TextUtils.getNotNull(title, "null title")),
					SCOREBOARDS_OJECTIVES_TARGET);
			
			// modify obj.
			obj.setDisplayName(
					TextUtils.translateColors(
					TextUtils.getShortenString(title, SCOREBOARDS_MEX_CHARACTERS))); // 40 characters as max.
			obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		}
		
		// get scoreboard data.
		final Scoreboard board = p.getScoreboard();
		final Objective obj    = board.getObjective(DisplaySlot.SIDEBAR);
		
		// show data.
		for (int i = 0; i < data.length; i++) {
			// check line is not null.
			if (data[i] != null) {
				// check is not already added line.
				if (obj.getScore(data[i]).getScore() != (length - i)) {
					// add line and set score.
					obj.getScore(data[i]).setScore((length - i));
				}
			}
		}
		
		// check entries scores again.
		for (String entry : p.getScoreboard().getEntries()) {
			// check is will be erase.
			boolean toErase = true;
			
			// check is valid added entry.
			for (String element : data) {
				// check is equals to any element
				if (element != null && element.equals(entry) && obj.getScore(entry)
						.getScore() == (length - Arrays.asList(data).indexOf(element))) {
					toErase = false; // set wont erase.
					break;
				}
			}

			// check is will be erase.
			if (toErase) {
				// reset scores.
				p.getScoreboard().resetScores(entry);
			}
		}
		return true;
	}
}