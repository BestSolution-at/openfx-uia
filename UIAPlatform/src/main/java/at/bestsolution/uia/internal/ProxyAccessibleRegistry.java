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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import at.bestsolution.uia.IUIAElement;
import at.bestsolution.uia.IUIAVirtualElement;

public class ProxyAccessibleRegistry {

    private static class LazyHolder {
        final static ProxyAccessibleRegistry INSTANCE = new ProxyAccessibleRegistry();
    }

    // TODO dispose = ?
    private Map<IUIAElement, ProxyAccessible> fxAccessibles = new HashMap<>();
    private Map<IUIAElement, ProxyAccessible> virtualAccessibles = new HashMap<>();

    // Lifecycle ownership is independent of the provider's current getChildren().
    // Use proxy identity: an application element's equals() is not ownership.
    private final Map<ProxyAccessible, ProxyAccessible> virtualOwners = new IdentityHashMap<>();
    private final Map<ProxyAccessible, Set<ProxyAccessible>> ownedVirtuals = new IdentityHashMap<>();


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
        ProxyAccessible owner = virtualOwners.remove(accessible);
        Set<ProxyAccessible> owned = ownedVirtuals.get(owner);
        if (owned != null) {
            owned.remove(accessible);
            if (owned.isEmpty()) ownedVirtuals.remove(owner);
        }
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
        if (context.isProxyDisposed()) throw new HResultException(HResultException.UIA_E_ELEMENTNOTAVAILABLE);
        ProxyAccessible fx = fxAccessibles.get(element);
        if (fx != null) return fx;
        // Note: intentionally not computeIfAbsent! Since java9 computeIfAbsent throws a ConcurrentModificationException caused by our children initialization
        ProxyAccessible proxy = virtualAccessibles.get(element);
        if (proxy == null) {
            proxy = new ProxyAccessible(context, element);
        } else if (element instanceof IUIAVirtualElement) {
            // A lookup after reparenting preserves the proxy but transfers ownership.
            ProxyAccessible parent = findAccessible(((IUIAVirtualElement) element).getParent());
            if (parent != null) setVirtualOwner(parent, proxy);
        }
        return proxy;
    }

    void registerVirtualAccessible(ProxyAccessible context, IUIAElement element, ProxyAccessible proxy) {
        // Publish before initialize(): initializing descendants may look up their parent.
        virtualAccessibles.put(element, proxy);
        ProxyAccessible owner = context;
        if (element instanceof IUIAVirtualElement) {
            IUIAElement parent = ((IUIAVirtualElement) element).getParent();
            ProxyAccessible parentProxy = findAccessible(parent);
            if (parentProxy != null) owner = parentProxy;
        }
        setVirtualOwner(owner, proxy);
    }

    private void setVirtualOwner(ProxyAccessible owner, ProxyAccessible proxy) {
        if (owner.isProxyDisposed()) throw new HResultException(HResultException.UIA_E_ELEMENTNOTAVAILABLE);
        for (ProxyAccessible ancestor = owner; ancestor != null; ancestor = virtualOwners.get(ancestor)) {
            if (ancestor == proxy) throw new IllegalArgumentException("Cyclic virtual element ownership");
        }
        ProxyAccessible previous = virtualOwners.get(proxy);
        if (previous == owner) return;
        Set<ProxyAccessible> previousChildren = ownedVirtuals.get(previous);
        if (previousChildren != null) {
            previousChildren.remove(proxy);
            if (previousChildren.isEmpty()) ownedVirtuals.remove(previous);
        }
        virtualOwners.put(proxy, owner);
        ownedVirtuals.computeIfAbsent(owner, key -> Collections.newSetFromMap(new IdentityHashMap<>())).add(proxy);
    }

    void disposeOwnedVirtuals(ProxyAccessible owner) {
        Set<ProxyAccessible> owned = ownedVirtuals.remove(owner);
        if (owned == null) return;
        Throwable failure = null;
        // Child disposal removes registry entries; don't iterate a live registry view.
        for (ProxyAccessible child : new ArrayList<>(owned)) {
            try {
                child.dispose();
            } catch (RuntimeException | Error ex) {
                if (failure == null) failure = ex;
                else if (failure != ex) failure.addSuppressed(ex);
            }
        }
        if (failure instanceof RuntimeException) throw (RuntimeException) failure;
        if (failure instanceof Error) throw (Error) failure;
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
