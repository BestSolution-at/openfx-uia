package javafx.uia;

public interface IStructureChangedEvent {
    void fire(StructureChangeType structureChangeType, int[] runtimeId);
}
