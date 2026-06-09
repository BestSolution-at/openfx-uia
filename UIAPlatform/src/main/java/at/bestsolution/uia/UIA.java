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
package at.bestsolution.uia;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import at.bestsolution.uia.internal.ProxyAccessible;
import at.bestsolution.uia.internal.ProxyAccessibleRegistry;
import javafx.scene.AccessibleAttribute;

public class UIA {

	static class Defaults {
		private static int nextId = 1;
		private static Map<Integer, Integer> idMap = new HashMap<>();

		public static String getAutomationId(IUIAElement element) {
			return "openfx-uia-" + System.identityHashCode(element);
		}

		public static int getId(IUIAElement element) {
			int hash = System.identityHashCode(element);
			return idMap.computeIfAbsent(hash, h -> nextId++);
		}

		public static <T> T getContext(Class<T> type, Object provider) {
			if (provider instanceof IUIAElement) {
				IUIAElement element = (IUIAElement) provider;
				ProxyAccessible instance = ProxyAccessibleRegistry.getInstance().findAccessible(element);
				if (instance != null) {
					return instance.getContext(type);
				}
			}
			return null;
		}

		public static <C> void runWithContext(Class<C> contextType, Object provider, Consumer<C> run) {
			C context = getContext(contextType, provider);
			if (context != null) {
				run.accept(context);
			}
		}
	}

	public static boolean isUIAQuery(AccessibleAttribute attribute, Object... parameters) {
		return (attribute == AccessibleAttribute.TEXT
				&& parameters.length == 2
				&& "getProvider".equals(parameters[0])
				&& IUIAElement.class == parameters[1]);
	}

}
