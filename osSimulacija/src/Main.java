public static void main(String[] args) throws InterruptedException {
    OSKarnel os = new OSKarnel();
    os.setRunningQuantum(500);
    os.boot();

    //os.createProcess(1, 20);
    //os.createProcess(1, 10);
    //os.createProcess(1, 3);

    System.out.println("\n=== TEST: Asembler ===");

    Asembler asembler = new Asembler();

    String asmKod =
            "; Testni program\n" +
                    "MOV R1, 5\n" +
                    "MOV R2, 3\n" +
                    "ADD R1, R2\n" +
                    "STORE R1, 10\n" +
                    "HLT";

    List<String> instrukcije = asembler.compile(asmKod);
    os.createProcess(5, instrukcije);

    os.setRunningQuantum(500);
    os.startCpuLoop();
    Thread.sleep(5000);


    System.out.println("\n--- Stanje procesa ---");
    for (PCB p : os.getProcessTable()) {
        System.out.println(p);
    }

    Shell shell = new Shell(os);
    shell.start();
}