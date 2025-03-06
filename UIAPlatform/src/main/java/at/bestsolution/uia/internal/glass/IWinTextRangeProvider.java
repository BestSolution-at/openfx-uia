package at.bestsolution.uia.internal.glass;

public interface IWinTextRangeProvider {
    public void onNativeDelete();
    public void setRange(int start, int end);
    public int getStart();
    public int getEnd();
    public IWinAccessible getAccessible();


    // ITextRangeProvider
    public IWinTextRangeProvider Clone();
    public boolean Compare(IWinTextRangeProvider range);
    public int CompareEndpoints(int endpoint, IWinTextRangeProvider targetRange, int targetEndpoint);
    public void ExpandToEnclosingUnit(int unit);
    public long FindAttribute(int attributeId, WinVariant val, boolean backward);
    public IWinTextRangeProvider FindText(String text, boolean backward, boolean ignoreCase);
    public WinVariant GetAttributeValue(int attributeId);
    public double[] GetBoundingRectangles();
    public long GetEnclosingElement();
    public String GetText(int maxLength);
    public int Move(int unit, final int requestedCount);
    public int MoveEndpointByUnit(int endpoint, int unit, final int requestedCount);
    public void MoveEndpointByRange(int endpoint, IWinTextRangeProvider targetRange, int targetEndpoint);
    public void Select();
    public void AddToSelection();
    public void RemoveFromSelection();
    public void ScrollIntoView(boolean alignToTop);
    public long[] GetChildren();




}
