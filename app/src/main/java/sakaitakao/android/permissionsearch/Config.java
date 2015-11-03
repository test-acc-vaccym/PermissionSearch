package sakaitakao.android.permissionsearch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.arnx.jsonic.JSON;
import sakaitakao.android.permissionsearch.entity.EasySearchInfo;
import sakaitakao.android.permissionsearch.util.PreferenceEnum;
import sakaitakao.android.permissionsearch.util.PreferenceEnumUtil;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

/**
 * 設定
 * 
 * @author takao
 * 
 */
public class Config {

	/** アプリケーション情報遷移先 */
	private static final String PREFERENCE_KEY_APP_INFO = "app_info";

	/** 詳細検索条件 */
	private static final String PREFERENCE_KEY_ADVANCED_SEARCH_CONDITIONS = "advanced_search_conditions";

	/** コンテキスト */
	private Context context;

	/**
	 * コンストラクタ
	 * 
	 * @param context
	 */
	public Config(Context context) {
		this.context = context;
	}

	/**
	 * アプリの概要を表示するアプリ
	 * 
	 * @return {@link ApplicationInfo#MARKET}または、
	 *         {@link ApplicationInfo#SETTINGS}
	 */
	public ApplicationInfo getApplicationInfo() {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		String val = sp.getString(PREFERENCE_KEY_APP_INFO, ApplicationInfo.MARKET.getKey());
		return PreferenceEnumUtil.getValue(ApplicationInfo.class, val);
	}

	/**
	 * 詳細検索条件に追加する。
	 * 
	 * @param easySearchInfo
	 */
	public void addAdvancedSearchCondition(EasySearchInfo easySearchInfo) {

		// 現在の設定を取得
		List<EasySearchInfo> easySearchInfoList = getAdvancedSearchConditions();
		easySearchInfoList.add(easySearchInfo);

		// 新しい設定を書き込み
		setAdvancedSearchConditions(easySearchInfoList);
	}

	/**
	 * 詳細検索条件を取得する。
	 * 
	 * @return 詳細検索条件のリスト。
	 */
	public List<EasySearchInfo> getAdvancedSearchConditions() {

		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		String json = sp.getString(PREFERENCE_KEY_ADVANCED_SEARCH_CONDITIONS, "");
		EasySearchInfo[] es = JSON.decode(json, EasySearchInfo[].class);
		return new ArrayList<EasySearchInfo>(Arrays.asList(es));
	}

	/**
	 * 詳細検索条件を削除する。
	 * 
	 * @param index
	 */
	public void removeAdvancedSearchCondition(int index) {

		// 現在の設定を取得
		List<EasySearchInfo> easySearchInfoList = getAdvancedSearchConditions();
		if (easySearchInfoList.size() > 0) {
			easySearchInfoList.remove(index);
		}

		// 新しい設定を書き込み
		setAdvancedSearchConditions(easySearchInfoList);
	}

	// -------------------------------------------------------------------------
	// Privates
	// -------------------------------------------------------------------------
	private void setAdvancedSearchConditions(List<EasySearchInfo> list) {

		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = sp.edit();
		String newConfig = JSON.encode(list);
		editor.putString(PREFERENCE_KEY_ADVANCED_SEARCH_CONDITIONS, newConfig);
		editor.commit();
	}

	// -------------------------------------------------------------------------
	// enum
	// -------------------------------------------------------------------------
	public enum ApplicationInfo implements PreferenceEnum {

		MARKET("0"), SETTINGS("1");

		private String key;

		private ApplicationInfo(String key) {
			this.key = key;
		}

		@Override
		public String getKey() {
			return key;
		}
	}
}
