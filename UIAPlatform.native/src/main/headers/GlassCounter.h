#ifndef _CUSTWIN_DUMMY_
#define _CUSTWIN_DUMMY_

#include "common.h"

// replaces GlassApplication::Increment/Decrement Accessibility
class GlassCounter {

public:
	static ULONG IncrementAccessibility();
	static ULONG DecrementAccessibility();
	static ULONG GetAccessibilityCount();

private:
	static ULONG s_accessibilityCount;

};

#endif //_CUSTWIN_DUMMY_
