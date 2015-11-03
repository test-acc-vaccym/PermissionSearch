package sakaitakao.android.permissionsearch.activity;

import java.util.List;

import sakaitakao.android.permissionsearch.Config;
import sakaitakao.android.permissionsearch.R;
import sakaitakao.android.permissionsearch.adaptor.SavedAdvancedSearchConditionAdaptor;
import sakaitakao.android.permissionsearch.adaptor.SavedAdvancedSearchConditionAdaptor.OnItemClickListener;
import sakaitakao.android.permissionsearch.entity.EasySearchInfo;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

/**
 * 設定画面(詳細検索条件)のアクティビティ
 * 
 * @author takao
 */
public class SavedAdvancedSearchConditionPreferenceActivity extends Activity {

	private static final int DIALOG_ID_QUERY_REMOVE_SEARCH_COND = 1;

	private SavedAdvancedSearchConditionAdaptor adaptor;
	private QueryRemoveSearchConditionDialogInfo queryRemoveSearchConditionDialogInfo;;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.saved_advanced_search_condition);

		Config config = new Config(this);
		List<EasySearchInfo> list = config.getAdvancedSearchConditions();
		adaptor = new SavedAdvancedSearchConditionAdaptor(this, R.layout.saved_advanced_search_condition_item, list);
		adaptor.setOnItemClickListener(new OnSavedSearchCondItemClickListener());

		ListView listView = (ListView) findViewById(R.id.saved_advanced_search_condition_list);
		listView.setAdapter(adaptor);
	}

	/*
	 * (非 Javadoc)
	 * 
	 * @see android.app.Activity#onCreateDialog(int)
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog ret = null;
		if (id == DIALOG_ID_QUERY_REMOVE_SEARCH_COND) {
			ret = createQueryRemoveSearchConditionDialog();
		}
		return ret;
	}

	// -------------------------------------------------------------------------
	// Privates
	// -------------------------------------------------------------------------
	private void onSavedSearchCondItemClick(int position, EasySearchInfo easySearchInfo) {

		// 消しちゃってもいいのか聞いてみる
		queryRemoveSearchConditionDialogInfo = new QueryRemoveSearchConditionDialogInfo();
		queryRemoveSearchConditionDialogInfo.position = position;
		queryRemoveSearchConditionDialogInfo.easySearchInfo = easySearchInfo;

		showDialog(DIALOG_ID_QUERY_REMOVE_SEARCH_COND);
		// onCreateDialog() へ
	}

	private Dialog createQueryRemoveSearchConditionDialog() {

		AlertDialog.Builder dlgBuilder = new AlertDialog.Builder(this);
		dlgBuilder.setTitle(R.string.query_remove_search_condition_dialog_title);
		dlgBuilder.setMessage(getResources().getString(R.string.query_remove_search_condition_dialog_message,
				queryRemoveSearchConditionDialogInfo.easySearchInfo.name));
		// 「はい」ボタン
		dlgBuilder.setPositiveButton(R.string.button_text_yes, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				onQueryRemoveSearchConditionDialogPositive();
			}
		});
		// 「いいえ」ボタン
		dlgBuilder.setNegativeButton(R.string.button_text_no, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				onQueryRemoveSearchConditionDialogNegative();
			}
		});
		return dlgBuilder.show();
	}

	private void onQueryRemoveSearchConditionDialogPositive() {

		// 消す
		Config config = new Config(this);
		config.removeAdvancedSearchCondition(queryRemoveSearchConditionDialogInfo.position);
		Toast.makeText(
				this,
				getResources().getString(R.string.query_remove_search_condition_dialog_removed,
						queryRemoveSearchConditionDialogInfo.easySearchInfo.name), Toast.LENGTH_LONG).show();
		removeDialog(DIALOG_ID_QUERY_REMOVE_SEARCH_COND);
		queryRemoveSearchConditionDialogInfo = null;

		// リストを更新
		List<EasySearchInfo> items = config.getAdvancedSearchConditions();
		adaptor.setItems(items);
		adaptor.notifyDataSetChanged();
	}

	private void onQueryRemoveSearchConditionDialogNegative() {

		removeDialog(DIALOG_ID_QUERY_REMOVE_SEARCH_COND);
		queryRemoveSearchConditionDialogInfo = null;
	}

	// -------------------------------------------------------------------------
	// Classes
	// -------------------------------------------------------------------------
	/**
	 * {@link SavedAdvancedSearchConditionAdaptor} からアイテムのタップ通知を受け取る
	 * 
	 * @author takao
	 * 
	 */
	private class OnSavedSearchCondItemClickListener implements OnItemClickListener {
		@Override
		public void onClick(int position, EasySearchInfo easySearchInfo) {
			onSavedSearchCondItemClick(position, easySearchInfo);
		}
	}

	/**
	 * 項目削除時の問い合わせダイアログに渡す情報を管理する。 android 2.2 以降ではいらない子。
	 * 
	 * @author takao
	 */
	private static class QueryRemoveSearchConditionDialogInfo {
		public int position;
		public EasySearchInfo easySearchInfo;
	}
}