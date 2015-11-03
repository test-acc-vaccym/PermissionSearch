package sakaitakao.android.permissionsearch.activity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import sakaitakao.android.permissionsearch.R;
import sakaitakao.android.permissionsearch.adaptor.PermissionListAdaptor;
import sakaitakao.android.permissionsearch.entity.PermissionInfoEx;
import sakaitakao.android.permissionsearch.model.AppPermissionsFinder;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 権限一覧のアクティビティ
 * 
 * @author takao
 * 
 */
public class PermissionListActivity extends Activity {

	private static final String INTENTEXTRA_CONDITION = PermissionListActivity.class.getName() + ".CONDITION";

	private PermissionListAdaptor adaptor;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.permission_list);

		SearchCondition condition = getCondition();

		PackageManager packageManager = getPackageManager();
		AppPermissionsFinder aph = new AppPermissionsFinder();
		List<PermissionInfoEx> list = aph.getPermissionInfoExList(packageManager, toAppPermissionsFinderCondition(condition));

		Resources resources = getResources();
		showHitCount(resources, list);
		showList(list);
	}

	/**
	 * アクティビティ起動時の検索条件を設定する。
	 * 
	 * @param intent
	 *            {@link Intent}
	 * @param searchCondition
	 *            検索条件
	 */
	public static void setSearchCondition(Intent intent, SearchCondition searchCondition) {
		intent.putExtra(PermissionListActivity.INTENTEXTRA_CONDITION, searchCondition);
	}

	// -------------------------------------------------------------------------
	// Privates
	// -------------------------------------------------------------------------
	/**
	 * @return
	 */
	private SearchCondition getCondition() {

		Intent intent = getIntent();
		SearchCondition searchCondition = intent.getParcelableExtra(INTENTEXTRA_CONDITION);
		if (searchCondition == null) {
			throw new RuntimeException("Please set extra data by Intent#putExtra(" + INTENTEXTRA_CONDITION + ", Parcelable)");
		}
		return searchCondition;
	}

	private AppPermissionsFinder.SearchCondition toAppPermissionsFinderCondition(SearchCondition cond) {

		AppPermissionsFinder.SearchCondition ret = new AppPermissionsFinder.SearchCondition();
		ret.includeSystemApps = cond.includeSystemApps;
		ret.permissionNamePatternList = cond.permissionNamePatternList;
		ret.protectionLevelSet = new HashSet<Integer>(cond.protectionLevelList);
		return ret;
	}

	/**
	 * @param resources
	 * @param list
	 */
	private void showHitCount(Resources resources, List<PermissionInfoEx> list) {

		TextView textView = (TextView) findViewById(R.id.permission_list_hit_count);
		textView.setText(resources.getString(R.string.hit_count, list.size()));
	}

	/**
	 * @param list
	 */
	private void showList(List<PermissionInfoEx> list) {
		ListView listView = (ListView) findViewById(R.id.permission_list_list);
		adaptor = new PermissionListAdaptor(this, R.layout.permission_list_item, list);
		listView.setAdapter(adaptor);
	}

	// -------------------------------------------------------------------------
	// Classes
	// -------------------------------------------------------------------------
	/**
	 * 
	 * @author takao
	 */
	public static class SearchCondition implements Parcelable {

		public boolean includeSystemApps;
		public List<String> permissionNamePatternList;
		public List<Integer> protectionLevelList;

		public SearchCondition() {
		}

		/**
		 * @param parcel
		 */
		private SearchCondition(Parcel parcel) {
			boolean[] bools = new boolean[1];
			parcel.readBooleanArray(bools);
			includeSystemApps = bools[0];
			permissionNamePatternList = new ArrayList<String>();
			parcel.readList(permissionNamePatternList, null);
			protectionLevelList = new ArrayList<Integer>();
			parcel.readList(protectionLevelList, null);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.Parcelable#describeContents()
		 */
		@Override
		public int describeContents() {
			return 0;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
		 */
		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeBooleanArray(new boolean[] { includeSystemApps });
			dest.writeList(permissionNamePatternList);
			dest.writeList(protectionLevelList);
		}

		/**
		 * 
		 */
		public static final Parcelable.Creator<SearchCondition> CREATOR = new Creator<SearchCondition>() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see android.os.Parcelable.Creator#newArray(int)
			 */
			@Override
			public SearchCondition[] newArray(int i) {
				return new SearchCondition[i];
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * android.os.Parcelable.Creator#createFromParcel(android.os.Parcel)
			 */
			@Override
			public SearchCondition createFromParcel(Parcel parcel) {
				return new SearchCondition(parcel);
			}
		};
	}
}