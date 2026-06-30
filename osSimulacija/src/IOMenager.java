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

    public void complitedIO(IODevice device) {
        karnel.handleIOCompletion(device);
    }

public String getName(){
        return "Glavni IO Menager";
}

}
