package switches;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import simulation.SimulationEntity;

@Data
@EqualsAndHashCode(callSuper = true)
public class TORSwitch extends Switch implements SimulationEntity {

    //Cisco Catalyst 4948 TOR switch 10 Gigabit
    @Getter public final int CAPACITY = 10000;
    @Getter public final int BASEPOWER = 176;
    @Getter public final int MAXPOWER = 300;

    public TORSwitch(int id){
        super(id);
    }

}
