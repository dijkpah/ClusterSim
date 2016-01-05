package cluster;

public class SmallServer extends Server{

    //Intel Xeon E5-2676 v3 has 12 cores and 24 hyperthreads
    //Source: http://www.cpu-world.com/CPUs/Xeon/Intel-Xeon%20E5-2676%20v3.html
    public static final int vCPUs = 12;

    public SmallServer() {
        super(2400);//2.4 GHz
    }
}
