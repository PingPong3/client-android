package red.itvirtuoso.pingpong3.app.server;

/**
 * Created by kenji on 15/05/03.
 */
public enum PacketType {
    CONNECT_SUCCESS,
    SWING,
    ME_READY,
    ME_SERVE,
    ME_RETURN,
    ME_BOUND_MY_AREA,
    ME_BOUND_RIVAL_AREA,
    ME_POINT,
    RIVAL_READY,
    RIVAL_SERVE,
    RIVAL_RETURN,
    RIVAL_BOUND_MY_AREA,
    RIVAL_BOUND_RIVAL_AREA,
    RIVAL_POINT,
}
