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
import java.util.WeakHashMap;
import java.util.stream.Collectors;

import at.bestsolution.uia.IUIAElement;
import at.bestsolution.uia.IUIAVirtualElement;
import at.bestsolution.uia.IUIAVirtualRootElement;
import javafx.application.Platform;

public class ProxyAccessibleRegistry {
    private static final Logger LOG = Logger.create(ProxyAccessibleRegistry.class);

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
    private final Set<ProxyAccessible> pendingReconciliation = identitySet();
    private final Set<ProxyAccessible> reconciledRoots = identitySet();
    // Tombstones must not themselves retain removed providers or their owners.
    private final Map<IUIAElement, Boolean> retiredElements = new WeakHashMap<>();
    private boolean reconciliationScheduled;


    private Map<ProxyAccessible, Long> natives = new HashMap<>();
    private Map<Long, ProxyAccessible> natives2 = new HashMap<>();

    public static ProxyAccessibleRegistry getInstance() {
        return LazyHolder.INSTANCE;
    }

    public void unregister(ProxyAccessible accessible) {
        pendingReconciliation.remove(accessible);
        reconciledRoots.remove(accessible);
        natives.remove(accessible);
        natives2.values().remove(accessible);
        fxAccessibles.values().remove(accessible);
        virtualAccessibles.values().remove(accessible);
        removeVirtualOwner(accessible);
    }

