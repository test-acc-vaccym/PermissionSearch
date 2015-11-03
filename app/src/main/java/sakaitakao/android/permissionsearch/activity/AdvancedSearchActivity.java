package sakaitakao.android.permissionsearch.activity;

import java.util.ArrayList;
import java.util.List;

import sakaitakao.android.permissionsearch.Config;
import sakaitakao.android.permissionsearch.R;
import sakaitakao.android.permissionsearch.activity.PermissionListActivity.SearchCondition;
import sakaitakao.android.permissionsearch.adaptor.AdvancedSearchListAdaptor;
import sakaitakao.android.permissionsearch.adaptor.AdvancedSearchListAdaptor.OnItemCheckedChangeListener;
import sakaitakao.android.permissionsearch.dialog.SaveAdvancedSearchConditionDialog;
import sakaitakao.android.permissionsearch.entity.ContextMenuItem;
import sakaitakao.android.permissionsearch.entity.EasySearchInfo;
import sakaitakao.android.permissionsearch.entity.PermissionInfoEx;
import sakaitakao.android.permissionsearch.model.AppPermissionsFinder;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

/**
 * 詳細検索のアクティビティ
 * 
 * @author takao
 * 
 */
public class AdvancedSearchActivity extends Activity {

	private static final int CONTEXT_MENU_ITEM_ID_SELECT_ALL = Menu.FIRST + 1;
	private static final int CONTEXT_MENU_ITEM_ID_DESELECT_ALL = Menu.FIRST + 2;
	private static final int CONTEXT_MENU_ITEM_ID_SAVE_CONDITION = Menu.FIRST + 3;
	private static final int DIALOG_ID_SAVE_CONDITION = 0;

	private AdvancedSearchListAdaptor adaptor;

	private ContextMenuItem[] contextMenuItems = new ContextMenuItem[] {
			new ContextMenuItem(CONTEXT_MENU_ITEM_ID_SELECT_ALL, R.string.context_menu_item_select_all, R.drawable.ic_menu_select_all,
					new OnMenuItemClickSelectAllListener()),
			new ContextMenuItem(CONTEXT_MENU_ITEM_ID_DESELECT_ALL, R.string.context_menu_item_deselect_all, R.drawable.ic_menu_unselect_all,
					new OnMenuItemClickDeselectAllListener()),
			new ContextMenuItem(CONTEXT_MENU_ITEM_ID_SAVE_CONDITION, R.string.context_menu_item_save_condition, android.R.drawable.ic_menu_save,
					new OnMenuItemClickSaveConditionListener()) };

