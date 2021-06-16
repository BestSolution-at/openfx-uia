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

import com.sun.glass.ui.uia.glass.WinVariant;

import javafx.uia.IEventId;
import javafx.uia.INativeEnum;
import javafx.uia.IPropertyId;

import com.sun.glass.ui.uia.ProxyAccessible;

public abstract class BaseAdapter<T> {
    
    protected final T provider;
    protected final ProxyAccessible accessible;


    public BaseAdapter(ProxyAccessible accessible, T provider) {
        this.accessible = accessible;
        this.provider = provider;
    }

    protected void UiaRaiseAutomationPropertyChangedEvent(int id, WinVariant oldV, WinVariant newV) {
        ProxyAccessible.UiaRaiseAutomationPropertyChangedEvent(accessible.getNativeAccessible(), id, oldV, newV);       
    }

    protected void UiaRaiseAutomationPropertyChangedEvent(IPropertyId id, boolean oldV, boolean newV) {
        UiaRaiseAutomationPropertyChangedEvent(id.getNativeValue(), Convert.variantBoolean(oldV), Convert.variantBoolean(newV));
    }
    protected void UiaRaiseAutomationPropertyChangedEvent(IPropertyId id, INativeEnum oldV, INativeEnum newV) {
        UiaRaiseAutomationPropertyChangedEvent(id.getNativeValue(), Convert.variantEnum(oldV), Convert.variantEnum(newV));
    }
    
    protected void UiaRaiseAutomationEvent(int id) {
        ProxyAccessible.UiaRaiseAutomationEvent(accessible.getNativeAccessible(), id);
    }

    protected void UiaRaiseAutomationEvent(IEventId eventId) {
        UiaRaiseAutomationEvent(eventId.getNativeValue());
    }

}
