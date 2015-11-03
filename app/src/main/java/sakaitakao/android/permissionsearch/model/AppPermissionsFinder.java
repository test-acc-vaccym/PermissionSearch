package sakaitakao.android.permissionsearch.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;

import sakaitakao.android.permissionsearch.entity.PermissionInfoEx;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

/**
 * 権限を検索する
 * 
 * @author takao
 * 
 */
public class AppPermissionsFinder {

	/**
	 * Constructor
	 */
	public AppPermissionsFinder() {
	}

	/**
	 * パーミッションとパーミッションを保持するアプリ群のリストを返す。
	 * 
	 * @param packageManager
	 *            {@link PackageManager}
	 * @return パーミッションとパーミッションを保持するアプリ群のリスト。
	 */
	public List<PermissionInfoEx> getPermissionInfoExList(PackageManager packageManager, SearchCondition condition) {

		Map<String, List<ApplicationInfo>> permissionToAppListMap = getPermissionNameToApplicationInfoListMap(packageManager, condition);

		List<PermissionInfoEx> retList = new ArrayList<PermissionInfoEx>();
		Set<String> permissonSet = permissionToAppListMap.keySet();
		for (String permissionName : permissonSet) {

			PermissionInfoEx permissionInfoWithApplicationList = createPermissionInfoEx(packageManager, permissionName, permissionToAppListMap
					.get(permissionName));
			retList.add(permissionInfoWithApplicationList);
		}

		Collections.sort(retList, new PermissionInfoEx.ProtectionLevelComparator());
		return retList;
	}

	/**
	 * 指定されたパーミッションを保持するアプリ群のリストを返す。
	 * 
	 * @param packageManager
	 *            {@link PackageManager}
	 * @param permissionName
	 *            パーミッション名
	 * @return
	 */
	public PermissionInfoEx getPermissionInfoEx(PackageManager packageManager, String permissionName) {

		SearchCondition cond = new SearchCondition();
		cond.permissionNamePatternList = new ArrayList<String>();
		cond.permissionNamePatternList.add(permissionName);
		Map<String, List<ApplicationInfo>> permissionToAppListMap = getPermissionNameToApplicationInfoListMap(packageManager, cond);

		return createPermissionInfoEx(packageManager, permissionName, permissionToAppListMap.get(permissionName));
	}

