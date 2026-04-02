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

    public void isBusy(){}

    public void startOperation(PCB p,IOOperation op){


    }




}
