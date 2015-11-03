package sakaitakao.android.permissionsearch.activity;

import sakaitakao.android.permissionsearch.R;
import android.app.Activity;
import android.os.Bundle;

/**
 * バージョン情報のアクティビティ
 * 
 * @author takao
 * 
 */
public class AboutActivity extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
	}
}