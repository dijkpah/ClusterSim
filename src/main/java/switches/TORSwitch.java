package switches;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import simulation.SimulationEntity;

@Data
@EqualsAndHashCode(callSuper = true)
public class TORSwitch extends Switch implements SimulationEntity {

    //Cisco Catalyst 4900M TOR switch
    @Getter public final int CAPACITY = 10000;
    @Getter public final int BASEPOWER = 300;
    @Getter public final int MAXPOWER = 446;

    public TORSwitch(int id){
        super(id);
    }

}
