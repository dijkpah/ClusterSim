package cluster;

import graph.Node;
import lombok.Data;
import simulation.SimulationEntity;

@Data
public class Switch extends Node implements SimulationEntity {

    public Switch(int id){
        super(id);
    }

    @Override
    public void tick() {

    }
}
