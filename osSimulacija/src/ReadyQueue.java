import java.util.LinkedList;
import java.util.Queue;

public class ReadyQueue {
    private Queue<PCB> queue;

    public ReadyQueue() {
        this.queue = new LinkedList<>();
    }

    public synchronized void add(PCB p) {
        p.setState(ProcessState.READY);
        queue.add(p);
    }

    public synchronized PCB removeNext() {
        return queue.poll();
    }

    public synchronized boolean isEmpty() {
        return queue.isEmpty();
    }

    public synchronized Queue<PCB> getQueue() {
        return queue;
    }
}
