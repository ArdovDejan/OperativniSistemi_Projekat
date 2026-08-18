public static void main(String[] args) throws InterruptedException {
    OSKarnel os = new OSKarnel();
    os.setRunningQuantum(500);
    os.boot();

    os.createProcess(1, 20);
    os.createProcess(1, 10);
    os.createProcess(1, 3);


    Thread.sleep(10000);


    System.out.println("\n--- Stanje procesa ---");
    for (PCB p : os.getProcessTable()) {
        System.out.println(p);
    }

    Shell shell = new Shell(os);
    shell.start();
}