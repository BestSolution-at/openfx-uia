package javafx.uia;

import java.util.Optional;
import java.util.stream.Stream;

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
	UIA_RuntimeIdPropertyId(30000),
	/** Identifies the BoundingRectangle property, which specifies the coordinates of the rectangle that completely encloses the automation element. The rectangle is expressed in physical screen coordinates. It can contain points that are not clickable if the shape or clickable region of the UI item is irregular, or if the item is obscured by other UI elements.
Variant type: VT_R8 | VT_ARRAY
Default value: [0,0,0,0]
[!Note]
This property is NULL if the item is not currently displaying a UI. */
	UIA_BoundingRectanglePropertyId(30001),
	/** Identifies the ProcessId property, which is an integer representing the process identifier (ID) of the automation element.
The process identifier (ID) is assigned by the operating system. It can be seen in the PID column of the Processes tab in Task Manager.
Variant type: VT_I4
Default value: 0 */
	UIA_ProcessIdPropertyId(30002),
	/** Identifies the ControlType property, which is a class that identifies the type of the automation element. ControlType defines characteristics of the UI elements by well known UI control primitives such as button or check box.
Variant type: VT_I4
Default value: UIA_CustomControlTypeId
[!Note]
Use the default value only if the automation element represents a completely new type of control. */
	UIA_ControlTypePropertyId(30003),

	/** Identifies the LocalizedControlType property, which is a text string describing the type of control that the automation element represents. The string should contain only lowercase characters:
Correct: "button"
Incorrect: "Button"

When LocalizedControlType is not specified by the element provider, the default localized string is supplied by the framework, according to the control type of the element (for example, "button" for the Button control type). An automation element with the Custom control type must support a localized control type string that represents the role of the element (for example, "color picker" for a custom control that enables users to choose and specify colors).
When a custom value is supplied, the string must match the application UI language or the operating system default UI language.
Variant type: VT_BSTR
Default value: empty string */
	UIA_LocalizedControlTypePropertyId(30004),
	/** Identifies the Name property, which is a string that holds the name of the automation element.
The Name property should be the same as the label text on screen. For example, Name should be "Browse" for a button element with the label "Browse". The Name property must not include the mnemonic character for the access keys (that is, "&amp;"), which is underlined in the UI text presentation. Also, the Name property should not be an extended or modified version of the on-screen label because the inconsistency between the name and the label can cause confusion among client applications and users.
When the corresponding label text is not visible on screen, or when it is replaced by graphics, alternative text should be chosen. The alternative text should be concise, intuitive, and localized to the application UI language, or to the operating system default UI language. The alternative text should not be a detailed description of the visual details, but a concise description of the UI function or feature as if it were labeled by simple text. For example, the Windows Start menu button is named "Start" (button) instead of "Windows Logo on blue round sphere graphics" (button). For more information, see Creating Text Equivalents for Images.
When a UI label uses text graphics (for example, using "&gt;&gt;" for a button that adds an item from left to right), the Name property should be overridden by an appropriate text alternative (for example, "Add"). However the practice of using text graphics as a UI label is discouraged due to both localization and accessibility concerns.
The Name property must not include the control role or type information, such as "button" or "list"; otherwise, it will conflict with the text from the LocalizedControlType property when these two properties are appended (many existing assistive technologies do this).
The Name property cannot be used as a unique identifier among siblings. However, as long as it is consistent with the UI presentation, the same Name value can be supported among peers. For test automation, the clients should consider using the AutomationId or RuntimeId property.
Text controls do not always have to have the Name property be identical to the text that is displayed within the control, so long as the Text pattern is also supported.
Variant type: VT_BSTR
Default value: empty string */
	UIA_NamePropertyId(30005),
	/** Identifies the AcceleratorKey property, which is a string containing the accelerator key (also called shortcut key) combinations for the automation element.
Shortcut key combinations invoke an action. For example, CTRL+O is often used to invoke the Open file common dialog box. An automation element that has the AcceleratorKey property can implement the Invoke control pattern for the action that is equivalent to the shortcut command.
Variant type: VT_BSTR
Default value: empty string */
	UIA_AcceleratorKeyPropertyId(30006),
	/** Identifies the AccessKey property, which is a string containing the access key character for the automation element.
An access key (sometimes called a mnemonic) is a character in the text of a menu, menu item, or label of a control such as a button, that activates the associated menu function. For example, to open the File menu, for which the access key is typically F, the user would press ALT+F.
Variant type: VT_BSTR
Default value: empty string */
	UIA_AccessKeyPropertyId(30007),
	/** Identifies the HasKeyboardFocus property, which is a Boolean value that indicates whether the automation element has keyboard focus.
Variant type: VT_BOOL
Default value: FALSE */
	UIA_HasKeyboardFocusPropertyId(30008),
	/** Identifies the IsKeyboardFocusable property, which is a Boolean value that indicates whether the automation element can accept keyboard focus.
Variant type: VT_BOOL
Default value: FALSE */
	UIA_IsKeyboardFocusablePropertyId(30009),
	/** Identifies the IsEnabled property, which is a Boolean value that indicates whether the UI item referenced by the automation element is enabled and can be interacted with.
When the enabled state of a control is FALSE, it is assumed that child controls are also not enabled. Clients should not expect property-changed events from child elements when the state of the parent control changes.
Variant type: VT_BOOL
Default value: FALSE */
	UIA_IsEnabledPropertyId(30010),
	/** Identifies the AutomationId property, which is a string containing the UI Automation identifier (ID) for the automation element.
When it is available, the AutomationId of an element must be the same in any instance of the application, regardless of the local language. The value should be unique among sibling elements, but not necessarily unique across the entire desktop. For example, multiple instances of an application, or multiple folder views in Microsoft Windows Explorer, can contain elements with the same AutomationId property, such as "SystemMenuBar".
Although support for AutomationId is always recommended for better automated testing support, this property is not mandatory. Where it is supported, AutomationId is useful for creating a test automation script that runs regardless of the UI language. Clients should make no assumptions regarding the AutomationId values exposed by other applications. AutomationId is not guaranteed to be stable across different releases or builds of an application.
Variant type: VT_BSTR
Default value: empty string */
	UIA_AutomationIdPropertyId(30011),
	/** Identifies the ClassName property, which is a string containing the class name for the automation element as assigned by the control developer.
The class name depends on the implementation of the UI Automation provider and therefore is not always in a standard format. However, if the class name is known, it can be used to verify that an application is working with the expected automation element.
Variant type: VT_BSTR
Default value: empty string */
	UIA_ClassNamePropertyId(30012),
	/** Identifies the HelpText property, which is a help text string associated with the automation element.
The HelpText property can be supported with placeholder text appearing in edit or list controls. For example, "Type text here for search" is a good candidate the HelpText property for an edit control that places the text prior to the user's actual input. However, it is not adequate for the name property of the edit control.
When HelpText is supported, the string must match the application UI language or the operating system default UI language.
Variant type: VT_BSTR
Default value: empty string */
	UIA_HelpTextPropertyId(30013),
	/** Identifies the ClickablePoint property, which is a point on the automation element that can be clicked. An element cannot be clicked if it is completely or partially obscured by another window.
Variant type: VT_R8 | VT_ARRAY
Default value: VT_EMPTY */
	UIA_ClickablePointPropertyId(30014),
	/** Identifies the <strong>Culture</strong> property, which contains a locale identifier for the automation element (for example, <code>0x0409</code> for "en-US" or English (United States)). Each locale has a unique identifier, a 32-bit value that consists of a language identifier and a sort order identifier. The locale identifier is a standard international numeric abbreviation and has the components necessary to uniquely identify one of the installed operating system-defined locales. For more information, see Language Identifier Constants and Strings. This property may exist on a per-control basis, but typically is only available on an application level. Variant type: <strong>VT_I4</strong> Default value: 0 */
	UIA_CulturePropertyId(30015),
	/** Identifies the <strong>IsControlElement</strong> property, which is a Boolean value that specifies whether the element appears in the control view of the automation element tree. For more information, see UI Automation Tree Overview. Variant type: <strong>VT_BOOL</strong> Default value: <strong>TRUE</strong> */
	UIA_IsControlElementPropertyId(30016),
	/** Identifies the <strong>IsContentElement</strong> property, which is a Boolean value that specifies whether the element appears in the content view of the automation element tree. For more information, see UI Automation Tree Overview.
<blockquote>
[!Note]
For an element to appear in the content view, both the <strong>IsContentElement</strong> property and the <strong>IsControlElement</strong> property must be <strong>TRUE</strong>.
</blockquote>
  Variant type: <strong>VT_BOOL</strong> Default value: <strong>TRUE</strong> */
	UIA_IsContentElementPropertyId(30017),
	/** Identifies the <strong>LabeledBy</strong> property, which is an automation element that contains the text label for this element. This property can be used to retrieve, for example, the static text label for a combo box. Variant type: <strong>VT_UNKNOWN</strong> Default value: <strong>NULL</strong> */
	UIA_LabeledByPropertyId(30018),
	/** Identifies the <strong>IsPassword</strong> property, which is a Boolean value that indicates whether the automation element contains protected content or a password. When the <strong>IsPassword</strong> property is <strong>TRUE</strong> and the element has the keyboard focus, a client application should disable keyboard echoing or keyboard input feedback that may expose the user's protected information. Attempting to access the <strong>Value</strong> property of the protected element (edit control) may cause an error to occur. Variant type: <strong>VT_BOOL</strong> Default value: <strong>FALSE</strong> */
	UIA_IsPasswordPropertyId(30019),
	/** Identifies the <strong>NativeWindowHandle</strong> property, which is an integer that represents the handle (<strong>HWND</strong>) of the automation element window, if it exists; otherwise, this property is 0. Variant type: <strong>VT_I4</strong> Default value: 0 */
	UIA_NativeWindowHandlePropertyId(30020),
	/** Identifies the <strong>ItemType</strong> property, which is a text string describing the type of the automation element. <strong>ItemType</strong> is used to obtain information about items in a list, tree view, or data grid. For example, an item in a file directory view might be a "Document File" or a "Folder". When <strong>ItemType</strong> is supported, the string must match the application UI language or the operating system default UI language. Variant type: <strong>VT_BSTR</strong> Default value: empty string */
	UIA_ItemTypePropertyId(30021),
	/** Identifies the <strong>IsOffscreen</strong> property, which is a Boolean value that indicates whether the automation element is entirely scrolled out of view (for example, an item in a list box that is outside the viewport of the container object) or collapsed out of view (for example, an item in a tree view or menu, or in a minimized window). If the element has a clickable point that can cause it to receive the focus, the element is considered to be on-screen while a portion of the element is off-screen. The value of the property is not affected by occlusion by other windows, or by whether the element is visible on a specific monitor. If the <strong>IsOffscreen</strong> property is <strong>TRUE</strong>, the UI element is scrolled off-screen or collapsed. The element is temporarily hidden, yet it remains in the end-user's perception and continues to be included in the UI model. The object can be brought back into view by scrolling, clicking a drop-down, and so on. Objects that the end-user does not perceive at all, or that are "programmatically hidden" (for example, a dialog box that has been dismissed, but the underlying object is still cached by the application) should not be in the automation element tree in the first place (instead of setting the state of <strong>IsOffscreen</strong> to <strong>TRUE</strong>). Variant type: <strong>VT_BOOL</strong> Default value: <strong>FALSE</strong> */
	UIA_IsOffscreenPropertyId(30022),
	/** Identifies the <strong>Orientation</strong> property, which indicates the orientation of the control represented by the automation element. The property is expressed as a value from the <strong>OrientationType_None</strong>) */
	UIA_OrientationPropertyId(30023),
	/** Identifies the <strong>FrameworkId</strong> property, which is a string containing the name of the underlying UI framework that the automation element belongs to. The <strong>FrameworkId</strong> enables client applications to process automation elements differently depending on the particular UI framework. Examples of property values include "Win32", "WinForm", and "DirectUI". Variant type: <strong>VT_BSTR</strong> Default value: empty string */
	UIA_FrameworkIdPropertyId(30024),
	/** Identifies the <strong>IsRequiredForForm</strong> property, which is a Boolean value that indicates whether the automation element is required to be filled out on a form. Variant type: <strong>VT_BOOL</strong> Default value: <strong>FALSE</strong> */
	UIA_IsRequiredForFormPropertyId(30025),
	/** Identifies the <strong>ItemStatus</strong> property, which is a text string describing the status of an item of the automation element. <strong>ItemStatus</strong> enables a client to ascertain whether an element is conveying status about an item as well as what the status is. For example, an item associated with a contact in a messaging application might be "Busy" or "Connected". When <strong>ItemStatus</strong> is supported, the string must match the application UI language or the operating system default UI language. Variant type: <strong>VT_BSTR</strong> Default value: empty string */
	UIA_ItemStatusPropertyId(30026),
	
	
	// Pattern available property ids (https://docs.microsoft.com/en-us/windows/win32/winauto/uiauto-control-pattern-availability-propids)
	
	/** Identifies the <strong>IsDockPatternAvailable</strong> property, which indicates whether the <strong>IUIAutomationDockPattern</strong> interface from the element. */
	UIA_IsDockPatternAvailablePropertyId(30027),
	/** Identifies the <strong>IsExpandCollapsePatternAvailable</strong> property, which indicates whether the <strong>IUIAutomationExpandCollapsePattern</strong> interface from the element. */
	UIA_IsExpandCollapsePatternAvailablePropertyId(30028),
	/** Identifies the <strong>IsGridItemPatternAvailable</strong> property, which indicates whether the <strong>IUIAutomationGridItemPattern</strong> interface from the element. */
	UIA_IsGridItemPatternAvailablePropertyId(30029),
	/** Identifies the <strong>IsGridPatternAvailable</strong> property, which indicates whether the <strong>IUIAutomationGridPattern</strong> interface from the element. */
	UIA_IsGridPatternAvailablePropertyId(30030),
	/** Identifies the <strong>IsInvokePatternAvailable</strong> property, which indicates whether the <strong>IUIAutomationInvokePattern</strong> interface from the element. */
	UIA_IsInvokePatternAvailablePropertyId(30031),
	/** Identifies the <strong>IsMultipleViewPatternAvailable</strong> property, which indicates whether the <strong>IUIAutomationMultipleViewPattern</strong> interface from the element. */
	UIA_IsMultipleViewPatternAvailablePropertyId(30032),
	/** Identifies the <strong>IsRangeValuePatternAvailable</strong> property, which indicates whether the <strong>IUIAutomationRangeValuePattern</strong> interface from the element. */
	UIA_IsRangeValuePatternAvailablePropertyId(30033),
	/** Identifies the <strong>IsScrollPatternAvailable</strong> property, which indicates whether the <strong>IUIAutomationScrollPattern</strong> interface from the element. */
	UIA_IsScrollPatternAvailablePropertyId(30034),
	/** Identifies the <strong>IsScrollItemPatternAvailable</strong> property, which indicates whether the <strong>IUIAutomationScrollItemPattern</strong> interface from the element. */
	UIA_IsScrollItemPatternAvailablePropertyId(30035),
	/** Identifies the <strong>IsSelectionItemPatternAvailable</strong> property, which indicates whether the <strong>IUIAutomationSelectionItemPattern</strong> interface from the element. */
	UIA_IsSelectionItemPatternAvailablePropertyId(30036),
	/** Identifies the <strong>IsSelectionPatternAvailable</strong> property, which indicates whether the <strong>IUIAutomationSelectionPattern</strong> interface from the element. */
	UIA_IsSelectionPatternAvailablePropertyId(30037),
	/** Identifies the <strong>IsTablePatternAvailable</strong> property, which indicates whether the <strong>IUIAutomationTablePattern</strong> interface from the element. */
	UIA_IsTablePatternAvailablePropertyId(30038),
	/** Identifies the <strong>IsTableItemPatternAvailable</strong> property, which indicates whether the <strong>IUIAutomationTableItemPattern</strong> interface from the element. */
	UIA_IsTableItemPatternAvailablePropertyId(30039),
	/** Identifies the <strong>IsTextPatternAvailable</strong> property, which indicates whether the <strong>IUIAutomationTextPattern</strong> interface from the element. */
	UIA_IsTextPatternAvailablePropertyId(30040),
	/** Identifies the <strong>IsTogglePatternAvailable</strong> property, which indicates whether the <strong>IUIAutomationTogglePattern</strong> interface from the element. */
	UIA_IsTogglePatternAvailablePropertyId(30041),
	/** Identifies the <strong>IsTransformPatternAvailable</strong> property, which indicates whether the <strong>IUIAutomationTransformPattern</strong> interface from the element. */
	UIA_IsTransformPatternAvailablePropertyId(30042),
	/** Identifies the <strong>IsTransformPattern2Available</strong> property, which indicates whether version two of the <strong>IUIAutomationTransformPattern2</strong> interface from the element. Supported starting with Windows 8. */
	UIA_IsValuePatternAvailablePropertyId(30043),
	/** Identifies the <strong>IsWindowPatternAvailable</strong> property, which indicates whether the <strong>IUIAutomationWindowPattern</strong> interface from the element. */
	UIA_IsWindowPatternAvailablePropertyId(30044),

	
	// control pattern property ids (https://docs.microsoft.com/en-us/windows/win32/winauto/uiauto-control-pattern-propids)
	
	/** Identifies the <strong>Value</strong> property of the Value control pattern. This property indicates the value of the automation element. Variant type: <strong>VT_BSTR</strong> Default value: empty string */
	UIA_ValueValuePropertyId(30045),
	/** Identifies the <strong>IsReadOnly</strong> property of the Value control pattern. This property indicates whether the value of the automation element is read-only.  Variant type: <strong>VT_BOOL</strong> Default value: <strong>TRUE</strong> */
	UIA_ValueIsReadOnlyPropertyId(30046),

	
	/** Identifies the Value property of the RangeValue control pattern. This property is the current value of the automation element.  Variant type: <strong>VT_R8</strong> Default value: 0 */
	UIA_RangeValueValuePropertyId(30047),
	/** Identifies the <strong>IsReadOnly</strong> property of the RangeValue control pattern. This property indicates whether the value of the automation element is read-only. Variant type: <strong>VT_BOOL</strong> Default value: <strong>TRUE</strong> */
	UIA_RangeValueIsReadOnlyPropertyId(30048),
	/** Identifies the <strong>Minimum</strong> property of the RangeValue control pattern. This property is the minimum range value supported by the automation element.  Variant type: <strong>VT_R8</strong> Default value: 0 */
	UIA_RangeValueMinimumPropertyId(30049),
	/** Identifies the <strong>Maximum</strong> property of the RangeValue control pattern. This property is the maximum range value supported by the automation element. Variant type: <strong>VT_R8</strong> Default value: 0 */
	UIA_RangeValueMaximumPropertyId(30050),
	/** Identifies the <strong>Value</strong> property. Variant type: <strong>VT_R8</strong> Default value: 0 */
	UIA_RangeValueLargeChangePropertyId(30051),
	/** Identifies the <strong>Value</strong> property.  Variant type: <strong>VT_R8</strong> Default value: 0 */
	UIA_RangeValueSmallChangePropertyId(30052),
	/** Identifies the <strong>HorizontalScrollPercent</strong> property of the Scroll control pattern. This property is the current horizontal scroll position expressed as a percentage of the total content area within the automation element. Variant type: <strong>VT_R8</strong> Default value: 0 */
	UIA_ScrollHorizontalScrollPercentPropertyId(30053),
	/** Identifies the <strong>HorizontalViewSize</strong> property of the Scroll control pattern.  This property is the horizontal size of the viewable region expressed as a percentage of the total content area within the element.  Variant type: <strong>VT_R8</strong> Default value: 100 */
	UIA_ScrollHorizontalViewSizePropertyId(30054),
	/** Identifies the <strong>VerticalScrollPercent</strong> property of the Scroll control pattern. This property is the current vertical scroll position expressed as a percentage of the total content area within the automation element. Variant type: <strong>VT_R8</strong> Default value: 0 */
	UIA_ScrollVerticalScrollPercentPropertyId(30055),
	/** Identifies the <strong>VerticalViewSize</strong> property of the Scroll control pattern. This property is the vertical size of the viewable region expressed as a percentage of the total content area within the element.  Variant type: <strong>VT_R8</strong> Default value: 100 */
	UIA_ScrollVerticalViewSizePropertyId(30056),
	/** Identifies the <strong>HorizontallyScrollable</strong> property of the Scroll control pattern. This property indicates whether the automation element can scroll horizontally.  Variant type: <strong>VT_BOOL</strong> Default value: <strong>FALSE</strong> */
	UIA_ScrollHorizontallyScrollablePropertyId(30057),
	/** Identifies the <strong>VerticallyScrollable</strong> property of the Scroll control pattern.  This property indicates whether the automation element can scroll vertically.  Variant type: <strong>VT_BOOL</strong> Default value: <strong>FALSE</strong> */
	UIA_ScrollVerticallyScrollablePropertyId(30058),
	/** Identifies the <strong>IsSelectionRequired</strong> property of the Selection control pattern. This property indicates whether the automation element requires at least one child item to be selected. Variant type: <strong>VT_BOOL</strong> Default value: <strong>FALSE</strong> */
	UIA_SelectionSelectionPropertyId(30059),
	/** Identifies the <strong>CanSelectMultiple</strong> property of the Selection control pattern. This property indicates whether the automation element allows more than one child element to be selected concurrently. Variant type: <strong>VT_BOOL</strong> Default value: <strong>FALSE</strong> */
	UIA_SelectionCanSelectMultiplePropertyId(30060),
	/** Identifies the <strong>IsSelectionRequired</strong> property of the Selection control pattern. This property indicates whether the automation element requires at least one child item to be selected. Variant type: <strong>VT_BOOL</strong> Default value: <strong>FALSE</strong> */
	UIA_SelectionIsSelectionRequiredPropertyId(30061),
	/** Identifies the <strong>RowCount</strong> property of the Grid control pattern. This property indicates the total number of rows in the grid. Variant type: <strong>VT_I4</strong> Default value: 0 */
	UIA_GridRowCountPropertyId(30062)
	
	
	,
	UIA_GridColumnCountPropertyId(30063),

	UIA_GridItemRowPropertyId(30064),

	UIA_GridItemColumnPropertyId(30065),

	UIA_GridItemRowSpanPropertyId(30066),

	UIA_GridItemColumnSpanPropertyId(30067),

	UIA_GridItemContainingGridPropertyId(30068),

	UIA_DockDockPositionPropertyId(30069),

	UIA_ExpandCollapseExpandCollapseStatePropertyId(30070),

	UIA_MultipleViewCurrentViewPropertyId(30071),

	UIA_MultipleViewSupportedViewsPropertyId(30072),

	UIA_WindowCanMaximizePropertyId(30073),

	UIA_WindowCanMinimizePropertyId(30074),

	UIA_WindowWindowVisualStatePropertyId(30075),

	UIA_WindowWindowInteractionStatePropertyId(30076),

	UIA_WindowIsModalPropertyId(30077),

	UIA_WindowIsTopmostPropertyId(30078),

	UIA_SelectionItemIsSelectedPropertyId(30079),

	UIA_SelectionItemSelectionContainerPropertyId(30080),

	UIA_TableRowHeadersPropertyId(30081),

	UIA_TableColumnHeadersPropertyId(30082),

	UIA_TableRowOrColumnMajorPropertyId(30083),

	UIA_TableItemRowHeaderItemsPropertyId(30084),

	UIA_TableItemColumnHeaderItemsPropertyId(30085),

	UIA_ToggleToggleStatePropertyId(30086),

	UIA_TransformCanMovePropertyId(30087),

	UIA_TransformCanResizePropertyId(30088),

	UIA_TransformCanRotatePropertyId(30089),

	UIA_IsLegacyIAccessiblePatternAvailablePropertyId(30090),

	UIA_LegacyIAccessibleChildIdPropertyId(30091),

	UIA_LegacyIAccessibleNamePropertyId(30092),

	UIA_LegacyIAccessibleValuePropertyId(30093),

	UIA_LegacyIAccessibleDescriptionPropertyId(30094),

	UIA_LegacyIAccessibleRolePropertyId(30095),

	UIA_LegacyIAccessibleStatePropertyId(30096),

	UIA_LegacyIAccessibleHelpPropertyId(30097),

	UIA_LegacyIAccessibleKeyboardShortcutPropertyId(30098),

	UIA_LegacyIAccessibleSelectionPropertyId(30099),

	UIA_LegacyIAccessibleDefaultActionPropertyId(30100),

	UIA_AriaRolePropertyId(30101),

	UIA_AriaPropertiesPropertyId(30102),

	UIA_IsDataValidForFormPropertyId(30103),

	UIA_ControllerForPropertyId(30104),

	UIA_DescribedByPropertyId(30105),

	UIA_FlowsToPropertyId(30106),

	UIA_ProviderDescriptionPropertyId(30107),

	UIA_IsItemContainerPatternAvailablePropertyId(30108),

	UIA_IsVirtualizedItemPatternAvailablePropertyId(30109),

	UIA_IsSynchronizedInputPatternAvailablePropertyId(30110),

	UIA_OptimizeForVisualContentPropertyId(30111),

	UIA_IsObjectModelPatternAvailablePropertyId(30112),

	UIA_AnnotationAnnotationTypeIdPropertyId(30113),

	UIA_AnnotationAnnotationTypeNamePropertyId(30114),

	UIA_AnnotationAuthorPropertyId(30115),

	UIA_AnnotationDateTimePropertyId(30116),

	UIA_AnnotationTargetPropertyId(30117),

	UIA_IsAnnotationPatternAvailablePropertyId(30118),

	UIA_IsTextPattern2AvailablePropertyId(30119),

	UIA_StylesStyleIdPropertyId(30120),

	UIA_StylesStyleNamePropertyId(30121),

	UIA_StylesFillColorPropertyId(30122),

	UIA_StylesFillPatternStylePropertyId(30123),

	UIA_StylesShapePropertyId(30124),

	UIA_StylesFillPatternColorPropertyId(30125),

	UIA_StylesExtendedPropertiesPropertyId(30126),

	UIA_IsStylesPatternAvailablePropertyId(30127),

	UIA_IsSpreadsheetPatternAvailablePropertyId(30128),

	UIA_SpreadsheetItemFormulaPropertyId(30129),

	UIA_SpreadsheetItemAnnotationObjectsPropertyId(30130),

	UIA_SpreadsheetItemAnnotationTypesPropertyId(30131),

	UIA_IsSpreadsheetItemPatternAvailablePropertyId(30132),

	UIA_Transform2CanZoomPropertyId(30133),

	UIA_IsTransformPattern2AvailablePropertyId(30134),

	UIA_LiveSettingPropertyId(30135),

	UIA_IsTextChildPatternAvailablePropertyId(30136),

	UIA_IsDragPatternAvailablePropertyId(30137),

	UIA_DragIsGrabbedPropertyId(30138),

	UIA_DragDropEffectPropertyId(30139),

	UIA_DragDropEffectsPropertyId(30140),

	UIA_IsDropTargetPatternAvailablePropertyId(30141),

	UIA_DropTargetDropTargetEffectPropertyId(30142),

	UIA_DropTargetDropTargetEffectsPropertyId(30143),

	UIA_DragGrabbedItemsPropertyId(30144),

	UIA_Transform2ZoomLevelPropertyId(30145),

	UIA_Transform2ZoomMinimumPropertyId(30146),

	UIA_Transform2ZoomMaximumPropertyId(30147),

	UIA_FlowsFromPropertyId(30148),

	UIA_IsTextEditPatternAvailablePropertyId(30149),

	UIA_IsPeripheralPropertyId(30150),

	UIA_IsCustomNavigationPatternAvailablePropertyId(30151),

	UIA_PositionInSetPropertyId(30152),

	UIA_SizeOfSetPropertyId(30153),

	UIA_LevelPropertyId(30154),

	UIA_AnnotationTypesPropertyId(30155),

	UIA_AnnotationObjectsPropertyId(30156),

	UIA_LandmarkTypePropertyId(30157),

	UIA_LocalizedLandmarkTypePropertyId(30158),

	UIA_FullDescriptionPropertyId(30159),

	UIA_FillColorPropertyId(30160),

	UIA_OutlineColorPropertyId(30161),

	UIA_FillTypePropertyId(30162),

	UIA_VisualEffectsPropertyId(30163),

	UIA_OutlineThicknessPropertyId(30164),

	UIA_CenterPointPropertyId(30165),

	UIA_RotationPropertyId(30166),

	UIA_SizePropertyId(30167),

	UIA_IsSelectionPattern2AvailablePropertyId(30168),

	UIA_Selection2FirstSelectedItemPropertyId(30169),

	UIA_Selection2LastSelectedItemPropertyId(30170),

	UIA_Selection2CurrentSelectedItemPropertyId(30171),

	UIA_Selection2ItemCountPropertyId(30172),

	UIA_HeadingLevelPropertyId(30173),

	UIA_IsDialogPropertyId(30174)
	
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
	
	public int getNativeValue() {
		return nativeValue;
	}
	
	
	private StandardPropertyIds(int nativeValue) {
		this.nativeValue = nativeValue;
	}
	
	public static Optional<StandardPropertyIds> fromNativeValue(int nativeValue) {
		return Stream.of(values()).filter(value -> value.getNativeValue() == nativeValue).findFirst();
	}
}
