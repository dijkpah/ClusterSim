package cluster;

import graph.Edge;
import graph.Node;
import graph.Path;
import lombok.Data;
import lombok.NonNull;
import lombok.ToString;
import simulation.Params;
import simulation.SimulationEntity;

import java.util.logging.Logger;

@Data
@ToString(callSuper = true)
public class Connection extends Path implements SimulationEntity{
    private final static Logger logger = Logger.getLogger(Connection.class.getName());

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
     * Apply any non-applied network traffic to the underlying cables.
     */
    public void applyNetworkTraffic() {
        int toApply = networkTraffic - applied;

        if(toApply == 0) return;

        switch(type){
            case MIGRATION:
                for(Edge edge : getEdges()){
                    Cable cable = (Cable) edge;
                    if(toApply > cable.getCapacity()-cable.getBandwidth()){
                        logger.severe("Cable capacity is not sufficient (migration): toApply=" + toApply + ", cable=" + cable);
                    }
                    cable.setMigrationBandwidth(cable.getMigrationBandwidth() + toApply);
                }
                break;
            case INTERNAL:
                for(Edge edge : getEdges()){
                    Cable cable = (Cable) edge;
                    if(toApply > cable.getCapacity()-cable.getBandwidth()){
                        logger.severe("Cable capacity is not sufficient (internal): toApply=" + toApply + ", cable=" + cable);
                    }
                    cable.setInternalCommunicationBandwidth(cable.getInternalCommunicationBandwidth() + toApply);
                }
                break;
            case EXTERNAL:
                for(Edge edge : getEdges()){
                    Cable cable = (Cable) edge;
                    if(toApply > cable.getCapacity()-cable.getBandwidth()){
                        logger.severe("Cable capacity is not sufficient (external): toApply=" + toApply + ", cable=" + cable);
                    }
                    cable.setExternalCommunicationBandwidth(cable.getExternalCommunicationBandwidth() + toApply);
                }
                break;
        }
        applied = networkTraffic;
    }

    /**
     * Get the remaining (all types) possible bandwidth on this connection, which is the minimum of the possible bandwidths of the cables.
     * @return The remaining possible bandwidth
     */
    public int getRemainingBandwidth(){
        return this.getEdges().stream().mapToInt((Edge edge) -> (edge instanceof Cable) ? ((Cable) edge).getCapacity() - ((Cable) edge).getBandwidth() : Integer.MAX_VALUE).min().getAsInt();
    }

    /**
     * Get the capacity of this connection, which is the minimum capacity of the cables.
     * @return The capacity of this connection.
     */
    public int getCapacity() {
        // For now this is simple, but if there are different speeds possible this should be changed.
        return this.getEdges().stream().mapToInt((Edge edge) -> (edge instanceof Cable) ? ((Cable)edge).getCapacity() : Integer.MAX_VALUE).min().getAsInt();
    }

    public enum Type{
        MIGRATION,
        INTERNAL,
        EXTERNAL,
    }
}
