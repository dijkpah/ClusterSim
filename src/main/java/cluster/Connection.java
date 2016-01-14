package cluster;

import graph.Node;
import graph.Path;
import lombok.NonNull;

import java.util.List;

public class Connection extends Path {

    @NonNull private List<Cable> cables;
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
                for(Cable cable : cables){
                    cable.setMigrationBandwidth(cable.getMigrationBandwidth() + networkTraffic);
                }
            case INTERNAL:
                for(Cable cable : cables){
                    cable.setInternalCommunicationBandwidth(cable.getInternalCommunicationBandwidth() + networkTraffic);
                }
            case EXTERNAL:
                for(Cable cable : cables){
                    cable.setExternalCommunicationBandwidth(cable.getExternalCommunicationBandwidth() + networkTraffic);
                }
        }
    }

    public enum Type{
        MIGRATION,
        INTERNAL,
        EXTERNAL,
    }
}
