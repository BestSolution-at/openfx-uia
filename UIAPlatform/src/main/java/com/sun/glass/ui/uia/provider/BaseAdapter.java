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
import com.sun.glass.ui.uia.ProxyTextRangeProvider;

import java.util.Arrays;

import com.sun.glass.ui.uia.Logger;

import com.sun.glass.ui.uia.winapi.Windows;

import javafx.uia.AsyncContentLoadedState;
import javafx.uia.IEventId;
import javafx.uia.IPropertyId;
import javafx.uia.ITextRangeProvider;
import javafx.uia.NotificationKind;
import javafx.uia.NotificationProcessing;
import javafx.uia.StructureChangeType;
import javafx.uia.TextEditChangeType;
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

    protected void UiaRaiseTextEditTextChangedEvent(TextEditChangeType changeType, String[] payload) {
        Logger.debug(this, () -> "UiaRaiseTextEditTextChangedEvent("+changeType+", "+Arrays.toString(payload)+")");
        long pProvider = accessible.getNativeAccessible();
        int textEditChangeType = changeType.getNativeValue();
        long pChangedData = Windows.SafeArrayCreateVector((short) Windows.VT_BSTR, 0, payload.length);
        for (int idx = 0; idx < payload.length; idx ++) {
            long pValue = Windows.SysAllocString(payload[idx]);
            Windows.SafeArrayPutElement(pChangedData, idx, pValue);
            Windows.SysFreeString(pValue);
        }
        long result = Windows.UiaRaiseTextEditTextChangedEvent(pProvider, textEditChangeType, pChangedData);
        Logger.debug(this, () -> "UiaRaiseTextEditTextChangedEvent => 0x" + Long.toHexString(result));
    }

    protected void UiaRaiseNotificationEvent(NotificationKind notificationKind, NotificationProcessing notificationProcessing, String displayString, String activityId) {
        Logger.debug(this, () -> "UiaRaiseNotificationEvent("+notificationKind+", " + notificationProcessing +", " + displayString + ", " + activityId +")");
        long result = Windows.UiaRaiseNotificationEvent(accessible.getNativeAccessible(), notificationKind.getNativeValue(), notificationProcessing.getNativeValue(), displayString, activityId);
        Logger.debug(this, () -> "UiaRaiseNotificationEvent => 0x" + Long.toHexString(result));
    }

    protected void UiaRaiseActiveTextPositionChangedEvent(ITextRangeProvider textRange) {
        Logger.debug(this, () -> "UiaRaiseActiveTextPositionChangedEvent(" + textRange + ")");
        long result = Windows.UiaRaiseActiveTextPositionChangedEvent(accessible.getNativeAccessible(), wrapNative(textRange));
        Logger.debug(this, () -> "UiaRaiseActiveTextPositionChangedEvent => 0x" + Long.toHexString(result));
    }

    protected void UiaRaiseAsyncContentLoadedEvent(AsyncContentLoadedState asyncContentLoadedState, double percentComplete) {
        Logger.debug(this, () -> "UiaRaiseAsyncContentLoadedEvent(" + asyncContentLoadedState + ", " + percentComplete + ")");
        long result = Windows.UiaRaiseAsyncContentLoadedEvent(accessible.getNativeAccessible(), asyncContentLoadedState.getNativeValue(), percentComplete);
        Logger.debug(this, () -> "UiaRaiseAsyncContentLoadedEvent => 0x" + Long.toHexString(result));       
    }

    protected void UiaRaiseStructureChangedEvent(StructureChangeType structureChangeType, int[] runtimeId) {
        Logger.debug(this, () -> "UiaRaiseStructureChangedEvent(" + structureChangeType + ", " + Arrays.toString(runtimeId) + ")");
        long result = Windows.UiaRaiseStructureChangedEvent(accessible.getNativeAccessible(), structureChangeType.getNativeValue(), runtimeId);
        Logger.debug(this, () -> "UiaRaiseStructureChangedEvent => 0x" + Long.toHexString(result));   
    }

    protected long wrapNative(ITextRangeProvider textRange) {
      return ProxyTextRangeProvider.wrapNative(accessible, textRange);
    }

    protected long[] wrapNative(ITextRangeProvider[] providers) {
      long result[] = new long[providers.length];
      for (int i = 0; i < providers.length; i++) {
          result[i] = wrapNative(providers[i]);
      }
      return result;
  }
}
