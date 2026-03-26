import java.util.LinkedList;
import java.util.List;

public class BlockedQueue {
    private List<PCB> list;

    public BlockedQueue(List<PCB> list) {
        this.list = new LinkedList<>();
    }

    public void block(PCB p){
        p.setState(ProcessState.WAITING);
        list.add(p);
    }

    public void unblock(PCB p){
        list.remove(p);
        p.setState(ProcessState.READY);
    }

    //private List<PCB> findByDevice(IODevice d){}  TODO: Implementirati poslje konsultacije sa kolegom Milanom

    public List<PCB> getList() {
        return list;
    }
}
