public class CPU {
    private PCB current;
    private long cycleCount;

    public CPU() {
        this.current = null;
        this.cycleCount = 0;
    }

    public void executeOneStep() {
        if (current == null) return;
        current.setProgramCounter(current.getProgramCounter() + 1);
        cycleCount++;
        if (current.getProgramCounter() >= current.getBurstTime()) {
            current.setState(ProcessState.TERMINATED);
        }
    }

    public void contextSwitch(PCB next) {
        if (current != null) {
            current.setState(ProcessState.READY);
        }
        this.current = next;
        if (next != null) {
            next.setState(ProcessState.RUNNING);
        }
    }

    public PCB getCurrent() {
        return current;
    }


    public long getCycleCount() {
        return cycleCount;
    }

}
