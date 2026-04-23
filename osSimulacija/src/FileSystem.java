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
  private int findFreeBlock() {
      int freeBlock= bitVector.nextClearBit(0);
      while (freeBlock < totalBlocks) {
          bitVector.set(freeBlock);
          return  freeBlock;

      }
      return -1;
  }
  public void createFile(String path, String filename){
        Directory targetDir=resolvePath(path);

        if(targetDir!=null){
            File newFile=new File(filename,targetDir);

            int blockIndex=findFreeBlock();
            if(blockIndex!=-1){
                newFile.addBlockIndices(blockIndex);
                targetDir.addChildNode(newFile);
                System.out.println("Fajl "+ filename+ " kreiran i postavljen u blok "+blockIndex);


            }else{
                System.err.println("Greska: Nema slobodnog prostora na disku.");
            }

        }


  }

public FSNode resolvePath(String path){
      if(path==null || path.isEmpty()) return null;


      FSNode current=root;

      String[] parts=path.split("/");

      for(String part:parts){
          if(part.isEmpty()) continue;

          if(current instanceof Directory){
              Directory dir=(Directory)current;
              FSNode next=dir.getChild(part);

              if(next==null){
                  return null;

              }
              current=next;

          }else {
              return null;
          }

      }

    return current;

}


}
