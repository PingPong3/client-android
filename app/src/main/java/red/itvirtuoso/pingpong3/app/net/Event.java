package red.itvirtuoso.pingpong3.app.net;

/**
 * Created by kenji on 15/05/03.
 */
public class Event {
    private EventType type;
    private EventArgs args;

    public Event(EventType type, EventArgs args) {
        this.type = type;
        this.args = args;
    }

    public EventType getType() {
        return type;
    }

    public EventArgs getArgs() {
        return args;
    }
}
