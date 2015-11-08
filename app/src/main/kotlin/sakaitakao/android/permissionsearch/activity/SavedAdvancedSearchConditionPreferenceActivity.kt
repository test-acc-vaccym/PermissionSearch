package sakaitakao.android.permissionsearch.activity

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.ListView
import android.widget.Toast
import sakaitakao.android.permissionsearch.Config
import sakaitakao.android.permissionsearch.R
import sakaitakao.android.permissionsearch.adaptor.SavedAdvancedSearchConditionAdaptor
import sakaitakao.android.permissionsearch.adaptor.SavedAdvancedSearchConditionAdaptor.OnItemClickListener
import sakaitakao.android.permissionsearch.entity.EasySearchInfo

/**
 * 設定画面(詳細検索条件)のアクティビティ

 * @author takao
 */
class SavedAdvancedSearchConditionPreferenceActivity : Activity() {

    private var adaptor: SavedAdvancedSearchConditionAdaptor? = null
    private var queryRemoveSearchConditionDialogInfo: QueryRemoveSearchConditionDialogInfo? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.saved_advanced_search_condition)

        val config = Config(this)
        val list = config.advancedSearchConditions
        adaptor = SavedAdvancedSearchConditionAdaptor(this, R.layout.saved_advanced_search_condition_item, list)
        adaptor!!.setOnItemClickListener(OnSavedSearchCondItemClickListener())

        val listView = findViewById(R.id.saved_advanced_search_condition_list) as ListView
        listView.adapter = adaptor
    }

    /*
	 * (非 Javadoc)
	 * 
	 * @see android.app.Activity#onCreateDialog(int)
	 */
    override fun onCreateDialog(id: Int): Dialog? {
        var ret: Dialog? = null
        if (id == DIALOG_ID_QUERY_REMOVE_SEARCH_COND) {
            ret = createQueryRemoveSearchConditionDialog()
        }
        return ret
    }

    // -------------------------------------------------------------------------
    // Privates
    // -------------------------------------------------------------------------
    private fun onSavedSearchCondItemClick(position: Int, easySearchInfo: EasySearchInfo) {

        // 消しちゃってもいいのか聞いてみる
        queryRemoveSearchConditionDialogInfo = QueryRemoveSearchConditionDialogInfo(position, easySearchInfo)

        showDialog(DIALOG_ID_QUERY_REMOVE_SEARCH_COND)
        // onCreateDialog() へ
    }

    private fun createQueryRemoveSearchConditionDialog(): Dialog {

        val dlgBuilder = AlertDialog.Builder(this)
        dlgBuilder.setTitle(R.string.query_remove_search_condition_dialog_title)
        dlgBuilder.setMessage(resources.getString(R.string.query_remove_search_condition_dialog_message,
                queryRemoveSearchConditionDialogInfo!!.easySearchInfo.name))

        // 「はい」ボタン
        dlgBuilder.setPositiveButton(R.string.button_text_yes, { dialog: DialogInterface, which: Int ->
            onQueryRemoveSearchConditionDialogPositive()
        })

        // 「いいえ」ボタン
        dlgBuilder.setNegativeButton(R.string.button_text_no, { dialog: DialogInterface, which: Int ->
            onQueryRemoveSearchConditionDialogNegative()
        })

        return dlgBuilder.show()
    }

    private fun onQueryRemoveSearchConditionDialogPositive() {

        // 消す
        val config = Config(this)
        config.removeAdvancedSearchCondition(queryRemoveSearchConditionDialogInfo!!.position)
        Toast.makeText(
                this,
                resources.getString(
                        R.string.query_remove_search_condition_dialog_removed,
                        queryRemoveSearchConditionDialogInfo!!.easySearchInfo.name),
                Toast.LENGTH_LONG).show()
        removeDialog(DIALOG_ID_QUERY_REMOVE_SEARCH_COND)
        queryRemoveSearchConditionDialogInfo = null

        // リストを更新
        val items = config.advancedSearchConditions
        adaptor!!.setItems(items)
        adaptor!!.notifyDataSetChanged()
    }

    private fun onQueryRemoveSearchConditionDialogNegative() {

        removeDialog(DIALOG_ID_QUERY_REMOVE_SEARCH_COND)
        queryRemoveSearchConditionDialogInfo = null
    }

    // -------------------------------------------------------------------------
    // Classes
    // -------------------------------------------------------------------------
    /**
     * [SavedAdvancedSearchConditionAdaptor] からアイテムのタップ通知を受け取る

     * @author takao
     */
    private inner class OnSavedSearchCondItemClickListener : OnItemClickListener {
        override fun onClick(position: Int, easySearchInfo: EasySearchInfo) {
            onSavedSearchCondItemClick(position, easySearchInfo)
        }
    }

    /**
     * 項目削除時の問い合わせダイアログに渡す情報を管理する。 android 2.2 以降ではいらない子。

     * @author takao
     */
    private class QueryRemoveSearchConditionDialogInfo(
            public val position: Int,
            public val easySearchInfo: EasySearchInfo
    )

    companion object {

        private val DIALOG_ID_QUERY_REMOVE_SEARCH_COND = 1
    }
}