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

import com.sun.glass.ui.Accessible;

import at.bestsolution.uia.core.internal.CoreLogger;
import at.bestsolution.uia.core.internal.CoreLoggerFactory;

import java.util.ServiceLoader;
import java.util.Optional;
import java.util.Iterator;

@SuppressWarnings("restriction")
public class AccessibleFactory {

    private static CoreLogger log = CoreLoggerFactory.create(AccessibleFactory.class);

    public static Accessible createAccessible() {
        Optional<IAccessibleFactory> service = findFirst(ServiceLoader.load(IAccessibleFactory.class));
        Accessible result = service.map(IAccessibleFactory::createAccessible).orElse(null);
        if (result == null) {
            log.fatal(() -> "Could not create accessible. openjfx-uia will not work. Is UIAPlatform.jar on the classpath?");
        }
        return result;
    }

    private static <S> Optional<S> findFirst(ServiceLoader<S> loader) {
        Iterator<S> iterator = loader.iterator();
        if (iterator.hasNext()) {
            return Optional.of(iterator.next());
        } else {
            return Optional.empty();
        }
    }
}
