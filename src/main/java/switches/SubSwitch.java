package switches;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import simulation.SimulationEntity;

@Data
@EqualsAndHashCode(callSuper = true)
public class SubSwitch extends Switch implements SimulationEntity {

    //Cisco Nexus 7000 M1-Series 32-port 10-Gigabit Ethernet I/O module
    @Getter public final int CAPACITY = 10000;
    @Getter public final int BASEPOWER = 600;
    @Getter public final int MAXPOWER = 750;

    public SubSwitch(int id){
        super(id);
    }

}