    private void removeVirtualOwner(ProxyAccessible accessible) {
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
            if (retiredElements.containsKey(element)) {
                ProxyAccessible root = reconciliationRoot(context);
                if (element instanceof IUIAVirtualElement) {
                    ProxyAccessible parent = findAccessible(((IUIAVirtualElement) element).getParent());
                    if (parent != null) root = reconciliationRoot(parent);
                }
                TreeSnapshot current = new TreeSnapshot();
                if (root == null || root.isProxyDisposed()) {
                    throw new HResultException(HResultException.UIA_E_ELEMENTNOTAVAILABLE);
                }
                current.visit(root.getUIAElement(), null);
                if (!current.reachable.contains(element)) {
                    throw new HResultException(HResultException.UIA_E_ELEMENTNOTAVAILABLE);
                }
            }
            proxy = new ProxyAccessible(context, element);
            retiredElements.remove(element); // Only a successful reinsertion clears retirement.
            ProxyAccessible root = reconciliationRoot(proxy);
            if (root != null && reconciledRoots.contains(root)) {
                // A delayed event can introduce a fresh wrapper for an already
                // removed model. Don't let that new identity escape reconciliation.
                scheduleVirtualReconciliation(proxy);
            }
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

    private static <T> Set<T> identitySet() {
        return Collections.newSetFromMap(new IdentityHashMap<>());
    }

    private ProxyAccessible reconciliationRoot(ProxyAccessible proxy) {
        ProxyAccessible root = null;
        for (ProxyAccessible current = proxy; current != null; current = virtualOwners.get(current)) {
            if (current.getUIAElement() instanceof IUIAVirtualRootElement) root = current;
        }
        return root;
    }

    /** Reconcile logical membership after the current notification batch finishes. */
    public void scheduleVirtualReconciliation(ProxyAccessible source) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> scheduleVirtualReconciliation(source));
            return;
        }
        if (source.isProxyDisposed()) return;
        ProxyAccessible root = reconciliationRoot(source);
        if (root == null) return;
        pendingReconciliation.add(root);
        if (!reconciliationScheduled) {
            reconciliationScheduled = true;
            Platform.runLater(this::reconcileVirtualTrees);
        }
    }

    private void collectOwned(ProxyAccessible owner, Set<ProxyAccessible> result) {
        Set<ProxyAccessible> children = ownedVirtuals.get(owner);
        if (children == null) return;
        for (ProxyAccessible child : children) {
            if (result.add(child)) collectOwned(child, result);
        }
    }

    private void reconcileVirtualTrees() {
        reconciliationScheduled = false;
        Set<ProxyAccessible> changedRoots = identitySet();
        changedRoots.addAll(pendingReconciliation);
        pendingReconciliation.clear();
        changedRoots.removeIf(ProxyAccessible::isProxyDisposed);
        if (changedRoots.isEmpty()) return;
        try {
            TreeSnapshot tree = new TreeSnapshot();
            Set<ProxyAccessible> snapshotRoots = identitySet();
            // Snapshot other registered roots too: an item may have moved there even
            // when only its former root has emitted a structural notification.
            for (ProxyAccessible proxy : new ArrayList<>(natives.keySet())) {
                if (!proxy.isProxyDisposed() && proxy.getUIAElement() instanceof IUIAVirtualRootElement
                        && reconciliationRoot(proxy) == proxy) {
                    tree.visit(proxy.getUIAElement(), null);
                    snapshotRoots.add(proxy);
                }
            }
            if (!pendingReconciliation.isEmpty()) {
                // Enumeration caused another structural change. Use the already
                // scheduled fresh pass rather than dispose from an obsolete snapshot.
                pendingReconciliation.addAll(changedRoots);
                return;
            }
            Set<ProxyAccessible> candidates = identitySet();
            for (ProxyAccessible root : changedRoots) {
                if (!root.isProxyDisposed()) collectOwned(root, candidates);
            }
            Map<ProxyAccessible, ProxyAccessible> owners = new IdentityHashMap<>();
            for (IUIAElement element : tree.order) {
                ProxyAccessible proxy = virtualAccessibles.get(element);
                if (proxy == null || proxy.isProxyDisposed()) continue;
                IUIAElement parent = tree.parents.get(element);
                while (parent != null) {
                    ProxyAccessible owner = findAccessible(parent);
                    if (owner != null) {
                        if (!owner.isProxyDisposed()) owners.put(proxy, owner);
                        break;
                    }
                    parent = tree.parents.get(parent);
                }
            }
            // Validate the complete proposed ownership graph before changing records.
            for (Map.Entry<ProxyAccessible, ProxyAccessible> entry : owners.entrySet()) {
                Set<ProxyAccessible> ancestors = identitySet();
                for (ProxyAccessible ancestor = entry.getValue(); ancestor != null;
                        ancestor = owners.containsKey(ancestor) ? owners.get(ancestor) : virtualOwners.get(ancestor)) {
                    if (ancestor == entry.getKey() || !ancestors.add(ancestor)) {
                        throw new IllegalArgumentException("Cyclic virtual element ownership");
                    }
                }
            }
            // Detach old edges first so valid ancestry reversals cannot look cyclic
            // halfway through a move. No provider code is called during this phase.
            owners.keySet().forEach(this::removeVirtualOwner);
            owners.forEach((proxy, owner) -> setVirtualOwner(owner, proxy));
            for (ProxyAccessible root : snapshotRoots) {
                if (!root.isProxyDisposed()) reconciledRoots.add(root);
            }
            List<ProxyAccessible> removed = new ArrayList<>();
            for (ProxyAccessible proxy : candidates) {
                if (!proxy.isProxyDisposed() && !tree.reachable.contains(proxy.getUIAElement())) {
                    retiredElements.put(proxy.getUIAElement(), Boolean.TRUE);
                    removed.add(proxy);
                }
            }
            Throwable failure = null;
            for (ProxyAccessible proxy : removed) {
                try {
                    proxy.dispose();
                } catch (RuntimeException | Error ex) {
                    if (failure == null) failure = ex;
                    else if (failure != ex) failure.addSuppressed(ex);
                }
            }
            if (failure instanceof RuntimeException) throw (RuntimeException) failure;
            if (failure instanceof Error) throw (Error) failure;
        } catch (RuntimeException | Error failure) {
            LOG.error(this, () -> "Virtual tree reconciliation failed", failure);
        }
    }

    private static final class TreeSnapshot {
        final Set<IUIAElement> reachable = identitySet();
        final Set<IUIAElement> visiting = identitySet();
        final Map<IUIAElement, IUIAElement> parents = new IdentityHashMap<>();
        final List<IUIAElement> order = new ArrayList<>();

        void visit(IUIAElement element, IUIAElement parent) {
            if (element == null || visiting.contains(element)) throw new IllegalArgumentException("Invalid virtual tree");
            if (parent != null) {
                if (parents.containsKey(element) && parents.get(element) != parent) {
                    throw new IllegalArgumentException("A virtual element has multiple logical parents");
                }
                parents.put(element, parent);
            }
            if (!reachable.add(element)) return;
            order.add(element);
            visiting.add(element);
            List<IUIAElement> children = Collections.emptyList();
            if (element instanceof IUIAVirtualRootElement) children = ((IUIAVirtualRootElement) element).getChildren();
            else if (element instanceof IUIAVirtualElement) children = ((IUIAVirtualElement) element).getChildren();
            if (children == null) throw new IllegalArgumentException("A virtual child list must not be null");
            for (IUIAElement child : new ArrayList<>(children)) visit(child, element);
            visiting.remove(element);
        }
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
