package switches;

import cluster.Cable;
import graph.Edge;
import graph.Node;
import lombok.Data;
import simulation.SimulationEntity;

@Data
public abstract class Switch extends Node implements SimulationEntity {

    public static final int CAPACITY = 0;
    public static final int BASEPOWER = 0;

    public Switch(int id){
        super(id);
    }

    @Override
    public void tick() {

    }

    public int getBaseConsumption(){
        return BASEPOWER;
    }

    public int getExternalCommunicationConsumption(){
        int result = 0;
        for(Edge edge: getEdges()){
            if(edge instanceof Cable){

            }
        }
        return result;
    }

}
