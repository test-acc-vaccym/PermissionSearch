package sakaitakao.android.permissionsearch.entity

import android.content.pm.ApplicationInfo
import android.content.pm.PermissionInfo
import android.os.Parcel
import android.os.Parcelable
import java.util.*

/**
 * パーミッションとパーミッションを保持するアプリのリスト

 * @author takao
 */
class PermissionInfoEx : Parcelable {

    var permissionInfo: PermissionInfo? = null
    var applicationInfoList: List<ApplicationInfo>? = null
    var isUnknownPermission: Boolean = false

    /**

     */
    constructor() {
    }

    /**
     * @param parcel
     */
    private constructor(parcel: Parcel) {
        permissionInfo = parcel.readParcelable<PermissionInfo>(null)
        applicationInfoList = ArrayList<ApplicationInfo>()
        parcel.readList(applicationInfoList, null)
        val bools = BooleanArray(1)
        parcel.readBooleanArray(bools)
        isUnknownPermission = bools[0]
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see android.os.Parcelable#describeContents()
	 */
    override fun describeContents(): Int {
        return 0
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
	 */
    override fun writeToParcel(parcel: Parcel, i: Int) {
        parcel.writeParcelable(permissionInfo, 0)
        parcel.writeList(applicationInfoList)
        parcel.writeBooleanArray(booleanArrayOf(isUnknownPermission))
    }

    /**
     * @author takao
     */
    class ProtectionLevelComparator : Comparator<PermissionInfoEx> {

        override fun compare(lhs: PermissionInfoEx, rhs: PermissionInfoEx): Int {

            var retVal = (rhs.permissionInfo?.protectionLevel ?: 0) - (lhs.permissionInfo?.protectionLevel ?: 0)
            if (retVal == 0) {
                retVal = (rhs.applicationInfoList?.size ?: 0) - (lhs.applicationInfoList?.size ?: 0)
            }
            return retVal
        }
    }

    companion object {

        @JvmField
        val CREATOR: Parcelable.Creator<PermissionInfoEx> = object : Parcelable.Creator<PermissionInfoEx> {

            /*
		 * (non-Javadoc)
		 * 
		 * @see android.os.Parcelable.Creator#newArray(int)
		 */
            override fun newArray(i: Int): Array<PermissionInfoEx?> {
                return arrayOfNulls(i)
            }

            /*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.os.Parcelable.Creator#createFromParcel(android.os.Parcel)
		 */
            override fun createFromParcel(parcel: Parcel): PermissionInfoEx {
                return PermissionInfoEx(parcel)
            }
        }
    }
}
