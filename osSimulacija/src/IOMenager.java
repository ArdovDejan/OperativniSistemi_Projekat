import java.util.*;

public class IOMenager {

    private List<IODevice> devices;
    private OSKarnel karnel;


    public void complitedIO(IODevice device, PCB p) {
        System.out.println("[IOMenager] Uredjaj " + device.getName() + " je zavrsio operaciju za proces " + p.getPid());

        p.getState(ProcessState.READY);

        //nije gotovo

}




}
