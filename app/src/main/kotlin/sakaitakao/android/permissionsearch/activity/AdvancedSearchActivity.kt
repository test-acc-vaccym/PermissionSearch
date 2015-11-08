package sakaitakao.android.permissionsearch.activity

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.MenuItem.OnMenuItemClickListener
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.CompoundButton
import android.widget.Toast
import kotlinx.android.synthetic.advanced_search.advanced_search_include_system_apps
import kotlinx.android.synthetic.advanced_search.advanced_search_list
import kotlinx.android.synthetic.advanced_search.advanced_search_search
import kotlinx.android.synthetic.dlg_save_condition.dlg_save_condition_name
import sakaitakao.android.permissionsearch.Config
import sakaitakao.android.permissionsearch.R
import sakaitakao.android.permissionsearch.activity.PermissionListActivity.SearchCondition
import sakaitakao.android.permissionsearch.adaptor.AdvancedSearchListAdaptor
import sakaitakao.android.permissionsearch.adaptor.AdvancedSearchListAdaptor.OnItemCheckedChangeListener
import sakaitakao.android.permissionsearch.dialog.SaveAdvancedSearchConditionDialog
import sakaitakao.android.permissionsearch.entity.ContextMenuItem
import sakaitakao.android.permissionsearch.entity.EasySearchInfo
import sakaitakao.android.permissionsearch.entity.PermissionInfoEx
import sakaitakao.android.permissionsearch.model.AppPermissionsFinder
import java.util.*

/**
 * 詳細検索のアクティビティ

 * @author takao
 */
class AdvancedSearchActivity : Activity() {

    private var adaptor: AdvancedSearchListAdaptor? = null

    private val contextMenuItems = arrayOf(
            ContextMenuItem(
                    CONTEXT_MENU_ITEM_ID_SELECT_ALL,
                    R.string.context_menu_item_select_all,
                    R.drawable.ic_menu_select_all,
                    OnMenuItemClickSelectAllListener()),
            ContextMenuItem(
                    CONTEXT_MENU_ITEM_ID_DESELECT_ALL,
                    R.string.context_menu_item_deselect_all,
                    R.drawable.ic_menu_unselect_all,
                    OnMenuItemClickDeselectAllListener()),
            ContextMenuItem(CONTEXT_MENU_ITEM_ID_SAVE_CONDITION,
                    R.string.context_menu_item_save_condition,
                    android.R.drawable.ic_menu_save,
                    OnMenuItemClickSaveConditionListener())
    )

    /*
	 * (非 Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.advanced_search)

        val list = getPermissionInfoExList(false)

        showList(list)
        setupIncludeSystemApps()
        setupSearchButton()
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        for (contextMenuItem in contextMenuItems) {
            contextMenuItem.addToMenu(menu)
        }
        return super.onCreateOptionsMenu(menu)
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPrepareOptionsMenu(android.view.Menu)
	 */
    override fun onPrepareOptionsMenu(menu: Menu): Boolean {

        val menuItemSelectAll = menu.findItem(CONTEXT_MENU_ITEM_ID_SELECT_ALL)
        val menuItemDeselectAll = menu.findItem(CONTEXT_MENU_ITEM_ID_DESELECT_ALL)
        val menuItemSaveCondition = menu.findItem(CONTEXT_MENU_ITEM_ID_SAVE_CONDITION)
        val checkCount = adaptor!!.checkedPermission.size

        // チェックの件数によって表示する項目を変える
        menuItemSelectAll.setVisible((checkCount == 0))
        menuItemDeselectAll.setVisible((checkCount > 0))
        menuItemSaveCondition.setVisible((checkCount > 0))
        return super.onPrepareOptionsMenu(menu)
    }

    /*
	 * (非 Javadoc)
	 * 
	 * @see android.app.Activity#onCreateDialog(int)
	 */
    override fun onCreateDialog(id: Int): Dialog? {
        var dialog: Dialog? = null
        if (id == DIALOG_ID_SAVE_CONDITION) {
            dialog = createSaveConditionDialog()
        }
        return dialog
    }

    // -------------------------------------------------------------------------
    // Privates
    // -------------------------------------------------------------------------
    private fun getPermissionInfoExList(includeSystemApps: Boolean): List<PermissionInfoEx> {

        val condition = AppPermissionsFinder.SearchCondition()
        condition.includeSystemApps = includeSystemApps
        condition.permissionNamePatternList = null
        condition.protectionLevelSet = null

        val packageManager = packageManager
        val aph = AppPermissionsFinder()
        return aph.getPermissionInfoExList(packageManager, condition)
    }

    private fun showList(list: List<PermissionInfoEx>) {

        adaptor = AdvancedSearchListAdaptor(this, R.layout.advanced_search_list_item, list.toArrayList())
        adaptor!!.setOnItemCheckedChangeListener(object : OnItemCheckedChangeListener {
            override fun onItemCheckedChange() {
                onListItemCheckedChange()
            }
        })
        advanced_search_list.adapter = adaptor
    }

    private fun setupIncludeSystemApps() {

        advanced_search_include_system_apps.setOnCheckedChangeListener({
            compoundButton: CompoundButton, flag: Boolean ->
            onIncludeSystemAppsCheckedChange(flag)
        })
    }

    /**
     * 検索ボタンを設定する
     */
    private fun setupSearchButton() {

        val button = advanced_search_search
        changeStateSearchButton(button)
        button.setOnClickListener({ onSearchClick() })
    }

