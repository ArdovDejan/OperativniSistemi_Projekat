import java.util.List;
import java.util.Scanner;

public class Shell {
    private OSKarnel osKarnel;
    private Scanner scanner;
    private boolean running;
    private String currentPath;

    public Shell(OSKarnel osKarnel) {
        this.osKarnel = osKarnel;
        this.scanner = new Scanner(System.in);
        this.running = true;
        this.currentPath = "/";
    }

    public void start() {
        while (running) {
            System.out.print(">> ");
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                executeCommand(input);
            }
        }
    }

    private void executeCommand(String input) {
        String[] info = input.split(" ");
        String command = info[0].toLowerCase();

        switch (command) {
            case "run":
                commandRun(info);
                break;
            case "ps":
                commandPs();
                break;
            case "mem":
                commandMem();
                break;
            case "exit":
                commandExit();
                break;
            case "kill":
                commandKill(info);
                break;
            case "ls":
                commandLs();
                break;
            case "cd":
                commandCd(info);
                break;
            case "mkdir":
                commandMkdir(info);
                break;
            case "rm":
                commandRm(info);
                break;
            case "touch":
                commandTouch(info);
                break;
            case "write":
                commandWrite(info);
                break;
            case "exec":
                commandExec(info);
                break;

            default:
                System.out.println("Nepoznata komanda: " + command);
                break;
        }
    }

    private void commandLs(){
        FSNode node=osKarnel.getFileSystem().resolvePath(currentPath);
        if(node instanceof Directory){
            Directory dir=(Directory)node;
            System.out.println("Sadrzaj direktorijuma [" + currentPath + "]:");
            for(FSNode child : dir.list()){
                String tip =(child instanceof Directory) ? "<DIR>" : "<<FILE>";
                System.out.println(" "+ tip+" "+child.getName());

            }
        }

    }
    private void commandCd(String[] info){
        if(info.length<2 || info[1].equals("/")){
            currentPath = "/";
            return;
        }
        String target=info[1];
        String targetPath;

        if(target.equals("..")){
            if(currentPath.equals("/"))return;
            int lastSlash=currentPath.lastIndexOf("/");
            targetPath=currentPath.substring(0,lastSlash);
            if(targetPath.isEmpty()) targetPath="/";


        }else{
            targetPath=currentPath.equals("/") ? "/" + target : currentPath + "/" +  info[1];

        }
        FSNode node=osKarnel.getFileSystem().resolvePath(targetPath);
        if(node instanceof Directory){
            currentPath=targetPath;

        }else{
            System.out.println("Greska: Direktorijum ne postoji.");
        }


    }

    private void commandMkdir(String[] info){
        if(info.length<2){
            System.out.println("Upotreba: mkdir <naziv_direktorijuma>");
            return;

        }
        osKarnel.getFileSystem().createDirectory(currentPath,info[1]);
        System.out.println("Kreiran direktorijum: " + info[1] );

    }
    private void commandRm(String[] info){
        if(info.length<2){
            System.out.println("Upotreba: rm <naziv_fajla_ili_foldera>");
            return;
        }
        String targetPath=currentPath.equals("/") ? "/" + info[1] : currentPath + "/" +  info[1];
        osKarnel.getFileSystem().delete(targetPath);


    }

    private void commandRun(String[] dijelovi) {
        if (dijelovi.length < 3) {
            System.out.println("Upotreba: run <prioritet> <burstTime>");
            return;
        }
        int prioritet = Integer.parseInt(dijelovi[1]);
        int burstTime = Integer.parseInt(dijelovi[2]);
        osKarnel.createProcess(prioritet, burstTime);
    }

    private void commandPs() {
        if (osKarnel.getProcessTable().isEmpty()) {
            System.out.println("Nema aktivnih procesa.");
            return;
        }
        for (PCB p : osKarnel.getProcessTable()) {
            System.out.println(p);
        }
    }

    private void commandMem() {
        System.out.println(osKarnel.getMemoryManager().dumpMemory());
    }

    private void commandExit() {
        System.out.println("Gasim OS...");
        osKarnel.stopCpuLoop();
        running = false;
    }

    private void commandKill(String[] dijelovi) {
        if (dijelovi.length < 2) {
            System.out.println("Upotreba: kill <pid>");
            return;
        }
        try {
            int pid = Integer.parseInt(dijelovi[1]);
            osKarnel.terminateProcess(pid);
        } catch (NumberFormatException e) {
            System.out.println("Greska: pid mora biti broj.");
        }
    }

    private void commandTouch(String[] info){
        if(info.length < 2){
            System.out.println("Upotreba: touch <naziv_fajla>");
            return;

        }
        osKarnel.getFileSystem().createFile(currentPath,info[1]);
    }

    private void commandWrite(String[] info){
        if(info.length < 2){
            System.out.println("Upotreba: write <naziv_fajla>");
            return;

        }
        String targetPath;
        if(currentPath.equals("/")){
            targetPath = "/"+info[1];
        }else{
            targetPath=currentPath+"/"+info[1];
        }
        OpenFileHandle handle=osKarnel.getFileSystem().openFile(targetPath);
        if(handle != null){
            String asmKod="MOV R1, 5\nMOV R2, 3\nADD R1, R2\nSTORE R1, 10\nHLT";
            handle.write(asmKod);
            System.out.println("[SilrSystem] U fajl '" + info[1] + "' upisam asemblerski kod. ");

        }


    }

    private void commandExec(String[] info){
        if(info.length < 2){
            System.out.println("Upotreba: exec <naziv_fajla>");
            return;
        }
        String targetPath;
        if(currentPath.equals("/")){
            targetPath = "/"+info[1];
        }else {
            targetPath=currentPath+"/"+info[1];
        }

        OpenFileHandle handle=osKarnel.getFileSystem().openFile(targetPath);
        if(handle == null) return;

        String asmKod= handle.read(1000);
        Asembler asembler=new Asembler();
        List<String> masinskiKod=asembler.compile(asmKod);

        int pid=osKarnel.createProcess(1,masinskiKod);
        if(pid!=-1){
            System.out.println("[Sistem] Program iz fajla uspjesno pokrenut pod PID: "+pid);
        }








    }


}