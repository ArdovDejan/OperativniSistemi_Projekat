import java.io.IOException;
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

    public void requestIO(PCB p,String deviceName,IOOperation op) throws IOException {
        System.out.println("[IOMenager] Request "+p.getPid()+"trazi I/O na uredjaju  "+deviceName);
        for (IODevice device:devices){
            if(device.getName().equals(deviceName)){
                p.setWaitingForDevice(device);
                p.setState(ProcessState.WAITING);
                device.startOperation(p,op);
                return;

            }

        }
        System.out.println("[IOMenager] Uredjaj " +deviceName+"nije pronadjen.");

    }


    public void completeIO(IODevice device) {
        karnel.handleIOCompletion(device);
    }

public String getName(){
        return "Glavni IO Menager";
}

}
