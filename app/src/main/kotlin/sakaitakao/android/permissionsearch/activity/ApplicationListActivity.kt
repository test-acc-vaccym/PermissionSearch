package sakaitakao.android.permissionsearch.activity

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.MenuItem.OnMenuItemClickListener
import android.widget.ListView
import android.widget.TextView
import sakaitakao.android.permissionsearch.R
import sakaitakao.android.permissionsearch.adaptor.ApplicationListAdaptor
import sakaitakao.android.permissionsearch.entity.ContextMenuItem
import sakaitakao.android.permissionsearch.entity.PermissionInfoEx
import sakaitakao.android.permissionsearch.util.PermissionInfoUtil
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.PrintWriter

/**
 * アプリケーションリストのアクティビティ

 * @author takao
 */
class ApplicationListActivity : Activity() {

    private val contextMenuItems = arrayOf(ContextMenuItem(CONTEXT_MENU_ITEM_ID_SHARE,
            R.string.context_menu_item_share, android.R.drawable.ic_menu_share, OnMenuItemClickShareListener()))

    /**
     * アプリケーションリストのアダプタ実装
     */
    private var applicationListAdaptor: ApplicationListAdaptor? = null

    private var permissionInfoEx: PermissionInfoEx? = null

    /*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.application_list)

        // 前の画面から渡されたデータを取得
        permissionInfoEx = condition

        // 画面表示
        val packageManager = packageManager
        val resources = resources
        showPermissionLabel(packageManager, resources, permissionInfoEx!!)
        showProtectionLevel(resources, permissionInfoEx!!)
        showPermissionName(permissionInfoEx!!)
        showPermissionDescription(packageManager, permissionInfoEx!!)
        showList(permissionInfoEx!!)
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

    // -------------------------------------------------------------------------
    // Privates
    // -------------------------------------------------------------------------
    private val condition: PermissionInfoEx
        get() {
            val intent = intent
            val permissionInfoEx = intent.getParcelableExtra<PermissionInfoEx>(INTENTEXTRA_PERMISSION_APP_LIST) ?: throw RuntimeException("Please set extra data by Intent#putExtra($INTENTEXTRA_PERMISSION_APP_LIST, Parcelable)")
            return permissionInfoEx
        }

    private fun showPermissionLabel(packageManager: PackageManager, resources: Resources, permissionInfoEx: PermissionInfoEx) {

        val textView = findViewById(R.id.application_list_permission_label) as TextView
        val name = PermissionInfoUtil.getPermissionLabel(packageManager, resources, permissionInfoEx)
        textView.text = name
        textView.requestFocus()
    }

    private fun showProtectionLevel(resources: Resources, permissionInfoEx: PermissionInfoEx) {

        val permissionLabelView = findViewById(R.id.application_list_protection_level) as TextView
        permissionLabelView.text = PermissionInfoUtil.formatProtectionLevel(resources, permissionInfoEx)
    }

    private fun showPermissionName(permissionInfoEx: PermissionInfoEx) {
        val permissionLabelView = findViewById(R.id.application_list_permission_name) as TextView
        permissionLabelView.text = permissionInfoEx.permissionInfo?.name
    }

    private fun showPermissionDescription(packageManager: PackageManager, permissionInfoEx: PermissionInfoEx) {
        val permissionLabelView = findViewById(R.id.application_list_permission_description) as TextView
        permissionLabelView.text = permissionInfoEx.permissionInfo?.loadDescription(packageManager)
    }

    private fun showList(permissionInfoEx: PermissionInfoEx) {
        val appListView = findViewById(R.id.application_list_list) as ListView
        applicationListAdaptor = ApplicationListAdaptor(this, R.layout.application_list_item, permissionInfoEx.applicationInfoList!!)
        appListView.adapter = applicationListAdaptor
    }

    private fun onOptionsItemShareSelected(): Boolean {

        // テキストを生成
        val text = createPermissionInfoTextForSharing(packageManager, resources, permissionInfoEx!!)

        // 共有の暗黙的インテントを発行
        val intent = Intent(android.content.Intent.ACTION_SEND)
        intent.setType("text/plain")
        intent.putExtra(Intent.EXTRA_TEXT, text)
        startActivity(Intent.createChooser(intent, getString(R.string.share_application_list)))

        return false
    }

    /**
     * 共有用のテキストを生成する。

     * @param packageManager
     * *            PackageManager
     * *
     * @param resources
     * *            Resources
     * *
     * @param permissionInfoEx
     * *            PermissionInfoEx
     * *
     * @return
     */
    private fun createPermissionInfoTextForSharing(packageManager: PackageManager, resources: Resources, permissionInfoEx: PermissionInfoEx): String {

        var bos: ByteArrayOutputStream? = null
        try {
            bos = ByteArrayOutputStream()
            val pw = PrintWriter(bos)

            val permissionLabel = PermissionInfoUtil.getPermissionLabel(packageManager, resources, permissionInfoEx)
            // タイトル
            pw.println(resources.getString(R.string.application_list_share_title, permissionLabel))
            pw.println()

            // 概要
            pw.println(resources.getString(R.string.application_list_share_permission_info, permissionLabel))
            pw.println(PermissionInfoUtil.formatProtectionLevel(resources, permissionInfoEx))
            pw.println(permissionInfoEx.permissionInfo?.loadDescription(packageManager))
            pw.println()

            // applications
            pw.println(resources.getString(R.string.application_list_share_application_list))
            permissionInfoEx.applicationInfoList!!.forEach { applicationInfo ->
                // App Name & link
                pw.println(
                        resources.getString(
                                R.string.application_list_share_app_name_and_market_uri,
                                applicationInfo.loadLabel(packageManager),
                                applicationInfo.packageName)
                )
            }

            pw.close()
        } finally {
            try {
                bos?.close()
            } catch (e: IOException) {
                Log.e("", "IOException caught when closing ByteArrayOutputStream.", e)
                throw RuntimeException(e)
            }

        }
        Log.v("", bos?.toString())
        return bos?.toString() ?: ""
    }

    // -------------------------------------------------------------------------
    // Classes
    // -------------------------------------------------------------------------
    private inner class OnMenuItemClickShareListener : OnMenuItemClickListener {
        override fun onMenuItemClick(item: MenuItem): Boolean {
            return onOptionsItemShareSelected()
        }
    }

    companion object {

        /** アプリケーション一覧を表示する条件を保持する Intent の extra データの名前  */
        val INTENTEXTRA_PERMISSION_APP_LIST = ApplicationListActivity::class.java.name + ".PERMISSION_APP_LIST"
        private val CONTEXT_MENU_ITEM_ID_SHARE = Menu.FIRST + 1
    }

}