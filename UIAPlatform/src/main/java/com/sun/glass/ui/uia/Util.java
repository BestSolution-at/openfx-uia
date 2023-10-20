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

import java.lang.Runnable;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

public class Util {

    private Util() {

    }

    static void onFallthrough(Throwable t) {
        t.printStackTrace();
    }

    static void guard(Runnable code) {
        try {
            code.run();
        } catch (HResultException he) {
            throw he;
        } catch (Throwable t) {
            onFallthrough(t);
            throw new HResultException(HResultException.E_FAIL);
        }
    }
    static int guard(IntSupplier code) {
        try {
            return code.getAsInt();
        } catch (HResultException he) {
            throw he;
        } catch (Throwable t) {
            onFallthrough(t);
            throw new HResultException(HResultException.E_FAIL);
        }
    }

    static long guard(LongSupplier code) {
        try {
            return code.getAsLong();
        } catch (HResultException he) {
            throw he;
        } catch (Throwable t) {
            t.printStackTrace();
            return -1;
        }
    }

    static long guard(LongSupplier code, long onFail) {
        try {
            return code.getAsLong();
        } catch (HResultException he) {
            throw he;
        } catch (Throwable t) {
            onFallthrough(t);
            throw new HResultException(HResultException.E_FAIL);
        }
    }

    static double guard(DoubleSupplier code) {
        try {
            return code.getAsDouble();
        } catch (HResultException he) {
            throw he;
        } catch (Throwable t) {
             onFallthrough(t);
            throw new HResultException(HResultException.E_FAIL);
        }
    }

    // static double guard(DoubleSupplier code, double onFail) {
    //     try {
    //         return code.getAsDouble();
    //     } catch (HResultException he) {
    //         throw he;
    //     } catch (Throwable t) {
    //         t.printStackTrace();
    //         return onFail;
    //     }
    // }

    static <T> T guard(Supplier<T> code) {
        try {
            return code.get();
        } catch (HResultException he) {
            throw he;
        } catch (Throwable t) {
            onFallthrough(t);
            throw new HResultException(HResultException.E_FAIL);
        }
    }
    // static <T> T guard(Supplier<T> code, T onFail) {
    //     try {
    //         return code.get();
    //     } catch (HResultException he) {
    //         throw he;
    //     } catch (Throwable t) {
    //         t.printStackTrace();
    //         return onFail;
    //     }
    // }
    static interface FloatArraySupplier {
        float[] get();
    }
    static float[] guard(FloatArraySupplier code) {
        try {
            return code.get();
        } catch (HResultException he) {
            throw he;
        } catch (Throwable t) {
            onFallthrough(t);
            throw new HResultException(HResultException.E_FAIL);
        }
    }
    static interface IntArraySupplier {
        int[] get();
    }

    static interface LongArraySupplier {
        long[] get();
    }
    static long[] guard(LongArraySupplier code) {
        try {
            return code.get();
        } catch (HResultException he) {
            throw he;
        } catch (Throwable t) {
            onFallthrough(t);
            throw new HResultException(HResultException.E_FAIL);
        }
    }
}
