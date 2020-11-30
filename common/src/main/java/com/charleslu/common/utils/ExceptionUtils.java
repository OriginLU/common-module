package com.charleslu.common.utils;

public class ExceptionUtils {


    private ExceptionUtils() {
    }

    @SuppressWarnings("unchecked")
    public static <E extends Throwable> void throwUncheck(Throwable e) throws E{
        throw (E)e;
    }
}
