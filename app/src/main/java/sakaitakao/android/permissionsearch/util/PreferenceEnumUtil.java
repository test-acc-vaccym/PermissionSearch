package sakaitakao.android.permissionsearch.util;

/**
 * {@link PreferenceEnum}インターフェイスのサポート
 * 
 * @author takao
 */
public final class PreferenceEnumUtil {

	private PreferenceEnumUtil() {
	}

	/**
	 * 指定されたキーを持つ enum 値を返す。
	 * 
	 * @param <E>
	 *            enum クラス
	 * @param klass
	 *            クラス E の {@link Class} オブジェクト
	 * @param key
	 *            キー
	 * @return enum の値
	 */
	public static <E extends PreferenceEnum> E getValue(Class<E> klass, String key) {
		E[] values = klass.getEnumConstants();
		for (E ai : values) {
			if (ai.getKey().equals(key)) {
				return ai;
			}
		}
		return null;
	}
}
