package sakaitakao.android.permissionsearch.adaptor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import sakaitakao.android.permissionsearch.R;
import sakaitakao.android.permissionsearch.entity.PermissionInfoEx;
import sakaitakao.android.permissionsearch.util.PermissionInfoUtil;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

/**
 * 詳細検索画面の権限リストのアダプタ
 * 
 * @author takao
 * 
 */
public class AdvancedSearchListAdaptor extends ArrayAdapter<PermissionInfoEx> {

	private static int ITEM_TAG_PERMISSION_NAME = R.string.advanced_search_list_item_tag_permission_name;

	private List<PermissionInfoEx> items;
	private Resources resources;
	private LayoutInflater layoutInflater;
	private PackageManager packageManager;
	private Set<String> checkedPermissionSet;
	private OnItemCheckedChangeListener onItemCheckedChangeListener;

	/**
	 * @param context
	 * @param textViewResourceId
	 * @param items
	 */
	public AdvancedSearchListAdaptor(Context context, int textViewResourceId, List<PermissionInfoEx> items) {
		super(context, textViewResourceId, items);
		this.resources = context.getResources();
		this.items = items;
		this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.packageManager = context.getPackageManager();
		this.checkedPermissionSet = new HashSet<String>();
		this.onItemCheckedChangeListener = null;
	}

	/**
	 * リストを設定する
	 * 
	 * @param items
	 */
	public void setItems(List<PermissionInfoEx> items) {
		this.items.clear();
		this.items.addAll(items);
		removeExtinctFromCheckedPermission();
	}

	/**
	 * @return
	 */
	public Set<String> getCheckedPermission() {
		return checkedPermissionSet;
	}

	/**
	 * @param listener
	 */
	public void setOnItemCheckedChangeListener(OnItemCheckedChangeListener listener) {
		this.onItemCheckedChangeListener = listener;
	}

	/**
	 * 
	 */
	public void checkAll() {
		for (PermissionInfoEx permissionInfoEx : items) {
			checkedPermissionSet.add(permissionInfoEx.permissionInfo.name);
		}
	}

	/**
	 * 
	 */
	public void uncheckAll() {
		checkedPermissionSet.clear();
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
			view = layoutInflater.inflate(R.layout.advanced_search_list_item, null);
		}

		// データを取得
		final PermissionInfoEx permissionInfoEx = items.get(position);

		showPermissionLabel(view, permissionInfoEx);
		showPermissionName(view, permissionInfoEx);

		// このビュー全体でチェックボックスを設定できるようにする
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CheckBox checkBox = (CheckBox) v.findViewById(R.id.advanced_search_list_item_check);
				checkBox.setChecked(!checkBox.isChecked());
			}
		});

		setupCheckBox(view, permissionInfoEx);

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

		TextView textView = (TextView) view.findViewById(R.id.advanced_search_list_item_permission_desc);
		String text = PermissionInfoUtil.getPermissionLabel(packageManager, resources, permissionInfoEx);
		textView.setText(text);
	}

	/**
	 * パーミッション名
	 * 
	 * @param view
	 * @param permissionInfoEx
	 */
	private void showPermissionName(View view, PermissionInfoEx permissionInfoEx) {

		TextView textView = (TextView) view.findViewById(R.id.advanced_search_list_item_permission_name);
		textView.setText(permissionInfoEx.permissionInfo.name);
	}

	/**
	 * チェックボックス
	 * 
	 * @param view
	 * @param permissionInfoEx
	 */
	private void setupCheckBox(View view, final PermissionInfoEx permissionInfoEx) {

		CheckBox checkBox = (CheckBox) view.findViewById(R.id.advanced_search_list_item_check);

		String permissionName = permissionInfoEx.permissionInfo.name;
		checkBox.setTag(ITEM_TAG_PERMISSION_NAME, permissionName);

		// チェックの初期状態
		checkBox.setChecked(checkedPermissionSet.contains(permissionName));

		// チェック状態が変わったら、リスナに通知する。
		checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean flag) {
				onCheckedChange(compoundButton, flag);
			}
		});
	}

	private void onCheckedChange(CompoundButton compoundButton, boolean flag) {

		String permissionName = (String) compoundButton.getTag(ITEM_TAG_PERMISSION_NAME);
		if (flag) {
			checkedPermissionSet.add(permissionName);
		} else {
			checkedPermissionSet.remove(permissionName);
		}
		if (onItemCheckedChangeListener != null) {
			onItemCheckedChangeListener.onItemCheckedChange();
		}
	}

	/**
	 * チェックされたパーミッションがリストになければ消す。
	 */
	private void removeExtinctFromCheckedPermission() {
		// items に存在するものだけで Set を作り直す。
		Set<String> newSet = new HashSet<String>();
		for (PermissionInfoEx permissionInfoEx : items) {
			String permissionName = permissionInfoEx.permissionInfo.name;
			if (checkedPermissionSet.contains(permissionName)) {
				newSet.add(permissionName);
			}
		}
		checkedPermissionSet = newSet;
	}

	// -------------------------------------------------------------------------
	// Interfaces
	// -------------------------------------------------------------------------
	public interface OnItemCheckedChangeListener {
		void onItemCheckedChange();
	}
}