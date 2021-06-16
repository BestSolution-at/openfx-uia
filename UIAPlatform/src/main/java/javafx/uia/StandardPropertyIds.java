package javafx.uia;

import java.util.Optional;
import java.util.stream.Stream;

import javafx.scene.Node;

import static javafx.uia.Variant.*;

/**
 * UI Automation Property Ids 
 * The descriptions are copied from above documentation. 
 *
 * see
 * <ul>
 * <li>https://docs.microsoft.com/en-us/windows/win32/winauto/uiauto-automation-element-propids</li>
 * <li>https://docs.microsoft.com/en-us/windows/win32/winauto/uiauto-control-pattern-availability-propids</li>
 * </ul>
 */
// TODO add javadoc from ms docs
public enum StandardPropertyIds implements IPropertyId {
	/**
	 * Identifies the RuntimeId property, which is an array of integers representing the identifier for an automation element.
The identifier is unique on the desktop, but it is guaranteed to be unique only within the UI of the desktop on which it was generated. Identifiers can be reused over time.
The format of RuntimeId can change. The returned identifier should be treated as an opaque value and used only for comparison; for example, to determine whether an automation element is in the cache.
Variant type: VT_I4 | VT_ARRAY
Default value: VT_EMPTY
	 */
	UIA_RuntimeIdPropertyId(30000, Variant.VT_I4 | Variant.VT_ARRAY, int[].class),
	/** Identifies the BoundingRectangle property, which specifies the coordinates of the rectangle that completely encloses the automation element. The rectangle is expressed in physical screen coordinates. It can contain points that are not clickable if the shape or clickable region of the UI item is irregular, or if the item is obscured by other UI elements.
Variant type: VT_R8 | VT_ARRAY
Default value: [0,0,0,0]
[!Note]
This property is NULL if the item is not currently displaying a UI. */
	UIA_BoundingRectanglePropertyId(30001, Variant.VT_R8 | Variant.VT_ARRAY, float[].class),
	/** Identifies the ProcessId property, which is an integer representing the process identifier (ID) of the automation element.
The process identifier (ID) is assigned by the operating system. It can be seen in the PID column of the Processes tab in Task Manager.
Variant type: VT_I4
Default value: 0 */
	UIA_ProcessIdPropertyId(30002, Variant.VT_I4, int.class),
	/** Identifies the ControlType property, which is a class that identifies the type of the automation element. ControlType defines characteristics of the UI elements by well known UI control primitives such as button or check box.
Variant type: VT_I4
Default value: UIA_CustomControlTypeId
[!Note]
Use the default value only if the automation element represents a completely new type of control. */
	UIA_ControlTypePropertyId(30003, Variant.VT_I4, int.class),

