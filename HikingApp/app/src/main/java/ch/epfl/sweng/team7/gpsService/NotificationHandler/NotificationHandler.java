package ch.epfl.sweng.team7.gpsService.NotificationHandler;

public class NotificationHandler {

    private static NotificationHandler instance = new NotificationHandler();

    public static NotificationHandler getInstance() {
        return instance;
    }
}
