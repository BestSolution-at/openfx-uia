package at.bestsolution.uia;

public interface IStructureChangedEvent {
    void fire(StructureChangeType structureChangeType, int[] runtimeId);
}
