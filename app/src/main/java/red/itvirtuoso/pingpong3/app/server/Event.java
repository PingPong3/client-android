package red.itvirtuoso.pingpong3.app.server;

/**
 * Created by kenji on 15/05/03.
 */
public class Event {

    private Turn turn;

    public Event(Turn turn) {
        this.turn = turn;
    }

    public Turn getTurn() {
        return this.turn;
    }
}