	// -------------------------------------------------------------------------
	// Privates
	// -------------------------------------------------------------------------
	/**
	 * パーミッション名がキー、{@link ApplicationInfo}が値の Map を返す。
	 * 
	 * @param packageManager
	 *            {@link PackageManager}
	 * @param permissionNamePatternList
	 * @return パーミッション名がキー、{@link ApplicationInfo}が値の Map。
	 */
	private Map<String, List<ApplicationInfo>> getPermissionNameToApplicationInfoListMap(PackageManager packageManager, SearchCondition condition) {

		// インストールされているパッケージを列挙
		int packageSearchFlag = PackageManager.GET_PERMISSIONS | PackageManager.GET_UNINSTALLED_PACKAGES;
		List<PackageInfo> packageInfoList = packageManager.getInstalledPackages(packageSearchFlag);

		boolean excludeSystemApps = !condition.includeSystemApps;
		Map<String, List<ApplicationInfo>> permissionToAppListMap = new HashMap<String, List<ApplicationInfo>>();
		for (PackageInfo packageInfo : packageInfoList) {

			// /system 以下のアプリを除外
			if (excludeSystemApps && ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0)) {
				continue;
			}

			String[] requestedPermissions = packageInfo.requestedPermissions;
			if (requestedPermissions != null) {

				// パーミッションをフィルタする
				List<String> permissionList = Arrays.asList(requestedPermissions);

				// 条件に指定されたpermissionをすべて持っているか確認する。
				if (!isTargetAppByPermissionName(permissionList, condition)) {
					continue;
				}

				// 条件に指定されたパーミッションを抽出
				permissionList = getMatchedPermissionName(permissionList, condition);

				// 条件に指定されたプロテクションレベルのパーミッションを抽出
				permissionList = getMatchedProtectionLevel(packageManager, permissionList, condition);

				// パーミッションごとにアプリを仕分け
				for (String permissionName : permissionList) {
					List<ApplicationInfo> appList = permissionToAppListMap.get(permissionName);
					if (appList == null) {
						appList = new ArrayList<ApplicationInfo>();
						permissionToAppListMap.put(permissionName, appList);
					}
					appList.add(packageInfo.applicationInfo);
					Log.v("getPermissionNameToApplicationInfoListMap", "App = " + packageInfo.applicationInfo.packageName + " : Permission = "
							+ permissionName);
				}
			}
		}
		return permissionToAppListMap;
	}

	/**
	 * 条件に指定されたパーミッションをすべて持っているアプリかどうか返す。
	 * 
	 * @param permissionList
	 *            アプリのパーミッション
	 * @param condition
	 *            条件
	 * @return 条件に指定されたパーミッションをすべて持っているアプリの場合、true を返す。
	 */
	private boolean isTargetAppByPermissionName(List<String> permissionList, SearchCondition condition) {

		if (CollectionUtils.isEmpty(condition.permissionNamePatternList)) {
			return true;
		}

		boolean retVal = true;
		for (String condPermissionName : condition.permissionNamePatternList) {
			Pattern pattern = Pattern.compile(condPermissionName);
			int matchedCount = 0;
			// 検索条件に指定されたパーミッション名(正規表現)が、アプリのパーミッションにマッチするか検査する
			for (String permissionName : permissionList) {
				if (pattern.matcher(permissionName).find()) {
					matchedCount++;
				}
			}
			// マッチしない条件があった場合は、対象外とする。
			if (matchedCount == 0) {
				retVal = false;
				break;
			}
		}

		return retVal;
	}

	/**
	 * 条件にマッチするパーミッションを抽出する。
	 * 
	 * @param permissionList
	 *            アプリのパーミッション。
	 * @param condition
	 *            条件。
	 * @return 条件でフィルタされたアプリのパーミッション。
	 */
	private List<String> getMatchedPermissionName(List<String> permissionList, SearchCondition condition) {

		if (CollectionUtils.isEmpty(condition.permissionNamePatternList)) {
			return permissionList;
		}

		List<String> retList = new ArrayList<String>();
		for (String condPermissionName : condition.permissionNamePatternList) {
			Pattern pattern = Pattern.compile(condPermissionName);
			for (String permissionName : permissionList) {
				if (pattern.matcher(permissionName).find()) {
					retList.add(permissionName);
				}
			}
		}

		return retList;
	}

	/**
	 * 条件にマッチするプロテクションレベルのパーミッションを返す。
	 * 
	 * @param packageManager
	 *            {@link PackageManager}
	 * @param permissionList
	 *            アプリのパーミッション。
	 * @param condition
	 *            条件。
	 * @return 条件でフィルタされたアプリのパーミッション。
	 */
	private List<String> getMatchedProtectionLevel(PackageManager packageManager, List<String> permissionList, SearchCondition condition) {

		if (CollectionUtils.isEmpty(condition.protectionLevelSet)) {
			return permissionList;
		}

		List<String> retList = new ArrayList<String>();
		for (String permissionName : permissionList) {
			PermissionInfo permissionInfo = null;
			try {
				permissionInfo = packageManager.getPermissionInfo(permissionName, 0);
			} catch (NameNotFoundException e) {
			}
			if (permissionInfo == null) {
				// プロテクションレヴェルがとれないものは、とりあえず対象とする。
				retList.add(permissionName);
			} else {
				if (condition.protectionLevelSet.contains(permissionInfo.protectionLevel)) {
					retList.add(permissionName);
				}
			}
		}
		return retList;
	}

	/**
	 * {@link PermissionInfoEx} を生成する。
	 * 
	 * @param packageManager
	 *            {@link PackageManager}
	 * @param permissionName
	 *            パーミッション名
	 * @param applicationInfoList
	 *            {@link ApplicationInfo} のリスト
	 * @return {@link PermissionInfoEx} のインスタンス
	 */
	private PermissionInfoEx createPermissionInfoEx(PackageManager packageManager, String permissionName, List<ApplicationInfo> applicationInfoList) {

		PermissionInfo permissionInfo = null;
		boolean isUnknownPermission = false;
		try {
			permissionInfo = packageManager.getPermissionInfo(permissionName, 0);
		} catch (NameNotFoundException e) {
			// 名前だけ入れる。
			permissionInfo = new PermissionInfo();
			permissionInfo.name = permissionName;
			permissionInfo.protectionLevel = -1;
			isUnknownPermission = true;
		}

		PermissionInfoEx permissionInfoWithApplicationList = new PermissionInfoEx();
		permissionInfoWithApplicationList.permissionInfo = permissionInfo;
		permissionInfoWithApplicationList.isUnknownPermission = isUnknownPermission;
		permissionInfoWithApplicationList.applicationInfoList = applicationInfoList;
		return permissionInfoWithApplicationList;
	}

	// -------------------------------------------------------------------------
	// Classes
	// -------------------------------------------------------------------------
	/**
	 * 検索条件を保持する。
	 * 
	 * @author takao
	 * 
	 */
	public static class SearchCondition {
		public boolean includeSystemApps;
		public List<String> permissionNamePatternList;
		public Set<Integer> protectionLevelSet;
	}
}