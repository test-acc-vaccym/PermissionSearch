package sakaitakao.android.permissionsearch.adaptor;

import java.util.List;

import sakaitakao.android.permissionsearch.Config;
import sakaitakao.android.permissionsearch.R;
import sakaitakao.android.permissionsearch.R.drawable;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * アプリ一覧画面のアプリリストのアダプタ
 * 
 * @author takao
 * 
 */
public class ApplicationListAdaptor extends ArrayAdapter<ApplicationInfo> {

	private static final String MARKET_DETAILS_URI_PREFIX = "market://details?id=";

	private List<ApplicationInfo> items;
	private Context context;
	private LayoutInflater layoutInflater;
	private PackageManager packageManager;

	/**
	 * @param context
	 * @param textViewResourceId
	 * @param items
	 */
	public ApplicationListAdaptor(Context context, int textViewResourceId, List<ApplicationInfo> items) {
		super(context, textViewResourceId, items);
		this.context = context;
		this.items = items;
		this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.packageManager = context.getPackageManager();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		// リスト項目の View を生成。
		View view = convertView;
		if (view == null) {
			view = layoutInflater.inflate(R.layout.application_list_item, null);
		}

		// データを取得
		final ApplicationInfo applicationInfo = items.get(position);

		showApplicationLabel(view, applicationInfo);
		showApplicationName(view, applicationInfo);
		showApplicationIcon(view, applicationInfo);

		// onclick イベント
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				onItemClicked(applicationInfo);
			}
		});

		return view;
	}

	// -------------------------------------------------------------------------
	// Privates
	// -------------------------------------------------------------------------
	/**
	 * アプリ名
	 * 
	 * @param view
	 * @param permissionInfoWithApplicationList
	 */
	private void showApplicationLabel(View view, ApplicationInfo applicationInfo) {
		TextView textView = (TextView) view.findViewById(R.id.application_list_item_primary);
		textView.setText(applicationInfo.loadLabel(packageManager));
	}

	/**
	 * @param view
	 * @param applicationInfo
	 */
	private void showApplicationName(View view, ApplicationInfo applicationInfo) {

		TextView textView = (TextView) view.findViewById(R.id.application_list_item_secondary);
		textView.setText(applicationInfo.packageName);
	}

	/**
	 * @param view
	 * @param applicationInfo
	 */
	private void showApplicationIcon(View view, ApplicationInfo applicationInfo) {

		ImageView imgView = (ImageView) view.findViewById(R.id.application_list_item_icon);
		Drawable icon = applicationInfo.loadIcon(packageManager);
		if (icon != null) {
			imgView.setImageDrawable(icon);
		} else {
			imgView.setImageResource(drawable.ic_launcher);
		}
	}

	private void onItemClicked(ApplicationInfo applicationInfo) {

		Config config = new Config(context);
		switch (config.getApplicationInfo()) {
		case MARKET:
			goToMarket(applicationInfo.packageName);
			break;

		case SETTINGS:
			goToSettings(applicationInfo.packageName);
			break;
		}
	}

	/**
	 * @param packageName
	 */
	private void goToSettings(String packageName) {

		// 参考
		try {
			// 設定へ遷移
			Intent intent = null;
			switch (Build.VERSION.SDK_INT) {
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6: // ～2.0 は無視する
				Toast.makeText(context, R.string.settings_not_supported, Toast.LENGTH_LONG).show();
				break;

			case 7:
				// 2.1
				// http://grepcode.com/file/repository.grepcode.com/java/ext/com.google.android/android-apps/2.1_r2/com/android/settings/InstalledAppDetails.java/
				intent = new Intent();
				intent.putExtra("com.android.settings.ApplicationPkgName", packageName);
				intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
				break;

			case 8:
				// 2.2
				// http://grepcode.com/file/repository.grepcode.com/java/ext/com.google.android/android-apps/2.2.1_r1/com/android/settings/InstalledAppDetails.java/
				intent = new Intent();
				intent.putExtra("pkg", packageName);
				// intent.setData(Uri.fromParts("package", packageName, null));
				intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
				break;

			default:
				// 2.3以降
				// http://code.google.com/p/sdwatch/source/browse/src/com/beaglebros/SDWatch/NotificationClicked.java?spec=svna6588c0c18c69e8e8b87a561f659a04ab7f92baa&r=a6588c0c18c69e8e8b87a561f659a04ab7f92baa
				intent = new Intent();
				intent.setData(Uri.fromParts("package", packageName, null));
				intent.setClassName("com.android.settings", "com.android.settings.applications.InstalledAppDetails");
				break;
			}
			if (intent != null) {
				context.startActivity(intent);
			}
		} catch (ActivityNotFoundException e) {
			// マーケットアプリなし
			Toast.makeText(context, R.string.settings_not_found, Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * @param packageName
	 */
	private void goToMarket(String packageName) {

		try {
			// マーケットへ遷移
			Uri uri = Uri.parse(MARKET_DETAILS_URI_PREFIX + packageName);
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			context.startActivity(intent);
		} catch (ActivityNotFoundException e) {
			// マーケットアプリなし
			Toast.makeText(context, R.string.market_not_found, Toast.LENGTH_LONG).show();
		}
	}
}