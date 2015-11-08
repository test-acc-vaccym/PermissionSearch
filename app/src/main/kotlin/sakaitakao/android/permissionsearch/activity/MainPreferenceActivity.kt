package sakaitakao.android.permissionsearch.activity

import android.content.Intent
import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceActivity
import android.preference.PreferenceScreen
import android.util.Log
import sakaitakao.android.permissionsearch.R

/**
 * 設定画面のアクティビティ

 * @author takao
 */
class MainPreferenceActivity : PreferenceActivity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preference)
    }

    /*
	 * (非 Javadoc)
	 * 
	 * @seeandroid.preference.PreferenceActivity#onPreferenceTreeClick(android.
	 * preference.PreferenceScreen, android.preference.Preference)
	 */
    override fun onPreferenceTreeClick(preferenceScreen: PreferenceScreen, preference: Preference): Boolean {

        Log.v("onPreferenceTreeClick", "preference.key = " + preference.key)

        if (SUB_PREFERENCE_SAVED_SEARCH_COND_KEY == preference.key) {
            startActivity(Intent(this, SavedAdvancedSearchConditionPreferenceActivity::class.java))
        } else if (SUB_PREFERENCE_ABOUT_KEY == preference.key) {
            startActivity(Intent(this, AboutActivity::class.java))
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference)
    }

    companion object {

        private val SUB_PREFERENCE_SAVED_SEARCH_COND_KEY = "saved_advanced_search_condition"
        private val SUB_PREFERENCE_ABOUT_KEY = "about"
    }
}