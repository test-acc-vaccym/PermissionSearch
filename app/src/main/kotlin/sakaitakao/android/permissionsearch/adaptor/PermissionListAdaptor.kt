package sakaitakao.android.permissionsearch.adaptor

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import sakaitakao.android.permissionsearch.R
import sakaitakao.android.permissionsearch.activity.ApplicationListActivity
import sakaitakao.android.permissionsearch.entity.PermissionInfoEx
import sakaitakao.android.permissionsearch.util.PermissionInfoUtil

/**
 * 権限一覧画面の権限リストのアダプタ

 * @author takao
 */
class PermissionListAdaptor
/**
 * @param ctx
 * *
 * @param textViewResourceId
 * *
 * @param items
 */
(private val ctx: Context, textViewResourceId: Int, private val items: List<PermissionInfoEx>) : ArrayAdapter<PermissionInfoEx>(ctx, textViewResourceId, items) {
    private val resources: Resources
    private val layoutInflater: LayoutInflater
    private val packageManager: PackageManager

    init {
        this.resources = ctx.resources
        this.layoutInflater = ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        this.packageManager = ctx.packageManager
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        // リスト項目の View を生成。
        var view: View = convertView ?: layoutInflater.inflate(R.layout.permission_list_item, null)

        // データを取得
        val permissionInfoEx = items[position]

        showPermissionLabel(view, permissionInfoEx)
        showProtectionLevel(view, permissionInfoEx)
        showApplicationCount(view, permissionInfoEx)

        // onclick イベント
        view.setOnClickListener(object : OnClickListener {
            override fun onClick(view: View) {

                // アプリ一覧へ遷移
                val intent = Intent()
                intent.setClass(ctx, ApplicationListActivity::class.java)
                intent.putExtra(ApplicationListActivity.INTENTEXTRA_PERMISSION_APP_LIST, permissionInfoEx)
                ctx.startActivity(intent)
            }
        })

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

        val textView = view.findViewById(R.id.permission_list_item_primary) as TextView
        val text = PermissionInfoUtil.getPermissionLabel(packageManager, resources, permissionInfoEx)
        textView.text = text
    }

    /**
     * プロテクションレヴェル

     * @param view
     * *
     * @param permissionInfoEx
     */
    private fun showProtectionLevel(view: View, permissionInfoEx: PermissionInfoEx) {

        val textView = view.findViewById(R.id.permission_list_item_secondary) as TextView
        textView.text = PermissionInfoUtil.formatProtectionLevel(resources, permissionInfoEx)
    }

    /**
     * 利用アプリ数

     * @param view
     * *
     * @param permissionInfoEx
     */
    private fun showApplicationCount(view: View, permissionInfoEx: PermissionInfoEx) {

        val applicationsLabelView = view.findViewById(R.id.permission_list_item_tertiary) as TextView
        applicationsLabelView.text = resources.getString(R.string.hit_applications, permissionInfoEx.applicationInfoList!!.size)
    }
}