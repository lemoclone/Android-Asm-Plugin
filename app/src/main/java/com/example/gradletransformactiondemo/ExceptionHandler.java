package com.example.gradletransformactiondemo;

import static java.sql.DriverManager.println;

public class ExceptionHandler {

    public static void logException(Throwable t) {
        println("Log exception");
        InstrutmentThrowable e = new InstrutmentThrowable(t);
        println(e.toString());
        e.printStackTrace();
    }


    public static class InstrutmentThrowable extends Throwable {
        public InstrutmentThrowable() {
            super();
        }

        public InstrutmentThrowable(Throwable t) {
            super(t);
        }
    }
}
