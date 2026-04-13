import java.io.IOException;
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

    private void processNextRequest() throws IOException {
        if(pendingRequests.isEmpty()){
            busy = false;
            return;
        }

        busy = true;

        DiskRequest bestRequest = null;
        int minDistance = Integer.MAX_VALUE;


        for(DiskRequest req: pendingRequests){
            int distance = Math.abs(req.targetTrack - currentTrack);
            if (distance < minDistance) {
                minDistance = distance;
                bestRequest = req;
            }

        }
        if (bestRequest != null) {
            pendingRequests.remove(bestRequest);
            currentTrack = bestRequest.targetTrack;


            //nije gotovo
        }

    }
    
    public void startOperation(IOOperation op,PCB p){
        pendingRequests.add(new DiskRequest(op,p));

        if(!busy){

                processNextRequest();

        }

    }
    





}
