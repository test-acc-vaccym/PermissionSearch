package sakaitakao.android.permissionsearch.entity

import java.io.Serializable
import java.util.*

/**
 * パーミッションとパーミッションを保持するアプリのリスト

 * @author takao
 */
class EasySearchInfo : Serializable {

    /**
     * 項目名
     */
    public var name: String = ""

    /**
     * 説明
     */
    public var description: String = ""

    /**
     * 権限名のパターン
     */
    public var permissionNamePatternList: List<String> = ArrayList()

    /**
     * プロテクションレヴェル
     */
    public var protectionLevelList: List<Int> = ArrayList()


    companion object {
        private val serialVersionUID = 1L
    }
}
