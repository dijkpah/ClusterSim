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
import java.util.stream.Collectors;

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
    /** The list of counts of servers which are in a specific state */
    private List<Map<Server.State, Integer>> serverStates = new ArrayList<>(Params.TICK_COUNT);


    public void tick(ClusterSimulation simulation) {
        int tickServerConsumption = 0;
        int tickBaseSwitchConsumption = 0;
        int tickExternalNetworkConsumption = 0;
        int tickInternalNetworkConsumption = 0;
        int tickMigrationNetworkConsumption = 0;
        int tickExternalNetworkUsage = 0;
        int tickInternalNetworkUsage = 0;
        int tickMigrationNetworkUsage = 0;
        Map<Server.State, Integer> tickServerStates = new HashMap<>();


        for (Node node : simulation.getCluster().getNodes()) {
            if (node instanceof Server) {
                Server server = (Server) node;
                tickServerConsumption += server.getPowerUsage();
                if(!tickServerStates.containsKey(server.getState())){
                    tickServerStates.put(server.getState(), 1);
                }else{
                    tickServerStates.put(server.getState(), tickServerStates.get(server.getState()) + 1);
                }
            } else if (node instanceof Switch) {
                Switch aSwitch = (Switch) node;
                tickBaseSwitchConsumption += aSwitch.getBaseConsumption();
                tickExternalNetworkConsumption += aSwitch.getExternalCommunicationConsumption();
                tickInternalNetworkConsumption += aSwitch.getInternalCommunicationConsumption();
                tickMigrationNetworkConsumption += aSwitch.getMigrationCommunicationConsumption();
            } else if (!(node instanceof World)) {
                new Exception("unknown Node type: " + node.getClass().getName()).printStackTrace();
            }
        }
        for(Cable cable : simulation.getCluster().getEdges()){
            tickExternalNetworkUsage += cable.getExternalCommunicationBandwidth();
            tickInternalNetworkUsage += cable.getInternalCommunicationBandwidth();
            tickMigrationNetworkUsage += cable.getMigrationBandwidth();
        }


        this.serverConsumption.add(tickServerConsumption);
        this.baseSwitchConsumption.add(tickBaseSwitchConsumption);
        this.externalNetworkConsumption.add(tickExternalNetworkConsumption);
        this.internalNetworkConsumption.add(tickInternalNetworkConsumption);
        this.migrationNetworkConsumption.add(tickMigrationNetworkConsumption);
        this.totalMigrations.add(simulation.getTotalMigrations());
        this.remainingMigrations.add(simulation.getRemainingMigrations());
        this.externalNetworkUsage.add(tickExternalNetworkUsage);
        this.internalNetworkUsage.add(tickInternalNetworkUsage);
        this.migrationNetworkUsage.add(tickMigrationNetworkUsage);
        this.serverStates.add(tickServerStates);
    }

    public void makeGraph(String outputFileName, Map<String, String> params) {
        int ticks = serverConsumption.size();

        //Create file
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(outputFileName, "UTF-8");
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
        for(Server.State state : Server.State.values()){
            this.printStats(writer, String.format("%1$-44s", "Server state - " + state), serverStates.stream().map(
                item -> item.containsKey(state) ? item.get(state) : 0
            ).collect(Collectors.toList()));
        }


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
}
