package sakaitakao.android.permissionsearch.test.activity;

import sakaitakao.android.permissionsearch.activity.AboutActivity;
import android.test.ActivityInstrumentationTestCase2;

public class AboutActivityTest extends ActivityInstrumentationTestCase2<AboutActivity> {

	private AboutActivity activity;

	public AboutActivityTest() {
		super("sakaitakao.android.permissionsearch.activity", AboutActivity.class);
	}

	public AboutActivityTest(String pkg, Class<AboutActivity> activityClass) {
		super(pkg, activityClass);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	/*
	 * (非 Javadoc)
	 * 
	 * @see android.test.ActivityInstrumentationTestCase2#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();

		activity = getActivity();
	}

}
