package sakaitakao.android.permissionsearch.dialog

import android.app.Dialog
import android.content.Context
import android.widget.Button
import sakaitakao.android.permissionsearch.R

/**
 * 詳細検索の検索条件の名前を入力するためのダイアログ

 * @author takao
 */
class SaveAdvancedSearchConditionDialog
/**
 * ダイアログを生成します。

 * @param context
 */
(context: Context) : Dialog(context) {

    init {

        setContentView(R.layout.dlg_save_condition)
        setTitle(R.string.title_dlg_save_condition)
    }

    /**
     * OK ボタンが押されたら呼び出すリスナを設定する。

     * @param onClickListener
     */
    fun setOkButtonOnClickListener(onClickListener: android.view.View.OnClickListener) {
        val buttonOk = findViewById(R.id.dlg_save_condition_ok) as Button
        buttonOk.setOnClickListener(onClickListener)
    }
}
