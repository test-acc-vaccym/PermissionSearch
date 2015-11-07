package sakaitakao.android.permissionsearch.activity

import android.app.Activity
import android.os.Bundle
import sakaitakao.android.permissionsearch.R

/**
 * バージョン情報のアクティビティ

 * @author takao
 */
class AboutActivity : Activity() {
    /** Called when the activity is first created.  */
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.about)
    }
}