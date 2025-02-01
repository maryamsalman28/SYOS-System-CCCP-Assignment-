package observer;

public class EmailNotificationObserver implements Observer {

    private final String emailAddress;

    public EmailNotificationObserver(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    @Override
    public void update(String message) {
        System.out.println("Email sent to " + emailAddress + ": " + message);
    }
}
