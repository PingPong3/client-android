package red.itvirtuoso.pingpong3.app.net;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import red.itvirtuoso.pingpong3.app.server.PacketType;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

/**
 * Created by kenji on 15/05/03.
 */
public class EventTypeTest {
    @Test
    public void PacketからEventTypeを生成する() throws Exception {
        List<PacketType> packetTypes = Arrays.asList(
                PacketType.ME_READY,
                PacketType.ME_SERVE,
                PacketType.ME_RETURN,
                PacketType.ME_BOUND_MY_AREA,
                PacketType.ME_BOUND_RIVAL_AREA,
                PacketType.ME_POINT,
                PacketType.RIVAL_READY,
                PacketType.RIVAL_SERVE,
                PacketType.RIVAL_RETURN,
                PacketType.RIVAL_BOUND_MY_AREA,
                PacketType.RIVAL_BOUND_RIVAL_AREA,
                PacketType.RIVAL_POINT
        );
        List<EventType> eventTypes = new ArrayList<>();
        for(PacketType packetType : packetTypes) {
            eventTypes.add(EventType.create(packetType));
        }

        assertThat(eventTypes, is(contains(
                EventType.ME_READY,
                EventType.ME_SERVE,
                EventType.ME_RETURN,
                EventType.ME_BOUND_MY_AREA,
                EventType.ME_BOUND_RIVAL_AREA,
                EventType.ME_POINT,
                EventType.RIVAL_READY,
                EventType.RIVAL_SERVE,
                EventType.RIVAL_RETURN,
                EventType.RIVAL_BOUND_MY_AREA,
                EventType.RIVAL_BOUND_RIVAL_AREA,
                EventType.RIVAL_POINT
        )));
    }
}
