package red.itvirtuoso.pingpong3.app.server;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by kenji on 15/05/03.
 */
public class LocalServerProxy implements ServerProxy, Runnable {
    private long stepTime;
    private Mode mode;
    private ArrayList<Action> actions;
    private ArrayList<Packet> packets;

    private ExecutorService service;

    public LocalServerProxy(long stepTime) {
        this.stepTime = stepTime;
        this.mode = Mode.READY;
        this.actions = new ArrayList<>();
        this.packets = new ArrayList<>();

        this.service = Executors.newSingleThreadExecutor();
        this.service.execute(this);
        this.service.shutdown();
    }

    @Override
    public void run() {
        while (true) {
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
                    Packet packet = new Packet(type);
                    synchronized (LocalServerProxy.this.packets) {
                        LocalServerProxy.this.packets.add(packet);
                    }
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
    public boolean connect() {
        return true;
    }

    @Override
    public void send(Packet packet) {
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
                addPacketAction(currentTime, 0, PacketType.SERVE);
                addPacketAction(currentTime, 1, PacketType.BOUND_MY_AREA);
                addPacketAction(currentTime, 2, PacketType.BOUND_RIVAL_AREA);
                addPacketAction(currentTime, 3, PacketType.RETURN);
                addPacketAction(currentTime, 5, PacketType.BOUND_MY_AREA);
                addModeAction(currentTime, 5, Mode.WAIT);
                addPacketAction(currentTime, 7, PacketType.POINT_RIVAL);
                addModeAction(currentTime, 7, Mode.READY);
                break;

            case WAIT:
                resetAction();
                addModeAction(currentTime, 0, Mode.BUSY);
                addPacketAction(currentTime, 0, PacketType.RETURN);
                addPacketAction(currentTime, 2, PacketType.BOUND_RIVAL_AREA);
                addPacketAction(currentTime, 3, PacketType.RETURN);
                addPacketAction(currentTime, 5, PacketType.BOUND_MY_AREA);
                addModeAction(currentTime, 5, Mode.WAIT);
                addPacketAction(currentTime, 7, PacketType.POINT_RIVAL);
                addModeAction(currentTime, 7, Mode.READY);
                break;

            default:
                /* nop */
        }
    }

    @Override
    public Packet receive() {
        synchronized (this.packets) {
            if (this.packets.size() == 0) {
                return null;
            }
            return this.packets.remove(0);
        }
    }
}
