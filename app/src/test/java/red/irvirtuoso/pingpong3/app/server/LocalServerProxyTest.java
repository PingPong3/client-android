package red.irvirtuoso.pingpong3.app.server;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import red.itvirtuoso.pingpong3.app.server.LocalServerProxy;
import red.itvirtuoso.pingpong3.app.server.Packet;
import red.itvirtuoso.pingpong3.app.server.PacketType;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

/**
 * Created by kenji on 15/05/03.
 */
public class LocalServerProxyTest {
    /* ゲームループの単位時間。テスト用に短くしている */
    private static final long STEP_TIME = 50;

    private class _LogBuilder {
        private long beginTime;
        private long stepTime;

        private _LogBuilder(long stepTime) {
            this.beginTime = System.currentTimeMillis();
            this.stepTime = stepTime;
        }

        private _Log create(PacketType type) {
            return new _Log((System.currentTimeMillis() - this.beginTime) / this.stepTime, type);
        }

        private _Log create(long step, PacketType type) {
            return new _Log(step, type);
        }
    }

    private class _Log {
        private long step;
        private PacketType type;

        private _Log(long step, PacketType type) {
            this.step = step;
            this.type = type;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            _Log log = (_Log) o;

            if (step != log.step) return false;
            if (type != log.type) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = (int) (step ^ (step >>> 32));
            result = 31 * result + type.hashCode();
            return result;
        }

        @Override
        public String toString() {
            return getClass().getSimpleName() + "{" +
                    "step=" + step +
                    ", type=" + type +
                    '}';
        }
    }

    @Test(timeout = 1000)
    public void サーブをする() throws Exception {
        /*
         * SWINGパケットを送信すると、次のパケットを順に受信する
         * <ul>
         *     <li>0, サーブ</li>
         *     <li>1, 自陣でのバウンド</li>
         *     <li>2, 相手陣でのバウンド</li>
         *     <li>3, 相手のリターン</li>
         *     <li>5, 自陣でのバウンド</li>
         *     <li>7, 相手の得点</li>
         * </ul>
         */
        LocalServerProxy server = new LocalServerProxy(STEP_TIME);
        _LogBuilder builder = new _LogBuilder(STEP_TIME);
        List<_Log> logs = new ArrayList<>();
        server.send(new Packet(PacketType.SWING));
        while (logs.size() < 6) {
            Thread.yield();
            Packet packet = server.receive();
            if (packet == null) {
                continue;
            }
            logs.add(builder.create(packet.getType()));
        }

        assertThat(logs, is(contains(
                builder.create(0, PacketType.SERVE),
                builder.create(1, PacketType.BOUND_MY_AREA),
                builder.create(2, PacketType.BOUND_RIVAL_AREA),
                builder.create(3, PacketType.RETURN),
                builder.create(5, PacketType.BOUND_MY_AREA),
                builder.create(7, PacketType.POINT_RIVAL)
        )));
    }

    @Test(timeout = 1000)
    public void リターンをする() throws Exception {
        /*
         * ボールが返ってきたときに1秒待ってからSWINGパケットを送信すると、次のパケットを順に受信する
         * <ul>
         *     <li>6, リターン</li>
         *     <li>8, 相手陣でのバウンド</li>
         *     <li>9, 相手のリターン</li>
         *     <li>11, 自陣でのバウンド</li>
         *     <li>13, 相手の得点</li>
         * </ul>
         */
        LocalServerProxy server = new LocalServerProxy(STEP_TIME);
        _LogBuilder builder = new _LogBuilder(STEP_TIME);
        List<_Log> tempLogs = new ArrayList<>();
        server.send(new Packet(PacketType.SWING));
        while (tempLogs.size() < 5) {
            Thread.yield();
            Packet packet = server.receive();
            if (packet == null) {
                continue;
            }
            tempLogs.add(builder.create(packet.getType()));
        }
        List<_Log> logs = new ArrayList<>();
        Thread.sleep(1 * STEP_TIME);
        server.send(new Packet(PacketType.SWING));
        while (logs.size() < 5) {
            Thread.yield();
            Packet packet = server.receive();
            if (packet == null) {
                continue;
            }
            logs.add(builder.create(packet.getType()));
        }

        assertThat(logs, is(contains(
                builder.create(6, PacketType.RETURN),
                builder.create(8, PacketType.BOUND_RIVAL_AREA),
                builder.create(9, PacketType.RETURN),
                builder.create(11, PacketType.BOUND_MY_AREA),
                builder.create(13, PacketType.POINT_RIVAL)
        )));
    }
}
