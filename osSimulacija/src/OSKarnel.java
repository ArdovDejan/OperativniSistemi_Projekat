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
    this.blockedQueue = new BlockedQueue(new ArrayList<>());
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



}