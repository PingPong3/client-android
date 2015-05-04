package red.itvirtuoso.pingpong3.app.server.local;

/**
 * Created by kenji on 15/05/03.
 */
abstract class Action {
    private long time;

    public Action(long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    /**
     * 処理を実行する
     * @return 処理が終了した場合はtrue、処理を継続する場合にはfalseを返す。
     */
    public abstract boolean execute();
}