	/** Identifies the LocalizedControlType property, which is a text string describing the type of control that the automation element represents. The string should contain only lowercase characters:
Correct: "button"
Incorrect: "Button"

When LocalizedControlType is not specified by the element provider, the default localized string is supplied by the framework, according to the control type of the element (for example, "button" for the Button control type). An automation element with the Custom control type must support a localized control type string that represents the role of the element (for example, "color picker" for a custom control that enables users to choose and specify colors).
When a custom value is supplied, the string must match the application UI language or the operating system default UI language.
Variant type: VT_BSTR
Default value: empty string */
	UIA_LocalizedControlTypePropertyId(30004, Variant.VT_BSTR, String.class),
	/** Identifies the Name property, which is a string that holds the name of the automation element.
The Name property should be the same as the label text on screen. For example, Name should be "Browse" for a button element with the label "Browse". The Name property must not include the mnemonic character for the access keys (that is, "&"), which is underlined in the UI text presentation. Also, the Name property should not be an extended or modified version of the on-screen label because the inconsistency between the name and the label can cause confusion among client applications and users.
When the corresponding label text is not visible on screen, or when it is replaced by graphics, alternative text should be chosen. The alternative text should be concise, intuitive, and localized to the application UI language, or to the operating system default UI language. The alternative text should not be a detailed description of the visual details, but a concise description of the UI function or feature as if it were labeled by simple text. For example, the Windows Start menu button is named "Start" (button) instead of "Windows Logo on blue round sphere graphics" (button). For more information, see Creating Text Equivalents for Images.
When a UI label uses text graphics (for example, using ">>" for a button that adds an item from left to right), the Name property should be overridden by an appropriate text alternative (for example, "Add"). However the practice of using text graphics as a UI label is discouraged due to both localization and accessibility concerns.
The Name property must not include the control role or type information, such as "button" or "list"; otherwise, it will conflict with the text from the LocalizedControlType property when these two properties are appended (many existing assistive technologies do this).
The Name property cannot be used as a unique identifier among siblings. However, as long as it is consistent with the UI presentation, the same Name value can be supported among peers. For test automation, the clients should consider using the AutomationId or RuntimeId property.
Text controls do not always have to have the Name property be identical to the text that is displayed within the control, so long as the Text pattern is also supported.
Variant type: VT_BSTR
Default value: empty string */
	UIA_NamePropertyId(30005, Variant.VT_BSTR, String.class),
	/** Identifies the AcceleratorKey property, which is a string containing the accelerator key (also called shortcut key) combinations for the automation element.
Shortcut key combinations invoke an action. For example, CTRL+O is often used to invoke the Open file common dialog box. An automation element that has the AcceleratorKey property can implement the Invoke control pattern for the action that is equivalent to the shortcut command.
Variant type: VT_BSTR
Default value: empty string */
	UIA_AcceleratorKeyPropertyId(30006, Variant.VT_BSTR, String.class),
	/** Identifies the AccessKey property, which is a string containing the access key character for the automation element.
An access key (sometimes called a mnemonic) is a character in the text of a menu, menu item, or label of a control such as a button, that activates the associated menu function. For example, to open the File menu, for which the access key is typically F, the user would press ALT+F.
Variant type: VT_BSTR
Default value: empty string */
	UIA_AccessKeyPropertyId(30007, Variant.VT_BSTR, String.class),
	/** Identifies the HasKeyboardFocus property, which is a Boolean value that indicates whether the automation element has keyboard focus.
Variant type: VT_BOOL
Default value: FALSE */
	UIA_HasKeyboardFocusPropertyId(30008, Variant.VT_BOOL, boolean.class),
	/** Identifies the IsKeyboardFocusable property, which is a Boolean value that indicates whether the automation element can accept keyboard focus.
Variant type: VT_BOOL
Default value: FALSE */
	UIA_IsKeyboardFocusablePropertyId(30009, Variant.VT_BOOL, boolean.class),
	/** Identifies the IsEnabled property, which is a Boolean value that indicates whether the UI item referenced by the automation element is enabled and can be interacted with.
When the enabled state of a control is FALSE, it is assumed that child controls are also not enabled. Clients should not expect property-changed events from child elements when the state of the parent control changes.
Variant type: VT_BOOL
Default value: FALSE */
	UIA_IsEnabledPropertyId(30010, Variant.VT_BOOL, boolean.class),
	/** Identifies the AutomationId property, which is a string containing the UI Automation identifier (ID) for the automation element.
When it is available, the AutomationId of an element must be the same in any instance of the application, regardless of the local language. The value should be unique among sibling elements, but not necessarily unique across the entire desktop. For example, multiple instances of an application, or multiple folder views in Microsoft Windows Explorer, can contain elements with the same AutomationId property, such as "SystemMenuBar".
Although support for AutomationId is always recommended for better automated testing support, this property is not mandatory. Where it is supported, AutomationId is useful for creating a test automation script that runs regardless of the UI language. Clients should make no assumptions regarding the AutomationId values exposed by other applications. AutomationId is not guaranteed to be stable across different releases or builds of an application.
Variant type: VT_BSTR
Default value: empty string */
	UIA_AutomationIdPropertyId(30011, Variant.VT_BSTR, String.class),
	/** Identifies the ClassName property, which is a string containing the class name for the automation element as assigned by the control developer.
The class name depends on the implementation of the UI Automation provider and therefore is not always in a standard format. However, if the class name is known, it can be used to verify that an application is working with the expected automation element.
Variant type: VT_BSTR
Default value: empty string */
	UIA_ClassNamePropertyId(30012, Variant.VT_BSTR, String.class),
	/** Identifies the HelpText property, which is a help text string associated with the automation element.
The HelpText property can be supported with placeholder text appearing in edit or list controls. For example, "Type text here for search" is a good candidate the HelpText property for an edit control that places the text prior to the user's actual input. However, it is not adequate for the name property of the edit control.
When HelpText is supported, the string must match the application UI language or the operating system default UI language.
Variant type: VT_BSTR
Default value: empty string */
	UIA_HelpTextPropertyId(30013, Variant.VT_BSTR, String.class),
	/** Identifies the ClickablePoint property, which is a point on the automation element that can be clicked. An element cannot be clicked if it is completely or partially obscured by another window.
Variant type: VT_R8 | VT_ARRAY
Default value: VT_EMPTY */
	UIA_ClickablePointPropertyId(30014, Variant.VT_R8 | Variant.VT_ARRAY, float[].class),
	/** <td>Identifies the <strong>Culture</strong> property, which contains a locale identifier for the automation element (for example, <code>0x0409</code> for "en-US" or English (United States)).<br> Each locale has a unique identifier, a 32-bit value that consists of a language identifier and a sort order identifier. The locale identifier is a standard international numeric abbreviation and has the components necessary to uniquely identify one of the installed operating system-defined locales. For more information, see <a href="/en-us/windows/desktop/Intl/language-identifier-constants-and-strings" data-linktype="absolute-path">Language Identifier Constants and Strings</a>.<br> This property may exist on a per-control basis, but typically is only available on an application level.<br> Variant type: <strong>VT_I4</strong><br> Default value: 0<br></td> */
	UIA_CulturePropertyId(30015, Variant.VT_I4, int.class),
	/** <td>Identifies the <strong>IsControlElement</strong> property, which is a Boolean value that specifies whether the element appears in the control view of the automation element tree. For more information, see <a href="uiauto-treeoverview" data-linktype="relative-path">UI Automation Tree Overview</a>.<br> Variant type: <strong>VT_BOOL</strong><br> Default value: <strong>TRUE</strong><br></td> */
	UIA_IsControlElementPropertyId(30016, Variant.VT_BOOL, boolean.class),
	/** <td>Identifies the <strong>IsContentElement</strong> property, which is a Boolean value that specifies whether the element appears in the content view of the automation element tree. For more information, see <a href="uiauto-treeoverview" data-linktype="relative-path">UI Automation Tree Overview</a>.<br>
<blockquote>
[!Note]<br>
For an element to appear in the content view, both the <strong>IsContentElement</strong> property and the <strong>IsControlElement</strong> property must be <strong>TRUE</strong>.
</blockquote>
<br> <br> Variant type: <strong>VT_BOOL</strong><br> Default value: <strong>TRUE</strong><br></td> */
	UIA_IsContentElementPropertyId(30017, Variant.VT_BOOL, boolean.class),
	/** <td>Identifies the <strong>LabeledBy</strong> property, which is an automation element that contains the text label for this element.<br> This property can be used to retrieve, for example, the static text label for a combo box.<br> Variant type: <strong>VT_UNKNOWN</strong><br> Default value: <strong>NULL</strong><br></td> */
	UIA_LabeledByPropertyId(30018, Variant.VT_UNKNOWN, Node.class),
	/** <td>Identifies the <strong>IsPassword</strong> property, which is a Boolean value that indicates whether the automation element contains protected content or a password.<br> When the <strong>IsPassword</strong> property is <strong>TRUE</strong> and the element has the keyboard focus, a client application should disable keyboard echoing or keyboard input feedback that may expose the user's protected information. Attempting to access the <strong>Value</strong> property of the protected element (edit control) may cause an error to occur.<br> Variant type: <strong>VT_BOOL</strong><br> Default value: <strong>FALSE</strong><br></td> */
	UIA_IsPasswordPropertyId(30019, Variant.VT_BOOL, boolean.class),
	/** <td>Identifies the <strong>NativeWindowHandle</strong> property, which is an integer that represents the handle (<strong>HWND</strong>) of the automation element window, if it exists; otherwise, this property is 0.<br> Variant type: <strong>VT_I4</strong><br> Default value: 0<br></td> */
	UIA_NativeWindowHandlePropertyId(30020, Variant.VT_I4, int.class),
	/** <td>Identifies the <strong>ItemType</strong> property, which is a text string describing the type of the automation element.<br> <strong>ItemType</strong> is used to obtain information about items in a list, tree view, or data grid. For example, an item in a file directory view might be a "Document File" or a "Folder".<br> When <strong>ItemType</strong> is supported, the string must match the application UI language or the operating system default UI language.<br> Variant type: <strong>VT_BSTR</strong><br> Default value: empty string<br></td> */
	UIA_ItemTypePropertyId(30021, Variant.VT_BSTR, String.class),
	/** <td>Identifies the <strong>IsOffscreen</strong> property, which is a Boolean value that indicates whether the automation element is entirely scrolled out of view (for example, an item in a list box that is outside the viewport of the container object) or collapsed out of view (for example, an item in a tree view or menu, or in a minimized window). If the element has a clickable point that can cause it to receive the focus, the element is considered to be on-screen while a portion of the element is off-screen.<br> The value of the property is not affected by occlusion by other windows, or by whether the element is visible on a specific monitor.<br> If the <strong>IsOffscreen</strong> property is <strong>TRUE</strong>, the UI element is scrolled off-screen or collapsed. The element is temporarily hidden, yet it remains in the end-user's perception and continues to be included in the UI model. The object can be brought back into view by scrolling, clicking a drop-down, and so on.<br> Objects that the end-user does not perceive at all, or that are "programmatically hidden" (for example, a dialog box that has been dismissed, but the underlying object is still cached by the application) should not be in the automation element tree in the first place (instead of setting the state of <strong>IsOffscreen</strong> to <strong>TRUE</strong>).<br> Variant type: <strong>VT_BOOL</strong><br> Default value: <strong>FALSE</strong><br></td> */
	UIA_IsOffscreenPropertyId(30022, VT_BOOL, boolean.class),
	/** <td>Identifies the <strong>Orientation</strong> property, which indicates the orientation of the control represented by the automation element. The property is expressed as a value from the <a href="/en-us/windows/desktop/api/UIAutomationCore/ne-uiautomationcore-orientationtype" data-linktype="absolute-path"><strong>OrientationType</strong></a> enumerated type.<br> The <strong>Orientation</strong> property is supported by controls, such as scroll bars and sliders, that can have either a vertical or a horizontal orientation. Otherwise, it can always be <a href="/en-us/windows/desktop/api/UIAutomationCore/ne-uiautomationcore-orientationtype" data-linktype="absolute-path"><strong>OrientationType_None</strong></a>, which means that the control has no orientation.<br> Variant type: <strong>VT_I4</strong><br> Default value: 0 (<a href="/en-us/windows/desktop/api/UIAutomationCore/ne-uiautomationcore-orientationtype" data-linktype="absolute-path"><strong>OrientationType_None</strong></a>)<br></td> */
	UIA_OrientationPropertyId(30023, VT_I4, OrientationType.class),
	/** <td>Identifies the <strong>FrameworkId</strong> property, which is a string containing the name of the underlying UI framework that the automation element belongs to.<br> The <strong>FrameworkId</strong> enables client applications to process automation elements differently depending on the particular UI framework. Examples of property values include "Win32", "WinForm", and "DirectUI".<br> Variant type: <strong>VT_BSTR</strong><br> Default value: empty string<br></td> */
	UIA_FrameworkIdPropertyId(30024, VT_BSTR, String.class),
	/** <td>Identifies the <strong>IsRequiredForForm</strong> property, which is a Boolean value that indicates whether the automation element is required to be filled out on a form.<br> Variant type: <strong>VT_BOOL</strong><br> Default value: <strong>FALSE</strong><br></td> */
	UIA_IsRequiredForFormPropertyId(30025, VT_BOOL, boolean.class),
	/** <td>Identifies the <strong>ItemStatus</strong> property, which is a text string describing the status of an item of the automation element.<br> <strong>ItemStatus</strong> enables a client to ascertain whether an element is conveying status about an item as well as what the status is. For example, an item associated with a contact in a messaging application might be "Busy" or "Connected".<br> When <strong>ItemStatus</strong> is supported, the string must match the application UI language or the operating system default UI language.<br> Variant type: <strong>VT_BSTR</strong><br> Default value: empty string<br></td> */
	UIA_ItemStatusPropertyId(30026, VT_BSTR, String.class),
	
	
	// Pattern available property ids (https://docs.microsoft.com/en-us/windows/win32/winauto/uiauto-control-pattern-availability-propids)
	
