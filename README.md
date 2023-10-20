# openfx-uia

Full UIA Support for OpenJFX

# Getting It

 https://maven.bestsolution.at/repos/releases/   
 Group ID: at.bestsolution.openfx-uia    

 You need the artifacts `UIAPlatform` and `UIAPlatform.agent` to develop an application.

 The agent adds `UIAPlatform.core` to the ext classpath and hooks the Accessible creation mechanism of javafx, while `UIAPlatform` contains the library and provides the API.

 `UIAPlatform.sample` contains some samples and   
 `UIAPlatform.fullsample` contains a self running jar launcher for the samples with all dependencies. It only needs the environment variable `JDK8FX` set to your java 8 + fx home.

# Usage

 * add `UIAPlatform.jar` to your classpath
 * launch your application with `-javaagent:UIAPlatform.agent.jar`

To create an UIA Element you have to implement a `IUIAElement` for it. There is also a `IUIAVirtualRootElement` for virtual roots and a `IUIAVirtaulElement` for virtual children. A virtual root is a JavaFX node with virtual children (the javafx children are no longer visible for the a11y). This can be used for example if you render on a canvas and want to add a11y information to the rendered content.

The UIA Element als may implement UIA Providers (see the [list of supported providers](#supported-providers))

To connect your UIA Element with a javafx node you have to extend the javafx node and overwrite `queryAccessibleAttribute()` with the following code:

```java
@Override
public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
  if (UIA.isUIAQuery(attribute, parameters)) {
    return yourUIAElementInstance;
  }
  return super.queryAccessibleAttribute(attribute, parameters);
}
```

You can look at the `UIAPlatform.sample` sources for further examples.

For how to use UIA look at the Microsoft documentation (here is a link to the [Control Pattern Interfaces](https://docs.microsoft.com/en-us/windows/win32/winauto/uiauto-cpinterfaces))

## Logging

 to enable logging add `-Duia.log=true`

# Supported Providers

| Provider                      |
| ------------------------      |
| IAnnotationProvider           |
| IDockProvider                 |
| IExpandCollapseProvider       |
| IGridItemProvider             |
| IGridProvider                 |
| IInvokeProvider               |
| IMultipleViewContainer        |
| IRangeValueProvider           |
| IScrollItemProvider           |
| IScrollProvider               |
| ISelectionItemProvider        |
| ISelectionProvider            |
| ISelectionProvider2           |
| IStylesProvider               |
| ISynchronziedInputProvider    |
| ITableItemProvider            |
| ITableProvider                |
| ITextChildProvider            |
| ITextEditProvider             |
| ITextProvider                 |
| ITextProvider2                |
| ITextRangeProvider            |
| ITextRangeProvider2           |
| IToggleProvider               |
| ITransformProvider            |
| ITransformProvider2           |
| IValueProvider                |
| IVirtualizedItemProvider      |
| IWindowProvider               |

# Differences between UIA and openfx-uia

 * The simple interfaces (`IRawElementPoviderSimple`, ...) are not supported and replaced by the `IUIAElement` and `IUIAVirtualRootElement`  
 * `IRawElementPRoviderFragment#Navigate()` cannot be implementeted, instead `IUIAVirtualRootElement#getChildren()` `IUIAVirtualElement#getChildren()` and `IUIAVirtualElement#getParent()` is provided.
 * Returing `HRESULT` is not supported.
 * Most types are provided in a java friendly way. Type names should be the same as in the C API.
 * Events are encapsulated in `IEvent` objects which can be obtained from the initialze method in the providers (There are also Context objects in the providers which provider the default events)
 * Property Change events are encapsulated in `IProperty` objects which can be obtained from the initialize method in the providers (Those are also provided by the Context objects)


 # Development Notes
 When launching via gradle the `--no-daemon` flag helps with the initial focus of the launched application. If omitted it behaves differently compared to starting by double clicking an icon.