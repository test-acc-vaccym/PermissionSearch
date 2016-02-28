package sakaitakao.android.permissionsearch.activity


import android.app.Activity
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.synthetic.main.permission_list.*
import sakaitakao.android.permissionsearch.R
import sakaitakao.android.permissionsearch.adaptor.PermissionListAdaptor
import sakaitakao.android.permissionsearch.entity.PermissionInfoEx
import sakaitakao.android.permissionsearch.model.AppPermissionsFinder
import java.util.*


/**
 * 権限一覧のアクティビティ

 * @author takao
 */
class PermissionListActivity : Activity() {


    /** Called when the activity is first created.  */
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.permission_list)

        val condition = condition

        val packageManager = packageManager
        val aph = AppPermissionsFinder()
        val list = aph.getPermissionInfoExList(packageManager, toAppPermissionsFinderCondition(condition))

        val resources = resources
        showHitCount(resources, list)
        showList(list)
    }

    // -------------------------------------------------------------------------
    // Privates
    // -------------------------------------------------------------------------
    /**
     * @return
     */
    private val condition: SearchCondition
        get() {
            val searchCondition = intent.getParcelableExtra<SearchCondition>(INTENTEXTRA_CONDITION) ?:
                    throw RuntimeException("Please set extra data by Intent#putExtra($INTENTEXTRA_CONDITION, Parcelable)")
            return searchCondition
        }

    private fun toAppPermissionsFinderCondition(cond: SearchCondition): AppPermissionsFinder.SearchCondition {

        val ret = AppPermissionsFinder.SearchCondition()
        ret.includeSystemApps = cond.includeSystemApps
        ret.permissionNamePatternList = cond.permissionNamePatternList.toMutableList()
        ret.protectionLevelSet = HashSet(cond.protectionLevelList)
        return ret
    }

    /**
     * @param resources
     * *
     * @param list
     */
    private fun showHitCount(resources: Resources, list: List<PermissionInfoEx>) {

        permission_list_hit_count.text = resources.getString(R.string.hit_count, list.size)
    }

    /**
     * @param list
     */
    private fun showList(list: List<PermissionInfoEx>) {

        val adaptor = PermissionListAdaptor(this, R.layout.permission_list_item, list)
        permission_list_list.adapter = adaptor
    }

    // -------------------------------------------------------------------------
    // Classes
    // -------------------------------------------------------------------------
    /**

     * @author takao
     */
    class SearchCondition : Parcelable {

        val includeSystemApps: Boolean
        val permissionNamePatternList: List<String>
        val protectionLevelList: List<Int>?

        constructor() {
            includeSystemApps = false
            permissionNamePatternList = ArrayList<String>()
            protectionLevelList = ArrayList<Int>()
        }

        constructor(includeSystemApps: Boolean, permissionNamePatternList: List<String>, protectionLevelList: List<Int>?) {
            this.includeSystemApps = includeSystemApps
            this.permissionNamePatternList = permissionNamePatternList
            this.protectionLevelList = protectionLevelList
        }

        /**
         * @param parcel
         */
        private constructor(parcel: Parcel) {
            val bools = BooleanArray(1)
            parcel.readBooleanArray(bools)
            includeSystemApps = bools[0]
            permissionNamePatternList = ArrayList<String>()
            parcel.readList(permissionNamePatternList, null)
            protectionLevelList = ArrayList<Int>()
            parcel.readList(protectionLevelList, null)
        }

        /*
		 * (non-Javadoc)
		 * 
		 * @see android.os.Parcelable#describeContents()
		 */
        override fun describeContents(): Int {
            return 0
        }

        /*
		 * (non-Javadoc)
		 * 
		 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
		 */
        override fun writeToParcel(dest: Parcel, flags: Int) {
            dest.writeBooleanArray(booleanArrayOf(includeSystemApps))
            dest.writeList(permissionNamePatternList)
            dest.writeList(protectionLevelList)
        }

        companion object {

            @JvmField
            val CREATOR: Parcelable.Creator<SearchCondition> = object : Parcelable.Creator<SearchCondition> {
                override fun newArray(i: Int): Array<SearchCondition?> {
                    return arrayOfNulls(i)
                }

                override fun createFromParcel(parcel: Parcel): SearchCondition {
                    return SearchCondition(parcel)
                }
            }
        }
    }

    companion object {

        private val INTENTEXTRA_CONDITION = PermissionListActivity::class.java.name + ".CONDITION"

        /**
         * アクティビティ起動時の検索条件を設定する。

         * @param intent
         * *            [Intent]
         * *
         * @param searchCondition
         * *            検索条件
         */
        fun setSearchCondition(intent: Intent, searchCondition: SearchCondition) {
            intent.putExtra(PermissionListActivity.INTENTEXTRA_CONDITION, searchCondition)
        }
    }
}