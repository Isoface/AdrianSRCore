package com.hotmail.AdrianSR.core.scoreboard.oldapi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

public class BoardAPI {

	public abstract class Board {
		
		public abstract void startDisplay(Player p);

		public abstract void stopDisplay(Player p);

		public void startDisplay(Collection<Player> players) {
			for (Player p : players) {
				this.startDisplay(p);
			}
		}
	}

	public class PagedBoard extends AutomaticBoard {
		private HashMap<BoardPage, Integer> pages;
		private int count, currentPageId;

		public PagedBoard() {
			super(1);
			this.pages = new HashMap<BoardPage, Integer>();
			this.count = 0;
		}

		public void addPage(BoardPage page, int ticks) {
			this.pages.put(page, ticks);
		}

		public void removePage(BoardPage page) {
			this.pages.remove(page);
		}

		@Override
		public void update(Player p) {
			getPage().update(p);
		}

		public BoardPage getPage() {
			return new ArrayList<BoardPage>(pages.keySet()).get(currentPageId);
		}

		@Override
		public void run() {
			super.run();
			if (++this.count >= pages.get(getPage())) {
				this.count = 0;
				this.currentPageId++;

				if (this.currentPageId >= this.pages.size()) {
					this.currentPageId = 0;
				}
			}
		}
	}

	public class StaticSidebarBoard extends Board {
		
		private SidebarBoardType type;
		private Object           data;

		public StaticSidebarBoard(String... elements) {
			this.data = elements;
			this.type = SidebarBoardType.UNRANKED;
		}

		public StaticSidebarBoard(String title, HashMap<String, Integer> elements) {
			this.data = new Object[] { title, elements };
			this.type = SidebarBoardType.UNRANKED;
		}

		@SuppressWarnings("unchecked")
		@Override
		public void startDisplay(Player p) {
			switch (this.type) {
			case RANKED:
				ScoreboardUtil.rankedSidebarDisplay(p, (String) ((Object[]) data)[0],
						(HashMap<String, Integer>) ((Object[]) data)[1]);
				return;
			case UNRANKED:
				ScoreboardUtil.unrankedSidebarDisplay(p, (String[]) data);
				return;
			}
		}

		@Override
		public void stopDisplay(Player p) {
			p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
		}
	}

	public static enum SidebarBoardType {
		RANKED(), UNRANKED();
	}

	public abstract class UpdatableBoard extends Board {
		private List<Player> players;

		public UpdatableBoard() {
			this.players = new ArrayList<Player>();
		}

		@Override
		public void startDisplay(Player p) {
			players.add(p);
			update(p);
		}

		@Override
		public void stopDisplay(Player p) {
			players.remove(p);
			p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
		}

		public void update() {
			for (Player p : players)
				update(p);
		}

		protected abstract void update(Player p);
	}

	public static class ScoreboardUtil {
		
		private ScoreboardUtil() {
			// nothing by default.
		}

		public static boolean unrankedSidebarDisplay(Player p, String[] elements) {
			return unrankedSidebarDisplay(Arrays.asList(p), elements);
		}
		
		private static String[] limited(String[] elements) {
			String[] limited = new String[16];
			if (elements.length < limited.length) {
				return elements;
			}
			
			for (int i = 0; i < limited.length; i++) {
				limited[i] = elements[i];
			}
			return limited;
		}

