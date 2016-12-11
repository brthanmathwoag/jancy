package jancy.core.helpers;

import java.util.Arrays;

public class ArraysHelper {
    public static <T> T[] copyIfNotEmpty(T[] ts) {
        return ts != null
            ? Arrays.copyOf(ts, ts.length)
            : (T[])new Object[0];
    }
}
