package sakaitakao.android.permissionsearch.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.MenuItem.OnMenuItemClickListener
import android.widget.CheckBox
import android.widget.ListView
import sakaitakao.android.permissionsearch.R
import sakaitakao.android.permissionsearch.adaptor.EasySearchListAdaptor
import sakaitakao.android.permissionsearch.entity.ContextMenuItem
import sakaitakao.android.permissionsearch.entity.EasySearchInfo
import sakaitakao.android.permissionsearch.model.EasySearchInfoProvider
import java.util.*

/**
 * かんたん検索画面のアクティビティ

 * @author takao
 */
class EasySearchActivity : Activity() {

    private var adaptor: EasySearchListAdaptor? = null

    private val contextMenuItems = arrayOf(
            ContextMenuItem(
                    CONTEXT_MENU_ITEM_ID_GOTO_ADVANCED_SEARCH,
                    R.string.context_menu_item_goto_advanced_search,
                    R.drawable.ic_menu_adv_search,
                    OptionsItemGoToAdvancedSearchSelected()),
            ContextMenuItem(
                    CONTEXT_MENU_ITEM_ID_PREFERENCE,
                    R.string.context_menu_item_preference,
                    android.R.drawable.ic_menu_manage,
                    OptionsItemPreferenceSelected())
    )

    /*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.easy_search)

        // リストの内容は onResume で取得する。
        // 詳細検索で条件を保存したり、設定画面で条件を消した場合も、リストにすぐに反映されるようにするため。
        adaptor = EasySearchListAdaptor(this, R.layout.easy_search_list_item, ArrayList<EasySearchInfo>(), DEFAULT_INCLUDE_SYSTEM_APPS)

        // リストを表示
        showList(adaptor!!)

        // チェックボックス
        showIncludeSystemApps()
    }

    /*
	 * (非 Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
    override fun onResume() {
        super.onResume()

        // 一覧に表示する項目を取得
        val easySearchInfoProvider = EasySearchInfoProvider()
        val easySearchInfoList = easySearchInfoProvider.getEasySearchInfoListForEasySearchActivity(this, resources)

        // リストに反映
        adaptor!!.setItems(easySearchInfoList)
        adaptor!!.notifyDataSetChanged()
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        contextMenuItems.forEach {
            it.addToMenu(menu)
        }
        return super.onCreateOptionsMenu(menu)
    }

    // -------------------------------------------------------------------------
    // Privates
    // -------------------------------------------------------------------------
    private fun showList(esla: EasySearchListAdaptor) {

        val listView = findViewById(R.id.easy_search_list) as ListView
        listView.adapter = esla
    }

    private fun showIncludeSystemApps() {

        val checkBox = findViewById(R.id.easy_search_include_system_apps) as CheckBox
        checkBox.isChecked = adaptor!!.includeSystemApps
        checkBox.setOnCheckedChangeListener({ buttonView, isChecked ->
            adaptor!!.includeSystemApps = isChecked
        })
    }

    /**
     * コンテキストメニューの「詳細検索」の処理

     * @return
     */
    private fun onOptionsItemGoToAdvancedSearchSelected(): Boolean {
        startActivity(Intent(this, AdvancedSearchActivity::class.java))
        return false
    }

    /**
     * コンテキストメニューの「設定」の処理

     * @return
     */
    private fun onOptionsItemPreferenceSelected(): Boolean {
        startActivity(Intent(this, MainPreferenceActivity::class.java))
        return false
    }

    // -------------------------------------------------------------------------
    // Classes
    // -------------------------------------------------------------------------
    /**
     * コンテキストメニューの「詳細検索」の処理

     * @author takao
     */
    private inner class OptionsItemGoToAdvancedSearchSelected : OnMenuItemClickListener {
        override fun onMenuItemClick(item: MenuItem): Boolean {
            return onOptionsItemGoToAdvancedSearchSelected()
        }
    }

    private inner class OptionsItemPreferenceSelected : OnMenuItemClickListener {
        override fun onMenuItemClick(item: MenuItem): Boolean {
            return onOptionsItemPreferenceSelected()
        }
    }

    companion object {

        private val DEFAULT_INCLUDE_SYSTEM_APPS = false

        private val CONTEXT_MENU_ITEM_ID_GOTO_ADVANCED_SEARCH = Menu.FIRST + 1
        private val CONTEXT_MENU_ITEM_ID_PREFERENCE = Menu.FIRST + 2
    }
}