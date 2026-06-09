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
package at.bestsolution.uia.internal.provider;

import java.util.Arrays;

import at.bestsolution.uia.AsyncContentLoadedState;
import at.bestsolution.uia.IEventId;
import at.bestsolution.uia.IPropertyId;
import at.bestsolution.uia.ITextRangeProvider;
import at.bestsolution.uia.NotificationKind;
import at.bestsolution.uia.NotificationProcessing;
import at.bestsolution.uia.StructureChangeType;
import at.bestsolution.uia.TextEditChangeType;
import at.bestsolution.uia.Variant;
import at.bestsolution.uia.internal.InstanceTracker;
import at.bestsolution.uia.internal.Logger;
import at.bestsolution.uia.internal.LoggerFactory;
import at.bestsolution.uia.internal.ProxyAccessible;
import at.bestsolution.uia.internal.ProxyTextRangeProvider;
import at.bestsolution.uia.internal.winapi.Windows;

public abstract class BaseAdapter<T> {

  private final static Logger LOG = LoggerFactory.create(BaseAdapter.class);

    protected final T provider;
    protected final ProxyAccessible accessible;


    public BaseAdapter(ProxyAccessible accessible, T provider) {
        this.accessible = accessible;
        this.provider = provider;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "["+this.provider+"]";
    }

    protected void UiaRaiseAutomationPropertyChangedEvent(IPropertyId propertyId, Variant oldV, Variant newV) {
        ProxyAccessible.UiaRaiseAutomationPropertyChangedEvent(accessible.getNativeAccessible(), propertyId.getNativeValue(), oldV.toWinVariant(), newV.toWinVariant());
    }

    protected void UiaRaiseAutomationEvent(IEventId eventId) {
        ProxyAccessible.UiaRaiseAutomationEvent(accessible.getNativeAccessible(), eventId.getNativeValue());
    }

    protected void UiaRaiseTextEditTextChangedEvent(TextEditChangeType changeType, String[] payload) {
        LOG.debug(this, () -> "UiaRaiseTextEditTextChangedEvent("+changeType+", "+Arrays.toString(payload)+")");
        long pProvider = accessible.getNativeAccessible();
        int textEditChangeType = changeType.getNativeValue();
        long pChangedData = Windows.SafeArrayCreateVector((short) Windows.VT_BSTR, 0, payload.length);
        for (int idx = 0; idx < payload.length; idx ++) {
            long pValue = Windows.SysAllocString(payload[idx]);
            Windows.SafeArrayPutElement(pChangedData, idx, pValue);
            Windows.SysFreeString(pValue);
        }
        long result = Windows.UiaRaiseTextEditTextChangedEvent(pProvider, textEditChangeType, pChangedData);
        LOG.debug(this, () -> "UiaRaiseTextEditTextChangedEvent => 0x" + Long.toHexString(result));
    }

    protected void UiaRaiseNotificationEvent(NotificationKind notificationKind, NotificationProcessing notificationProcessing, String displayString, String activityId) {
        LOG.debug(this, () -> "UiaRaiseNotificationEvent("+notificationKind+", " + notificationProcessing +", " + displayString + ", " + activityId +")");
        long result = Windows.UiaRaiseNotificationEvent(accessible.getNativeAccessible(), notificationKind.getNativeValue(), notificationProcessing.getNativeValue(), displayString, activityId);
        LOG.debug(this, () -> "UiaRaiseNotificationEvent => 0x" + Long.toHexString(result));
    }

    protected void UiaRaiseActiveTextPositionChangedEvent(ITextRangeProvider textRange) {
      InstanceTracker.withReason("UiaRaiseActiveTextPositionChangedEvent", () -> {
        LOG.debug(this, () -> "UiaRaiseActiveTextPositionChangedEvent(" + textRange + ")");
        long result = Windows.UiaRaiseActiveTextPositionChangedEvent(accessible.getNativeAccessible(), wrapNative(textRange));
        LOG.debug(this, () -> "UiaRaiseActiveTextPositionChangedEvent => 0x" + Long.toHexString(result));
        return null;
      });
    }

    protected void UiaRaiseAsyncContentLoadedEvent(AsyncContentLoadedState asyncContentLoadedState, double percentComplete) {
        LOG.debug(this, () -> "UiaRaiseAsyncContentLoadedEvent(" + asyncContentLoadedState + ", " + percentComplete + ")");
        long result = Windows.UiaRaiseAsyncContentLoadedEvent(accessible.getNativeAccessible(), asyncContentLoadedState.getNativeValue(), percentComplete);
        LOG.debug(this, () -> "UiaRaiseAsyncContentLoadedEvent => 0x" + Long.toHexString(result));
    }

    protected void UiaRaiseStructureChangedEvent(StructureChangeType structureChangeType, int[] runtimeId) {
        LOG.debug(this, () -> "UiaRaiseStructureChangedEvent(" + structureChangeType + ", " + Arrays.toString(runtimeId) + ")");
        long result = Windows.UiaRaiseStructureChangedEvent(accessible.getNativeAccessible(), structureChangeType.getNativeValue(), runtimeId);
        LOG.debug(this, () -> "UiaRaiseStructureChangedEvent => 0x" + Long.toHexString(result));
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
