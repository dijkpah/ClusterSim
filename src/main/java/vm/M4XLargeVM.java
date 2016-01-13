package vm;

public class M4XLargeVM extends VM {

    public static final int vCPUs = 4;
    public static final int maxRAM = 16;
    public static final int maxBandwidth = 750;

    public M4XLargeVM(int id){
        super(id, vCPUs, maxRAM, maxBandwidth);
    }
}
