import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemoryManager {
    private RAM ram;
    private List<MemorySegment> segments;
    private Map<Integer, List<Integer>> freeLists;

    public MemoryManager(RAM ram) {
        this.ram = ram;
        this.segments = new ArrayList<>();
        this.freeLists = new HashMap<>();
    }

    public RAM getRam() {
        return ram;
    }

    public void setRam(RAM ram) {
        this.ram = ram;
    }



    private int nextPowerOfTwo(int n){ //ova metoda odredjuje kolicinu memorije koja je potrebna procesu (Buddy sistem)
        if(n == 0) return 1;
        int power =1;
        while(power < n){
            power *=2;

        }
        return power;
    }

    private int findFreeBlock(int n){ //ova metoda treba da pronalazi slobodnu memoriju - jos je u pripremi

    int i =0 ;




    return i ;
    }

    public boolean allocate(PCB p,int size){

    int sizeToAllocate=nextPowerOfTwo(size);




    return true ;
    }

}
