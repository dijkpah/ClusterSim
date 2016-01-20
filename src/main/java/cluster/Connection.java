package cluster;

import graph.Edge;
import graph.Node;
import graph.Path;
import lombok.Data;
import lombok.NonNull;
import simulation.Params;

@Data
public class Connection extends Path{

    @NonNull private Type type;
    private int networkTraffic;

    public Connection(Type type, Node firstEndPoint, Node secondEndPoint) {
        super(firstEndPoint, secondEndPoint);
        this.type = type;
        this.networkTraffic = 0;
    }

    public void addNetworkTraffic(int additionalNetworkTraffic) {
        this.networkTraffic += additionalNetworkTraffic;
    }

    public void applyNetworkTraffic() {
        switch(type){
            case MIGRATION:
                for(Edge cable : getEdges()){
                    ((Cable)cable).setMigrationBandwidth(((Cable)cable).getMigrationBandwidth() + networkTraffic);
                }
            case INTERNAL:
                for(Edge cable : getEdges()){
                    ((Cable)cable).setInternalCommunicationBandwidth(((Cable)cable).getInternalCommunicationBandwidth() + networkTraffic);
                }
            case EXTERNAL:
                for(Edge cable : getEdges()){
                    ((Cable)cable).setExternalCommunicationBandwidth(((Cable)cable).getExternalCommunicationBandwidth() + networkTraffic);
                }
        }
    }

    public int getBandwidth() {
        // For now this is simple, but if there are different speeds possible this should be changed.
        return Params.CABLE_CAPACITY;
    }

    public enum Type{
        MIGRATION,
        INTERNAL,
        EXTERNAL,
    }
}