	/** <td style="text-align: left;">Identifies the <strong>IsDockPatternAvailable</strong> property, which indicates whether the <a href="uiauto-implementingdock" data-linktype="relative-path">Dock</a> control pattern is available for the automation element. If <strong>TRUE</strong>, a client can retrieve an <a href="/en-us/windows/desktop/api/UIAutomationClient/nn-uiautomationclient-iuiautomationdockpattern" data-linktype="absolute-path"><strong>IUIAutomationDockPattern</strong></a> interface from the element.<br></td> */
	UIA_IsDockPatternAvailablePropertyId(30027, VT_BOOL, boolean.class),
	/** <td style="text-align: left;">Identifies the <strong>IsExpandCollapsePatternAvailable</strong> property, which indicates whether the <a href="uiauto-implementingexpandcollapse" data-linktype="relative-path">ExpandCollapse</a> control pattern is available for the automation element. If <strong>TRUE</strong>, a client can retrieve an <a href="/en-us/windows/desktop/api/UIAutomationClient/nn-uiautomationclient-iuiautomationexpandcollapsepattern" data-linktype="absolute-path"><strong>IUIAutomationExpandCollapsePattern</strong></a> interface from the element.<br></td> */
	UIA_IsExpandCollapsePatternAvailablePropertyId(30028, VT_BOOL, boolean.class),
	/** <td style="text-align: left;">Identifies the <strong>IsGridItemPatternAvailable</strong> property, which indicates whether the <a href="uiauto-implementinggriditem" data-linktype="relative-path">GridItem</a> control pattern is available for the automation element. If <strong>TRUE</strong>, a client can retrieve an <a href="/en-us/windows/desktop/api/UIAutomationClient/nn-uiautomationclient-iuiautomationgriditempattern" data-linktype="absolute-path"><strong>IUIAutomationGridItemPattern</strong></a> interface from the element.<br></td> */
	UIA_IsGridItemPatternAvailablePropertyId(30029, VT_BOOL, boolean.class),
	/** <td style="text-align: left;">Identifies the <strong>IsGridPatternAvailable</strong> property, which indicates whether the <a href="uiauto-implementinggrid" data-linktype="relative-path">Grid</a> control pattern is available for the automation element. If <strong>TRUE</strong>, a client can retrieve an <a href="/en-us/windows/desktop/api/UIAutomationClient/nn-uiautomationclient-iuiautomationgridpattern" data-linktype="absolute-path"><strong>IUIAutomationGridPattern</strong></a> interface from the element.<br></td> */
	UIA_IsGridPatternAvailablePropertyId(30030, VT_BOOL, boolean.class),
	/** <td style="text-align: left;">Identifies the <strong>IsInvokePatternAvailable</strong> property, which indicates whether the <a href="uiauto-implementinginvoke" data-linktype="relative-path">Invoke</a> control pattern is available for the automation element. If <strong>TRUE</strong>, a client can retrieve an <a href="/en-us/windows/desktop/api/UIAutomationClient/nn-uiautomationclient-iuiautomationinvokepattern" data-linktype="absolute-path"><strong>IUIAutomationInvokePattern</strong></a> interface from the element.<br></td> */
	UIA_IsInvokePatternAvailablePropertyId(30031, VT_BOOL, boolean.class),
	/** <td style="text-align: left;">Identifies the <strong>IsMultipleViewPatternAvailable</strong> property, which indicates whether the <a href="uiauto-implementingmultipleview" data-linktype="relative-path">MultipleView</a> control pattern is available for the automation element. If <strong>TRUE</strong>, a client can retrieve an <a href="/en-us/windows/desktop/api/UIAutomationClient/nn-uiautomationclient-iuiautomationmultipleviewpattern" data-linktype="absolute-path"><strong>IUIAutomationMultipleViewPattern</strong></a> interface from the element.<br></td> */
	UIA_IsMultipleViewPatternAvailablePropertyId(30032, VT_BOOL, boolean.class),
	/** <td style="text-align: left;">Identifies the <strong>IsRangeValuePatternAvailable</strong> property, which indicates whether the <a href="uiauto-implementingrangevalue" data-linktype="relative-path">RangeValue</a> control pattern is available for the automation element. If <strong>TRUE</strong>, a client can retrieve an <a href="/en-us/windows/desktop/api/UIAutomationClient/nn-uiautomationclient-iuiautomationrangevaluepattern" data-linktype="absolute-path"><strong>IUIAutomationRangeValuePattern</strong></a> interface from the element.<br></td> */
	UIA_IsRangeValuePatternAvailablePropertyId(30033, VT_BOOL, boolean.class),
	/** <td style="text-align: left;">Identifies the <strong>IsScrollPatternAvailable</strong> property, which indicates whether the <a href="uiauto-implementingscroll" data-linktype="relative-path">Scroll</a> control pattern is available for the automation element. If <strong>TRUE</strong>, a client can retrieve an <a href="/en-us/windows/desktop/api/UIAutomationClient/nn-uiautomationclient-iuiautomationscrollpattern" data-linktype="absolute-path"><strong>IUIAutomationScrollPattern</strong></a> interface from the element.<br></td> */
	UIA_IsScrollPatternAvailablePropertyId(30034, VT_BOOL, boolean.class),
	/** <td style="text-align: left;">Identifies the <strong>IsScrollItemPatternAvailable</strong> property, which indicates whether the <a href="uiauto-implementingscrollitem" data-linktype="relative-path">ScrollItem</a> control pattern is available for the automation element. If <strong>TRUE</strong>, a client can retrieve an <a href="/en-us/windows/desktop/api/UIAutomationClient/nn-uiautomationclient-iuiautomationscrollitempattern" data-linktype="absolute-path"><strong>IUIAutomationScrollItemPattern</strong></a> interface from the element.<br></td> */
	UIA_IsScrollItemPatternAvailablePropertyId(30035, VT_BOOL, boolean.class),
	/** <td style="text-align: left;">Identifies the <strong>IsSelectionItemPatternAvailable</strong> property, which indicates whether the <a href="uiauto-implementingselectionitem" data-linktype="relative-path">SelectionItem</a> control pattern is available for the automation element. If <strong>TRUE</strong>, a client can retrieve an <a href="/en-us/windows/desktop/api/UIAutomationClient/nn-uiautomationclient-iuiautomationselectionitempattern" data-linktype="absolute-path"><strong>IUIAutomationSelectionItemPattern</strong></a> interface from the element.<br></td> */
	UIA_IsSelectionItemPatternAvailablePropertyId(30036, VT_BOOL, boolean.class),
	/** <td style="text-align: left;">Identifies the <strong>IsSelectionPatternAvailable</strong> property, which indicates whether the <a href="uiauto-implementingselection" data-linktype="relative-path">Selection</a> control pattern is available for the automation element. If <strong>TRUE</strong>, a client can retrieve an <a href="/en-us/windows/desktop/api/UIAutomationClient/nn-uiautomationclient-iuiautomationselectionpattern" data-linktype="absolute-path"><strong>IUIAutomationSelectionPattern</strong></a> interface from the element.<br></td> */
	UIA_IsSelectionPatternAvailablePropertyId(30037, VT_BOOL, boolean.class),
	/** <td style="text-align: left;">Identifies the <strong>IsTablePatternAvailable</strong> property, which indicates whether the <a href="uiauto-implementingtable" data-linktype="relative-path">Table</a> control pattern is available for the automation element. If <strong>TRUE</strong>, a client can retrieve an <a href="/en-us/windows/desktop/api/UIAutomationClient/nn-uiautomationclient-iuiautomationtablepattern" data-linktype="absolute-path"><strong>IUIAutomationTablePattern</strong></a> interface from the element.<br></td> */
	UIA_IsTablePatternAvailablePropertyId(30038, VT_BOOL, boolean.class),
	/** <td style="text-align: left;">Identifies the <strong>IsTableItemPatternAvailable</strong> property, which indicates whether the <a href="uiauto-implementingtableitem" data-linktype="relative-path">TableItem</a> control pattern is available for the automation element. If <strong>TRUE</strong>, a client can retrieve an <a href="/en-us/windows/desktop/api/UIAutomationClient/nn-uiautomationclient-iuiautomationtableitempattern" data-linktype="absolute-path"><strong>IUIAutomationTableItemPattern</strong></a> interface from the element.<br></td> */
	UIA_IsTableItemPatternAvailablePropertyId(30039, VT_BOOL, boolean.class),
	/** <td style="text-align: left;">Identifies the <strong>IsTextPatternAvailable</strong> property, which indicates whether the <a href="uiauto-implementingtextandtextrange" data-linktype="relative-path">Text</a> control pattern is available for the automation element. If <strong>TRUE</strong>, a client can retrieve an <a href="/en-us/windows/desktop/api/UIAutomationClient/nn-uiautomationclient-iuiautomationtextpattern" data-linktype="absolute-path"><strong>IUIAutomationTextPattern</strong></a> interface from the element.<br></td> */
	UIA_IsTextPatternAvailablePropertyId(30040, VT_BOOL, boolean.class),
	/** <td style="text-align: left;">Identifies the <strong>IsTogglePatternAvailable</strong> property, which indicates whether the <a href="uiauto-implementingtoggle" data-linktype="relative-path">Toggle</a> control pattern is available for the automation element. If <strong>TRUE</strong>, a client can retrieve an <a href="/en-us/windows/desktop/api/UIAutomationClient/nn-uiautomationclient-iuiautomationtogglepattern" data-linktype="absolute-path"><strong>IUIAutomationTogglePattern</strong></a> interface from the element.<br></td> */
	UIA_IsTogglePatternAvailablePropertyId(30041, VT_BOOL, boolean.class),
	/** <td style="text-align: left;">Identifies the <strong>IsTransformPatternAvailable</strong> property, which indicates whether the <a href="uiauto-implementingtransform" data-linktype="relative-path">Transform</a> control pattern is available for the automation element. If <strong>TRUE</strong>, a client can retrieve an <a href="/en-us/windows/desktop/api/UIAutomationClient/nn-uiautomationclient-iuiautomationtransformpattern" data-linktype="absolute-path"><strong>IUIAutomationTransformPattern</strong></a> interface from the element.<br></td> */
	UIA_IsTransformPatternAvailablePropertyId(30042, VT_BOOL, boolean.class),
	/** <td style="text-align: left;">Identifies the <strong>IsTransformPattern2Available</strong> property, which indicates whether version two of the <a href="uiauto-implementingtransform" data-linktype="relative-path">Transform</a> control pattern is available for the automation element. If <strong>TRUE</strong>, a client can retrieve an <a href="/en-us/windows/desktop/api/uiautomationclient/nn-uiautomationclient-iuiautomationtransformpattern2" data-linktype="absolute-path"><strong>IUIAutomationTransformPattern2</strong></a> interface from the element. Supported starting with Windows 8.<br></td> */
	UIA_IsValuePatternAvailablePropertyId(30043, VT_BOOL, boolean.class),
	/** <td style="text-align: left;">Identifies the <strong>IsWindowPatternAvailable</strong> property, which indicates whether the <a href="uiauto-implementingwindow" data-linktype="relative-path">Window</a> control pattern is available for the automation element. If <strong>TRUE</strong>, a client can retrieve an <a href="/en-us/windows/desktop/api/UIAutomationClient/nn-uiautomationclient-iuiautomationwindowpattern" data-linktype="absolute-path"><strong>IUIAutomationWindowPattern</strong></a> interface from the element.<br></td> */
	UIA_IsWindowPatternAvailablePropertyId(30044, VT_BOOL, boolean.class),

	
	// control pattern property ids (https://docs.microsoft.com/en-us/windows/win32/winauto/uiauto-control-pattern-propids)
	
