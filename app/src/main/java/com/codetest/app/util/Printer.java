package com.codetest.app.util;

/**
 * This is a class for printing any an all messages. The reasoning behind having
 * this class is that if it is properly used, it is very easy to change all the
 * different print methods to simply print messages to different log files,
 * which makes some debugging easier, in my experience.
 * <p>
 * It is also a good thing to note that you want to have security files as
 * noice-free as possible and therefore it is smart to seperate the
 * security-critical output from that of the rest of the logs.
 * 
 * @version 1.0.0
 * 
 */
public abstract class Printer {
    private final String RED = "";
    private final String BLUE = "";
    private final String GREEN = "";
    private final String RESET = "";
    private final String BOLD = "";
    private final String YELLOW = "";

    public static void log(String message) {

    }

    public static void debug(String message) {

    }

    public static void error(String message) {

    }

    public static void security(String message) {

    }
}
