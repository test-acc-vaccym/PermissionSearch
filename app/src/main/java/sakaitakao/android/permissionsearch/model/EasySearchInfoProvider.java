package sakaitakao.android.permissionsearch.model;

import java.util.ArrayList;
import java.util.List;

import net.arnx.jsonic.JSON;
import sakaitakao.android.permissionsearch.Config;
import sakaitakao.android.permissionsearch.R;
import sakaitakao.android.permissionsearch.entity.EasySearchInfo;
import android.content.Context;
import android.content.res.Resources;

/**
 * 簡単検索の条件を検索する
 * 
 * @author takao
 * 
 */
public class EasySearchInfoProvider {

	/**
	 * Constructor
	 */
	public EasySearchInfoProvider() {
	}

	/**
	 * @param resources
	 * @return
	 */
	public List<EasySearchInfo> getEasySearchInfoListForEasySearchActivity(Context context, Resources resources) {

		// 保存された詳細検索
		List<EasySearchInfo> ret = new ArrayList<EasySearchInfo>();
		ret.addAll(getSavedAdvancedSearchConditionList(context));

		// 簡単検索
		ret.addAll(getEasySearchInfoList(resources));
		return ret;
	}

	// -------------------------------------------------------------------------
	// Privates
	// -------------------------------------------------------------------------
	private List<EasySearchInfo> getSavedAdvancedSearchConditionList(Context context) {

		Config config = new Config(context);
		return config.getAdvancedSearchConditions();
	}

	private List<EasySearchInfo> getEasySearchInfoList(Resources resources) {

		String[] jsonData = resources.getStringArray(R.array.easy_search_info);
		List<EasySearchInfo> ret = new ArrayList<EasySearchInfo>();
		for (String json : jsonData) {
			EasySearchInfo item = JSON.decode(json, EasySearchInfo.class);
			ret.add(item);
		}
		return ret;
	}
}