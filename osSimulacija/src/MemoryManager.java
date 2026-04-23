import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;




public class MemoryManager {
    private RAM ram;
    private List<MemorySegment> segments;
    private Map<Integer, List<Integer>> freeLists;
    private int totalRamSize;


    public MemoryManager(RAM ram) {
        this.ram = ram;
        this.segments = new ArrayList<>();
        this.freeLists = new HashMap<>();
        this.totalRamSize = ram.getSize();


        List<Integer> initialList = new ArrayList<>();
        initialList.add(0);
        freeLists.put(totalRamSize, initialList);

    }

    public RAM getRam() {
        return ram;
    }

    public void setRam(RAM ram) {
        this.ram = ram;
    }


    private int nextPowerOfTwo(int n) { //ova metoda odredjuje kolicinu memorije koja je potrebna procesu (Buddy sistem)
        if (n == 0) return 1;
        int power = 1;
        while (power < n) {
            power *= 2;

        }
        return power;
    }

    private int findFreeBlock(int size) { //ova metoda treba da pronalazi slobodnu memoriju - jos je u pripremi

        List<Integer> adrese = freeLists.get(size);

        if (adrese != null && !adrese.isEmpty()) {

            return adrese.remove(0);
        }


        if (size < totalRamSize) {
            int biggerBlockAdress = findFreeBlock(size * 2);

            if (biggerBlockAdress != -1) {
                int buddyAdress = biggerBlockAdress + size;

                freeLists.computeIfAbsent(size, k -> new ArrayList<>()).add(buddyAdress);
                return biggerBlockAdress;

            }

        }


        return -1;
    }

    public boolean allocate(PCB p, int size) {

        int sizeToAllocate = nextPowerOfTwo(size);
        int baseAdr = findFreeBlock(sizeToAllocate);
        if (baseAdr != -1) {
            MemorySegment segment = new MemorySegment();
            segment.setOwner(p);
            segment.setBase(baseAdr);
            segment.setLimit(sizeToAllocate);
            segments.add(segment);

            p.setBaseAddress(baseAdr);
            p.setLimit(sizeToAllocate);

            return true;

        }
        return false;


    }

    public void free(PCB p) {

        MemorySegment target = null;

        for (MemorySegment s : segments) {

            if (s.getOwner().equals(p)) {
                target = s;
                break;

            }

        }

        if (target != null) {
            segments.remove(target);
            coalesce(target.getBase(),target.getLimit());

        }



    }


    private void coalesce(int address, int size) {

        if(size>=totalRamSize){
            freeLists.computeIfAbsent(size, k -> new ArrayList<>()).add(address);
            return ;

        }


    }


}
