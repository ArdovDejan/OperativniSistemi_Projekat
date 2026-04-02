public class DiskRequest {
    private IOOperation op;
    private PCB p;
    private int targetTrack;

    public DiskRequest(IOOperation op, PCB p) {
        this.op = op;
        this.p = p;
        this.targetTrack = targetTrack;
    }
}
