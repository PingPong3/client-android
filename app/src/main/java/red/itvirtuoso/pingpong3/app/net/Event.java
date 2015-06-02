package red.itvirtuoso.pingpong3.app.net;

import java.util.Arrays;

/**
 * Created by kenji on 15/05/03.
 */
public class Event {
    private EventType type;
    private int[] data;

    public Event(EventType type, int[] data) {
        this.type = type;
        this.data = data;
    }

    public EventType getType() {
        return type;
    }

    public int[] getData() {
        return data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Event event = (Event) o;

        if (!Arrays.equals(data, event.data)) return false;
        if (type != event.type) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + Arrays.hashCode(data);
        return result;
    }

    @Override
    public String toString() {
        return "Event{" +
                "type=" + type +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}
