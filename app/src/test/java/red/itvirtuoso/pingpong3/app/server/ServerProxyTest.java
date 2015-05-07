package red.itvirtuoso.pingpong3.app.server;

import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

/**
 * Created by kenji on 15/05/07.
 */
public class ServerProxyTest {
    private class TestServerProxy extends ServerProxy {
        @Override
        public void connect() throws IOException {
            /* nop */
        }

        @Override
        public void send(Packet packet) throws IOException {
            /* nop */
        }

        @Override
        public void disconnect() {
            /* nop */
        }
    }

    @Test
    public void 何も追加していないときはnullが返る() throws Exception {
        TestServerProxy serverProxy = new TestServerProxy();
        assertThat(serverProxy.receive(), is(nullValue()));
    }

    @Test
    public void 追加したパケットが順に取り出せる() throws Exception {
        TestServerProxy serverProxy = new TestServerProxy();
        Packet packet1 = new Packet(PacketType.ME_READY);
        Packet packet2 = new Packet(PacketType.RIVAL_READY);
        serverProxy.add(packet1);
        serverProxy.add(packet2);

        assertThat(serverProxy.receive(), is(packet1));
        assertThat(serverProxy.receive(), is(packet2));
        assertThat(serverProxy.receive(), is(nullValue()));
    }
}
