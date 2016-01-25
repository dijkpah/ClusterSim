package cluster;

import graph.Edge;
import graph.Node;
import graph.Path;
import lombok.Data;
import lombok.NonNull;
import lombok.ToString;
import simulation.Params;
import simulation.SimulationEntity;

@Data
@ToString(callSuper = true)
public class Connection extends Path implements SimulationEntity{

    @NonNull private Type type;
    private int networkTraffic;
    private int applied;

    public Connection(Type type, Cluster graph, Node firstEndPoint, Node secondEndPoint) {
        super(graph, firstEndPoint, secondEndPoint);
        this.type = type;
        this.networkTraffic = 0;
        this.applied = 0;
    }

    /**
     * Reset the connection.
     */
    public void reset(){
        this.networkTraffic = 0;
        this.applied = 0;
    }

    @Override
    public void tick() {
        this.applyNetworkTraffic();
    }

    public void addNetworkTraffic(int additionalNetworkTraffic) {
        this.networkTraffic += additionalNetworkTraffic;
    }

    /**
     * Apply any non applied network traffic to the underlying cables.
     */
    public void applyNetworkTraffic() {
        switch(type){
            case MIGRATION:
                for(Edge cable : getEdges()){
                    ((Cable)cable).setMigrationBandwidth(((Cable)cable).getMigrationBandwidth() + networkTraffic - applied);
                }
                break;
            case INTERNAL:
                for(Edge cable : getEdges()){
                    ((Cable)cable).setInternalCommunicationBandwidth(((Cable)cable).getInternalCommunicationBandwidth() + networkTraffic - applied);
                }
                break;
            case EXTERNAL:
                for(Edge cable : getEdges()){
                    ((Cable)cable).setExternalCommunicationBandwidth(((Cable)cable).getExternalCommunicationBandwidth() + networkTraffic - applied);
                }
                break;
        }
        applied = networkTraffic;
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
