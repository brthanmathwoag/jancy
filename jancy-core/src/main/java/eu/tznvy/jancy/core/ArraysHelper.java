package eu.tznvy.jancy.core;

import java.util.Arrays;

class ArraysHelper {
    static <T> T[] copyIfNotEmpty(T[] ts) {
        return ts != null
            ? Arrays.copyOf(ts, ts.length)
            : (T[])new Object[0];
    }
}
