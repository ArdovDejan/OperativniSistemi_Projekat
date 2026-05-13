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
            int distance = Math.abs(req.track - currentTrack);
            if (distance < minDistance) {
                minDistance = distance;
                bestRequest = req;
            }

        }
        if (bestRequest != null) {
            pendingRequests.remove(bestRequest);
            int targetTrack = bestRequest.track;
            System.out.println("[Disk] Glava se pomjera sa " + currentTrack + " na " + targetTrack);

            currentTrack = targetTrack;

            final DiskRequest finalReq =bestRequest;
            new Thread (()->{
                try{
                    Thread.sleep((finalReq.op.getDuration()));
                    System.out.println("[Disk] Operacija završena na stazi " + finalReq.track);

                    ioMenager.complitedIO(this,finalReq.p);
                    processNextRequest();

                }catch (InterruptedException e){
                    e.printStackTrace();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).start() ;


        }

    }

    public void startOperation(IOOperation op,PCB p) throws IOException {
        pendingRequests.add(new DiskRequest(op,p));

        System.out.println("[Disk] Primljen zahtjev za stazu " + op.getAddress() + " od procesa " + p.getPid());

        if(!busy){

            try {
                processNextRequest();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

    }
    





}
