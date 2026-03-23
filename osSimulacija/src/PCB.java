import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PCB {

    private int pid;
    private ProcessState state;
    private int priority;
    private int programCounter;
    private Map<String,Integer> registers;
    private int baseAddress;
    private int limit;

    private int burstTime;//
    private int waitingTime;// Trebace mi zbog HRRN algoritma rasporedjivanja procesa

    // TODO: dodati kada Milan implementira OpenFileHandle
    // private List<OpenFileHandle> openFiles;

    public PCB(int pid, int priority, int burstTime) {
        this.pid = pid;
        this.priority = priority;
        this.burstTime = burstTime;
        this.state = ProcessState.NEW;
        this.programCounter = 0;
        this.waitingTime = 0;
        this.registers = new HashMap<>();
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public ProcessState getState() {
        return state;
    }

    public void setState(ProcessState state) {
        this.state = state;
    }

    public int getProgramCounter() {
        return programCounter;
    }

    public void setProgramCounter(int programCounter) {
        this.programCounter = programCounter;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Map<String, Integer> getRegisters() {
        return registers;
    }

    public void setRegisters(Map<String, Integer> registers) {
        this.registers = registers;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getBaseAddress() {
        return baseAddress;
    }

    public void setBaseAddress(int baseAddress) {
        this.baseAddress = baseAddress;
    }

    public int getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(int waitingTime) {
        this.waitingTime = waitingTime;
    }

    public int getBurstTime() {
        return burstTime;
    }

    public void setBurstTime(int burstTime) {
        this.burstTime = burstTime;
    }

    @Override
    public String toString() {
        return "PID: " + pid + " | State: " + state + " | Priority: " + priority + " | PC: " + programCounter;
    }

}
