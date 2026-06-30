import java.util.ArrayList;
import java.util.List;

public class OSKarnel {

    private List<PCB> processTable;
    private ReadyQueue readyQueue;
    private BlockedQueue blockedQueue;
    private CPU cpu;
    private Scheduler scheduler;
    private MemoryManager memoryManager;
    private FileSystem fileSystem;
    private IOMenager ioMenager;
    private int nextPid;
    private int quantumCounter=0;


 public OSKarnel() {
     this.processTable = new ArrayList<>();
     this.nextPid=1;
 }

public void boot(){
    System.out.println("--- [Sistem] Pokretanje OS Simulatora ---");

    RAM ram=new RAM(1024,new int[1024]);
    this.memoryManager=new MemoryManager(ram);
    System.out.println("[Sistem] RAM memorija inicijalizovana (1kb).");

    this.ioMenager=new IOMenager(this);

    DiskDevice disk=new DiskDevice("HardDisk_0",this.ioMenager);
    System.out.println("[Sistem] Disk uredjaj spreman.");

    this.fileSystem=new FileSystem(disk,512);
    System.out.println("[Sistem] File sistem montiran.");

    this.readyQueue=new ReadyQueue();
    this.blockedQueue = new BlockedQueue();
    this.scheduler=new HRRNScheduler(5);

    this.cpu=new CPU();
    System.out.println("[Sistem] CPU spreman.");
    System.out.println("--- [Sistem] Boot proces zavrsen uspjesno. ---");



}

public void createProcess(int priority, int burstTime){
     PCB  newProcess = new PCB(nextPid++,priority,burstTime);

     if(memoryManager.allocate(newProcess,64)){
         newProcess.setState(ProcessState.READY);
         processTable.add(newProcess);
         readyQueue.add(newProcess);
         System.out.println("[Karnel] Kreiran proces PID: " + newProcess.getPid());
     }else{
         System.out.println("[Karnel] Neuspjesna alokacija memorije za novi proces.");
     }

}


    public void terminateProcess(int pid) {
        for (PCB p : processTable) {
            if (p.getPid() == pid) {
                p.setState(ProcessState.TERMINATED);
                memoryManager.free(p);
                processTable.remove(p);
                System.out.println("[Karnel] Proces " + pid + " ugašen.");
                return;
            }
        }
        System.out.println("[Karnel] Proces " + pid + " nije pronađen.");
    }


public void unblockProcess(PCB p){
     blockedQueue.unblock(p);
     readyQueue.add(p);
    System.out.println("[Karnel] Proces "+p.getPid()+" vracen u ReadyQueue.");
}

    public FileSystem getFileSystem() {
        return fileSystem;
    }

    public void runOneStep() {
    PCB current = cpu.getCurrent();

    if (current != null) {
        cpu.executeOneStep();
        quantumCounter++;
        System.out.println("[CPU] Proces " + current.getPid() + " izvrsava korak. PC: "+current.getProgramCounter()+", Kvant: "+quantumCounter);

        if(current.getPid() == 1 && current.getProgramCounter()==7){
           // handleSyscall(current,"IO_REQUEST");
            return ;

        }




        if(current.getState()==ProcessState.TERMINATED){
        System.out.println("[Karnel] Proces "+ current.getPid()+" je zavrsio rad.");
        memoryManager.free(current);
        cpu.contextSwitch(null);
        quantumCounter=0;
    }else if(quantumCounter>=((HRRNScheduler)scheduler).getTimeQuantum()){
        System.out.println("[Karnel] Procesu " + current.getPid()+" je istekao kvant vremena. Prekidam ga.");
        current.setState(ProcessState.READY);
        readyQueue.add(current);
        cpu.contextSwitch(null);
        quantumCounter=0;

    }
    }
    if(cpu.getCurrent()==null){
        PCB next=scheduler.chooseNext(readyQueue)   ;
        if(next!=null){
            readyQueue.getQueue().remove(next);
            cpu.contextSwitch(next);
            quantumCounter=0;
            System.out.println("[Karnel] Cpu je preuzeo proces "+ next.getPid()+" (Preostalo Burst: "+next.getBurstTime()+")");

        }else {
            System.out.println("[Karnel] Nema procesa u ReadyQueue.");
        }
    }

    for(PCB p: readyQueue.getQueue()){
        p.incrementWaitingTime();
    }


}

public void handleSyscall(Syscall req){
    PCB p=cpu.getCurrent();
    if(p == null)return;

    //dovrsiti


}

    public List<PCB> getProcessTable() {
        return processTable;
    }

    public MemoryManager getMemoryManager() {
        return memoryManager;
    }
}
