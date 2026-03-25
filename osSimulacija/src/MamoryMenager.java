import java.util.List;
import java.util.TreeMap;

public class MamoryMenager {

    private int totalSize;
    private TreeMap<Integer, List<Integer>> freeBlocks;
    public MamoryMenager(int size) {
        this.totalSize = size;
    }

}
