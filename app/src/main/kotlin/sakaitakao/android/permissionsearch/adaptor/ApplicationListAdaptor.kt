package sakaitakao.android.permissionsearch.adaptor

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import sakaitakao.android.permissionsearch.Config
import sakaitakao.android.permissionsearch.R
import sakaitakao.android.permissionsearch.R.drawable

/**
 * アプリ一覧画面のアプリリストのアダプタ

 * @author takao
 */
class ApplicationListAdaptor
/**
 * @param ctx
 * *
 * @param textViewResourceId
 * *
 * @param items
 */
(private val ctx: Context,
 textViewResourceId: Int,
 private val items: List<ApplicationInfo>)
: ArrayAdapter<ApplicationInfo>(ctx, textViewResourceId, items) {

    private val layoutInflater: LayoutInflater
    private val packageManager: PackageManager

    init {
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
        var view: View = convertView ?: layoutInflater.inflate(R.layout.application_list_item, null)

        // データを取得
        val applicationInfo = items[position]

        showApplicationLabel(view, applicationInfo)
        showApplicationName(view, applicationInfo)
        showApplicationIcon(view, applicationInfo)

        // onclick イベント
        view.setOnClickListener({
            onItemClicked(applicationInfo)
        })

        return view
    }

    // -------------------------------------------------------------------------
    // Privates
    // -------------------------------------------------------------------------
    /**
     * アプリ名

     * @param view
     * *
     * @param applicationInfo
     */
    private fun showApplicationLabel(view: View, applicationInfo: ApplicationInfo) {
        val textView = view.findViewById(R.id.application_list_item_primary) as TextView
        textView.text = applicationInfo.loadLabel(packageManager)
    }

    /**
     * @param view
     * *
     * @param applicationInfo
     */
    private fun showApplicationName(view: View, applicationInfo: ApplicationInfo) {

        val textView = view.findViewById(R.id.application_list_item_secondary) as TextView
        textView.text = applicationInfo.packageName
    }

    /**
     * @param view
     * *
     * @param applicationInfo
     */
    private fun showApplicationIcon(view: View, applicationInfo: ApplicationInfo) {

        val imgView = view.findViewById(R.id.application_list_item_icon) as ImageView
        val icon = applicationInfo.loadIcon(packageManager)
        if (icon != null) {
            imgView.setImageDrawable(icon)
        } else {
            imgView.setImageResource(drawable.ic_launcher)
        }
    }

    private fun onItemClicked(applicationInfo: ApplicationInfo) {

        val config = Config(ctx)
        when (config.applicationInfo) {
            Config.ApplicationInfo.MARKET -> goToMarket(applicationInfo.packageName)
            Config.ApplicationInfo.SETTINGS -> goToSettings(applicationInfo.packageName)
        }
    }

    /**
     * @param packageName
     */
    private fun goToSettings(packageName: String) {

        // 参考
        try {
            // 設定へ遷移
            var intent: Intent? = when (Build.VERSION.SDK_INT) {
                1, 2, 3, 4, 5, 6 -> {
                    // ～2.0 は無視する
                    Toast.makeText(ctx, R.string.settings_not_supported, Toast.LENGTH_LONG).show()
                    null
                }

                7 -> {
                    // 2.1
                    // http://grepcode.com/file/repository.grepcode.com/java/ext/com.google.android/android-apps/2.1_r2/com/android/settings/InstalledAppDetails.java/
                    val intent = Intent()
                    intent.putExtra("com.android.settings.ApplicationPkgName", packageName)
                    intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails")
                    intent
                }

                8 -> {
                    // 2.2
                    // http://grepcode.com/file/repository.grepcode.com/java/ext/com.google.android/android-apps/2.2.1_r1/com/android/settings/InstalledAppDetails.java/
                    val intent = Intent()
                    intent.putExtra("pkg", packageName)
                    // intent.setData(Uri.fromParts("package", packageName, null));
                    intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails")
                    intent
                }

                else -> {
                    // 2.3以降
                    // http://code.google.com/p/sdwatch/source/browse/src/com/beaglebros/SDWatch/NotificationClicked.java?spec=svna6588c0c18c69e8e8b87a561f659a04ab7f92baa&r=a6588c0c18c69e8e8b87a561f659a04ab7f92baa
                    val intent = Intent()
                    intent.setData(Uri.fromParts("package", packageName, null))
                    intent.setClassName("com.android.settings", "com.android.settings.applications.InstalledAppDetails")
                    intent
                }
            }
            if (intent != null) {
                ctx.startActivity(intent)
            }
        } catch (e: ActivityNotFoundException) {
            // マーケットアプリなし
            Toast.makeText(ctx, R.string.settings_not_found, Toast.LENGTH_LONG).show()
        }

    }

    /**
     * @param packageName
     */
    private fun goToMarket(packageName: String) {

        try {
            // マーケットへ遷移
            val uri = Uri.parse(MARKET_DETAILS_URI_PREFIX + packageName)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            ctx.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // マーケットアプリなし
            Toast.makeText(ctx, R.string.market_not_found, Toast.LENGTH_LONG).show()
        }

    }

    companion object {

        private val MARKET_DETAILS_URI_PREFIX = "market://details?id="
    }
}