package sakaitakao.android.permissionsearch.adaptor;

import java.util.List;

import sakaitakao.android.permissionsearch.R;
import sakaitakao.android.permissionsearch.activity.PermissionListActivity;
import sakaitakao.android.permissionsearch.activity.PermissionListActivity.SearchCondition;
import sakaitakao.android.permissionsearch.entity.EasySearchInfo;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * かんたん検索画面の項目リストのアダプタ
 * 
 * @author takao
 * 
 */
public class EasySearchListAdaptor extends ArrayAdapter<EasySearchInfo> {

	public boolean includeSystemApps;

	private List<EasySearchInfo> items;
	private Context context;
	private LayoutInflater layoutInflater;

	/**
	 * @param context
	 * @param textViewResourceId
	 * @param items
	 */
	public EasySearchListAdaptor(Context context, int textViewResourceId, List<EasySearchInfo> items, boolean includeSystenApps) {
		super(context, textViewResourceId, items);
		this.context = context;
		this.items = items;
		this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.includeSystemApps = includeSystenApps;
	}

	/**
	 * @param items
	 */
	public void setItems(List<EasySearchInfo> items) {
		this.items.clear();
		this.items.addAll(items);
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
			view = layoutInflater.inflate(R.layout.easy_search_list_item, null);
		}
		// データを取得
		final EasySearchInfo easySearchInfo = items.get(position);
		showName(view, easySearchInfo);
		showDescription(view, easySearchInfo);

		// onclick イベント
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {

				// Permission 一覧へ遷移
				SearchCondition searchCondition = new SearchCondition();
				searchCondition.includeSystemApps = includeSystemApps;
				searchCondition.permissionNamePatternList = easySearchInfo.permissionNamePatternList;
				searchCondition.protectionLevelList = easySearchInfo.protectionLevelList;

				Intent intent = new Intent(context, PermissionListActivity.class);
				PermissionListActivity.setSearchCondition(intent, searchCondition);
				context.startActivity(intent);
			}
		});

		return view;
	}

	// -------------------------------------------------------------------------
	// Privates
	// -------------------------------------------------------------------------
	/**
	 * @param view
	 * @param easySearchInfo
	 */
	private void showDescription(View view, EasySearchInfo easySearchInfo) {

		TextView textView = (TextView) view.findViewById(R.id.easy_search_list_item_primary);
		textView.setText(easySearchInfo.name);
	}

	/**
	 * @param view
	 * @param easySearchInfo
	 */
	private void showName(View view, EasySearchInfo easySearchInfo) {

		TextView textView = (TextView) view.findViewById(R.id.easy_search_list_item_secondary);
		textView.setText(easySearchInfo.description);
	}
}