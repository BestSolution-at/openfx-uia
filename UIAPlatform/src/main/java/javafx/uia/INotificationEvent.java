package javafx.uia;

public interface INotificationEvent {
    void fire(NotificationKind notificationKind, NotificationProcessing notificaitonProcessing, String displayString, String activityId);
}
