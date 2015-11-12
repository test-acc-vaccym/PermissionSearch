package sakaitakao.android.permissionsearch.adaptor

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import sakaitakao.android.permissionsearch.R
import sakaitakao.android.permissionsearch.activity.PermissionListActivity
import sakaitakao.android.permissionsearch.activity.PermissionListActivity.SearchCondition
import sakaitakao.android.permissionsearch.entity.EasySearchInfo

/**
 * かんたん検索画面の項目リストのアダプタ

 * @author takao
 */
class EasySearchListAdaptor
/**
 * @param ctx
 * *
 * @param textViewResourceId
 * *
 * @param items
 */
(private val ctx: Context,
 textViewResourceId: Int,
 private val items: MutableList<EasySearchInfo>,
 var includeSystemApps: Boolean)
: ArrayAdapter<EasySearchInfo>(ctx, textViewResourceId, items) {

    private val layoutInflater: LayoutInflater

    init {
        this.layoutInflater = ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    /**
     * @param items
     */
    fun setItems(items: List<EasySearchInfo>) {
        this.items.clear()
        this.items.addAll(items)
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        // リスト項目の View を生成。
        var view: View = convertView ?: layoutInflater.inflate(R.layout.easy_search_list_item, null)

        // データを取得
        val easySearchInfo = items[position]
        showName(view, easySearchInfo)
        showDescription(view, easySearchInfo)

        // onclick イベント
        view.setOnClickListener({
            // Permission 一覧へ遷移
            val searchCondition = SearchCondition(
                    includeSystemApps,
                    easySearchInfo.permissionNamePatternList,
                    easySearchInfo.protectionLevelList
            )

            val intent = Intent(ctx, PermissionListActivity::class.java)
            PermissionListActivity.setSearchCondition(intent, searchCondition)
            ctx.startActivity(intent)
        })

        return view
    }

    // -------------------------------------------------------------------------
    // Privates
    // -------------------------------------------------------------------------
    /**
     * @param view
     * *
     * @param easySearchInfo
     */
    private fun showDescription(view: View, easySearchInfo: EasySearchInfo) {

        val textView = view.findViewById(R.id.easy_search_list_item_primary) as TextView
        textView.text = easySearchInfo.name
    }

    /**
     * @param view
     * *
     * @param easySearchInfo
     */
    private fun showName(view: View, easySearchInfo: EasySearchInfo) {

        val textView = view.findViewById(R.id.easy_search_list_item_secondary) as TextView
        textView.text = easySearchInfo.description
    }
}