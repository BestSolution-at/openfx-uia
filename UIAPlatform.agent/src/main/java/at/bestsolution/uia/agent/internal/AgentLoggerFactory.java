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
package at.bestsolution.uia.agent.internal;

import java.util.function.Supplier;

public class AgentLoggerFactory {
    private static boolean LOG = Boolean.getBoolean("uia.log");
    private static int LOG_LEVEL = Integer.getInteger("uia.loglevel", 2);

    private static class StdErrLogger implements AgentLogger {
      private String name;

      private StdErrLogger(String name) {
        this.name = name;
      }

      public void log(Object source, Level level, Supplier<String> message, Throwable e, LocationData loc) {
        if (!isLevel(level)) {
          return;
        }
        try {
          String lang = loc != null && loc.lang != null ? loc.lang + " " : "";
          String location = loc != null ? loc.functionName + "("+loc.fileName+":"+loc.lineNumber+"): " : "";
          System.err.println(lang + "[" + level + "] " + name + " " + location + " " + withSource(message, source).get());
          if (e != null) {
            System.err.print(lang + "[" + level + "] ");
            e.printStackTrace();
          }
        } catch (Exception ex) {
          System.err.print("[ERROR] Logger exception: ");
          ex.printStackTrace();
        }
      }

      public boolean isLevel(Level level) {
        return LOG && LOG_LEVEL <= level.ordinal();
      }

    }

    private static Supplier<String> withSource(Supplier<String> message, Object source) {
      String sourceString = source == null ? "" : (source + ": ");
      return () -> sourceString + message.get();
    }


    public static boolean isLog() {
      return LOG;
    }

    public static AgentLogger create(Class<?> cls) {
        return new StdErrLogger(cls.getName());
    }


}