	/*
	 * (非 Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.advanced_search);

		List<PermissionInfoEx> list = getPermissionInfoExList(false);

		showList(list);
		setupIncludeSystemApps();
		setupSearchButton();
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPrepareOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		MenuItem menuItemSelectAll = menu.findItem(CONTEXT_MENU_ITEM_ID_SELECT_ALL);
		MenuItem menuItemDeselectAll = menu.findItem(CONTEXT_MENU_ITEM_ID_DESELECT_ALL);
		MenuItem menuItemSaveCondition = menu.findItem(CONTEXT_MENU_ITEM_ID_SAVE_CONDITION);
		int checkCount = adaptor.getCheckedPermission().size();

		// チェックの件数によって表示する項目を変える
		menuItemSelectAll.setVisible((checkCount == 0));
		menuItemDeselectAll.setVisible((checkCount > 0));
		menuItemSaveCondition.setVisible((checkCount > 0));
		return super.onPrepareOptionsMenu(menu);
	}

	/*
	 * (非 Javadoc)
	 * 
	 * @see android.app.Activity#onCreateDialog(int)
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		if (id == DIALOG_ID_SAVE_CONDITION) {
			dialog = createSaveConditionDialog();
		}
		return dialog;
	}

	// -------------------------------------------------------------------------
	// Privates
	// -------------------------------------------------------------------------
	private List<PermissionInfoEx> getPermissionInfoExList(boolean includeSystemApps) {

		AppPermissionsFinder.SearchCondition condition = new AppPermissionsFinder.SearchCondition();
		condition.includeSystemApps = includeSystemApps;
		condition.permissionNamePatternList = null;
		condition.protectionLevelSet = null;

		PackageManager packageManager = getPackageManager();
		AppPermissionsFinder aph = new AppPermissionsFinder();
		return aph.getPermissionInfoExList(packageManager, condition);
	}

	private void showList(List<PermissionInfoEx> list) {

		ListView listView = (ListView) findViewById(R.id.advanced_search_list);
		adaptor = new AdvancedSearchListAdaptor(this, R.layout.advanced_search_list_item, list);
		adaptor.setOnItemCheckedChangeListener(new OnItemCheckedChangeListener() {
			@Override
			public void onItemCheckedChange() {
				onListItemCheckedChange();
			}
		});
		listView.setAdapter(adaptor);
	}

	private void setupIncludeSystemApps() {

		CheckBox button = (CheckBox) findViewById(R.id.advanced_search_include_system_apps);
		button.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton compoundbutton, boolean flag) {
				onIncludeSystemAppsCheckedChange(compoundbutton, flag);
			}
		});
	}

	/**
	 * 検索ボタンを設定する
	 */
	private void setupSearchButton() {

		Button button = (Button) findViewById(R.id.advanced_search_search);
		changeStateSearchButton(button);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onSearchClick(v);
			}
		});
	}

	/**
	 * リストの項目のチェック状態が変わったら、呼び出される。
	 */
	private void onListItemCheckedChange() {
		changeStateSearchButton(null);
	}

	/**
	 * [プリインストールアプリを含む]チェックボックスの状態が変わったら呼び出される。
	 * 
	 * @param compoundButton
	 *            チェックボックス
	 * @param flag
	 *            状態
	 */
	private void onIncludeSystemAppsCheckedChange(CompoundButton compoundButton, boolean flag) {

		// リストを取得
		List<PermissionInfoEx> list = getPermissionInfoExList(flag);

		// リストを更新
		adaptor.setItems(list);
		adaptor.notifyDataSetChanged();
	}

	/**
	 * [検索]ボタンの押下処理
	 * 
	 * @param view
	 */
	private void onSearchClick(View view) {

		// /system アプリを含むボタン
		CheckBox includeSysAppsCheckBox = (CheckBox) findViewById(R.id.advanced_search_include_system_apps);

		// 検索条件
		SearchCondition searchCondition = new SearchCondition();
		searchCondition.includeSystemApps = includeSysAppsCheckBox.isChecked();
		searchCondition.permissionNamePatternList = new ArrayList<String>(adaptor.getCheckedPermission());
		searchCondition.protectionLevelList = null;

		// パーミッション一覧へgo
		Intent intent = new Intent(this, PermissionListActivity.class);
		PermissionListActivity.setSearchCondition(intent, searchCondition);
		startActivity(intent);
	}

	/**
	 * すべてチェックする
	 * 
	 * @return
	 */
	private boolean onOptionsItemSelectAllSelected() {
		adaptor.checkAll();
		adaptor.notifyDataSetChanged();
		return true;
	}

	/**
	 * すべてチェック解除する
	 * 
	 * @return
	 */
	private boolean onOptionsItemDeselectAllSelected() {
		adaptor.uncheckAll();
		adaptor.notifyDataSetChanged();
		return true;
	}

	/**
	 * 条件を保存
	 * 
	 * @return
	 */
	private boolean onOptionsItemSaveConditionSelected() {
		showDialog(DIALOG_ID_SAVE_CONDITION);
		return true;
	}

	/**
	 * 検索ボタンの状態を変える
	 * 
	 * @param button
	 *            検索ボタン
	 */
	private void changeStateSearchButton(Button button) {
		if (button == null) {
			button = (Button) findViewById(R.id.advanced_search_search);
		}
		button.setEnabled(adaptor.getCheckedPermission().size() > 0);
	}

	/**
	 * 検索条件を保存するダイアログを生成する
	 * 
	 * @return
	 */
	private Dialog createSaveConditionDialog() {

		final SaveAdvancedSearchConditionDialog dialog = new SaveAdvancedSearchConditionDialog(this);
		// OK ボタンが押された時の処理
		dialog.setOkButtonOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				onSaveCondition(dialog);
			}
		});
		// キャンセルされた時の処理
		dialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				onSaveConditionDialogCanceled();
			}
		});
		return dialog;
	}

	/**
	 * 検索条件を保存するダイアログで OK ボタンが押されたら
	 * 
	 * @param dialog
	 */
	private void onSaveCondition(Dialog dialog) {

		EditText editName = (EditText) dialog.findViewById(R.id.dlg_save_condition_name);
		String name = editName.getText().toString();
		if (name.length() == 0) {
			Toast.makeText(this, R.string.name_is_empty, Toast.LENGTH_LONG).show();
			return;
		}

		// ほぞんしちゃう
		EasySearchInfo easySearchInfo = new EasySearchInfo();
		easySearchInfo.name = name;
		easySearchInfo.permissionNamePatternList = new ArrayList<String>(adaptor.getCheckedPermission());

		Config config = new Config(this);
		config.addAdvancedSearchCondition(easySearchInfo);

		// ダイアログを完全に消す。(dismiss では再利用されちゃう)
		removeDialog(DIALOG_ID_SAVE_CONDITION);

		// トーストでお知らせ
		Toast.makeText(this, getResources().getString(R.string.advanced_search_cond_saved, name), Toast.LENGTH_LONG).show();
	}

	/**
	 * 検索条件を保存するダイアログで back キーが押されたら
	 */
	protected void onSaveConditionDialogCanceled() {
		// ダイアログを完全に消す。
		removeDialog(DIALOG_ID_SAVE_CONDITION);
	}

	// -------------------------------------------------------------------------
	// Classes
	// -------------------------------------------------------------------------
	/**
	 * コンテキストメニューの「すべて選択」をハンドルする
	 * 
	 * @author takao
	 */
	private class OnMenuItemClickSelectAllListener implements OnMenuItemClickListener {
		@Override
		public boolean onMenuItemClick(MenuItem item) {
			return onOptionsItemSelectAllSelected();
		}
	}

	/**
	 * コンテキストメニューの「すべて選択解除」をハンドルする
	 * 
	 * @author takao
	 */
	private class OnMenuItemClickDeselectAllListener implements OnMenuItemClickListener {
		@Override
		public boolean onMenuItemClick(MenuItem item) {
			return onOptionsItemDeselectAllSelected();
		}
	}

	/**
	 * @author takao
	 * 
	 */
	private class OnMenuItemClickSaveConditionListener implements OnMenuItemClickListener {
		@Override
		public boolean onMenuItemClick(MenuItem item) {
			return onOptionsItemSaveConditionSelected();
		}
	}
}