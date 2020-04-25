package com.zeroone.design.patterns.state;

import java.lang.annotation.*;

/**
 * 状态模式控制器
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface State {

    int value();
}
