package com.example.gradletransformactiondemo;

import java.util.HashMap;
import java.util.Map;

public class TimeCache {
    public static Map<String, Long> mStartTimes = new HashMap<>();

    public static Map<String, Long> mEndTimes = new HashMap<>();

    private static long lastClickTime = 0L;


    public static void putStartTime(String methodName, String className) {
        mStartTimes.put(methodName + "," + className, System.currentTimeMillis());
    }

    public static void putEndTime(String methodName, String className) {
        mEndTimes.put(methodName + "," + className, System.currentTimeMillis());
        printlnTime(methodName,className);
    }

    public static void printlnTime(String methodName,String className) {
        String key = methodName + "," + className;
        if (!mStartTimes.containsKey(key) || !mEndTimes.containsKey(key)) {
            System.out.println("className =" + key + "not exist");
        }
        long currTime = mEndTimes.get(key) - mStartTimes.get(key);
        System.out.println("className =" + className +" methodName =" + methodName + "，time consuming " + currTime + "  ms");
    }

    public static void checkDoubleClick(String methodName,String className) {
        long passed = System.currentTimeMillis() - lastClickTime;
        if (passed < 1000) {
            System.out.println("double click, time passed = " + passed);
            return ;
        }
        lastClickTime = System.currentTimeMillis();
    }
}