	/** <td style="text-align: left;">Identifies the <a href="/en-us/windows/desktop/api/UIAutomationCore/nf-uiautomationcore-ivalueprovider-get_value" data-linktype="absolute-path"><strong>Value</strong></a> property of the <a href="uiauto-implementingvalue" data-linktype="relative-path">Value</a> control pattern.<br> This property indicates the value of the automation element.<br> Variant type: <strong>VT_BSTR</strong><br> Default value: empty string<br></td> */
	UIA_ValueValuePropertyId(30045, VT_BSTR, String.class),
	/** <td style="text-align: left;">Identifies the <a href="/en-us/windows/desktop/api/UIAutomationCore/nf-uiautomationcore-ivalueprovider-get_isreadonly" data-linktype="absolute-path"><strong>IsReadOnly</strong></a> property of the <a href="uiauto-implementingvalue" data-linktype="relative-path">Value</a> control pattern.<br> This property indicates whether the value of the automation element is read-only. <br> Variant type: <strong>VT_BOOL</strong><br> Default value: <strong>TRUE</strong><br></td> */
	UIA_ValueIsReadOnlyPropertyId(30046, VT_BOOL, boolean.class),

	
	/** <td style="text-align: left;">Identifies the Value property of the <a href="uiauto-implementingrangevalue" data-linktype="relative-path">RangeValue</a> control pattern.<br> This property is the current value of the automation element. <br> Variant type: <strong>VT_R8</strong><br> Default value: 0<br></td> */
	UIA_RangeValueValuePropertyId(30047, VT_R8, float.class),
	/** <td style="text-align: left;">Identifies the <a href="/en-us/windows/desktop/api/UIAutomationCore/nf-uiautomationcore-irangevalueprovider-get_isreadonly" data-linktype="absolute-path"><strong>IsReadOnly</strong></a> property of the <a href="uiauto-implementingrangevalue" data-linktype="relative-path">RangeValue</a> control pattern.<br> This property indicates whether the value of the automation element is read-only.<br> Variant type: <strong>VT_BOOL</strong><br> Default value: <strong>TRUE</strong><br></td> */
	UIA_RangeValueIsReadOnlyPropertyId(30048, VT_BOOL, boolean.class),
	/** <td style="text-align: left;">Identifies the <a href="/en-us/windows/desktop/api/UIAutomationCore/nf-uiautomationcore-irangevalueprovider-get_minimum" data-linktype="absolute-path"><strong>Minimum</strong></a> property of the <a href="uiauto-implementingrangevalue" data-linktype="relative-path">RangeValue</a> control pattern.<br> This property is the minimum range value supported by the automation element. <br> Variant type: <strong>VT_R8</strong><br> Default value: 0<br></td> */
	UIA_RangeValueMinimumPropertyId(30049, VT_R8, float.class),
	/** <td style="text-align: left;">Identifies the <a href="/en-us/windows/desktop/api/UIAutomationCore/nf-uiautomationcore-irangevalueprovider-get_maximum" data-linktype="absolute-path"><strong>Maximum</strong></a> property of the <a href="uiauto-implementingrangevalue" data-linktype="relative-path">RangeValue</a> control pattern.<br> This property is the maximum range value supported by the automation element.<br> Variant type: <strong>VT_R8</strong><br> Default value: 0<br></td> */
	UIA_RangeValueMaximumPropertyId(30050, VT_R8, float.class),
	/** <td style="text-align: left;">Identifies the <a href="/en-us/windows/desktop/api/UIAutomationCore/nf-uiautomationcore-irangevalueprovider-get_largechange" data-linktype="absolute-path"><strong>LargeChange</strong></a> property of the <a href="uiauto-implementingrangevalue" data-linktype="relative-path">RangeValue</a> control pattern.<br> This property is the large-change value, unique to the automation element, that is added to or subtracted from the <a href="/en-us/windows/desktop/api/UIAutomationCore/nf-uiautomationcore-irangevalueprovider-get_value" data-linktype="absolute-path"><strong>Value</strong></a> property.<br> Variant type: <strong>VT_R8</strong><br> Default value: 0<br></td> */
	UIA_RangeValueLargeChangePropertyId(30051, VT_R8, float.class),
	/** <td style="text-align: left;">Identifies the <a href="/en-us/windows/desktop/api/UIAutomationCore/nf-uiautomationcore-irangevalueprovider-get_smallchange" data-linktype="absolute-path"><strong>SmallChange</strong></a> property of the <a href="uiauto-implementingrangevalue" data-linktype="relative-path">RangeValue</a> control pattern.<br> This property is the small-change value, unique to the automation element, that is added to or subtracted from the <a href="/en-us/windows/desktop/api/UIAutomationCore/nf-uiautomationcore-irangevalueprovider-get_value" data-linktype="absolute-path"><strong>Value</strong></a> property. <br> Variant type: <strong>VT_R8</strong><br> Default value: 0<br></td> */
	UIA_RangeValueSmallChangePropertyId(30052, VT_R8, float.class),
	/** <td style="text-align: left;">Identifies the <a href="/en-us/windows/desktop/api/UIAutomationCore/nf-uiautomationcore-iscrollprovider-get_horizontalscrollpercent" data-linktype="absolute-path"><strong>HorizontalScrollPercent</strong></a> property of the <a href="uiauto-implementingscroll" data-linktype="relative-path">Scroll</a> control pattern.<br> This property is the current horizontal scroll position expressed as a percentage of the total content area within the automation element.<br> Variant type: <strong>VT_R8</strong><br> Default value: 0<br></td> */
	UIA_ScrollHorizontalScrollPercentPropertyId(30053, VT_R8, float.class),
	/** <td style="text-align: left;">Identifies the <a href="/en-us/windows/desktop/api/UIAutomationCore/nf-uiautomationcore-iscrollprovider-get_horizontalviewsize" data-linktype="absolute-path"><strong>HorizontalViewSize</strong></a> property of the <a href="uiauto-implementingscroll" data-linktype="relative-path">Scroll</a> control pattern. <br> This property is the horizontal size of the viewable region expressed as a percentage of the total content area within the element. <br> Variant type: <strong>VT_R8</strong><br> Default value: 100<br></td> */
	UIA_ScrollHorizontalViewSizePropertyId(30054, VT_R8, float.class),
	/** <td style="text-align: left;">Identifies the <a href="/en-us/windows/desktop/api/UIAutomationCore/nf-uiautomationcore-iscrollprovider-get_verticalscrollpercent" data-linktype="absolute-path"><strong>VerticalScrollPercent</strong></a> property of the <a href="uiauto-implementingscroll" data-linktype="relative-path">Scroll</a> control pattern.<br> This property is the current vertical scroll position expressed as a percentage of the total content area within the automation element.<br> Variant type: <strong>VT_R8</strong><br> Default value: 0<br></td> */
	UIA_ScrollVerticalScrollPercentPropertyId(30055, VT_R8, float.class),
	/** <td style="text-align: left;">Identifies the <a href="/en-us/windows/desktop/api/UIAutomationCore/nf-uiautomationcore-iscrollprovider-get_verticalviewsize" data-linktype="absolute-path"><strong>VerticalViewSize</strong></a> property of the <a href="uiauto-implementingscroll" data-linktype="relative-path">Scroll</a> control pattern.<br> This property is the vertical size of the viewable region expressed as a percentage of the total content area within the element. <br> Variant type: <strong>VT_R8</strong><br> Default value: 100<br></td> */
	UIA_ScrollVerticalViewSizePropertyId(30056, VT_R8, float.class),
	/** <td style="text-align: left;">Identifies the <a href="/en-us/windows/desktop/api/UIAutomationCore/nf-uiautomationcore-iscrollprovider-get_horizontallyscrollable" data-linktype="absolute-path"><strong>HorizontallyScrollable</strong></a> property of the <a href="uiauto-implementingscroll" data-linktype="relative-path">Scroll</a> control pattern.<br> This property indicates whether the automation element can scroll horizontally. <br> Variant type: <strong>VT_BOOL</strong><br> Default value: <strong>FALSE</strong><br></td> */
	UIA_ScrollHorizontallyScrollablePropertyId(30057, VT_BOOL, boolean.class),
	/** <td style="text-align: left;">Identifies the <a href="/en-us/windows/desktop/api/UIAutomationCore/nf-uiautomationcore-iscrollprovider-get_verticallyscrollable" data-linktype="absolute-path"><strong>VerticallyScrollable</strong></a> property of the <a href="uiauto-implementingscroll" data-linktype="relative-path">Scroll</a> control pattern. <br> This property indicates whether the automation element can scroll vertically. <br> Variant type: <strong>VT_BOOL</strong><br> Default value: <strong>FALSE</strong><br></td> */
	UIA_ScrollVerticallyScrollablePropertyId(30058, VT_BOOL, boolean.class),
	/** <td style="text-align: left;">Identifies the <a href="/en-us/windows/desktop/api/UIAutomationCore/nf-uiautomationcore-iselectionprovider-get_isselectionrequired" data-linktype="absolute-path"><strong>IsSelectionRequired</strong></a> property of the <a href="uiauto-implementingselection" data-linktype="relative-path">Selection</a> control pattern.<br> This property indicates whether the automation element requires at least one child item to be selected.<br> Variant type: <strong>VT_BOOL</strong><br> Default value: <strong>FALSE</strong><br></td> */
	UIA_SelectionSelectionPropertyId(30059, VT_UNKNOWN | VT_ARRAY, Node[].class),
	/** <td style="text-align: left;">Identifies the <a href="/en-us/windows/desktop/api/UIAutomationCore/nf-uiautomationcore-iselectionprovider-get_canselectmultiple" data-linktype="absolute-path"><strong>CanSelectMultiple</strong></a> property of the <a href="uiauto-implementingselection" data-linktype="relative-path">Selection</a> control pattern.<br> This property indicates whether the automation element allows more than one child element to be selected concurrently.<br> Variant type: <strong>VT_BOOL</strong><br> Default value: <strong>FALSE</strong><br></td> */
	UIA_SelectionCanSelectMultiplePropertyId(30060, VT_BOOL, boolean.class),
	/** <td style="text-align: left;">Identifies the <a href="/en-us/windows/desktop/api/UIAutomationCore/nf-uiautomationcore-iselectionprovider-get_isselectionrequired" data-linktype="absolute-path"><strong>IsSelectionRequired</strong></a> property of the <a href="uiauto-implementingselection" data-linktype="relative-path">Selection</a> control pattern.<br> This property indicates whether the automation element requires at least one child item to be selected.<br> Variant type: <strong>VT_BOOL</strong><br> Default value: <strong>FALSE</strong><br></td> */
	UIA_SelectionIsSelectionRequiredPropertyId(30061, VT_BOOL, boolean.class),
	/** <td style="text-align: left;">Identifies the <a href="/en-us/windows/desktop/api/UIAutomationCore/nf-uiautomationcore-igridprovider-get_rowcount" data-linktype="absolute-path"><strong>RowCount</strong></a> property of the <a href="uiauto-implementinggrid" data-linktype="relative-path">Grid</a> control pattern.<br> This property indicates the total number of rows in the grid.<br> Variant type: <strong>VT_I4</strong><br> Default value: 0<br></td> */
	UIA_GridRowCountPropertyId(30062, VT_I4, int.class)
	
	
	,
	UIA_GridColumnCountPropertyId(30063, 0, null),

