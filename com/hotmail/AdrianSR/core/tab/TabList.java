package com.hotmail.AdrianSR.core.tab;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;

import com.hotmail.AdrianSR.core.main.CustomPlugin;
import com.hotmail.AdrianSR.core.tab.versions.universal.UniversalCustomTab;

/**
 * Represents the customizable
 * players tab lists.
 * <p>
 * @author AdrianSR
 */
public interface TabList {
	
	// TODO: Read the description of the class 'UniversalCustomTab' and check if is
	// compatible with all the new Spigot versions.
	
	/**
	 * Tab list text spliter.
	 */
	public static final String TAB_TEXT_SPLITER = "\n";
	
	/**
	 * Set player tab list.
	 * <p>
	 * @param player the player owner.
	 * @param header the tab header.
	 * @param footer the tab footer.
	 * @param plugin the {@link CustomPlugin} instance.
	 * @return the new player custom tab list.
	 */
	public static TabList sendTabList(final Player player, String header[], String footer[],
			final CustomPlugin plugin) {
		/* send the custom tab list, creating it from the class UniversalCustomTab */
		return new UniversalCustomTab(player, header, footer, plugin).setup();
	}
	
	/**
	 * Setup tab list.
	 * <p>
	 * @return this.
	 */
	public TabList setup();
	
	/**
	 * Get {@link Player} tab owner.
	 * <p>
	 * @return tab owner.
	 */
	public Player getPlayer();

	/**
	 * Get Tab header.
	 * <p>
	 * @return tab header.
	 */
	public String[] getHeader();
	
	/**
	 * Set tab header.
	 * <p>
	 * @param header new tab header.
	 * @return this.
	 */
	public TabList setHeader(final String[] header);
	
	/**
	 * Get tab footer.
	 * <p>
	 * @return tab footer.
	 */
	public String[] getFooter();
	
	/**
	 * Set tab footer.
	 * <p>
	 * @param footer new tab footer.
	 * @return this.
	 */
	public TabList setFooter(final String[] footer);
	
	/**
	 * Clar tab header and footer.
	 * <p>
	 * @return this.
	 */
	public TabList clearHeaderFooter();
	
	/**
	 * Add online player to list.
	 * <p>
	 * @param player the online {@link Player}.
	 * @return this.
	 */
	public TabList addPlayer(final Player player);
	
	/**
	 * Add fake player to list.
	 * <p>
	 * @param name fake player name.
	 * @param ping fake player ping.
	 * @return this.
	 */
	public TabList addFakePlayer(final String name, final int ping);
	
	/**
	 * Remove online player from tab.
	 * <p>
	 * @param player the online {@link Player} to remove.
	 * @return this.
	 */
	public TabList removePlayer(final Player player);
	
	/**
	 * Remove fake player.
	 * <p>
	 * @param name fake player name
	 * @return this.
	 */
	public TabList removeFakePlayer(final String name);
	
	/**
	 * Clear online players in list.
	 * <p>
	 * @return this.
	 */
	public TabList clearPlayers();
	
	/**
	 * Clear fake players in list.
	 * <p>
	 * @return this.
	 */
	public TabList clearFakePlayers();
	
	/**
	 * Clear online players list,
	 * fake players list,
	 * header and footer.
	 * <p>
	 * @return this.
	 */
	public TabList clearAll();
	
	/**
	 * Refresh tab list.
	 * <p>
	 * @return this.
	 */
	public TabList refresh();
	
	/**
	 * Get split String array by tab spliter.
	 * <p>
	 * @param string the array to split.
	 * @return splig String array by tab spliter.
	 */
	default String splitByTabSpliter(final String[] string) {
		// check array.
		if (string == null) {
			return "";
		}
		
		// get text split by tab spliter.
		String text = "";
		for (String part : string) {
			text += part + TAB_TEXT_SPLITER;
		}
		
		// remove last spliter.
		text = StringUtils.removeEnd(text, TAB_TEXT_SPLITER);
		return text;
	}
}