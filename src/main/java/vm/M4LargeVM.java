package vm;

public class M4LargeVM extends VM {

    public static final int vCPUs = 2;
    public static final int maxRAM = 8;
    public static final int maxBandwidth = 450;
    public static final int size = 8096;

    public M4LargeVM(int id){
        super(id, vCPUs, maxRAM, maxBandwidth, size);
    }

    @Override
    public VM createReservedSpace() {
        VM result = new M4LargeVM(-id);
        result.setState(State.RESERVED);
        return result;
    }
}
