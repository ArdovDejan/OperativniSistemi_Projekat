public class OpenFileHandle {
    private File file;
    private int cursor;


    public OpenFileHandle(File file) {
        this.file = file;
        this.cursor = 0;
    }

    public String read(int size){
        String content = file.read();
        int end =Math.min(cursor+size,content.length());

        String data = content.substring(cursor,end);
        cursor = end;
        return data;

    }

    public void write(String data){
        file.append(data);
        cursor += data.length();
    }


}