		public static boolean unrankedSidebarDisplay(Collection<Player> players, String[] lines) {
			String[] elements = limited(lines);
			if (lines.length == 0) {
				return false;
			}
			
			/* avoid the null name */
			if (elements[0] == null) {
				elements[0] = "Unamed board";
			}

			/* limiting title length */
			if (elements[0].length() > 40) {
				elements[0] = elements[0].substring(0, 40);
			}
			
			/* limiting the length of the the elements */
			for (int i = 1; i < elements.length; i++) {
				if (elements[i] != null) {
					if (elements[i].length() > 40) {
						elements[i] = elements[i].substring(0, 40); /* set 40 characters at maximum */
					}
				}
			}
			
			try {
				for (Player p : players) {
					final String objName = p.getUniqueId().toString().substring(0, 16);
					if (p.getScoreboard() == null || p.getScoreboard().getObjective(objName) == null) {
						/* giving a new scoreboard */
						p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
						
						/* set a new objective */
						p.getScoreboard().registerNewObjective(objName, "dummy").setDisplaySlot(DisplaySlot.SIDEBAR);
					}
					
					// get Objectives.
					Objective objFromName = p.getScoreboard().getObjective(objName);
					Objective objFromSlot = p.getScoreboard().getObjective(DisplaySlot.SIDEBAR);

					// set scoreboard title.
					objFromSlot.setDisplayName(elements[0]);

					// add lines.
					for (int i = 1; i < elements.length; i++) {
						if (elements[i] == null) {
							continue;
						}
						
						// check is not already added line.
						if (objFromSlot.getScore(elements[i]).getScore() != (16 - i)) {
							// add line and set score.
							objFromSlot.getScore(elements[i]).setScore( (16 - i) );

							// check entries scores.
							for (String entry : p.getScoreboard().getEntries()) {
								// check have the same score.
								if ( objFromName.getScore(entry).getScore() == (16 - i) ) {
									// check have the same text.
									if (entry != elements[i]) {
										// reset scores if does not have the same text.
										p.getScoreboard().resetScores(entry);
									}
								}
							}
						}
					}

					// removing old lines on scoreboard
					for (String entry : p.getScoreboard().getEntries()) {
						// check is will be erase.
						boolean toErase = true;
						
						// check is valid added entry.
						for (String element : elements) {
							// check is equals to any element
							if (element != null && element.equals(entry) && objFromName.getScore(entry)
									.getScore() == (16 - Arrays.asList(elements).indexOf(element))) {
								toErase = false; // set wont erase.
								break;
							}
						}

						// erase
						if (toErase) {
							p.getScoreboard().resetScores(entry);
						}
					}
				}
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}

		public static boolean rankedSidebarDisplay(Player p, String title, HashMap<String, Integer> elements) {
			return rankedSidebarDisplay(Arrays.asList(p), title, elements);
		}

		public static boolean rankedSidebarDisplay(Collection<Player> players, String title,
				HashMap<String, Integer> elements) {
			if (title == null) {
				title = "Unamed board";
			}

			if (title.length() > 32) {
				title = title.substring(0, 32);
			}

			while (elements.size() > 15) {
				String minimumKey = (String) elements.keySet().toArray()[0];
				int minimum = elements.get(minimumKey);

				for (String string : elements.keySet()) {
					if (elements.get(string) < minimum
							|| (elements.get(string) == minimum && string.compareTo(minimumKey) < 0)) {
						minimumKey = string;
						minimum = elements.get(string);
					}
				}

				elements.remove(minimumKey);
			}

			for (String string : new ArrayList<String>(elements.keySet())) {
				if (string != null) {
					if (string.length() > 40) {
						int value = elements.get(string);
						elements.remove(string);
						elements.put(string.substring(0, 40), value);
					}
				}
			}

			try {
				for (Player p : players) {
					if (p.getScoreboard() == null
							|| p.getScoreboard().getObjective(p.getUniqueId().toString().substring(0, 16)) == null) {
						p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
						p.getScoreboard().registerNewObjective(p.getUniqueId().toString().substring(0, 16), "dummy");
						p.getScoreboard().getObjective(p.getUniqueId().toString().substring(0, 16))
								.setDisplaySlot(DisplaySlot.SIDEBAR);
					}

					p.getScoreboard().getObjective(DisplaySlot.SIDEBAR).setDisplayName(title);

					for (String string : elements.keySet()) {
						if (p.getScoreboard().getObjective(DisplaySlot.SIDEBAR).getScore(string).getScore() != elements
								.get(string)) {
							p.getScoreboard().getObjective(DisplaySlot.SIDEBAR).getScore(string)
									.setScore(elements.get(string));
						}
					}

					for (String string : new ArrayList<String>(p.getScoreboard().getEntries())) {
						if (!elements.keySet().contains(string)) {
							p.getScoreboard().resetScores(string);
						}
					}
				}

				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
	}

	public interface BoardPage {
		public void update(Player p);
	}

	public abstract class AutomaticBoard extends UpdatableBoard implements Runnable {
		private int taskId, delay;

		public AutomaticBoard(int delay) {
			this.taskId = -1;
			this.delay = delay;
		}

		public void start(Plugin plugin) {
			this.taskId = Bukkit.getScheduler().runTaskTimer(plugin, this, 0, this.delay).getTaskId();
		}

		public void stop() {
			if (this.taskId != -1) {
				Bukkit.getScheduler().cancelTask(this.taskId);
				this.taskId = -1;
			}
		}

		@Override
		public void run() {
			this.update();
		}
	}
}
