package sakaitakao.android.permissionsearch.activity;

import sakaitakao.android.permissionsearch.R;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.util.Log;

/**
 * 設定画面のアクティビティ
 * 
 * @author takao
 */
public class MainPreferenceActivity extends PreferenceActivity {

	private static final String SUB_PREFERENCE_SAVED_SEARCH_COND_KEY = "saved_advanced_search_condition";
	private static final String SUB_PREFERENCE_ABOUT_KEY = "about";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preference);
	}

	/*
	 * (非 Javadoc)
	 * 
	 * @seeandroid.preference.PreferenceActivity#onPreferenceTreeClick(android.
	 * preference.PreferenceScreen, android.preference.Preference)
	 */
	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {

		Log.v("onPreferenceTreeClick", "preference.key = " + preference.getKey());

		if (SUB_PREFERENCE_SAVED_SEARCH_COND_KEY.equals(preference.getKey())) {
			startActivity(new Intent(this, SavedAdvancedSearchConditionPreferenceActivity.class));
		} else if (SUB_PREFERENCE_ABOUT_KEY.equals(preference.getKey())) {
			startActivity(new Intent(this, AboutActivity.class));
		}
		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}
}