	UIA_GridItemRowPropertyId(30064, 0, null),

	UIA_GridItemColumnPropertyId(30065, 0, null),

	UIA_GridItemRowSpanPropertyId(30066, 0, null),

	UIA_GridItemColumnSpanPropertyId(30067, 0, null),

	UIA_GridItemContainingGridPropertyId(30068, 0, null),

	UIA_DockDockPositionPropertyId(30069, 0, null),

	UIA_ExpandCollapseExpandCollapseStatePropertyId(30070, 0, null),

	UIA_MultipleViewCurrentViewPropertyId(30071, 0, null),

	UIA_MultipleViewSupportedViewsPropertyId(30072, 0, null),

	UIA_WindowCanMaximizePropertyId(30073, 0, null),

	UIA_WindowCanMinimizePropertyId(30074, 0, null),

	UIA_WindowWindowVisualStatePropertyId(30075, 0, null),

	UIA_WindowWindowInteractionStatePropertyId(30076, 0, null),

	UIA_WindowIsModalPropertyId(30077, 0, null),

	UIA_WindowIsTopmostPropertyId(30078, 0, null),

	UIA_SelectionItemIsSelectedPropertyId(30079, 0, null),

	UIA_SelectionItemSelectionContainerPropertyId(30080, 0, null),

