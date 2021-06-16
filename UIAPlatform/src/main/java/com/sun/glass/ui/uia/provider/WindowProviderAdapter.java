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
package com.sun.glass.ui.uia.provider;

import com.sun.glass.ui.uia.ProxyAccessible;

import javafx.uia.IWindowProvider;
import javafx.uia.StandardEventIds;
import javafx.uia.StandardPropertyIds;
import javafx.uia.WindowInteractionState;
import javafx.uia.WindowVisualState;

public class WindowProviderAdapter extends BaseAdapter<IWindowProvider> implements NativeIWindowProvider {
    
    private final IWindowProvider.IWindowProviderEvents events = new IWindowProvider.IWindowProviderEvents() {
        @Override
        public void notifyCanMaximizeChanged(boolean oldValue, boolean newValue) {
            UiaRaiseAutomationPropertyChangedEvent(StandardPropertyIds.UIA_WindowCanMinimizePropertyId, oldValue, newValue);            
        }

        @Override
        public void notifyCanMinimizeChanged(boolean oldValue, boolean newValue) {
            UiaRaiseAutomationPropertyChangedEvent(StandardPropertyIds.UIA_WindowCanMinimizePropertyId, oldValue, newValue);            
        }

        @Override
        public void notifyIsModalChanged(boolean oldValue, boolean newValue) {
            UiaRaiseAutomationPropertyChangedEvent(StandardPropertyIds.UIA_WindowIsModalPropertyId, oldValue, newValue);            
        }

        @Override
        public void notifyIsTopmostChanged(boolean oldValue, boolean newValue) {
            UiaRaiseAutomationPropertyChangedEvent(StandardPropertyIds.UIA_WindowIsTopmostPropertyId, oldValue, newValue);            
        }

        @Override
        public void notifyWindowInteractionStateChanged(WindowInteractionState oldValue, WindowInteractionState newValue) {
            UiaRaiseAutomationPropertyChangedEvent(StandardPropertyIds.UIA_WindowWindowInteractionStatePropertyId, oldValue, newValue);            
        }

        @Override
        public void notifyWindowVisualStateChanged(WindowVisualState oldValue, WindowVisualState newValue) {
            UiaRaiseAutomationPropertyChangedEvent(StandardPropertyIds.UIA_WindowWindowVisualStatePropertyId, oldValue, newValue);            
        }

        @Override
        public void notifyWindowOpened() {
            UiaRaiseAutomationEvent(StandardEventIds.UIA_Window_WindowOpenedEventId);
        }

        @Override
        public void notifyWindowClosed() {
            UiaRaiseAutomationEvent(StandardEventIds.UIA_Window_WindowClosedEventId);    
        }
       
    };

    public WindowProviderAdapter(ProxyAccessible accessible, IWindowProvider provider) {
        super(accessible, provider);
        provider.initialize(events);
    }

    @Override
    public void Close() {
        provider.Close();
    }

    @Override
    public boolean get_CanMaximize() {
        return provider.get_CanMaximize();
    }

    @Override
    public boolean get_CanMinimize() {
        return provider.get_CanMinimize();
    }

    @Override
    public boolean get_IsModal() {
        return provider.get_IsModal();
    }

    @Override
    public boolean get_IsTopmost() {
        return provider.get_IsTopmost();
    }

    @Override
    public int get_WindowInteractionState() {
        return provider.get_WindowInteractionState().getNativeValue();
    }

    @Override
    public int get_WindowVisualState() {
        return provider.get_WindowVisualState().getNativeValue();
    }

    @Override
    public void SetVisualState(int state) {
        WindowVisualState.fromNativeValue(state).ifPresent(provider::SetVisualState);
    }

    @Override
    public boolean WaitForInputIdle(int milliseconds) {
        return provider.WaitForInputIdle(milliseconds);
    }

}
