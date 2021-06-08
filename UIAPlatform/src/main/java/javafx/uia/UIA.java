package javafx.uia;

import javafx.scene.AccessibleAttribute;

public class UIA {
	
	

	public static boolean isProviderQuery(Class<?> provider, AccessibleAttribute attribute, Object... parameters) {
		return (attribute == AccessibleAttribute.TEXT 
				&& parameters.length == 2 
				&& "getProvider".equals(parameters[0])
				&& provider == parameters[1]);
	}
}
