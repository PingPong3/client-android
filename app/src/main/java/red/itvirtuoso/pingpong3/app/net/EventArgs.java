package red.itvirtuoso.pingpong3.app.net;

/**
 * Created by kenji on 15/05/03.
 */
public class EventArgs {
    private Turn turn;

    public EventArgs(Turn turn) {
        this.turn = turn;
    }

    public Turn getTurn() {
        return this.turn;
    }
}
