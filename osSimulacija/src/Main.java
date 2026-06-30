public class Main {
    static void main(String[] args) {


        OSKarnel os=new OSKarnel();
        os.boot();

        Shell shell=new Shell(os);
        shell.start();

/**
        System.out.println("--- Kreiranje procesa ---");
        os.createProcess(1,10);
        os.createProcess(2,5);

        System.out.println("\n ---- Pocetak simulacije rada cpu-a --- ");

        for (int i = 0; i < 15; i++) {

            if(i==12){
                System.out.println("\n[Hardver] Disk je zavrsio citanje podataka za Proces 1.");
            }


            os.runOneStep();
            try{Thread.sleep(500);}catch(InterruptedException e){
                System.out.println(e.getMessage());
            }

        }*/







    }
}
