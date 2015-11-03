package sakaitakao.android.permissionsearch.entity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import android.content.pm.ApplicationInfo;
import android.content.pm.PermissionInfo;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * パーミッションとパーミッションを保持するアプリのリスト
 * 
 * @author takao
 * 
 */
public class PermissionInfoEx implements Parcelable {

	public PermissionInfo permissionInfo;
	public List<ApplicationInfo> applicationInfoList;
	public boolean isUnknownPermission;

	/**
	 * 
	 */
	public PermissionInfoEx() {
	}

	/**
	 * @param parcel
	 */
	private PermissionInfoEx(Parcel parcel) {
		permissionInfo = parcel.readParcelable(null);
		applicationInfoList = new ArrayList<ApplicationInfo>();
		parcel.readList(applicationInfoList, null);
		boolean[] bools = new boolean[1];
		parcel.readBooleanArray(bools);
		isUnknownPermission = bools[0];
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
	public void writeToParcel(Parcel parcel, int i) {
		parcel.writeParcelable(permissionInfo, 0);
		parcel.writeList(applicationInfoList);
		parcel.writeBooleanArray(new boolean[] { isUnknownPermission });
	}

	/**
	 * 
	 */
	public static final Parcelable.Creator<PermissionInfoEx> CREATOR = new Creator<PermissionInfoEx>() {

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.Parcelable.Creator#newArray(int)
		 */
		@Override
		public PermissionInfoEx[] newArray(int i) {
			return new PermissionInfoEx[i];
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.os.Parcelable.Creator#createFromParcel(android.os.Parcel)
		 */
		@Override
		public PermissionInfoEx createFromParcel(Parcel parcel) {
			return new PermissionInfoEx(parcel);
		}
	};

	/**
	 * @author takao
	 * 
	 */
	public static class ProtectionLevelComparator implements Comparator<PermissionInfoEx> {

		@Override
		public int compare(PermissionInfoEx lhs, PermissionInfoEx rhs) {

			int retVal = rhs.permissionInfo.protectionLevel - lhs.permissionInfo.protectionLevel;
			if (retVal == 0) {
				retVal = rhs.applicationInfoList.size() - lhs.applicationInfoList.size();
			}
			return retVal;
		}
	}
}
