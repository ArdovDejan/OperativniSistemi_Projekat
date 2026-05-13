public class DiskRequest {
     IOOperation op;
     PCB p;
    int track;

    public DiskRequest(IOOperation op, PCB p) {
        this.op = op;
        this.p = p;
        this.track = op.getAddress();
    }
}
