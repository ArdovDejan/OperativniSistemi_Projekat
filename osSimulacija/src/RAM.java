public class RAM {
    public int size;
    public int[] cells;

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int[] getCells() {
        return cells;
    }

    public void setCells(int[] cells) {
        this.cells = cells;
    }

    public RAM(int size, int[] cells) {
        this.size = size;
        this.cells = cells;
    }
}
