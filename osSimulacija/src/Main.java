public class Main {
    static void main(String[] args) {


        OSKarnel os=new OSKarnel();
        os.boot();

        System.out.println("--- Kreiranje procesa ---");
        os.createProcess(1,10);
        os.createProcess(2,5);

        System.out.println("\n ---- Pocetak simulacije rada cpu-a --- ");

        for (int i = 0; i < 15; i++) {

            os.runOneStep();
            try{Thread.sleep(500);}catch(InterruptedException e){}

        }







    }
}
