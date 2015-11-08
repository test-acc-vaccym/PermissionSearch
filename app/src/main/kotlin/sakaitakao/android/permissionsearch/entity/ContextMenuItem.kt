package sakaitakao.android.permissionsearch.entity

import android.view.Menu
import android.view.MenuItem
import android.view.MenuItem.OnMenuItemClickListener

/**
 * コンテキストメニュー項目

 * @author takao
 */
class ContextMenuItem {

    val groupId: Int
    val itemId: Int
    val order: Int
    val titleRes: Int
    val iconRes: Int
    val listener: OnMenuItemClickListener

    /**
     * コンストラクタ
     */
    constructor(itemId: Int, titleRes: Int, iconRes: Int, listener: OnMenuItemClickListener) {
        this.groupId = Menu.NONE
        this.itemId = itemId
        this.order = Menu.NONE
        this.titleRes = titleRes
        this.iconRes = iconRes
        this.listener = listener
    }

    /**
     * @param menu
     * *
     * @return
     */
    fun addToMenu(menu: Menu): MenuItem {
        val menuItem = menu.add(groupId, itemId, order, titleRes)
        if (iconRes != 0) {
            menuItem.setIcon(iconRes)
        }
        menuItem.setOnMenuItemClickListener(listener)
        return menuItem
    }
}
