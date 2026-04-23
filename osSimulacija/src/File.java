import java.util.ArrayList;
import java.util.List;

public class File extends FSNode {

    private StringBuilder content ;
    private List<Integer> blockIndices;

    public File(String name, Directory parent) {
        super(name, parent);
        this.content = new StringBuilder();
        this.blockIndices = new ArrayList<>();

    }
    public String read(){
        return content.toString();
    }
    public void write(String data){
        this.content.append(data);
    }


    public void append(String data){
        this.content.append(data);
    }

    public List<Integer> getBlockIndices() {
        return blockIndices;
    }
    public void addBlockIndices(int index) {
        this.blockIndices.add(index);
    }
    public int getSizeInBlocks(){
        return blockIndices.size();
    }

}
