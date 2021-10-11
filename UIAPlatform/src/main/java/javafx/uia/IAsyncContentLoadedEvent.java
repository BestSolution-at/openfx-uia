package javafx.uia;

public interface IAsyncContentLoadedEvent {
    void fire(AsyncContentLoadedState asyncContentLoadedState, double percentComplete);
}
