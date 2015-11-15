package sakaitakao.android.permissionsearch.model

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.PackageManager.NameNotFoundException
import android.content.pm.PermissionInfo
import android.util.Log
import org.apache.commons.collections.CollectionUtils
import sakaitakao.android.permissionsearch.entity.PermissionInfoEx
import java.util.*
import java.util.regex.Pattern

/**
 * 権限を検索する

 * @author takao
 */
class AppPermissionsFinder {

    /**
     * パーミッションとパーミッションを保持するアプリ群のリストを返す。

     * @param packageManager
     * *            [PackageManager]
     * *
     * @return パーミッションとパーミッションを保持するアプリ群のリスト。
     */
    fun getPermissionInfoExList(packageManager: PackageManager, condition: SearchCondition): List<PermissionInfoEx> {

        val permissionToAppListMap = getPermissionNameToApplicationInfoListMap(packageManager, condition)

        val retList = permissionToAppListMap.map { entry ->
            createPermissionInfoEx(packageManager, entry.key, entry.value)
        }.sortedWith(PermissionInfoEx.ProtectionLevelComparator())

        return retList
    }


    // -------------------------------------------------------------------------
    // Privates
    // -------------------------------------------------------------------------
    /**
     * パーミッション名がキー、[ApplicationInfo]が値の Map を返す。

     * @param packageManager
     * *            [PackageManager]
     * *
     * @param condition
     * *
     * @return パーミッション名がキー、[ApplicationInfo]が値の Map。
     */
    private fun getPermissionNameToApplicationInfoListMap(packageManager: PackageManager, condition: SearchCondition): Map<String, List<ApplicationInfo>> {

        // インストールされているパッケージを列挙
        val packageSearchFlag = PackageManager.GET_PERMISSIONS or PackageManager.GET_UNINSTALLED_PACKAGES
        val packageInfoList = packageManager.getInstalledPackages(packageSearchFlag)

        val excludeSystemApps = !condition.includeSystemApps
        val permissionToAppListMap = HashMap<String, MutableList<ApplicationInfo>>()

        packageInfoList.filterNot { packageInfo ->
            // /system 以下のアプリを除外
            excludeSystemApps && ((packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0)
        }.filter { packageInfo ->
            packageInfo.requestedPermissions != null
        }.filter { packageInfo ->
            val requestedPermissions = packageInfo.requestedPermissions
            var permissionList = Arrays.asList(*requestedPermissions)
            // 条件に指定されたpermissionをすべて持っているか確認する。
            isTargetAppByPermissionName(permissionList, condition)
        }.forEach { packageInfo ->
            val requestedPermissions = packageInfo.requestedPermissions
            var permissionList = Arrays.asList(*requestedPermissions)

            // 条件に指定されたパーミッションを抽出
            permissionList = getMatchedPermissionName(permissionList, condition)

            // 条件に指定されたプロテクションレベルのパーミッションを抽出
            permissionList = getMatchedProtectionLevel(packageManager, permissionList, condition)

            // パーミッションごとにアプリを仕分け
            for (permissionName in permissionList) {
                var appList: MutableList<ApplicationInfo>? = permissionToAppListMap[permissionName]
                if (appList == null) {
                    appList = ArrayList<ApplicationInfo>()
                    permissionToAppListMap.put(permissionName, appList)
                }
                appList.add(packageInfo.applicationInfo)
                Log.v("getPermissionNameToApplicationInfoListMap", "App = " + packageInfo.applicationInfo.packageName + " : Permission = " + permissionName)
            }
        }

        return permissionToAppListMap
    }

    /**
     * 条件に指定されたパーミッションをすべて持っているアプリかどうか返す。

     * @param permissionList
     * *            アプリのパーミッション
     * *
     * @param condition
     * *            条件
     * *
     * @return 条件に指定されたパーミッションをすべて持っているアプリの場合、true を返す。
     */
    private fun isTargetAppByPermissionName(permissionList: List<String>, condition: SearchCondition): Boolean {

        if (CollectionUtils.isEmpty(condition.permissionNamePatternList)) {
            return true
        }

        var retVal = true
        for (condPermissionName in condition.permissionNamePatternList ?: ArrayList<String>()) {
            val pattern = Pattern.compile(condPermissionName)
            var matchedCount = 0
            // 検索条件に指定されたパーミッション名(正規表現)が、アプリのパーミッションにマッチするか検査する
            for (permissionName in permissionList) {
                if (pattern.matcher(permissionName).find()) {
                    matchedCount++
                }
            }
            // マッチしない条件があった場合は、対象外とする。
            if (matchedCount == 0) {
                retVal = false
                break
            }
        }

        return retVal
    }

    /**
     * 条件にマッチするパーミッションを抽出する。

     * @param permissionList
     * *            アプリのパーミッション。
     * *
     * @param condition
     * *            条件。
     * *
     * @return 条件でフィルタされたアプリのパーミッション。
     */
    private fun getMatchedPermissionName(permissionList: List<String>, condition: SearchCondition): List<String> {

        if (CollectionUtils.isEmpty(condition.permissionNamePatternList)) {
            return permissionList
        }

        val retList = ArrayList<String>()
        for (condPermissionName in condition.permissionNamePatternList ?: ArrayList<String>()) {
            val pattern = Pattern.compile(condPermissionName)
            for (permissionName in permissionList) {
                if (pattern.matcher(permissionName).find()) {
                    retList.add(permissionName)
                }
            }
        }

        return retList
    }

    /**
     * 条件にマッチするプロテクションレベルのパーミッションを返す。

     * @param packageManager
     * *            [PackageManager]
     * *
     * @param permissionList
     * *            アプリのパーミッション。
     * *
     * @param condition
     * *            条件。
     * *
     * @return 条件でフィルタされたアプリのパーミッション。
     */
    private fun getMatchedProtectionLevel(packageManager: PackageManager, permissionList: List<String>, condition: SearchCondition): List<String> {

        if (CollectionUtils.isEmpty(condition.protectionLevelSet)) {
            return permissionList
        }

        val retList = ArrayList<String>()
        for (permissionName in permissionList) {
            var permissionInfo: PermissionInfo? = null
            try {
                permissionInfo = packageManager.getPermissionInfo(permissionName, 0)
            } catch (e: NameNotFoundException) {
            }

            if (permissionInfo == null) {
                // プロテクションレヴェルがとれないものは、とりあえず対象とする。
                retList.add(permissionName)
            } else {
                if (condition.protectionLevelSet?.contains(permissionInfo.protectionLevel) ?: false) {
                    retList.add(permissionName)
                }
            }
        }
        return retList
    }

    /**
     * [PermissionInfoEx] を生成する。

     * @param packageManager
     * *            [PackageManager]
     * *
     * @param permissionName
     * *            パーミッション名
     * *
     * @param applicationInfoList
     * *            [ApplicationInfo] のリスト
     * *
     * @return [PermissionInfoEx] のインスタンス
     */
    private fun createPermissionInfoEx(packageManager: PackageManager, permissionName: String, applicationInfoList: List<ApplicationInfo>): PermissionInfoEx {

        var permissionInfo: PermissionInfo?
        var isUnknownPermission = false
        try {
            permissionInfo = packageManager.getPermissionInfo(permissionName, 0)
        } catch (e: NameNotFoundException) {
            // 名前だけ入れる。
            permissionInfo = PermissionInfo()
            permissionInfo.name = permissionName
            permissionInfo.protectionLevel = -1
            isUnknownPermission = true
        }

        val permissionInfoWithApplicationList = PermissionInfoEx()
        permissionInfoWithApplicationList.permissionInfo = permissionInfo
        permissionInfoWithApplicationList.isUnknownPermission = isUnknownPermission
        permissionInfoWithApplicationList.applicationInfoList = applicationInfoList
        return permissionInfoWithApplicationList
    }

    // -------------------------------------------------------------------------
    // Classes
    // -------------------------------------------------------------------------
    /**
     * 検索条件を保持する。

     * @author takao
     */
    class SearchCondition {
        var includeSystemApps: Boolean = false
        var permissionNamePatternList: MutableList<String>? = null
        var protectionLevelSet: Set<Int>? = null
    }
}
/**
 * Constructor
 */