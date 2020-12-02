package com.charleslu.common.utils;

/**
 * @author zero-one.lu
 * @since 2020-11-29
 */
public class ExceptionUtils {


    private ExceptionUtils() { }


    @SuppressWarnings("unchecked")
    public static <E extends Throwable>  void throwUncheckException(Runnable runnable) throws E{

        try {
            runnable.run();
        }catch (Exception e){
            throw (E)e;
        }
    }

    @SuppressWarnings("unchecked")
    public static <E extends Throwable> void throwUncheckException(Throwable e) throws E{
        throw  (E)e;
    }
}
