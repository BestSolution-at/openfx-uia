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
