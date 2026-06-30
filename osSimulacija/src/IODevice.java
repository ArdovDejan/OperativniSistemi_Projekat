import java.io.IOException;

public abstract class IODevice {
    protected String name;


    public IODevice(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isBusy(){
        return false;

    }

    public void startOperation(PCB p,IOOperation op) throws IOException {


    }




}
