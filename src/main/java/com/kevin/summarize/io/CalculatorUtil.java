package com.kevin.summarize.io;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * @类名: Calculator
 * @包名：com.kevin.summarize.io
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/5/14 17:27
 * @版本：1.0
 * @描述：计算工具类
 */
public class CalculatorUtil {

    private final static ScriptEngine jse = new ScriptEngineManager()
            .getEngineByName("JavaScript");

    public static String cal(String expression) throws ScriptException {
        return jse.eval(expression).toString();
    }
}
