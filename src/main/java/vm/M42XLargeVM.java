package vm;

import lombok.ToString;

@ToString(callSuper = true)
public class M42XLargeVM extends VM {

    public static final int vCPUs = 8;
    public static final int maxRAM = 32;
    public static final int maxBandwidth = 1000;
    public static final int size = 8096;

    public M42XLargeVM(int id){
        super(id, vCPUs, maxRAM, maxBandwidth, size);
    }
}
