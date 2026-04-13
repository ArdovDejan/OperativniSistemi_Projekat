import java.util.BitSet;

public class FileSystem {
  private Directory root;
  private DiskDevice disk;
  private BitSet bitVector;
  private int totalBlocks;

  public FileSystem(DiskDevice disk, int totalBlocks ) {
      this.disk = disk;
      this.totalBlocks = totalBlocks;
      this.root= new Directory("/", null);
      this.bitVector = new BitSet(totalBlocks);

  }

  public void createrDir(String path, String filename){


  }




}
