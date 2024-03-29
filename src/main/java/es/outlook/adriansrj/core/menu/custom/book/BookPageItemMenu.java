package es.outlook.adriansrj.core.menu.custom.book;

import java.util.Arrays;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import es.outlook.adriansrj.core.menu.Item;
import es.outlook.adriansrj.core.menu.ItemMenu;
import es.outlook.adriansrj.core.menu.action.ItemClickAction;
import es.outlook.adriansrj.core.menu.action.ItemMenuClickAction;
import es.outlook.adriansrj.core.menu.size.ItemMenuSize;

public class BookPageItemMenu extends ItemMenu {
	
	private int page_number;

	public BookPageItemMenu(BookItemMenu book, Item... contents) {
		super(book.getTitle(), book.getPagesSize(), book, contents);
	}
	
	@Override
	public final String getTitle() {
		return super.getTitle();
	}
	
	public final ItemMenuSize getPageSize() {
		return this.getBookMenu().getPagesSize();
	}

	@Override @Deprecated
	public final ItemMenuSize getSize() {
		return getPageSize();
	}
	
	@Override
	public final Item[] getContents() { // don't return the buttons
		return Arrays.copyOfRange(super.getContents(), 0, ItemMenuSize.beforeTo(getPageSize()).getSize());
	}
	
	/**
	 * Returns the {@link BookItemMenu} owner of this.
	 */
	public final BookItemMenu getBookMenu() {
		return (BookItemMenu) super.getParent();
	}
	
	/**
	 * Returns the page next to this page on the book, or null if this is the last
	 * page.
	 * <p>
	 * @return the {@link BookPageItemMenu} next to this page on the book, or null
	 *         if this is the last page.
	 */
	public final BookPageItemMenu getNextBookPage() {
		return ( page_number + 1 ) < getBookMenu().getPages().length ? getBookMenu().getPage(page_number + 1) : null;
	}
	
	/**
	 * Returns the page before this page on the book, or null if this is the first
	 * page.
	 * <p>
	 * @return the {@link BookPageItemMenu} before this page on the book, or null if
	 *         this is the first page.
	 */
	public final BookPageItemMenu getBeforeBookPage() {
		return ( page_number - 1 ) >= 0 ? getBookMenu().getPage(page_number - 1) : null;
	}
	
	/**
	 * Returns the number of this page on the book.
	 * <p>
	 * @return the number of this {@link BookPageItemMenu} on the book.
	 */
	public final int getPageNumber() {
		return page_number;
	}
	
	/**
	 * Returns the {@link BookItemMenu} owner of this.
	 */
	@Override @Deprecated
	public final BookItemMenu getParent() {
		return getBookMenu();
	}
	
	@Override
	public boolean hasParent() {
		return true;
	}
	
	@Override
	public final BookPageItemMenu setTitle(String title) {
		this.getBookMenu().setTitle(title);
		return this;
	}
	
//	@Override @Deprecated
//	public final BookPageItemMenu setSize(ItemMenuSize size) {
//		return this;
//	}
	
	@Override
	public Inventory open(Player player) {
		return this.getBookMenu().applyBarButtons(super.open(player));
	}
	
	public ItemMenu onClick(final ItemMenuClickAction action) {
		if (this.getHandler() == null) {
			throw new UnsupportedOperationException("This menu has never been registered!");
		}
		
		if (!action.isRightClick() && !action.isLeftClick()) {
			return this;
		}
		
		if (action.getSlot() < 0) {
			return this;
		}
		
		ItemClickAction sub_action = new ItemClickAction(this, action.getInventoryView(),
				action.getClickType(), action.getInventoryAction(), action.getSlotType(), action.getSlot(),
				action.getRawSlot(), action.getCurrentItem(), action.getHotbarKey(), false, false, false);
		
		/* calling click method of the clicked item */
		if (action.getSlot() >= getContents().length) {
			int bar_button_slot = action.getSlot() - getContents().length;
			if (bar_button_slot < getBookMenu().getBarButtons().length) { // clicking a bar button
				Item bar_button = this.getBookMenu().getBarButton(bar_button_slot);
				if (bar_button == null) {
					return this;
				}
				bar_button.onClick(sub_action);
			}
		} else {
			Item item = getItem ( action.getSlot ( ) );

			if ( item != null ) {
				item.onClick ( sub_action );
			}
		}
		
		if (sub_action.isWillUpdate()) {
			update(action.getPlayer());
		} else if (sub_action.isWillClose() || sub_action.isWillGoBack()) {
			getHandler().delayedClose(action.getPlayer(), 1);
		}
		return this;
	}
	
	final BookPageItemMenu setPageNumber(int number) {
		this.page_number = number;
		return this;
	}
}