package simulation;

import cluster.Cable;
import cluster.Cluster;
import cluster.Server;
import cluster.World;
import graph.Node;
import lombok.Data;
import switches.Switch;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

@Data
public class ExcelLogger {

    private List<Integer> serverConsumption = new ArrayList<>(Params.TICK_COUNT);
    private List<Integer> externalNetworkConsumption = new ArrayList<>(Params.TICK_COUNT);
    private List<Integer> internalNetworkConsumption = new ArrayList<>(Params.TICK_COUNT);
    private List<Integer> migrationNetworkConsumption = new ArrayList<>(Params.TICK_COUNT);
    private List<Integer> baseSwitchConsumption = new ArrayList<>(Params.TICK_COUNT);
    private static final String SEPARATOR = "\t";
    private List<Integer> totalMigrations = new ArrayList<>(Params.TICK_COUNT);
    private List<Integer> remainingMigrations = new ArrayList<>(Params.TICK_COUNT);


    public void tick(ClusterSimulation simulation) {
        int serverConsumption = 0;
        int baseSwitchConsumption = 0;
        int externalNetworkConsumption = 0;
        int internalNetworkConsumption = 0;
        int migrationNetworkConsumption = 0;

        for (Node node : simulation.getCluster().getNodes()) {
            if (node instanceof Server) {
                serverConsumption += ((Server) node).getPowerUsage();
            } else if (node instanceof Switch) {
                Switch aSwitch = (Switch) node;
                baseSwitchConsumption += aSwitch.getBaseConsumption();
                externalNetworkConsumption += aSwitch.getExternalCommunicationConsumption();
                internalNetworkConsumption += aSwitch.getInternalCommunicationConsumption();
                migrationNetworkConsumption += aSwitch.getMigrationCommunicationConsumption();
            } else if (!(node instanceof World)) {
                new Exception("unknown Node type: " + node.getClass().getName()).printStackTrace();
            }
        }
        this.addTick(serverConsumption, baseSwitchConsumption, externalNetworkConsumption, internalNetworkConsumption, migrationNetworkConsumption, simulation.getTotalMigrations(), simulation.getRemainingMigrations());
    }

    public void addTick(int server, int switchBase, int external, int internal, int migration, int totalMigrations, int remainingMigrations) {
        this.serverConsumption.add(server);
        this.baseSwitchConsumption.add(switchBase);
        this.externalNetworkConsumption.add(external);
        this.internalNetworkConsumption.add(internal);
        this.migrationNetworkConsumption.add(migration);
        this.totalMigrations.add(totalMigrations);
        this.remainingMigrations.add(remainingMigrations);
    }

    public void makeGraph() {
        int ticks = serverConsumption.size();

        //Create file
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(Params.OUTPUT_FILE, "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        this.printStats(writer, "Power Servers (W)                  ", serverConsumption);
        this.printStats(writer, "Power Switch - Base (W)            ", baseSwitchConsumption);
        this.printStats(writer, "Power Switch - External Traffic (W)", externalNetworkConsumption);
        this.printStats(writer, "Power Switch - Internal Traffic (W)", internalNetworkConsumption);
        this.printStats(writer, "Power Switch - Migrations (W)      ", migrationNetworkConsumption);
        this.printStats(writer, "Migrations                         ", totalMigrations);
        this.printStats(writer, "Unfinished migrations              ", remainingMigrations);

        writer.close();
    }

    private void printStats(PrintWriter writer, String name, List<Integer> values) {
        writer.print(name + SEPARATOR);
        for(Integer value : values){
            writer.print(value + SEPARATOR);
        }
        writer.println();
    }

    public static void main(String[] args) {
        ExcelLogger logger = new ExcelLogger();
        logger.makeGraph();
    }

}
