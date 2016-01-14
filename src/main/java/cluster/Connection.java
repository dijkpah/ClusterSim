package cluster;

import graph.Node;
import graph.Path;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

import java.util.List;

public class Connection extends Path {

    @NonNull private List<Cable> edges;
    @NonNull private Type type;
    private int networkTraffic;

    public Connection(Type type, Node firstEndPoint, Node secondEndPoint) {
        super(firstEndPoint, secondEndPoint);
        this.type = type;
        this.networkTraffic = 0;
    }

    public void setNetworkTraffic(int networkTraffic) {
        this.networkTraffic = networkTraffic;
    }

    public void addNetworkTraffic(int additionalNetworkTraffic) {
        this.networkTraffic += additionalNetworkTraffic;
    }

    public void applyNetworkTraffic() {
        //TODO apply traffic to underlying cables;
    }

    public enum Type{
        MIGRATION,
        INTERNAL,
        EXTERNAL,
    }
}
