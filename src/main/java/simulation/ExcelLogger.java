package simulation;

import cluster.Cable;
import cluster.Server;
import cluster.World;
import graph.Node;
import lombok.Data;
import switches.Switch;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class ExcelLogger {


    /** Consumption in Watt for the servers */
    private List<Integer> serverConsumption = new ArrayList<>(Params.TICK_COUNT);
    /** Consumption in Watt for external communication */
    private List<Integer> externalNetworkConsumption = new ArrayList<>(Params.TICK_COUNT);
    /** Consumption in Watt for internal communication */
    private List<Integer> internalNetworkConsumption = new ArrayList<>(Params.TICK_COUNT);
    /** Consumption in Watt for migration communication */
    private List<Integer> migrationNetworkConsumption = new ArrayList<>(Params.TICK_COUNT);
    /** Consumption in Watt for the base energy of switches */
    private List<Integer> baseSwitchConsumption = new ArrayList<>(Params.TICK_COUNT);

    /** The total number of migrations running */
    private List<Integer> totalMigrations = new ArrayList<>(Params.TICK_COUNT);
    /** The number of remaining migrations at the end of the tick */
    private List<Integer> remainingMigrations = new ArrayList<>(Params.TICK_COUNT);
    /** The network traffic in Mb/s for external traffic */
    private List<Integer> externalNetworkUsage = new ArrayList<>(Params.TICK_COUNT);
    /** The network traffic in Mb/s for internal traffic */
    private List<Integer> internalNetworkUsage = new ArrayList<>(Params.TICK_COUNT);
    /** The network traffic in Mb/s for migration traffic */
    private List<Integer> migrationNetworkUsage = new ArrayList<>(Params.TICK_COUNT);


    public void tick(ClusterSimulation simulation) {
        int serverConsumption = 0;
        int baseSwitchConsumption = 0;
        int externalNetworkConsumption = 0;
        int internalNetworkConsumption = 0;
        int migrationNetworkConsumption = 0;

        int externalNetworkMbps = 0;
        int internalNetworkMbps = 0;
        int migrationNetworkMbps = 0;


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
        for(Cable cable : simulation.getCluster().getEdges()){
            externalNetworkMbps += cable.getExternalCommunicationBandwidth();
            internalNetworkMbps += cable.getInternalCommunicationBandwidth();
            migrationNetworkMbps += cable.getMigrationBandwidth();
        }

        this.addTick(
                serverConsumption,
                baseSwitchConsumption,
                externalNetworkConsumption,
                internalNetworkConsumption,
                migrationNetworkConsumption,
                simulation.getTotalMigrations(),
                simulation.getRemainingMigrations(),
                externalNetworkMbps,
                internalNetworkMbps,
                migrationNetworkMbps
        );
    }

    public void addTick(int server, int switchBase, int external, int internal, int migration, int totalMigrations, int remainingMigrations, int externalNetworkMbps, int internalNetworkMbps, int migrationNetworkMbps) {
        this.serverConsumption.add(server);
        this.baseSwitchConsumption.add(switchBase);
        this.externalNetworkConsumption.add(external);
        this.internalNetworkConsumption.add(internal);
        this.migrationNetworkConsumption.add(migration);
        this.totalMigrations.add(totalMigrations);
        this.remainingMigrations.add(remainingMigrations);
        this.externalNetworkUsage.add(externalNetworkMbps);
        this.internalNetworkUsage.add(internalNetworkMbps);
        this.migrationNetworkUsage.add(migrationNetworkMbps);
    }

    public void makeGraph(Map<String, String> params) {
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

        this.printParams(writer, params);
        this.printStats(writer, "Power Servers (W)                           ", serverConsumption);
        this.printStats(writer, "Power Switch - Base (W)                     ", baseSwitchConsumption);
        this.printStats(writer, "Power Switch - External Traffic (W)         ", externalNetworkConsumption);
        this.printStats(writer, "Power Switch - Internal Traffic (W)         ", internalNetworkConsumption);
        this.printStats(writer, "Power Switch - Migrations (W)               ", migrationNetworkConsumption);
        this.printStats(writer, "Network Bandwidth - External Traffic (Mbps) ", externalNetworkUsage);
        this.printStats(writer, "Network Bandwidth - Internal Traffic (Mbps) ", internalNetworkUsage);
        this.printStats(writer, "Network Bandwidth - Migrations (Mbps)       ", migrationNetworkUsage);
        this.printStats(writer, "Migrations                                  ", totalMigrations);
        this.printStats(writer, "Unfinished migrations                       ", remainingMigrations);

        writer.close();
    }

    private void printParams(PrintWriter writer, Map<String, String> params){
        for(Map.Entry<String, String> param : params.entrySet()){
            writer.print("Param - "+param.getKey()+ Params.OUTPUT_SEPARATOR);
            writer.print(param.getValue()+ Params.OUTPUT_SEPARATOR);
            writer.println();
        }
    }

    private void printStats(PrintWriter writer, String name, List<Integer> values) {
        writer.print(name + Params.OUTPUT_SEPARATOR);
        for (Integer value : values) {
            writer.print(value + Params.OUTPUT_SEPARATOR);
        }
        writer.println();
    }

    public static void main(String[] args) {
        ExcelLogger logger = new ExcelLogger();
        logger.makeGraph(new HashMap<>());
    }

}
