package sakaitakao.android.permissionsearch.adaptor;

import java.util.List;

import sakaitakao.android.permissionsearch.R;
import sakaitakao.android.permissionsearch.entity.EasySearchInfo;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * 検索条件設定画面の検索条件リストのアダプタ
 * 
 * @author takao
 * 
 */
public class SavedAdvancedSearchConditionAdaptor extends ArrayAdapter<EasySearchInfo> {

	private List<EasySearchInfo> items;
	private LayoutInflater layoutInflater;
	private OnItemClickListener onItemClickListener;

	/**
	 * @param context
	 * @param textViewResourceId
	 * @param items
	 */
	public SavedAdvancedSearchConditionAdaptor(Context context, int textViewResourceId, List<EasySearchInfo> items) {
		super(context, textViewResourceId, items);
		this.items = items;
		this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	/**
	 * @param onItemClickListener
	 *            セットする onItemClickListener
	 */
	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
	}

	/**
	 * @param items
	 *            セットする items
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
			view = layoutInflater.inflate(R.layout.saved_advanced_search_condition_item, null);
		}

		// データを取得
		final EasySearchInfo easySearchInfo = items.get(position);
		final int index = position;

		showName(view, easySearchInfo);
		showDetail(view, easySearchInfo);

		// onclick イベント
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (onItemClickListener != null) {
					onItemClickListener.onClick(index, easySearchInfo);
				}
			}
		});

		return view;
	}

	// -------------------------------------------------------------------------
	// Privates
	// -------------------------------------------------------------------------
	private void showName(View view, EasySearchInfo easySearchInfo) {

		TextView textView = (TextView) view.findViewById(R.id.saved_advanced_search_condition_item_name);
		textView.setText(easySearchInfo.name);
	}

	private void showDetail(View view, EasySearchInfo easySearchInfo) {

		TextView textView = (TextView) view.findViewById(R.id.saved_advanced_search_condition_item_detail);
		StringBuilder sb = new StringBuilder();
		for (String name : easySearchInfo.permissionNamePatternList) {
			if (sb.length() > 0) {
				sb.append(", ");
			}
			sb.append(name);
		}
		textView.setText(sb.toString());
	}

	// -------------------------------------------------------------------------
	// Interfaces
	// -------------------------------------------------------------------------
	public interface OnItemClickListener {
		public void onClick(int position, EasySearchInfo easySearchInfo);
	}
}