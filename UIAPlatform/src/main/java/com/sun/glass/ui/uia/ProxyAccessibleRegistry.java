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

import java.util.HashMap;
import java.util.Map;

import javafx.uia.IUIAElement;

public class ProxyAccessibleRegistry {
    
    private final static ProxyAccessibleRegistry INSTANCE = new ProxyAccessibleRegistry();

    // TODO dispose = ?
    private Map<IUIAElement, ProxyAccessible> fxAccessibles = new HashMap<>();
    // TODO management & dispose
    private Map<IUIAElement, ProxyAccessible> virtualAccessibles = new HashMap<>();

    public static ProxyAccessibleRegistry getInstance() {
        return INSTANCE;
    }

    public void registerFXAccessible(IUIAElement element, ProxyAccessible accessible) {
        fxAccessibles.put(element, accessible);
    }

    public ProxyAccessible findFXAccessible(IUIAElement element) {
        return fxAccessibles.get(element);
    }

    public ProxyAccessible getVirtualAccessible(ProxyAccessible context, IUIAElement element) {
        return virtualAccessibles.computeIfAbsent(element, el -> new ProxyAccessible(context, el));
    }

    public ProxyAccessible findVirtualAccessible(IUIAElement element) {
        return virtualAccessibles.get(element);
    }

    public ProxyAccessible findAccessible(IUIAElement element) {
        ProxyAccessible result = findFXAccessible(element);
        if (result == null) {
            result = findVirtualAccessible(element);
        }
        return result;
    }
}
