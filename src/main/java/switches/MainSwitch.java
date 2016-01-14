package switches;

import lombok.Data;
import simulation.SimulationEntity;

@Data
public class MainSwitch extends Switch implements SimulationEntity {

    public static final int CAPACITY = 1000;
    public static final int BASEPOWER = 90;
    public static final int MAXPOWER = 120;

    public MainSwitch(int id){
        super(id);
    }

}
