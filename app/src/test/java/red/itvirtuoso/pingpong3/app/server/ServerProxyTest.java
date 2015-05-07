package red.itvirtuoso.pingpong3.app.server;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

/**
 * Created by kenji on 15/05/07.
 */
public class ServerProxyTest {
    private class TestServerProxy extends ServerProxy {
        @Override
        public boolean connect() {
            return false;
        }

        @Override
        public void send(Packet packet) {
            /* nop */
        }

        @Override
        public Packet receive() {
            return null;
        }

        @Override
        public void disconnect() {
            /* nop */
        }
    }

    @Test
    public void 何も追加していないときはnullが返る() throws Exception {
        TestServerProxy proxy = new TestServerProxy();
        assertThat(proxy.receive(), is(nullValue()));
    }

    @Test
    public void 追加したパケットが順に取り出せる() throws Exception {
        TestServerProxy proxy = new TestServerProxy();
        Packet packet1 = new Packet(PacketType.ME_READY);
        Packet packet2 = new Packet(PacketType.RIVAL_READY);
        proxy.add(packet1);
        proxy.add(packet2);

        assertThat(proxy.receive(), is(packet1));
        assertThat(proxy.receive(), is(packet2));
        assertThat(proxy.receive(), is(nullValue()));
    }
}