	UIA_TableRowHeadersPropertyId(30081, 0, null),

	UIA_TableColumnHeadersPropertyId(30082, 0, null),

	UIA_TableRowOrColumnMajorPropertyId(30083, 0, null),

	UIA_TableItemRowHeaderItemsPropertyId(30084, 0, null),

	UIA_TableItemColumnHeaderItemsPropertyId(30085, 0, null),

	UIA_ToggleToggleStatePropertyId(30086, 0, null),

	UIA_TransformCanMovePropertyId(30087, 0, null),

	UIA_TransformCanResizePropertyId(30088, 0, null),

	UIA_TransformCanRotatePropertyId(30089, 0, null),

	UIA_IsLegacyIAccessiblePatternAvailablePropertyId(30090, 0, null),

	UIA_LegacyIAccessibleChildIdPropertyId(30091, 0, null),

	UIA_LegacyIAccessibleNamePropertyId(30092, 0, null),

	UIA_LegacyIAccessibleValuePropertyId(30093, 0, null),

	UIA_LegacyIAccessibleDescriptionPropertyId(30094, 0, null),

	UIA_LegacyIAccessibleRolePropertyId(30095, 0, null),

	UIA_LegacyIAccessibleStatePropertyId(30096, 0, null),

	UIA_LegacyIAccessibleHelpPropertyId(30097, 0, null),

