package sakaitakao.android.permissionsearch.util

import java.io.Closeable

/**
 * ローンパターンなのです
 * Created by takao on 2015/11/08.
 */
object ClosableLoan {

    /**
     * ローンパターンなのです。
     * @param s java.io.Closable を実装するオブジェクトなのです。
     * @param f java.io.Closable を引数に取り何かを返す関数リテラルなのです。
     * @return [f] が返した値なのです。
     */
    fun <A : Closeable, R> using(s: A, f: (A) -> R): R {
        try {
            return f(s)
        } finally {
            s.close()
        }
    }
}