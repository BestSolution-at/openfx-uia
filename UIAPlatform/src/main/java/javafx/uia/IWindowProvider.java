package javafx.uia;

public interface IWindowProvider {
	
	interface IWindowProviderEvents {
		void notifyCanMaximizeChanged(boolean oldValue, boolean newValue);
		void notifyCanMinimizeChanged(boolean oldValue, boolean newValue);
		void notifyIsModalChanged(boolean oldValue, boolean newValue);
		void notifyIsTopmostChanged(boolean oldValue, boolean newValue);
		void notifyWindowInteractionStateChanged(WindowInteractionState oldValue, WindowInteractionState newValue);
		void notifyWindowVisualStateChanged(WindowVisualState oldValue, WindowVisualState newValue);
		
		void notifyWindowOpened();
		void notifyWindowClosed();
	}
	
	void Close();
	boolean get_CanMaximize();
	boolean get_CanMinimize();
	boolean get_IsModal();
	boolean get_IsTopmost();
	WindowInteractionState get_WindowInteractionState();
	WindowVisualState get_WindowVisualState();
	void SetVisualState(WindowVisualState state);
	boolean WaitForInputIdle(int milliseconds);
	
	
	void initialize(IWindowProviderEvents events);
	
}
