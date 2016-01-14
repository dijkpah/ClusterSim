package cluster;

import graph.Edge;
import graph.Node;
import graph.Path;
import lombok.NonNull;

public class Connection extends Path{

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
        switch(type){
            case MIGRATION:
                for(Edge cable : edges){
                    ((Cable)cable).setMigrationBandwidth(((Cable)cable).getMigrationBandwidth() + networkTraffic);
                }
            case INTERNAL:
                for(Edge cable : edges){
                    ((Cable)cable).setInternalCommunicationBandwidth(((Cable)cable).getInternalCommunicationBandwidth() + networkTraffic);
                }
            case EXTERNAL:
                for(Edge cable : edges){
                    ((Cable)cable).setExternalCommunicationBandwidth(((Cable)cable).getExternalCommunicationBandwidth() + networkTraffic);
                }
        }
    }

    public enum Type{
        MIGRATION,
        INTERNAL,
        EXTERNAL,
    }
}
