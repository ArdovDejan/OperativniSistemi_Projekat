import java.util.ArrayList;
import java.util.List;

public class DiskDevice extends IODevice{

    private List<DiskRequest> pendingRequests = new ArrayList<>();
    private int currentTrack;
    private boolean busy  = false;
    private IOMenager ioMenager;

    public DiskDevice(String name, IOMenager ioMenager){
        super(name);
        this.ioMenager = ioMenager;

    }


    public void startOperation(IOOperation op,PCB p){
        pendingRequests.add(new DiskRequest(op,p));

        if(!busy){
            //ako ne radi, pokreni obradu
        }

    }






}
