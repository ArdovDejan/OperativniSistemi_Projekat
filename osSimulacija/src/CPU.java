public class CPU {
    private PCB current;
    private long cycleCount;

    public CPU() {
        this.current = null;
        this.cycleCount = 0;
    }

    public PCB getCurrent() {
        return current;
    }
}
