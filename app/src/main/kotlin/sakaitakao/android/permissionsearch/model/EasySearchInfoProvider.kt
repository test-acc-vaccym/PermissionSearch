package sakaitakao.android.permissionsearch.model

import android.content.Context
import android.content.res.Resources
import net.arnx.jsonic.JSON
import sakaitakao.android.permissionsearch.Config
import sakaitakao.android.permissionsearch.R
import sakaitakao.android.permissionsearch.entity.EasySearchInfo
import java.util.*

/**
 * 簡単検索の条件を検索する

 * @author takao
 */
class EasySearchInfoProvider {

    /**
     * @param resources
     * *
     * @return
     */
    fun getEasySearchInfoListForEasySearchActivity(context: Context, resources: Resources): List<EasySearchInfo> {

        // 保存された詳細検索
        val ret = ArrayList<EasySearchInfo>()
        ret.addAll(getSavedAdvancedSearchConditionList(context))

        // 簡単検索
        ret.addAll(getEasySearchInfoList(resources))
        return ret
    }

    // -------------------------------------------------------------------------
    // Privates
    // -------------------------------------------------------------------------
    private fun getSavedAdvancedSearchConditionList(context: Context): List<EasySearchInfo> {

        val config = Config(context)
        return config.advancedSearchConditions
    }

    private fun getEasySearchInfoList(resources: Resources): List<EasySearchInfo> {

        val jsonData = resources.getStringArray(R.array.easy_search_info)
        val ret = ArrayList<EasySearchInfo>()
        for (json in jsonData) {
            val item = JSON.decode(json, EasySearchInfo::class.java)
            ret.add(item)
        }
        return ret
    }
}
/**
 * Constructor
 */