package at.bestsolution.uia;

public interface IAsyncContentLoadedEvent {
    void fire(AsyncContentLoadedState asyncContentLoadedState, double percentComplete);
}
