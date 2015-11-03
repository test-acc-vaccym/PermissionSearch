package sakaitakao.android.permissionsearch.activity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import sakaitakao.android.permissionsearch.R;
import sakaitakao.android.permissionsearch.adaptor.ApplicationListAdaptor;
import sakaitakao.android.permissionsearch.entity.ContextMenuItem;
import sakaitakao.android.permissionsearch.entity.PermissionInfoEx;
import sakaitakao.android.permissionsearch.util.PermissionInfoUtil;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

/**
 * アプリケーションリストのアクティビティ
 * 
 * @author takao
 * 
 */
public class ApplicationListActivity extends Activity {

	/** アプリケーション一覧を表示する条件を保持する Intent の extra データの名前 */
	public static final String INTENTEXTRA_PERMISSION_APP_LIST = ApplicationListActivity.class.getName() + ".PERMISSION_APP_LIST";
	private static final int CONTEXT_MENU_ITEM_ID_SHARE = Menu.FIRST + 1;

	private ContextMenuItem[] contextMenuItems = new ContextMenuItem[] { new ContextMenuItem(CONTEXT_MENU_ITEM_ID_SHARE,
			R.string.context_menu_item_share, android.R.drawable.ic_menu_share, new OnMenuItemClickShareListener()) };

	/**
	 * アプリケーションリストのアダプタ実装
	 */
	private ApplicationListAdaptor applicationListAdaptor;

	private PermissionInfoEx permissionInfoEx;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.application_list);

		// 前の画面から渡されたデータを取得
		permissionInfoEx = getCondition();

		// 画面表示
		PackageManager packageManager = getPackageManager();
		Resources resources = getResources();
		showPermissionLabel(packageManager, resources, permissionInfoEx);
		showProtectionLevel(resources, permissionInfoEx);
		showPermissionName(permissionInfoEx);
		showPermissionDescription(packageManager, permissionInfoEx);
		showList(permissionInfoEx);
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
	private PermissionInfoEx getCondition() {
		Intent intent = getIntent();
		PermissionInfoEx permissionInfoEx = intent.getParcelableExtra(INTENTEXTRA_PERMISSION_APP_LIST);
		if (permissionInfoEx == null) {
			throw new RuntimeException("Please set extra data by Intent#putExtra(" + INTENTEXTRA_PERMISSION_APP_LIST + ", Parcelable)");
		}
		return permissionInfoEx;
	}

	private void showPermissionLabel(PackageManager packageManager, Resources resources, PermissionInfoEx permissionInfoEx) {

		TextView textView = (TextView) findViewById(R.id.application_list_permission_label);
		String name = PermissionInfoUtil.getPermissionLabel(packageManager, resources, permissionInfoEx);
		textView.setText(name);
		textView.requestFocus();
	}

	private void showProtectionLevel(Resources resources, PermissionInfoEx permissionInfoEx) {

		TextView permissionLabelView = (TextView) findViewById(R.id.application_list_protection_level);
		permissionLabelView.setText(PermissionInfoUtil.formatProtectionLevel(resources, permissionInfoEx));
	}

	private void showPermissionName(PermissionInfoEx permissionInfoEx) {
		TextView permissionLabelView = (TextView) findViewById(R.id.application_list_permission_name);
		permissionLabelView.setText(permissionInfoEx.permissionInfo.name);
	}

	private void showPermissionDescription(PackageManager packageManager, PermissionInfoEx permissionInfoEx) {
		TextView permissionLabelView = (TextView) findViewById(R.id.application_list_permission_description);
		permissionLabelView.setText(permissionInfoEx.permissionInfo.loadDescription(packageManager));
	}

	private void showList(PermissionInfoEx permissionInfoEx) {
		ListView appListView = (ListView) findViewById(R.id.application_list_list);
		applicationListAdaptor = new ApplicationListAdaptor(this, R.layout.application_list_item, permissionInfoEx.applicationInfoList);
		appListView.setAdapter(applicationListAdaptor);
	}

	private boolean onOptionsItemShareSelected() {

		// テキストを生成
		String text = createPermissionInfoTextForSharing(getPackageManager(), getResources(), permissionInfoEx);

		// 共有の暗黙的インテントを発行
		Intent intent = new Intent(android.content.Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT, text);
		startActivity(Intent.createChooser(intent, getString(R.string.share_application_list)));

		return false;
	}

	/**
	 * 共有用のテキストを生成する。
	 * 
	 * @param packageManager
	 *            PackageManager
	 * @param resources
	 *            Resources
	 * @param permissionInfoEx
	 *            PermissionInfoEx
	 * @return
	 */
	private String createPermissionInfoTextForSharing(PackageManager packageManager, Resources resources, PermissionInfoEx permissionInfoEx) {

		ByteArrayOutputStream bos = null;
		try {
			bos = new ByteArrayOutputStream();
			PrintWriter pw = new PrintWriter(bos);

			String permissionLabel = PermissionInfoUtil.getPermissionLabel(packageManager, resources, permissionInfoEx);
			// タイトル
			pw.println(resources.getString(R.string.application_list_share_title, permissionLabel));
			pw.println();

			// 概要
			pw.println(resources.getString(R.string.application_list_share_permission_info, permissionLabel));
			pw.println(PermissionInfoUtil.formatProtectionLevel(resources, permissionInfoEx));
			pw.println(permissionInfoEx.permissionInfo.loadDescription(packageManager));
			pw.println();

			// applications
			pw.println(resources.getString(R.string.application_list_share_application_list));
			for (ApplicationInfo applicationInfo : permissionInfoEx.applicationInfoList) {
				// App Name & link
				pw.println(resources.getString(R.string.application_list_share_app_name_and_market_uri, applicationInfo.loadLabel(packageManager),
						applicationInfo.packageName));
			}
			pw.close();
		} finally {
			try {
				bos.close();
			} catch (IOException e) {
				Log.e("", "IOException caught when closing ByteArrayOutputStream.", e);
				throw new RuntimeException(e);
			}
		}
		Log.v("", bos.toString());
		return (bos != null ? bos.toString() : "");
	}

	// -------------------------------------------------------------------------
	// Classes
	// -------------------------------------------------------------------------
	private class OnMenuItemClickShareListener implements OnMenuItemClickListener {
		@Override
		public boolean onMenuItemClick(MenuItem item) {
			return onOptionsItemShareSelected();
		}
	}

}