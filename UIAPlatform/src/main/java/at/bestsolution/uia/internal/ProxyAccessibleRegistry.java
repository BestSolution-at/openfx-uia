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

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import at.bestsolution.uia.IUIAElement;

public class ProxyAccessibleRegistry {

    private static class LazyHolder {
        final static ProxyAccessibleRegistry INSTANCE = new ProxyAccessibleRegistry();
    }

    // TODO dispose = ?
    private Map<IUIAElement, ProxyAccessible> fxAccessibles = new HashMap<>();
    // TODO management & dispose
    private Map<IUIAElement, ProxyAccessible> virtualAccessibles = new HashMap<>();


    private Map<ProxyAccessible, Long> natives = new HashMap<>();
    private Map<Long, ProxyAccessible> natives2 = new HashMap<>();

    public static ProxyAccessibleRegistry getInstance() {
        return LazyHolder.INSTANCE;
    }

    public void unregister(ProxyAccessible accessible) {
        natives.remove(accessible);
        natives2.values().remove(accessible);
        fxAccessibles.values().remove(accessible);
        virtualAccessibles.values().remove(accessible);
    }

    public List<ProxyAccessible> getAccessibles() {
        return natives.keySet().stream()
        .sorted(Comparator.comparingInt(ProxyAccessible::getNum))
        .collect(Collectors.toList());
    }

    public void registerFXAccessible(IUIAElement element, ProxyAccessible accessible) {
        fxAccessibles.put(element, accessible);
    }

    public ProxyAccessible findFXAccessible(IUIAElement element) {
        return fxAccessibles.get(element);
    }

    boolean guard = false;


    public ProxyAccessible getVirtualAccessible(ProxyAccessible context, IUIAElement element) {
        // Note: intentionally not computeIfAbsent! Since java9 computeIfAbsent throws a ConcurrentModificationException caused by our children initialization
        ProxyAccessible proxy = virtualAccessibles.get(element);
        if (proxy == null) {
            proxy = new ProxyAccessible(context, element);
            virtualAccessibles.put(element, proxy);
        }
        return proxy;
    }

    public void ensureExists(ProxyAccessible context, List<IUIAElement> elements) {
        elements.forEach(element -> ensureExists(context, element));
    }

    public boolean ensureExists(ProxyAccessible context, IUIAElement element) {
        boolean virtual = virtualAccessibles.containsKey(element);
        boolean fx = fxAccessibles.containsKey(element);
        boolean createVirtual = !virtual && !fx;
        if (createVirtual) {
            getVirtualAccessible(context, element);
        }
        return createVirtual;
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

    public void registerNative(ProxyAccessible proxyAccessible, long peer) {
        natives.put(proxyAccessible, peer);
        natives2.put(peer, proxyAccessible);
    }

    public ProxyAccessible getByNative(long peer) {
        return natives2.get(peer);
    }
}
