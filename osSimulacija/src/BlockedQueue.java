import java.util.LinkedList;
import java.util.List;

public class BlockedQueue {
    private List<PCB> list;

    public BlockedQueue() {
        this.list = new LinkedList<>();
    }

    public synchronized void block(PCB p){
        p.setState(ProcessState.WAITING);
        list.add(p);
    }

    public synchronized void unblock(PCB p){
        list.remove(p);
        p.setState(ProcessState.READY);
    }

    public synchronized List<PCB> findByDevice(IODevice d){
        List<PCB> result = new LinkedList<>();
        for (PCB p : list) {
            if (p.getWaitingForDevice() == d) {
                result.add(p);
            }
        }
        return result;
    }

    public List<PCB> getList() {
        return list;
    }
}
