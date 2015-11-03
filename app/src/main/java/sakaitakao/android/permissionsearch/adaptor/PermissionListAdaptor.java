package sakaitakao.android.permissionsearch.adaptor;

import java.util.List;

import sakaitakao.android.permissionsearch.R;
import sakaitakao.android.permissionsearch.activity.ApplicationListActivity;
import sakaitakao.android.permissionsearch.entity.PermissionInfoEx;
import sakaitakao.android.permissionsearch.util.PermissionInfoUtil;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * 権限一覧画面の権限リストのアダプタ
 * 
 * @author takao
 * 
 */
public class PermissionListAdaptor extends ArrayAdapter<PermissionInfoEx> {

	private List<PermissionInfoEx> items;
	private Context context;
	private Resources resources;
	private LayoutInflater layoutInflater;
	private PackageManager packageManager;

	/**
	 * @param context
	 * @param textViewResourceId
	 * @param items
	 */
	public PermissionListAdaptor(Context context, int textViewResourceId, List<PermissionInfoEx> items) {
		super(context, textViewResourceId, items);
		this.context = context;
		this.resources = context.getResources();
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
			view = layoutInflater.inflate(R.layout.permission_list_item, null);
		}

		// データを取得
		final PermissionInfoEx permissionInfoEx = items.get(position);

		showPermissionLabel(view, permissionInfoEx);
		showProtectionLevel(view, permissionInfoEx);
		showApplicationCount(view, permissionInfoEx);

		// onclick イベント
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {

				// アプリ一覧へ遷移
				Intent intent = new Intent();
				intent.setClass(context, ApplicationListActivity.class);
				intent.putExtra(ApplicationListActivity.INTENTEXTRA_PERMISSION_APP_LIST, permissionInfoEx);
				context.startActivity(intent);
			}
		});

		return view;
	}

	// -------------------------------------------------------------------------
	// Privates
	// -------------------------------------------------------------------------
	/**
	 * パーミッションラベル
	 * 
	 * @param view
	 * @param permissionInfoEx
	 */
	private void showPermissionLabel(View view, PermissionInfoEx permissionInfoEx) {

		TextView textView = (TextView) view.findViewById(R.id.permission_list_item_primary);
		String text = PermissionInfoUtil.getPermissionLabel(packageManager, resources, permissionInfoEx);
		textView.setText(text);
	}

	/**
	 * プロテクションレヴェル
	 * 
	 * @param view
	 * @param permissionInfoEx
	 */
	private void showProtectionLevel(View view, PermissionInfoEx permissionInfoEx) {

		TextView textView = (TextView) view.findViewById(R.id.permission_list_item_secondary);
		textView.setText(PermissionInfoUtil.formatProtectionLevel(resources, permissionInfoEx));
	}

	/**
	 * 利用アプリ数
	 * 
	 * @param view
	 * @param permissionInfoEx
	 */
	private void showApplicationCount(View view, PermissionInfoEx permissionInfoEx) {

		TextView applicationsLabelView = (TextView) view.findViewById(R.id.permission_list_item_tertiary);
		applicationsLabelView.setText(resources.getString(R.string.hit_applications, permissionInfoEx.applicationInfoList.size()));
	}
}