	UIA_LegacyIAccessibleKeyboardShortcutPropertyId(30098, 0, null),

	UIA_LegacyIAccessibleSelectionPropertyId(30099, 0, null),

	UIA_LegacyIAccessibleDefaultActionPropertyId(30100, 0, null),

	UIA_AriaRolePropertyId(30101, 0, null),

	UIA_AriaPropertiesPropertyId(30102, 0, null),

	UIA_IsDataValidForFormPropertyId(30103, 0, null),

	UIA_ControllerForPropertyId(30104, 0, null),

	UIA_DescribedByPropertyId(30105, 0, null),

	UIA_FlowsToPropertyId(30106, 0, null),

	UIA_ProviderDescriptionPropertyId(30107, 0, null),

	UIA_IsItemContainerPatternAvailablePropertyId(30108, 0, null),

	UIA_IsVirtualizedItemPatternAvailablePropertyId(30109, 0, null),

	UIA_IsSynchronizedInputPatternAvailablePropertyId(30110, 0, null),

	UIA_OptimizeForVisualContentPropertyId(30111, 0, null),

	UIA_IsObjectModelPatternAvailablePropertyId(30112, 0, null),

	UIA_AnnotationAnnotationTypeIdPropertyId(30113, 0, null),

	UIA_AnnotationAnnotationTypeNamePropertyId(30114, 0, null),

