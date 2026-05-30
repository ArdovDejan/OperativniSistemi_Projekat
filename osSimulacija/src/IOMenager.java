import java.util.*;

public class IOMenager {

    private List<IODevice> devices;
    private OSKarnel karnel;

    public IOMenager(OSKarnel osKarnel) {
        this.karnel=karnel;
        this.devices=new ArrayList<>();
    }


    public void complitedIO(IODevice device, PCB p) {
        System.out.println("[IOMenager] Uredjaj " + device.getName() + " je zavrsio operaciju za proces " + p.getPid());

        p.setState(ProcessState.READY);

        if(karnel != null){
            karnel.unblockProcess(p);
        }

}


public String getName(){
        return "Glavni IO Menager";
}

}
