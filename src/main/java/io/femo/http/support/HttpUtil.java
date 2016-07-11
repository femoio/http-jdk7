package io.femo.http.support;

/**
 * Created by felix on 7/11/16.
 */
public final class HttpUtil {

    public static String joinPaths(String a, String b) {
        if(a == null || a.isEmpty()) {
            a = "";
        }
        while(a.endsWith("/")) {
            a = a.substring(0, a.length() - 1);
        }
        if(b == null || b.isEmpty()) {
            b = "";
        }
        while (b.startsWith("/")) {
            b = b.substring(1);
        }
        return a + "/" + b;
    }
}
