import java.util.Scanner;

public class Shell {
    private OSKarnel osKarnel;
    private Scanner scanner;
    private boolean running;

    public Shell(OSKarnel osKarnel) {
        this.osKarnel = osKarnel;
        this.scanner = new Scanner(System.in);
        this.running = true;
    }

    public void start() {
        while (running) {
            System.out.print("> ");
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
            default:
                System.out.println("Nepoznata komanda: " + command);
                break;
        }
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

}