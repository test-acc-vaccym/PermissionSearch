package sakaitakao.android.permissionsearch.util;

import sakaitakao.android.permissionsearch.R;
import sakaitakao.android.permissionsearch.entity.PermissionInfoEx;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.content.res.Resources;

/**
 * {@link PermissionInfo}用のユーティリティ
 * 
 * @author takao
 */
public final class PermissionInfoUtil {

	private PermissionInfoUtil() {
	}

	/**
	 * プロテクションレベルを文字列にする。
	 * 
	 * @param resources
	 *            {@link Resources}
	 * @param protectionLevel
	 *            プロテクションレベル
	 * @return プロテクションレベルを示す文字列
	 */
	public static String toStringProtectionLevel(Resources resources, int protectionLevel) {

		int stringId;
		switch (protectionLevel) {
		case PermissionInfo.PROTECTION_DANGEROUS:
			stringId = R.string.protection_level_dangerous;
			break;
		case PermissionInfo.PROTECTION_NORMAL:
			stringId = R.string.protection_level_normal;
			break;
		case PermissionInfo.PROTECTION_SIGNATURE:
			stringId = R.string.protection_level_signature;
			break;
		case PermissionInfo.PROTECTION_SIGNATURE_OR_SYSTEM:
			stringId = R.string.protection_level_signature_or_system;
			break;
		default:
			stringId = R.string.protection_level_unknown;
		}
		return resources.getString(stringId);
	}

	/**
	 * 表示用のプロテクションレベルを取得する。
	 * 
	 * @param resources
	 *            {@link Resources}
	 * @param permissionInfoEx
	 *            {@link PermissionInfoEx}
	 * @return 表示用のプロテクションレベル
	 */
	public static String formatProtectionLevel(Resources resources, PermissionInfoEx permissionInfoEx) {

		if (permissionInfoEx.isUnknownPermission) {
			return "";
		} else {
			String protectionLevel = toStringProtectionLevel(resources, permissionInfoEx.permissionInfo.protectionLevel);
			return resources.getString(R.string.protection_level, protectionLevel);
		}
	}

	public static String getPermissionLabel(PackageManager packageManager, Resources resources, PermissionInfoEx permissionInfoEx) {
		String retVal = "";
		PermissionInfo permissionInfo = permissionInfoEx.permissionInfo;
		if (permissionInfoEx.isUnknownPermission) {
			retVal = resources.getString(R.string.unknown_permission, permissionInfo.name);
		} else {
			retVal = new StringBuilder(permissionInfo.loadLabel(packageManager)).toString();
		}
		return retVal;
	}
}
