import java.util.LinkedList;
import java.util.Queue;

public class ReadyQueue {
    private Queue<PCB> queue;

    public ReadyQueue() {
        this.queue = new LinkedList<>();
    }

    public void add(PCB p) {
        p.setState(ProcessState.READY);
        queue.add(p);
    }

    public PCB removeNext() {
        return queue.poll();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public Queue<PCB> getQueue() {
        return queue;
    }
}
