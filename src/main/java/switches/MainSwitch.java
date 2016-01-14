package switches;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import simulation.SimulationEntity;

@Data
@EqualsAndHashCode(callSuper = true)
public class MainSwitch extends Switch implements SimulationEntity {

    @Getter public final int CAPACITY = 1000;
    @Getter public final int BASEPOWER = 90;
    @Getter public final int MAXPOWER = 120;

    public MainSwitch(int id){
        super(id);
    }

}
