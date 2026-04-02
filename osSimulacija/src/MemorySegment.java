public class MemorySegment {
    public PCB owner;
    public int base;
    public int limit;




    public PCB getOwner() {
        return owner;
    }

    public void setOwner(PCB owner) {
        this.owner = owner;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getBase() {
        return base;
    }

    public void setBase(int base) {
        this.base = base;
    }
}
