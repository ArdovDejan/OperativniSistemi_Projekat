import java.util.List;
import java.util.Map;

public class MemoryManager {
    private RAM ram;
    public List<MemorySegment> segments;
    private Map<Integer, List<Integer>> freeLists;

    private int nextPowerOfTwo(int n){ //ova metoda odredjuje kolicinu memorije koja je potrebna procesu (Buddy sistem)
        if(n=0) return 1;
        int power =1;
        while(power < n){
            power *=2;

        }
        return power;
    }

    private int findFreeBlock(int n){ //ova metoda treba da pronalazi slobodnu memoriju - jos je u pripremi







    }

    public boolean allocate(PCB p,int size){

    int sizeToAllocate=nextPowerOfTwo(size);





    }

}
