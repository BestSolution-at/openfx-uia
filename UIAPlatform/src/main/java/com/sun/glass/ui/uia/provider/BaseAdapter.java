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

import javafx.uia.IEventId;
import javafx.uia.IPropertyId;
import javafx.uia.Variant;

public abstract class BaseAdapter<T> {
    
    protected final T provider;
    protected final ProxyAccessible accessible;


    public BaseAdapter(ProxyAccessible accessible, T provider) {
        this.accessible = accessible;
        this.provider = provider;
    }

    protected void UiaRaiseAutomationPropertyChangedEvent(IPropertyId propertyId, Variant oldV, Variant newV) {
        ProxyAccessible.UiaRaiseAutomationPropertyChangedEvent(accessible.getNativeAccessible(), propertyId.getNativeValue(), oldV.toWinVariant(), newV.toWinVariant());       
    }

    protected void UiaRaiseAutomationEvent(IEventId eventId) {
        ProxyAccessible.UiaRaiseAutomationEvent(accessible.getNativeAccessible(), eventId.getNativeValue());
    }

}
