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
package at.bestsolution.uia.javafx.uia;

import java.util.Optional;
import java.util.stream.Stream;

/** Contains values that specify the type of UI Automation provider. The IRawElementProviderSimple::ProviderOptions property uses this enumeration. */
public enum ProviderOptions implements INativeEnum {
    /** The provider is a client-side (proxy) provider. */
    ProviderOptions_ClientSideProvider(0x1),
    /** The provider is a server-side provider. */
    ProviderOptions_ServerSideProvider(0x2),
    /** The provider is a non-client-area provider. */
    ProviderOptions_NonClientAreaProvider(0x4),
    /** The provider overrides another provider. */
    ProviderOptions_OverrideProvider(0x8),
    /** The provider handles its own focus, and does not want UI Automation to set focus to the nearest window on its behalf. This option is typically used by providers for windows that appear to take focus without actually receiving Win32 focus, such as menus and drop-downs. */
    ProviderOptions_ProviderOwnsSetFocus(0x10),
    /** The provider has explicit support for COM threading models, so that calls by UI Automation on COM-based providers are received on the appropriate thread. This means that STA-based provider implementations will be called back on their own STA thread, and therefore do not need extra synchronization to safely access resources that belong to that STA. MTA-based provider implementations will be called back on some other thread in the MTA, and will require appropriate synchronization to be added, as is usual for MTA code. */
    ProviderOptions_UseComThreading(0x20),
    /** The provider handles its own non-client area and does not want UI Automation to provide default accessibility support for controls in the non-client area, such as minimize/maximize buttons and menu bars. */
    ProviderOptions_RefuseNonClientSupport(0x40),
    /** The provider implements the IAccessible interface. */
    ProviderOptions_HasNativeIAccessible(0x80),
    /** The provider works in client coordinates instead of screen coordinates. */
    ProviderOptions_UseClientCoordinates(0x100);

    private int value;
    private ProviderOptions(int value) {
        this.value = value;
    }

    @Override
    public int getNativeValue() {
        return value;
    }

    @Override
    public String getConstantName() {
        return name();
    }

    public static Optional<ProviderOptions> fromNativeValue(int nativeValue) {
		return Stream.of(values()).filter(value -> value.getNativeValue() == nativeValue).findFirst();
	}
}
