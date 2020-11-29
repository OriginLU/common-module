package com.zeroone.test;

import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;
import org.junit.Test;

/**
 * @author zero-one.lu
 * @since 2020-11-21
 */
public class AviatorTests {



    public static void main(String[] args) {
        Expression compile = AviatorEvaluator.compile("1+2");
        System.out.println(compile.execute());
    }
}
