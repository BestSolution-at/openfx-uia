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
package com.sun.glass.ui.uia;

import com.sun.glass.ui.Application;
import com.sun.glass.ui.InvokeLaterDispatcher.InvokeLaterSubmitter;
import com.sun.glass.ui.Menu;
import com.sun.glass.ui.MenuBar;
import com.sun.glass.ui.MenuItem;
import com.sun.glass.ui.PlatformFactory;
import com.sun.glass.ui.delegate.ClipboardDelegate;
import com.sun.glass.ui.delegate.MenuBarDelegate;
import com.sun.glass.ui.delegate.MenuDelegate;
import com.sun.glass.ui.delegate.MenuItemDelegate;
import com.sun.glass.ui.win.WinPlatformFactory;

@SuppressWarnings("restriction")
public class UIAPlatformFactory extends PlatformFactory {

	private final WinPlatformFactory winPlatformFactory = new WinPlatformFactory();
	
	@Override
	public Application createApplication() {
		Application app = winPlatformFactory.createApplication();
		return new ReflectiveApplication(app, (InvokeLaterSubmitter) app, () -> new ProxyAccessible());
	}

	@Override
	public MenuBarDelegate createMenuBarDelegate(MenuBar menubar) {
		return winPlatformFactory.createMenuBarDelegate(menubar);
	}

	@Override
	public MenuDelegate createMenuDelegate(Menu menu) {
		return winPlatformFactory.createMenuDelegate(menu);
	}

	@Override
	public MenuItemDelegate createMenuItemDelegate(MenuItem menuItem) {
		return winPlatformFactory.createMenuItemDelegate(menuItem);
	}

	@Override
	public ClipboardDelegate createClipboardDelegate() {
		return winPlatformFactory.createClipboardDelegate();
	}

}
