import java.util.*;

public class IOMenager {
    private Map<String, IODevice> devices=new HashMap<>();



    public void requestIO(String deviceName,IODevice device) {
        IODevice dev = devices.get(deviceName);
        if( dev == null ) {
            dev.startOperation(deviceName);

        }


    }
}
