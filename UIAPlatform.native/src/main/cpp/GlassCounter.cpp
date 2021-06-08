
#include "GlassCounter.h"


ULONG GlassCounter::s_accessibilityCount = 0;

/* static */
ULONG GlassCounter::IncrementAccessibility()
{
    return InterlockedIncrement(&GlassCounter::s_accessibilityCount);
}

/* static */
ULONG GlassCounter::DecrementAccessibility()
{
    return InterlockedDecrement(&GlassCounter::s_accessibilityCount);
}

/* static */
ULONG GlassCounter::GetAccessibilityCount()
{
    return GlassCounter::s_accessibilityCount;
}