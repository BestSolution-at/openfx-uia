package javafx.uia;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Contains values that specify the current state of the window for purposes of user interaction.
 */
public enum WindowInteractionState implements INativeEnum {
	/**
	 * The window is running. This does not guarantee that the window is ready for user interaction or is responding.
	 */
	Running(0),
	/**
	 * The window is closing.
	 */
	Closing(1), 
	/**
	 * The window is ready for user interaction.
	 */
	ReadyForUserInteraction(2), 
	/**
	 * The window is blocked by a modal window.
	 */
	BlockedByModalWindow(3),
	/**
	 * The window is not responding.
	 */
	NotResponding(4);
	
	private int nativeValue;
	
	@Override
	public int getNativeValue() {
		return nativeValue;
	}

	@Override
	public String getConstantName() {
		return name();
	}
	
	private WindowInteractionState(int nativeValue) {
		this.nativeValue = nativeValue;
	}
	
	public static Optional<WindowInteractionState> fromNativeValue(int nativeValue) {
		return Stream.of(values()).filter(value -> value.getNativeValue() == nativeValue).findFirst();
	}
}
