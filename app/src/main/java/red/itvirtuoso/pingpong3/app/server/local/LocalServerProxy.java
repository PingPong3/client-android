package red.itvirtuoso.pingpong3.app.server.local;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import red.itvirtuoso.pingpong3.app.server.Packet;
import red.itvirtuoso.pingpong3.app.server.PacketType;
import red.itvirtuoso.pingpong3.app.server.ServerProxy;

/**
 * Created by kenji on 15/05/03.
 */
public class LocalServerProxy extends ServerProxy implements Runnable {
    private long stepTime;
    private Mode mode;
    private ArrayList<Action> actions;

    private ExecutorService service;

    public LocalServerProxy(long stepTime) {
        this.stepTime = stepTime;
        this.mode = Mode.UNREADY;
        this.actions = new ArrayList<>();

        this.service = Executors.newSingleThreadExecutor();
        this.service.execute(this);
        this.service.shutdown();
    }

    @Override
    public void run() {
        while (this.mode != Mode.STOP) {
            Thread.yield();
            synchronized (this.actions) {
                if (this.actions.size() == 0) {
                    continue;
                }
                Action action = this.actions.get(0);
                if (System.currentTimeMillis() < action.getTime()) {
                    continue;
                }
                boolean result = action.execute();
                if (result) {
                    this.actions.remove(0);
                }
            }
        }
    }

    private void resetAction() {
        synchronized (this.actions) {
            this.actions.clear();
        }
    }

    private void addPacketAction(long currentTime, long step, final PacketType type) {
        synchronized (this.actions) {
            long time = currentTime + step * stepTime;
            this.actions.add(new PacketAction(time, type) {
                @Override
                public boolean execute() {
                    LocalServerProxy.this.add(new Packet(type));
                    return true;
                }
            });
        }
    }

    private void addModeAction(long currentTime, long step, final Mode mode) {
        synchronized (this.actions) {
            long time = currentTime + step * stepTime;
            this.actions.add(new ModeAction(time, mode) {
                @Override
                public boolean execute() {
                    LocalServerProxy.this.mode = mode;
                    return true;
                }
            });
        }
    }

    @Override
    public void connect() throws IOException {
        long currentTime = System.currentTimeMillis();
        addPacketAction(currentTime, 0, PacketType.BEGIN);
        addModeAction(currentTime, 0, Mode.READY);
        addPacketAction(currentTime, 0, PacketType.ME_READY);
    }

    @Override
    public void disconnect() {
        long currentTime = System.currentTimeMillis();
        addModeAction(currentTime, 0, Mode.STOP);
    }

    @Override
    public void send(Packet packet) throws IOException {
        switch (packet.getType()) {
            case SWING:
                sendSwing();
                break;
            default: /* nop */
        }
    }

    private void sendSwing() {
        long currentTime = System.currentTimeMillis();
        switch (this.mode) {
            case READY:
                addModeAction(currentTime, 0, Mode.BUSY);
                addPacketAction(currentTime, 0, PacketType.ME_SERVE);
                addPacketAction(currentTime, 1, PacketType.RIVAL_BOUND_MY_AREA);
                addPacketAction(currentTime, 2, PacketType.RIVAL_BOUND_RIVAL_AREA);
                addPacketAction(currentTime, 3, PacketType.RIVAL_RETURN);
                addModeAction(currentTime, 5, Mode.WAIT);
                addPacketAction(currentTime, 5, PacketType.ME_BOUND_MY_AREA);
                addPacketAction(currentTime, 7, PacketType.RIVAL_POINT);
                addModeAction(currentTime, 7, Mode.READY);
                break;

            case WAIT:
                resetAction();
                addModeAction(currentTime, 0, Mode.BUSY);
                addPacketAction(currentTime, 0, PacketType.ME_RETURN);
                addPacketAction(currentTime, 2, PacketType.RIVAL_BOUND_RIVAL_AREA);
                addPacketAction(currentTime, 3, PacketType.RIVAL_RETURN);
                addModeAction(currentTime, 5, Mode.WAIT);
                addPacketAction(currentTime, 5, PacketType.ME_BOUND_MY_AREA);
                addPacketAction(currentTime, 7, PacketType.RIVAL_POINT);
                addModeAction(currentTime, 7, Mode.READY);
                break;

            default:
                /* nop */
        }
    }
}
