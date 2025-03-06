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
package at.bestsolution.uia.internal;

import java.lang.Runnable;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

public class Util {

    private static final Logger LOG = LoggerFactory.create(Util.class);

    private Util() {

    }

    static void onFallthrough(Throwable t) {
        LOG.error(null, () -> "A client exception occured in guarded code", t);
    }

    static void guardVoid(Runnable code) {
        try {
            code.run();
        } catch (HResultException he) {
            throw he;
        } catch (Throwable t) {
            onFallthrough(t);
            throw new HResultException(HResultException.E_JAVAEXCEPTION);
        }
    }
    static int guardInt(IntSupplier code) {
        try {
            return code.getAsInt();
        } catch (HResultException he) {
            throw he;
        } catch (Throwable t) {
            onFallthrough(t);
            throw new HResultException(HResultException.E_JAVAEXCEPTION);
        }
    }
    static boolean guardBoolean(BooleanSupplier code) {
        try {
            return code.getAsBoolean();
        } catch (HResultException he) {
            throw he;
        } catch (Throwable t) {
            onFallthrough(t);
            throw new HResultException(HResultException.E_JAVAEXCEPTION);
        }
    }

    static long guardLong(LongSupplier code) {
        try {
            return code.getAsLong();
        } catch (HResultException he) {
            throw he;
        } catch (Throwable t) {
            t.printStackTrace();
            return -1;
        }
    }

    static double guardDouble(DoubleSupplier code) {
        try {
            return code.getAsDouble();
        } catch (HResultException he) {
            throw he;
        } catch (Throwable t) {
             onFallthrough(t);
            throw new HResultException(HResultException.E_JAVAEXCEPTION);
        }
    }

    static <T> T guardObject(Supplier<T> code) {
        try {
            return code.get();
        } catch (HResultException he) {
            throw he;
        } catch (Throwable t) {
            onFallthrough(t);
            throw new HResultException(HResultException.E_JAVAEXCEPTION);
        }
    }
    static interface FloatArraySupplier {
        float[] get();
    }
    static float[] guardFloatArray(FloatArraySupplier code) {
        try {
            return code.get();
        } catch (HResultException he) {
            throw he;
        } catch (Throwable t) {
            onFallthrough(t);
            throw new HResultException(HResultException.E_JAVAEXCEPTION);
        }
    }
    static interface DoubleArraySupplier {
        double[] get();
    }
    static double[] guardDoubleArray(DoubleArraySupplier code) {
        try {
            return code.get();
        } catch (HResultException he) {
            throw he;
        } catch (Throwable t) {
            onFallthrough(t);
            throw new HResultException(HResultException.E_JAVAEXCEPTION);
        }
    }
    static interface IntArraySupplier {
        int[] get();
    }
    static int[] guardIntArray(IntArraySupplier code) {
        try {
            return code.get();
        } catch (HResultException he) {
            throw he;
        } catch (Throwable t) {
            onFallthrough(t);
            throw new HResultException(HResultException.E_JAVAEXCEPTION);
        }
    }

    static interface LongArraySupplier {
        long[] get();
    }
    static long[] guardLongArray(LongArraySupplier code) {
        try {
            return code.get();
        } catch (HResultException he) {
            throw he;
        } catch (Throwable t) {
            onFallthrough(t);
            throw new HResultException(HResultException.E_JAVAEXCEPTION);
        }
    }
}
