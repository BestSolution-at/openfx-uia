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

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.function.Supplier;

import com.sun.glass.ui.Accessible;
import com.sun.glass.ui.Application;
import com.sun.glass.ui.CommonDialogs.ExtensionFilter;
import com.sun.glass.ui.CommonDialogs.FileChooserResult;
import com.sun.glass.ui.Cursor;
import com.sun.glass.ui.InvokeLaterDispatcher;
import com.sun.glass.ui.Pixels;
import com.sun.glass.ui.Robot;
import com.sun.glass.ui.Screen;
import com.sun.glass.ui.Size;
import com.sun.glass.ui.Timer;
import com.sun.glass.ui.View;
import com.sun.glass.ui.Window;

// This class delegates all calls to the given application but allows a custom Accessible Factory
@SuppressWarnings("restriction")
public class ReflectiveApplication extends Application implements InvokeLaterDispatcher.InvokeLaterSubmitter {

	private final Application application;
	private final InvokeLaterDispatcher.InvokeLaterSubmitter submitter;
	
	private final Reflect reflect;
	
	private final Supplier<Accessible> accessibleFactory;
	
	public ReflectiveApplication(Application application, InvokeLaterDispatcher.InvokeLaterSubmitter submitter, Supplier<Accessible> accessibleFactory) {
		Logger.debug(this, () -> "new ReflectiveApplication");
		this.application = application;
		this.submitter = submitter;
		this.accessibleFactory = accessibleFactory;
		this.reflect = new Reflect(application);
	}


	@Override
	public Accessible createAccessible() {
		if (accessibleFactory == null) {
			return application.createAccessible();
		} else {
			return accessibleFactory.get();
		}
	}
	
	@Override
	protected void runLoop(Runnable launchable) {
		reflect.invokeVoid1("runLoop", Runnable.class, launchable);
	}
	
	@Override
	protected void finishTerminating() {
		reflect.invokeVoid0("finishTerminating");
	}
	
	@Override
	public boolean shouldUpdateWindow() {
		return application.shouldUpdateWindow();
	}
	
	@Override
	protected Object _enterNestedEventLoop() {
		return reflect.invokeObject0("_enterNestedEventLoop");
	}
	
	@Override
	protected void _leaveNestedEventLoop(Object retValue) {
		reflect.invokeVoid1("_leaveNestedEventLoop", Object.class, retValue);
	}

	 // FACTORY METHODS
	@Override
	public Window createWindow(Window owner, Screen screen, int styleMask) {
		return application.createWindow(owner, screen, styleMask);
	}

	@Override
	public Window createWindow(long parent) {
		return application.createWindow(parent);
	}

	@Override
	public View createView() {
		return application.createView();
	}

	@Override
	public Cursor createCursor(int type) {
		return application.createCursor(type);
	}

	@Override
	public Cursor createCursor(int x, int y, Pixels pixels) {
		return application.createCursor(x, y, pixels);
	}

	@Override
	protected void staticCursor_setVisible(boolean visible) {
		reflect.invokeObject1("staticCursor_setVisible", boolean.class, visible);
	}

	@Override
	protected Size staticCursor_getBestSize(int width, int height) {
		return (Size) reflect.invokeObject2("staticCursor_getBestSize", int.class, int.class, width, height);
	}
	
	@Override
	public Pixels createPixels(int width, int height, ByteBuffer data) {
		return application.createPixels(width, height, data);
	}

	@Override
	public Pixels createPixels(int width, int height, IntBuffer data) {
		return application.createPixels(width, height, data);
	}

	@Override
	public Pixels createPixels(int width, int height, IntBuffer data, float scale) {
		return application.createPixels(width, height, data, scale);
	}
	
	@Override
	protected int staticPixels_getNativeFormat() {
		return (int) reflect.invokeObject0("staticPixels_getNativeFormat");
	}
	
	@Override
	public Robot createRobot() {
		return application.createRobot();
	}
	
	@Override
	protected double staticScreen_getVideoRefreshPeriod() {
		return (double) reflect.invokeObject0("staticScreen_getVideoRefreshPeriod");
	}
	
	@Override
	protected Screen[] staticScreen_getScreens() {
		return (Screen[]) reflect.invokeObject0("staticScreen_getScreens");
	}

	@Override
	public Timer createTimer(Runnable runnable) {
		return application.createTimer(runnable);
	}
	
	@Override
	protected int staticTimer_getMinPeriod() {
		return (int) reflect.invokeObject0("staticTimer_getMinPeriod");
	}

	@Override
	protected int staticTimer_getMaxPeriod() {
		return (int) reflect.invokeObject0("staticTimer_getMaxPeriod");
	}
	
	@Override
	protected FileChooserResult staticCommonDialogs_showFileChooser(Window owner, String folder, String filename,
			String title, int type, boolean multipleMode, ExtensionFilter[] extensionFilters, int defaultFilterIndex) {
		return (FileChooserResult) reflect.invokeObject8("staticCommonDialogs_showFileChooser", 
				Window.class, String.class, String.class, String.class, int.class, boolean.class, ExtensionFilter[].class, int.class,
				owner, folder,filename, title, type, multipleMode, extensionFilters, defaultFilterIndex);
	}

	@Override
	protected File staticCommonDialogs_showFolderChooser(Window owner, String folder, String title) {
		return (File) reflect.invokeObject3("staticCommonDialogs_showFolderChooser", Window.class, String.class, String.class, owner, folder, title);
	}

	@Override
	protected long staticView_getMultiClickTime() {
		return (long) reflect.invokeObject0("staticView_getMultiClickTime");
	}

	@Override
	protected int staticView_getMultiClickMaxX() {
		return (int) reflect.invokeObject0("staticView_getMultiClickMaxX");
	}

	@Override
	protected int staticView_getMultiClickMaxY() {
		return (int) reflect.invokeObject0("staticView_getMultiClickMaxY");
	}

	
	@Override
	protected void _invokeAndWait(Runnable runnable) {
		reflect.invokeVoid1("_invokeAndWait", Runnable.class, runnable);
	}

	@Override
	public void submitForLaterInvocation(Runnable r) {
		submitter.submitForLaterInvocation(r);
	}
	
	@Override
	protected void _invokeLater(Runnable runnable) {
		reflect.invokeVoid1("_invokeLater", Runnable.class, runnable);
	}

	@Override
	public String getHighContrastTheme() {
		return application.getHighContrastTheme();
	}
	
	@Override
	protected boolean _supportsInputMethods() {
		return (boolean) reflect.invokeObject0("_supportsInputMethods");
	}
	
	@Override
	protected boolean _supportsTransparentWindows() {
		return (boolean) reflect.invokeObject0("_supportsTransparentWindows");
	}

	@Override
	protected boolean _supportsUnifiedWindows() {
		return (boolean) reflect.invokeObject0("_supportsUnifiedWindows");
	}
	
	@Override
	protected int _getKeyCodeForChar(char c) {
		return (int) reflect.invokeObject1("_getKeyCodeForChar", char.class, c);
	}

}
