/*
 * -----------------------------------------------------------------
 * Copyright (c) 2021 BestSolution.at EDV Systemhaus GmbH
 * All Rights Reserved.
 *
 * BestSolution.at MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE
 * SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE  OR NON - INFRINGEMENT.
 * BestSolution.at SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY
 * LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS
 * SOFTWARE OR ITS DERIVATIVES.
 *
 * This software is released under the terms of the
 *
 *                  "GNU General Public License, Version 2 
 *                         with classpath exception"
 *
 * and may only be distributed and used under the terms of the
 * mentioned license. You should have received a copy of the license
 * along with this software product, if not you can download it from
 * http://www.gnu.org/licenses/gpl.html
 * ----------------------------------------------------------------
 */
package com.sun.glass.ui.uia;

import java.util.function.Supplier;

public class Logger {

    public static enum Level {
        DEBUG, WARNING, ERROR, FATAL
    }
    
    public static void log(Object source, Level level, Supplier<String> message) {
        log(source, level, message, null);
    }

    public static void log(Object source, Level level, Supplier<String> message, Exception e) {
        System.err.println("[" + level + "] " + source + ": " + message.get());
        if (e != null) {
            e.printStackTrace();
        }
    }

    public static void debug(Object source, Supplier<String> message) {
        log(source, Level.DEBUG, message);
    }

    public static void debug(Object source, Supplier<String> message, Exception e) {
        log(source, Level.DEBUG, message, e);
    }

    public static void warning(Object source, Supplier<String> message) {
        log(source, Level.WARNING, message);
    }

    public static void warning(Object source, Supplier<String> message, Exception e) {
        log(source, Level.WARNING, message, e);
    }

    public static void error(Object source, Supplier<String> message) {
        log(source, Level.ERROR, message);
    }

    public static void error(Object source, Supplier<String> message, Exception e) {
        log(source, Level.ERROR, message, e);
    }

    public static void fatal(Object source, Supplier<String> message) {
        log(source, Level.FATAL, message);
    }

    public static void fatal(Object source, Supplier<String> message, Exception e) {
        log(source, Level.FATAL, message, e);
    }

}
