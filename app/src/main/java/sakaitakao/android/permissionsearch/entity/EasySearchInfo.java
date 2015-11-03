package sakaitakao.android.permissionsearch.entity;

import java.io.Serializable;
import java.util.List;

/**
 * パーミッションとパーミッションを保持するアプリのリスト
 * 
 * @author takao
 * 
 */
public class EasySearchInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 項目名
	 */
	public String name;

	/**
	 * 説明
	 */
	public String description;

	/**
	 * 権限名のパターン
	 */
	public List<String> permissionNamePatternList;

	/**
	 * プロテクションレヴェル
	 */
	public List<Integer> protectionLevelList;

	/**
	 * コンストラクタ
	 */
	public EasySearchInfo() {
	}
}
