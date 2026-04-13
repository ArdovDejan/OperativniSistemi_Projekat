public class DiskRequest {
     IOOperation op;
     PCB p;
    int targetTrack;

    public DiskRequest(IOOperation op, PCB p) {
        this.op = op;
        this.p = p;
        this.targetTrack = targetTrack;
    }
}
