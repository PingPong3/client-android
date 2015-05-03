package red.itvirtuoso.pingpong3.app.net;

import java.util.EnumMap;

import red.itvirtuoso.pingpong3.app.server.PacketType;

/**
 * Created by kenji on 15/05/03.
 */
public enum EventType {
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
    ;

    private static EnumMap<PacketType, EventType> map = new EnumMap<>(PacketType.class);
    static {
        map.put(PacketType.ME_READY, ME_READY);
        map.put(PacketType.ME_SERVE, ME_SERVE);
        map.put(PacketType.ME_RETURN, ME_RETURN);
        map.put(PacketType.ME_BOUND_MY_AREA, ME_BOUND_MY_AREA);
        map.put(PacketType.ME_BOUND_RIVAL_AREA, ME_BOUND_RIVAL_AREA);
        map.put(PacketType.ME_POINT, ME_POINT);
        map.put(PacketType.RIVAL_READY, RIVAL_READY);
        map.put(PacketType.RIVAL_SERVE, RIVAL_SERVE);
        map.put(PacketType.RIVAL_RETURN, RIVAL_RETURN);
        map.put(PacketType.RIVAL_BOUND_MY_AREA, RIVAL_BOUND_MY_AREA);
        map.put(PacketType.RIVAL_BOUND_RIVAL_AREA, RIVAL_BOUND_RIVAL_AREA);
        map.put(PacketType.RIVAL_POINT, RIVAL_POINT);
    }

    public static EventType create(PacketType type) {
        return map.get(type);
    }
}
