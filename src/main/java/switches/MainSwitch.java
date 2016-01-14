package switches;

import lombok.Data;
import lombok.EqualsAndHashCode;
import simulation.SimulationEntity;

@Data
@EqualsAndHashCode(callSuper = true)
public class MainSwitch extends Switch implements SimulationEntity {

    public static final int CAPACITY = 1000;
    public static final int BASEPOWER = 90;

    public MainSwitch(int id){
        super(id);
    }

}
