package sakaitakao.android.permissionsearch.activity;

import java.util.ArrayList;
import java.util.List;

import sakaitakao.android.permissionsearch.R;
import sakaitakao.android.permissionsearch.adaptor.EasySearchListAdaptor;
import sakaitakao.android.permissionsearch.entity.ContextMenuItem;
import sakaitakao.android.permissionsearch.entity.EasySearchInfo;
import sakaitakao.android.permissionsearch.model.EasySearchInfoProvider;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.CompoundButton.OnCheckedChangeListener;

/**
 * かんたん検索画面のアクティビティ
 * 
 * @author takao
 * 
 */
public class EasySearchActivity extends Activity {

	private static final boolean DEFAULT_INCLUDE_SYSTEM_APPS = false;

	private static final int CONTEXT_MENU_ITEM_ID_GOTO_ADVANCED_SEARCH = Menu.FIRST + 1;
	private static final int CONTEXT_MENU_ITEM_ID_PREFERENCE = Menu.FIRST + 2;

	private EasySearchListAdaptor adaptor;
	private ContextMenuItem[] contextMenuItems = new ContextMenuItem[] {
			new ContextMenuItem(CONTEXT_MENU_ITEM_ID_GOTO_ADVANCED_SEARCH, R.string.context_menu_item_goto_advanced_search,
					R.drawable.ic_menu_adv_search, new OptionsItemGoToAdvancedSearchSelected()),
			new ContextMenuItem(CONTEXT_MENU_ITEM_ID_PREFERENCE, R.string.context_menu_item_preference, android.R.drawable.ic_menu_manage,
					new OptionsItemPreferenceSelected()), };

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.easy_search);

		// リストの内容は onResume で取得する。
		// 詳細検索で条件を保存したり、設定画面で条件を消した場合も、リストにすぐに反映されるようにするため。
		adaptor = new EasySearchListAdaptor(this, R.layout.easy_search_list_item, new ArrayList<EasySearchInfo>(), DEFAULT_INCLUDE_SYSTEM_APPS);

		// リストを表示
		showList();

		// チェックボックス
		showIncludeSystemApps();
	}

	/*
	 * (非 Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();

		// 一覧に表示する項目を取得
		Resources resources = getResources();
		EasySearchInfoProvider easySearchInfoProvider = new EasySearchInfoProvider();
		List<EasySearchInfo> easySearchInfoList = easySearchInfoProvider.getEasySearchInfoListForEasySearchActivity(this, resources);

		// リストに反映
		adaptor.setItems(easySearchInfoList);
		adaptor.notifyDataSetChanged();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		for (ContextMenuItem contextMenuItem : contextMenuItems) {
			contextMenuItem.addToMenu(menu);
		}
		return super.onCreateOptionsMenu(menu);
	}

	// -------------------------------------------------------------------------
	// Privates
	// -------------------------------------------------------------------------
	private void showList() {

		ListView listView = (ListView) findViewById(R.id.easy_search_list);
		listView.setAdapter(adaptor);
	}

	private void showIncludeSystemApps() {

		CheckBox checkBox = (CheckBox) findViewById(R.id.easy_search_include_system_apps);
		checkBox.setChecked(adaptor.includeSystemApps);
		checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				adaptor.includeSystemApps = isChecked;
			}
		});
	}

	/**
	 * コンテキストメニューの[詳細検索]の処理
	 * 
	 * @return
	 */
	private boolean onOptionsItemGoToAdvancedSearchSelected() {
		startActivity(new Intent(this, AdvancedSearchActivity.class));
		return false;
	}

	/**
	 * コンテキストメニューの[設定]の処理
	 * 
	 * @return
	 */
	private boolean onOptionsItemPreferenceSelected() {
		startActivity(new Intent(this, MainPreferenceActivity.class));
		return false;
	}

	// -------------------------------------------------------------------------
	// Classes
	// -------------------------------------------------------------------------
	/**
	 * コンテキストメニューの[詳細検索]の処理
	 * 
	 * @author takao
	 */
	private class OptionsItemGoToAdvancedSearchSelected implements OnMenuItemClickListener {
		@Override
		public boolean onMenuItemClick(MenuItem item) {
			return onOptionsItemGoToAdvancedSearchSelected();
		}
	}

	private class OptionsItemPreferenceSelected implements OnMenuItemClickListener {
		@Override
		public boolean onMenuItemClick(MenuItem item) {
			return onOptionsItemPreferenceSelected();
		}
	}
}