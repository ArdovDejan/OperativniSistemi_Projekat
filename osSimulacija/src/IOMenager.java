import java.util.*;

public class IOMenager {

    private List<IODevice> devices;
    private OSKarnel karnel;

    public IOMenager(OSKarnel osKarnel) {
        this.karnel=osKarnel;
        this.devices=new ArrayList<>();
    }

    public void addDevice(IODevice ioDevice){
        devices.add(ioDevice);
        System.out.println("[IOMenager] Registrovan uredjaj: " + ioDevice.getName() );

    }

    public void completeIO(IODevice device, PCB p) {
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
