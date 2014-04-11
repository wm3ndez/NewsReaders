package com.wmendez.newsreader.lib.util;

public class Utils {

    public static String join(Object[] pieces, String glue) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < pieces.length; i++) {
            if (i != 0)
                sb.append(glue);
            sb.append(pieces[i].toString());
        }
        return sb.toString();
    }
}
