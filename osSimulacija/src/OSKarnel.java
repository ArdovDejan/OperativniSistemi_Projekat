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
    private Thread cpuThread;
    private boolean cpuRunning;
    private int runningQuantum;

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

    startCpuLoop();

    }

    public void  startCpuLoop(){
        cpuRunning = true;
        cpuThread = new Thread(() -> {
            while (cpuRunning){
                timerTick();
                try {
                    Thread.sleep(runningQuantum);
                } catch (InterruptedException e){
                    Thread.currentThread().interrupt();
                }
            }
        });
        cpuThread.start();
        System.out.println("[Sistem] CPU loop pokrenut (" + runningQuantum + "ms po ciklusu).");
    }

    public void stopCpuLoop(){
        cpuRunning = false;
        if(cpuThread != null) {
            cpuThread.interrupt();
        }
    }

    public void setRunningQuantum(int runningQuantum) {
        this.runningQuantum = runningQuantum;
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


    public void timerTick(){
     PCB current=cpu.getCurrent();

     if(current==null){
         dodjeliSledeciProces();
         increaseWaitingTime();
         return;
     }

     cpu.executeOneStep();
     quantumCounter++;
     System.out.println("[CPU] Proces " + current.getPid() + " izvrsava korak. PC: " + current.getProgramCounter() + ", Kvant: " + quantumCounter);

     if(current.getState()==ProcessState.TERMINATED){
         System.out.println("[Karnel] Proces " + current.getPid() + " je zavrsio rad.");
         memoryManager.free(current);
         processTable.remove(current);
         cpu.contextSwitch(null);
         quantumCounter = 0;
         increaseWaitingTime();
         return;
     }

     if (quantumCounter >= ((HRRNScheduler) scheduler).getTimeQuantum()){
         System.out.println("[Karnel] Proces " + current.getPid() + " je istekao kvant vrijeme");
         readyQueue.add(current);
         cpu.contextSwitch(null);
         quantumCounter = 0;
     }
        increaseWaitingTime();
    }

    private void dodjeliSledeciProces(){
     PCB next = scheduler.chooseNext(readyQueue);
     if(next != null){
         readyQueue.getQueue().remove(next);
         cpu.contextSwitch(next);
         quantumCounter = 0;
         System.out.println("[Karnel] CPU preuzeo proces " + next.getPid());
     }//else
         //System.out.println("[Karnel] Nema procesa u ReadyQueue");
    }

    private void increaseWaitingTime(){
     for (PCB p : readyQueue.getQueue())
         p.incrementWaitingTime();
    }


    public void syscall(Syscall request) {
     PCB current = cpu.getCurrent();
     if(current==null)
         return;

     System.out.println("[Sistemski poziv] Proces " + current.getPid() + " zatrazio: " + request.getType());

     switch (request.getType()){
         case EXIT:
             terminateProcess(current.getPid());
             cpu.contextSwitch(null);
             quantumCounter = 0;
             break;
         case SLEEP:
             blockedQueue.block(current);
             cpu.contextSwitch(null);
             quantumCounter = 0;
             break;
         case YIELD:
             readyQueue.add(current);
             cpu.contextSwitch(null);
             quantumCounter = 0;
             break;
         case READ:
         case WRITE:
         case OPEN:
             System.out.println("[Karnel] Proces " + current.getPid() + " blokiran zbog I/O (" + request.getType() + ").");
             blockedQueue.block(current);
             cpu.contextSwitch(null);
             quantumCounter = 0;
             break;
         case CREATE_PROCESS:
             if (request.getArgs().size() >= 2) {
                 int priority = Integer.parseInt(request.getArgs().get(0));
                 int burstTime = Integer.parseInt(request.getArgs().get(1));
                 createProcess(priority, burstTime);
             } else {
                 createProcess(current.getPriority(), 10); //10 defolt vrijednost ako nema argumenata
             }
             break;
         default:
             System.out.println("[Karnel] Nepoznat syscall.");
             break;
     }

    }


    public void handleIOCompletion(IODevice device) {
        System.out.println("[Karnel] Uredjaj " + device.getName() + " zavrsio I/O operaciju.");

        List<PCB> procesi = blockedQueue.findByDevice(device);

        for (PCB p : procesi) {
            blockedQueue.unblock(p);
            readyQueue.add(p);
            System.out.println("[Karnel] Proces " + p.getPid() + " odblokiran i vraca se u ReadyQueue.");
        }
    }

    public List<PCB> getProcessTable() {
        return processTable;
    }

    public MemoryManager getMemoryManager() {
        return memoryManager;
    }
}
