package sakaitakao.android.permissionsearch.adaptor

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.CompoundButton.OnCheckedChangeListener
import android.widget.TextView
import sakaitakao.android.permissionsearch.R
import sakaitakao.android.permissionsearch.entity.PermissionInfoEx
import sakaitakao.android.permissionsearch.util.PermissionInfoUtil
import java.util.*

/**
 * 詳細検索画面の権限リストのアダプタ

 * @author takao
 */
class AdvancedSearchListAdaptor
/**
 * @param context
 * *
 * @param textViewResourceId
 * *
 * @param items
 */
(context: Context, textViewResourceId: Int, private val items: MutableList<PermissionInfoEx>) : ArrayAdapter<PermissionInfoEx>(context, textViewResourceId, items) {
    private val resources: Resources
    private val layoutInflater: LayoutInflater
    private val packageManager: PackageManager
    private var checkedPermissionSet: MutableSet<String>? = null
    private var onItemCheckedChangeListener: OnItemCheckedChangeListener? = null

    init {
        this.resources = context.resources
        this.layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        this.packageManager = context.packageManager
        this.checkedPermissionSet = HashSet<String>()
        this.onItemCheckedChangeListener = null
    }

    /**
     * リストを設定する

     * @param items
     */
    fun setItems(items: List<PermissionInfoEx>) {
        this.items.clear()
        this.items.addAll(items)
        removeExtinctFromCheckedPermission()
    }

    /**
     * @return
     */
    val checkedPermission: Set<String>
        get() = checkedPermissionSet!!

    /**
     * @param listener
     */
    fun setOnItemCheckedChangeListener(listener: OnItemCheckedChangeListener) {
        this.onItemCheckedChangeListener = listener
    }

    /**

     */
    fun checkAll() {
        for (permissionInfoEx in items) {
            checkedPermissionSet!!.add(permissionInfoEx.permissionInfo!!.name)
        }
    }

    /**

     */
    fun uncheckAll() {
        checkedPermissionSet!!.clear()
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        // リスト項目の View を生成。
        var view: View = convertView ?: layoutInflater.inflate(R.layout.advanced_search_list_item, null)

        // データを取得
        val permissionInfoEx = items[position]

        showPermissionLabel(view, permissionInfoEx)
        showPermissionName(view, permissionInfoEx)

        // このビュー全体でチェックボックスを設定できるようにする
        view.setOnClickListener(object : OnClickListener {
            override fun onClick(v: View) {
                val checkBox = v.findViewById(R.id.advanced_search_list_item_check) as CheckBox
                checkBox.isChecked = !checkBox.isChecked
            }
        })

        setupCheckBox(view, permissionInfoEx)

        return view
    }

    // -------------------------------------------------------------------------
    // Privates
    // -------------------------------------------------------------------------
    /**
     * パーミッションラベル

     * @param view
     * *
     * @param permissionInfoEx
     */
    private fun showPermissionLabel(view: View, permissionInfoEx: PermissionInfoEx) {

        val textView = view.findViewById(R.id.advanced_search_list_item_permission_desc) as TextView
        val text = PermissionInfoUtil.getPermissionLabel(packageManager, resources, permissionInfoEx)
        textView.text = text
    }

    /**
     * パーミッション名

     * @param view
     * *
     * @param permissionInfoEx
     */
    private fun showPermissionName(view: View, permissionInfoEx: PermissionInfoEx) {

        val textView = view.findViewById(R.id.advanced_search_list_item_permission_name) as TextView
        textView.text = permissionInfoEx.permissionInfo!!.name
    }

    /**
     * チェックボックス

     * @param view
     * *
     * @param permissionInfoEx
     */
    private fun setupCheckBox(view: View, permissionInfoEx: PermissionInfoEx) {

        val checkBox = view.findViewById(R.id.advanced_search_list_item_check) as CheckBox

        val permissionName = permissionInfoEx.permissionInfo!!.name
        checkBox.setTag(ITEM_TAG_PERMISSION_NAME, permissionName)

        // チェックの初期状態
        checkBox.isChecked = checkedPermissionSet!!.contains(permissionName)

        // チェック状態が変わったら、リスナに通知する。
        checkBox.setOnCheckedChangeListener(object : OnCheckedChangeListener {
            override fun onCheckedChanged(compoundButton: CompoundButton, flag: Boolean) {
                onCheckedChange(compoundButton, flag)
            }
        })
    }

    private fun onCheckedChange(compoundButton: CompoundButton, flag: Boolean) {

        val permissionName = compoundButton.getTag(ITEM_TAG_PERMISSION_NAME) as String
        if (flag) {
            checkedPermissionSet!!.add(permissionName)
        } else {
            checkedPermissionSet!!.remove(permissionName)
        }
        if (onItemCheckedChangeListener != null) {
            onItemCheckedChangeListener!!.onItemCheckedChange()
        }
    }

    /**
     * チェックされたパーミッションがリストになければ消す。
     */
    private fun removeExtinctFromCheckedPermission() {
        // items に存在するものだけで Set を作り直す。
        val newSet = HashSet<String>()
        for (permissionInfoEx in items) {
            val permissionName = permissionInfoEx.permissionInfo!!.name
            if (checkedPermissionSet!!.contains(permissionName)) {
                newSet.add(permissionName)
            }
        }
        checkedPermissionSet = newSet
    }

    // -------------------------------------------------------------------------
    // Interfaces
    // -------------------------------------------------------------------------
    interface OnItemCheckedChangeListener {
        fun onItemCheckedChange()
    }

    companion object {

        private val ITEM_TAG_PERMISSION_NAME = R.string.advanced_search_list_item_tag_permission_name
    }
}