package sakaitakao.android.permissionsearch.entity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;

/**
 * コンテキストメニュー項目
 * 
 * @author takao
 * 
 */
public class ContextMenuItem {

	public int groupId;
	public int itemId;
	public int order;
	public int titleRes;
	public int iconRes;
	public OnMenuItemClickListener listener;

	/**
	 * コンストラクタ
	 */
	public ContextMenuItem() {
	}

	public ContextMenuItem(int itemId, int titleRes, int iconRes, OnMenuItemClickListener listener) {
		this.groupId = Menu.NONE;
		this.itemId = itemId;
		this.order = Menu.NONE;
		this.titleRes = titleRes;
		this.iconRes = iconRes;
		this.listener = listener;
	}

	/**
	 * @param menu
	 * @return
	 */
	public MenuItem addToMenu(Menu menu) {
		MenuItem menuItem = menu.add(groupId, itemId, order, titleRes);
		if (iconRes != 0) {
			menuItem.setIcon(iconRes);
		}
		menuItem.setOnMenuItemClickListener(listener);
		return menuItem;
	}
}