	UIA_AnnotationAuthorPropertyId(30115, 0, null),

	UIA_AnnotationDateTimePropertyId(30116, 0, null),

	UIA_AnnotationTargetPropertyId(30117, 0, null),

	UIA_IsAnnotationPatternAvailablePropertyId(30118, 0, null),

	UIA_IsTextPattern2AvailablePropertyId(30119, 0, null),

	UIA_StylesStyleIdPropertyId(30120, 0, null),

	UIA_StylesStyleNamePropertyId(30121, 0, null),

	UIA_StylesFillColorPropertyId(30122, 0, null),

	UIA_StylesFillPatternStylePropertyId(30123, 0, null),

	UIA_StylesShapePropertyId(30124, 0, null),

	UIA_StylesFillPatternColorPropertyId(30125, 0, null),

	UIA_StylesExtendedPropertiesPropertyId(30126, 0, null),

	UIA_IsStylesPatternAvailablePropertyId(30127, 0, null),

	UIA_IsSpreadsheetPatternAvailablePropertyId(30128, 0, null),

	UIA_SpreadsheetItemFormulaPropertyId(30129, 0, null),

	UIA_SpreadsheetItemAnnotationObjectsPropertyId(30130, 0, null),

	UIA_SpreadsheetItemAnnotationTypesPropertyId(30131, 0, null),

	UIA_IsSpreadsheetItemPatternAvailablePropertyId(30132, 0, null),

	UIA_Transform2CanZoomPropertyId(30133, 0, null),

	UIA_IsTransformPattern2AvailablePropertyId(30134, 0, null),

	UIA_LiveSettingPropertyId(30135, 0, null),

	UIA_IsTextChildPatternAvailablePropertyId(30136, 0, null),

	UIA_IsDragPatternAvailablePropertyId(30137, 0, null),

	UIA_DragIsGrabbedPropertyId(30138, 0, null),

	UIA_DragDropEffectPropertyId(30139, 0, null),

	UIA_DragDropEffectsPropertyId(30140, 0, null),

	UIA_IsDropTargetPatternAvailablePropertyId(30141, 0, null),

	UIA_DropTargetDropTargetEffectPropertyId(30142, 0, null),

	UIA_DropTargetDropTargetEffectsPropertyId(30143, 0, null),

	UIA_DragGrabbedItemsPropertyId(30144, 0, null),

	UIA_Transform2ZoomLevelPropertyId(30145, 0, null),

	UIA_Transform2ZoomMinimumPropertyId(30146, 0, null),

	UIA_Transform2ZoomMaximumPropertyId(30147, 0, null),

	UIA_FlowsFromPropertyId(30148, 0, null),

	UIA_IsTextEditPatternAvailablePropertyId(30149, 0, null),

	UIA_IsPeripheralPropertyId(30150, 0, null),

	UIA_IsCustomNavigationPatternAvailablePropertyId(30151, 0, null),

	UIA_PositionInSetPropertyId(30152, 0, null),

	UIA_SizeOfSetPropertyId(30153, 0, null),

	UIA_LevelPropertyId(30154, 0, null),

	UIA_AnnotationTypesPropertyId(30155, 0, null),

	UIA_AnnotationObjectsPropertyId(30156, 0, null),

	UIA_LandmarkTypePropertyId(30157, 0, null),

	UIA_LocalizedLandmarkTypePropertyId(30158, 0, null),

	UIA_FullDescriptionPropertyId(30159, 0, null),

	UIA_FillColorPropertyId(30160, 0, null),

	UIA_OutlineColorPropertyId(30161, 0, null),

	UIA_FillTypePropertyId(30162, 0, null),

	UIA_VisualEffectsPropertyId(30163, 0, null),

	UIA_OutlineThicknessPropertyId(30164, 0, null),

	UIA_CenterPointPropertyId(30165, 0, null),

	UIA_RotationPropertyId(30166, 0, null),

	UIA_SizePropertyId(30167, 0, null),

	UIA_IsSelectionPattern2AvailablePropertyId(30168, 0, null),

	UIA_Selection2FirstSelectedItemPropertyId(30169, 0, null),

	UIA_Selection2LastSelectedItemPropertyId(30170, 0, null),

	UIA_Selection2CurrentSelectedItemPropertyId(30171, 0, null),

	UIA_Selection2ItemCountPropertyId(30172, 0, null),

	UIA_HeadingLevelPropertyId(30173, 0, null),

	UIA_IsDialogPropertyId(30174, 0, null)
	
	;

	static enum Type {
		VT_BOOL,
		VT_BSTR,
		VT_I4,
		VT_ARRAY,
		VT_R8,
		VT_UNKNOWN
	}
	
	private int nativeValue;
	private int type;
	private Class<?> javaType;
	
	public int getNativeValue() {
		return nativeValue;
	}
	
	
	private StandardPropertyIds(int nativeValue, int type, Class<?> javaType) {
		this.nativeValue = nativeValue;
		this.type = type;
		this.javaType = javaType;
	}
	
	public static Optional<StandardPropertyIds> fromNativeValue(int nativeValue) {
		return Stream.of(values()).filter(value -> value.getNativeValue() == nativeValue).findFirst();
	}
}
