package javafx.uia;

import java.util.Optional;
import java.util.stream.Stream;

public enum WindowVisualState implements INativeEnum {
	Normal(0), 
	Maximized(1), 
	Minimized(2);
	
	private int nativeValue;
	
	@Override
	public int getNativeValue() {
		return nativeValue;
	}

	@Override
	public String getConstantName() {
		return name();
	}
	
	private WindowVisualState(int nativeValue) {
		this.nativeValue = nativeValue;
	}
	
	public static Optional<WindowVisualState> fromNativeValue(int nativeValue) {
		return Stream.of(values()).filter(value -> value.getNativeValue() == nativeValue).findFirst();
	}
}
