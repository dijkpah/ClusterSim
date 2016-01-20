package switches;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import simulation.SimulationEntity;

@Data
@EqualsAndHashCode(callSuper = true)
public class MainSwitch extends Switch implements SimulationEntity {

    //Cisco Nexus 7000 M2-Series 6-Port 40 Gigabit Ethernet Module with XL Option
    @Getter public final int CAPACITY = 40000;
    @Getter public final int BASEPOWER = 700;
    @Getter public final int MAXPOWER = 800;

    public MainSwitch(int id){
        super(id);
    }

}
