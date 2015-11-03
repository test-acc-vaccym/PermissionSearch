package sakaitakao.android.permissionsearch.test.activity;

import sakaitakao.android.permissionsearch.R;
import sakaitakao.android.permissionsearch.activity.EasySearchActivity;
import android.content.res.Resources;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Adapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

public class EasySearchActivityTest extends ActivityInstrumentationTestCase2<EasySearchActivity> {

	private EasySearchActivity activity;
	private Resources resources;
	private TextView description;
	private CheckBox includeSystemApps;
	private ListView listView;

	public EasySearchActivityTest() {
		super("sakaitakao.android.permissionsearch", EasySearchActivity.class);
	}

	public EasySearchActivityTest(String pkg, Class<EasySearchActivity> activityClass) {
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
		resources = activity.getResources();
		description = (TextView) activity.findViewById(R.id.easy_search_description);
		includeSystemApps = (CheckBox) activity.findViewById(R.id.easy_search_include_system_apps);
		listView = (ListView) activity.findViewById(R.id.easy_search_list);
		// public static final int easy_search_list_item_primary = 0x7f080016;
		// public static final int easy_search_list_item_secondary = 0x7f080017;
	}

	public void testDescription() {
		CharSequence actual = description.getText();
		String expected = resources.getString(R.string.easy_search_description);
		assertEquals(expected, actual);
	}

	public void testIncludeSystemApp() {
		CharSequence actual = includeSystemApps.getText();
		String expected = resources.getString(R.string.easy_search_include_system_apps);
		assertEquals(expected, actual);

		boolean actualChecked = includeSystemApps.isChecked();
		assertFalse(actualChecked);
	}

	public void testList() {
		Adapter adapter = listView.getAdapter();
		assertNotNull(adapter);
		assertEquals(listView.getCount(), adapter.getCount());
	}
}
