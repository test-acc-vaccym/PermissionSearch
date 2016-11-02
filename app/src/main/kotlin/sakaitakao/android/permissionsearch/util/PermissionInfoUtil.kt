package sakaitakao.android.permissionsearch.util

import android.content.pm.PackageManager
import android.content.pm.PermissionInfo
import android.content.res.Resources
import sakaitakao.android.permissionsearch.R
import sakaitakao.android.permissionsearch.entity.PermissionInfoEx

/**
 * [PermissionInfo]用のユーティリティ

 * @author takao
 */
object PermissionInfoUtil {

    /**
     * プロテクションレベルを文字列にする。

     * @param resources
     * *            [Resources]
     * *
     * @param protectionLevel
     * *            プロテクションレベル
     * *
     * @return プロテクションレベルを示す文字列
     */
    fun toStringProtectionLevel(resources: Resources, protectionLevel: Int): String {

        val stringId: Int
        when (protectionLevel) {
            PermissionInfo.PROTECTION_DANGEROUS ->
                stringId = R.string.protection_level_dangerous
            PermissionInfo.PROTECTION_NORMAL ->
                stringId = R.string.protection_level_normal
            PermissionInfo.PROTECTION_SIGNATURE ->
                stringId = R.string.protection_level_signature
            (PermissionInfo.PROTECTION_SIGNATURE or PermissionInfo.PROTECTION_FLAG_PRIVILEGED) ->
                stringId = R.string.protection_level_signature_or_system
            else -> stringId = R.string.protection_level_unknown
        }
        return resources.getString(stringId)
    }

    /**
     * 表示用のプロテクションレベルを取得する。

     * @param resources
     * *            [Resources]
     * *
     * @param permissionInfoEx
     * *            [PermissionInfoEx]
     * *
     * @return 表示用のプロテクションレベル
     */
    fun formatProtectionLevel(resources: Resources, permissionInfoEx: PermissionInfoEx): String {

        if (permissionInfoEx.isUnknownPermission) {
            return ""
        } else {
            val protectionLevel = toStringProtectionLevel(resources, permissionInfoEx.permissionInfo!!.protectionLevel)
            return resources.getString(R.string.protection_level, protectionLevel)
        }
    }

    fun getPermissionLabel(packageManager: PackageManager, resources: Resources, permissionInfoEx: PermissionInfoEx): String {
        val permissionInfo = permissionInfoEx.permissionInfo
        var retVal = if (permissionInfoEx.isUnknownPermission) {
            resources.getString(R.string.unknown_permission, permissionInfo?.name)
        } else {
            StringBuilder(permissionInfo?.loadLabel(packageManager)).toString()
        }
        return retVal
    }
}
