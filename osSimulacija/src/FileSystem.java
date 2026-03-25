import java.util.*;

public class FileSystem {
    private Map <String,String> files=new HashMap<>();

    public void open(String path){

        System.out.println("Otvaram fajl:  "+path);

    }

    public void read(String path){
        System.out.println("Čitanje fajla: "+path);
    }
    public void write(String path, String content){
        System.out.println("Upisujem u fajl: "+ path + "->" + content );

    }
}
