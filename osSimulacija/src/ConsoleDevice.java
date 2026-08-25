import java.io.IOException;

public class ConsoleDevice extends IODevice{
    private IOMenager ioMenager;

    public ConsoleDevice(String name,IOMenager ioMenager){


        super(name);
        this.ioMenager = ioMenager;

    }


    public void startOperation(PCB p,IOOperation op) throws IOException {
        p.setWaitingForDevice(this);
        System.out.println("[ConsoleDevice] Primljen zahtjev (" + op.getType() + ") od procesa PID: " + p.getPid());

        new Thread(()->{
            try{
                Thread.sleep(op.getDuration());
                if(op.getType() == IOType.WRITE){
                    System.out.println("\n>>> [KONZOLA ISPIS - PID " + p.getPid() + "]: " + op.getData() + " <<<\n");
                }else if (op.getType() == IOType.READ){
                    System.out.println("\n>>> [KONZOLA UČITAVANJE - PID " + p.getPid() + "]: Podaci uspešno pročitani <<<\n");
                }

                ioMenager.completeIO(this);

            }catch (InterruptedException e){
                e.printStackTrace();
            }

        }).start();


    }



}
