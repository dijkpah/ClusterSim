package vm;

public class M42XLargeVM extends VM {

    public static final int vCPUs = 8;
    public static final int maxRAM = 32;
    public static final int maxBandwidth = 1000;

    public M42XLargeVM(int id){
        super(id, vCPUs, maxRAM, maxBandwidth);
    }
}
