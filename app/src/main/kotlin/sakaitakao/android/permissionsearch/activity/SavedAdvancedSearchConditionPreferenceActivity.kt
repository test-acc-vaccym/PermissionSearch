package sakaitakao.android.permissionsearch.activity


import android.app.*
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

    private val QUERY_REMOVE_SEARCH_CONDITION_DIALOG_TAG = "queryRemoveSearchConditionDialog"

    private var adaptor: SavedAdvancedSearchConditionAdaptor? = null


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


    // -------------------------------------------------------------------------
    // Privates
    // -------------------------------------------------------------------------
    private fun onSavedSearchCondItemClick(position: Int, easySearchInfo: EasySearchInfo) {

        // 消しちゃってもいいのか聞いてみる
        val fm: FragmentManager = fragmentManager
        val dialog = QueryRemoveSearchConditionDialog(position, easySearchInfo)
        dialog.show(fm, QUERY_REMOVE_SEARCH_CONDITION_DIALOG_TAG)
    }

    private fun onQueryRemoveSearchConditionDialogPositive() {

        val fm = fragmentManager
        val dialog: QueryRemoveSearchConditionDialog? =
                fm.findFragmentByTag(QUERY_REMOVE_SEARCH_CONDITION_DIALOG_TAG) as QueryRemoveSearchConditionDialog

        if (dialog != null) {

            // 消す
            val config = Config(this)
            config.removeAdvancedSearchCondition(dialog.position)
            Toast.makeText(
                    this,
                    resources.getString(
                            R.string.query_remove_search_condition_dialog_removed,
                            dialog.easySearchInfo.name),
                    Toast.LENGTH_LONG).show()


            dialog.dismiss()
            fm.beginTransaction().remove(dialog).commit()


            // リストを更新
            val items = config.advancedSearchConditions
            adaptor!!.setItems(items)
            adaptor!!.notifyDataSetChanged()
        }
    }

    private fun onQueryRemoveSearchConditionDialogNegative() {

        val fm = fragmentManager
        val fragment: DialogFragment? = fm.findFragmentByTag(QUERY_REMOVE_SEARCH_CONDITION_DIALOG_TAG) as DialogFragment
        if (fragment != null) {
            fragment.dismiss()
            fm.beginTransaction().remove(fragment).commit()
        }
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
     * 検索条件を消してもいいかいダイアログ
     */
    private inner class QueryRemoveSearchConditionDialog(
            public val position: Int,
            public val easySearchInfo: EasySearchInfo
    ) : DialogFragment() {

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

            val dlgBuilder = AlertDialog.Builder(activity)
            dlgBuilder.setTitle(R.string.query_remove_search_condition_dialog_title)
            dlgBuilder.setMessage(resources.getString(
                    R.string.query_remove_search_condition_dialog_message,
                    easySearchInfo.name))

            // 「はい」ボタン
            dlgBuilder.setPositiveButton(R.string.button_text_yes, { dialog: DialogInterface, which: Int ->
                onQueryRemoveSearchConditionDialogPositive()
            })

            // 「いいえ」ボタン
            dlgBuilder.setNegativeButton(R.string.button_text_no, { dialog: DialogInterface, which: Int ->
                onQueryRemoveSearchConditionDialogNegative()
            })

            return dlgBuilder.create()
        }
    }
}