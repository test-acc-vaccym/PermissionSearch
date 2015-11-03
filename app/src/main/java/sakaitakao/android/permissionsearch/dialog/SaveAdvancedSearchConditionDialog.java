package sakaitakao.android.permissionsearch.dialog;

import sakaitakao.android.permissionsearch.R;
import android.app.Dialog;
import android.content.Context;
import android.widget.Button;

/**
 * 詳細検索の検索条件の名前を入力するためのダイアログ
 * 
 * @author takao
 * 
 */
public class SaveAdvancedSearchConditionDialog extends Dialog {

	/**
	 * ダイアログを生成します。
	 * 
	 * @param context
	 */
	public SaveAdvancedSearchConditionDialog(Context context) {
		super(context);

		setContentView(R.layout.dlg_save_condition);
		setTitle(R.string.title_dlg_save_condition);
	}

	/**
	 * OK ボタンが押されたら呼び出すリスナを設定する。
	 * 
	 * @param onClickListener
	 */
	public void setOkButtonOnClickListener(android.view.View.OnClickListener onClickListener) {
		Button buttonOk = (Button) findViewById(R.id.dlg_save_condition_ok);
		buttonOk.setOnClickListener(onClickListener);
	}
}
