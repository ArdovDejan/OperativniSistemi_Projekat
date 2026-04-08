public abstract class FSNode {
    public String name;
    private Directory parent;


    public FSNode(String name, Directory parent) {
        this.name = name;
        this.parent = parent;
    }

    public String getName() {
        return name;
    }


    public Directory getParent() {
        return parent;
    }

}