    /**
     * リストの項目のチェック状態が変わったら、呼び出される。
     */
    private fun onListItemCheckedChange() {
        changeStateSearchButton(null)
    }

    /**
     * 「プリインストールアプリを含む」チェックボックスの状態が変わったら呼び出される。

     * @param flag
     * *            状態
     */
    private fun onIncludeSystemAppsCheckedChange(flag: Boolean) {

        // リストを取得
        val list = getPermissionInfoExList(flag)

        // リストを更新
        adaptor!!.setItems(list)
        adaptor!!.notifyDataSetChanged()
    }

    /**
     * 「検索」ボタンの押下処理
     */
    private fun onSearchClick() {

        // /system アプリを含むボタン
        val includeSysAppsCheckBox = advanced_search_include_system_apps

        // 検索条件
        val searchCondition = SearchCondition()
        searchCondition.includeSystemApps = includeSysAppsCheckBox.isChecked
        searchCondition.permissionNamePatternList = ArrayList(adaptor!!.checkedPermission)
        searchCondition.protectionLevelList = null

        // パーミッション一覧へgo
        val intent = Intent(this, PermissionListActivity::class.java)
        PermissionListActivity.setSearchCondition(intent, searchCondition)
        startActivity(intent)
    }

    /**
     * すべてチェックする

     * @return
     */
    private fun onOptionsItemSelectAllSelected(): Boolean {
        adaptor!!.checkAll()
        adaptor!!.notifyDataSetChanged()
        return true
    }

    /**
     * すべてチェック解除する

     * @return
     */
    private fun onOptionsItemDeselectAllSelected(): Boolean {
        adaptor!!.uncheckAll()
        adaptor!!.notifyDataSetChanged()
        return true
    }

    /**
     * 条件を保存

     * @return
     */
    private fun onOptionsItemSaveConditionSelected(): Boolean {
        showDialog(DIALOG_ID_SAVE_CONDITION)
        return true
    }

    /**
     * 検索ボタンの状態を変える

     * @param button
     * *            検索ボタン
     */
    private fun changeStateSearchButton(button: Button?) {
        var btn = button ?: advanced_search_search
        btn.isEnabled = adaptor!!.checkedPermission.size > 0
    }

    /**
     * 検索条件を保存するダイアログを生成する

     * @return
     */
    private fun createSaveConditionDialog(): Dialog {

        val dialog = SaveAdvancedSearchConditionDialog(this)
        // OK ボタンが押された時の処理
        dialog.setOkButtonOnClickListener(object : OnClickListener {
            override fun onClick(view: View) {
                onSaveCondition(dialog)
            }
        })
        // キャンセルされた時の処理
        dialog.setOnCancelListener({ onSaveConditionDialogCanceled() })
        return dialog
    }

    /**
     * 検索条件を保存するダイアログで OK ボタンが押されたら

     * @param dialog
     */
    private fun onSaveCondition(dialog: Dialog) {

        val editName = dlg_save_condition_name
        val name = editName.text.toString()
        if (name.length == 0) {
            Toast.makeText(this, R.string.name_is_empty, Toast.LENGTH_LONG).show()
            return
        }

        // ほぞんしちゃう
        val easySearchInfo = EasySearchInfo()
        easySearchInfo.name = name
        easySearchInfo.permissionNamePatternList = ArrayList(adaptor!!.checkedPermission)

        val config = Config(this)
        config.addAdvancedSearchCondition(easySearchInfo)

        // ダイアログを完全に消す。(dismiss では再利用されちゃう)
        removeDialog(DIALOG_ID_SAVE_CONDITION)

        // トーストでお知らせ
        Toast.makeText(this, resources.getString(R.string.advanced_search_cond_saved, name), Toast.LENGTH_LONG).show()
    }

    /**
     * 検索条件を保存するダイアログで back キーが押されたら
     */
    protected fun onSaveConditionDialogCanceled() {
        // ダイアログを完全に消す。
        removeDialog(DIALOG_ID_SAVE_CONDITION)
    }

    // -------------------------------------------------------------------------
    // Classes
    // -------------------------------------------------------------------------
    /**
     * コンテキストメニューの「すべて選択」をハンドルする

     * @author takao
     */
    private inner class OnMenuItemClickSelectAllListener : OnMenuItemClickListener {
        override fun onMenuItemClick(item: MenuItem): Boolean {
            return onOptionsItemSelectAllSelected()
        }
    }

    /**
     * コンテキストメニューの「すべて選択解除」をハンドルする

     * @author takao
     */
    private inner class OnMenuItemClickDeselectAllListener : OnMenuItemClickListener {
        override fun onMenuItemClick(item: MenuItem): Boolean {
            return onOptionsItemDeselectAllSelected()
        }
    }

    /**
     * @author takao
     */
    private inner class OnMenuItemClickSaveConditionListener : OnMenuItemClickListener {
        override fun onMenuItemClick(item: MenuItem): Boolean {
            return onOptionsItemSaveConditionSelected()
        }
    }

    companion object {

        private val CONTEXT_MENU_ITEM_ID_SELECT_ALL = Menu.FIRST + 1
        private val CONTEXT_MENU_ITEM_ID_DESELECT_ALL = Menu.FIRST + 2
        private val CONTEXT_MENU_ITEM_ID_SAVE_CONDITION = Menu.FIRST + 3
        private val DIALOG_ID_SAVE_CONDITION = 0
    }
}