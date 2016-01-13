package vm;

public class M4LargeVM extends VM {

    public static final int vCPUs = 2;
    public static final int maxRAM = 8;
    public static final int maxBandwidth = 1000;

    public M4LargeVM(int id){
        super(id, vCPUs, maxRAM, maxBandwidth);
    }
}
