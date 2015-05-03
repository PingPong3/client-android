package red.itvirtuoso.pingpong3.app.net;

/**
 * Created by kenji on 15/05/03.
 */
public class Event {
    private EventType type;

    public Event(EventType type) {
        this.type = type;
    }

    public EventType getType() {
        return type;
    }
}
