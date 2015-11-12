package sakaitakao.android.permissionsearch.adaptor

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import sakaitakao.android.permissionsearch.R
import sakaitakao.android.permissionsearch.entity.EasySearchInfo

/**
 * 検索条件設定画面の検索条件リストのアダプタ

 * @author takao
 */
class SavedAdvancedSearchConditionAdaptor
/**
 * @param ctx
 * *
 * @param textViewResourceId
 * *
 * @param items
 */
(ctx: Context,
 textViewResourceId: Int,
 private val items: MutableList<EasySearchInfo>)
: ArrayAdapter<EasySearchInfo>(ctx, textViewResourceId, items) {

    private val layoutInflater: LayoutInflater
    private var onItemClickListener: OnItemClickListener? = null

    init {
        this.layoutInflater = ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    /**
     * @param onItemClickListener
     * *            セットする onItemClickListener
     */
    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    /**
     * @param items
     * *            セットする items
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
        var view: View = convertView ?: layoutInflater.inflate(R.layout.saved_advanced_search_condition_item, null)

        // データを取得
        val easySearchInfo = items[position]
        val index = position

        showName(view, easySearchInfo)
        showDetail(view, easySearchInfo)

        // onclick イベント
        view.setOnClickListener({
            onItemClickListener?.onClick(index, easySearchInfo)
        })

        return view
    }

    // -------------------------------------------------------------------------
    // Privates
    // -------------------------------------------------------------------------
    private fun showName(view: View, easySearchInfo: EasySearchInfo) {

        val textView = view.findViewById(R.id.saved_advanced_search_condition_item_name) as TextView
        textView.text = easySearchInfo.name
    }

    private fun showDetail(view: View, easySearchInfo: EasySearchInfo) {

        val textView = view.findViewById(R.id.saved_advanced_search_condition_item_detail) as TextView
        val sb = StringBuilder()
        easySearchInfo.permissionNamePatternList.forEach { name ->
            if (sb.length() > 0) {
                sb.append(", ")
            }
            sb.append(name)
        }
        textView.text = sb.toString()
    }

    // -------------------------------------------------------------------------
    // Interfaces
    // -------------------------------------------------------------------------
    interface OnItemClickListener {
        fun onClick(position: Int, easySearchInfo: EasySearchInfo)
    }
}