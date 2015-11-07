package sakaitakao.android.permissionsearch.entity

import android.view.Menu
import android.view.MenuItem
import android.view.MenuItem.OnMenuItemClickListener

/**
 * コンテキストメニュー項目

 * @author takao
 */
class ContextMenuItem {

    var groupId: Int = 0
    var itemId: Int = 0
    var order: Int = 0
    var titleRes: Int = 0
    var iconRes: Int = 0
    var listener: OnMenuItemClickListener? = null

    /**
     * コンストラクタ
     */
    constructor() {
    }

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
