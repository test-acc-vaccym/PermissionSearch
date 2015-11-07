package sakaitakao.android.permissionsearch

import android.content.Context
import android.preference.PreferenceManager
import net.arnx.jsonic.JSON
import sakaitakao.android.permissionsearch.entity.EasySearchInfo
import java.util.*

/**
 * 設定

 * @author takao
 */
class Config
/**
 * @param context コンテキスト
 */
(private val context: Context) {

    /**
     * アプリの概要を表示するアプリ

     * @return [ApplicationInfo.MARKET]または、
     * *         [ApplicationInfo.SETTINGS]
     */
    val applicationInfo: ApplicationInfo?
        get() {
            val sp = PreferenceManager.getDefaultSharedPreferences(context)
            return ApplicationInfo.fromKey(sp.getString(PREFERENCE_KEY_APP_INFO, ApplicationInfo.MARKET.key))
        }

    /**
     * 詳細検索条件に追加する。

     * @param easySearchInfo
     */
    fun addAdvancedSearchCondition(easySearchInfo: EasySearchInfo) {

        // 現在の設定を取得
        val easySearchInfoList = advancedSearchConditions
        easySearchInfoList.add(easySearchInfo)

        // 新しい設定を書き込み
        advancedSearchConditions = easySearchInfoList
    }

    /**
     * 詳細検索条件を取得する。

     * @return 詳細検索条件のリスト。
     */
    // -------------------------------------------------------------------------
    // Privates
    // -------------------------------------------------------------------------
    var advancedSearchConditions: MutableList<EasySearchInfo>
        get() {

            val sp = PreferenceManager.getDefaultSharedPreferences(context)
            val json = sp.getString(PREFERENCE_KEY_ADVANCED_SEARCH_CONDITIONS, "")
            val es = JSON.decode<Array<EasySearchInfo>>(json, Array<EasySearchInfo>::class.java)
            return ArrayList(Arrays.asList(*es))
        }
        private set(list) {

            val sp = PreferenceManager.getDefaultSharedPreferences(context)
            val editor = sp.edit()
            val newConfig = JSON.encode(list)
            editor.putString(PREFERENCE_KEY_ADVANCED_SEARCH_CONDITIONS, newConfig)
            editor.commit()
        }

    /**
     * 詳細検索条件を削除する。

     * @param index
     */
    fun removeAdvancedSearchCondition(index: Int) {

        // 現在の設定を取得
        val easySearchInfoList = advancedSearchConditions
        if (easySearchInfoList.size > 0) {
            easySearchInfoList.removeAt(index)
        }

        // 新しい設定を書き込み
        advancedSearchConditions = easySearchInfoList
    }

    // -------------------------------------------------------------------------
    // enum
    // -------------------------------------------------------------------------
    enum class ApplicationInfo(public val key: String) {
        MARKET("0"), SETTINGS("1");

        companion object {
            public fun fromKey(k: String): ApplicationInfo {
                return ApplicationInfo.values.filter { it.key == k }[0]
            }
        }
    }

    companion object {

        /** アプリケーション情報遷移先  */
        private val PREFERENCE_KEY_APP_INFO = "app_info"

        /** 詳細検索条件  */
        private val PREFERENCE_KEY_ADVANCED_SEARCH_CONDITIONS = "advanced_search_conditions"
    }
}
