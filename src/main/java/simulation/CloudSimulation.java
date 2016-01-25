package simulation;

import cluster.*;
import graph.Node;
import lombok.Data;
import migration.MigrationPolicy;
import switches.HubSwitch;
import switches.MainSwitch;
import switches.TORSwitch;
import vm.M42XLargeVM;
import vm.M4LargeVM;
import vm.M4XLargeVM;
import vm.VM;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * A simulation of a full cloud service
 */
@Data
public class CloudSimulation extends ClusterSimulation{

    private static final int nthServerSleeps = 5;//every nth server is put to sleep
    private static final int amountOfRacks = 4;
    private static final int serversPerRack = 10;
    private static final double fractionInGroup = 0.2;
    private static final int groupSize = 2;


    public CloudSimulation(Cluster<Node, Cable> cluster, MigrationPolicy migrationPolicy) {
        super(cluster, migrationPolicy);
    }

    /**
     * Creates a simple cluster with one switch, two servers and one VM on both server.
     */
    public static Cluster<Node, Cable> simpleCloud() {



        List<Node> nodes = new ArrayList<Node>();
        List<Cable> edges = new ArrayList<Cable>();

        // Create world
        World world = new World(0);
        nodes.add(world);

        // Create primary switches
        MainSwitch prim1 = new MainSwitch(1);
        MainSwitch prim2 = new MainSwitch(2);
        nodes.add(prim1);
        nodes.add(prim2);

        // Create Hub switches
        HubSwitch hub1 = new HubSwitch(1);
        HubSwitch hub2 = new HubSwitch(2);
        HubSwitch hub3 = new HubSwitch(3);
        HubSwitch hub4 = new HubSwitch(4);
        nodes.add(hub1);
        nodes.add(hub2);
        nodes.add(hub3);
        nodes.add(hub4);

        // Create TOR switches
        TORSwitch[] tors = new TORSwitch[amountOfRacks];
        TORSwitch tor1 = new TORSwitch(1);
        TORSwitch tor2 = new TORSwitch(2);
        TORSwitch tor3 = new TORSwitch(3);
        TORSwitch tor4 = new TORSwitch(4);
        tors[0] = tor1;
        tors[1] = tor2;
        tors[2] = tor3;
        tors[3] = tor4;
        nodes.add(tor1);
        nodes.add(tor2);
        nodes.add(tor3);
        nodes.add(tor4);

        // Create servers
        List<Server>[] racks = new ArrayList[amountOfRacks];
        for(int i =0; i<amountOfRacks;i++){
            racks[i] = new ArrayList<>();
            for(int j=0;j<serversPerRack;j++){
                Server server = new Server(100+i*100+j);
                racks[i].add(server);
                nodes.add(server); //add server to graph
                edges.add(ClusterFactory.createCable(server, tors[i])); //add edge to this rack's TOR switch
            }
        }

        // Create edges
        // Link world to primary switches
        edges.add(ClusterFactory.createCable(prim1, world));
        edges.add(ClusterFactory.createCable(prim2, world));
        // Link hubs to primary switches
        edges.add(ClusterFactory.createCable(hub1, prim1));
        edges.add(ClusterFactory.createCable(hub2, prim1));
        edges.add(ClusterFactory.createCable(hub3, prim1));
        edges.add(ClusterFactory.createCable(hub4, prim1));
        edges.add(ClusterFactory.createCable(hub1, prim2));
        edges.add(ClusterFactory.createCable(hub2, prim2));
        edges.add(ClusterFactory.createCable(hub3, prim2));
        edges.add(ClusterFactory.createCable(hub4, prim2));
        // Link hubs to TORs
        edges.add(ClusterFactory.createCable(hub1, tor1));
        edges.add(ClusterFactory.createCable(hub2, tor1));
        edges.add(ClusterFactory.createCable(hub1, tor2));
        edges.add(ClusterFactory.createCable(hub2, tor2));
        edges.add(ClusterFactory.createCable(hub3, tor3));
        edges.add(ClusterFactory.createCable(hub4, tor3));
        edges.add(ClusterFactory.createCable(hub3, tor4));
        edges.add(ClusterFactory.createCable(hub4, tor4));



        List<Integer>[] combinations = new ArrayList[5];
        combinations[0] = IntStream.of(new int[]{4, 8}).boxed().collect(Collectors.toList());
        combinations[1] = IntStream.of(new int[]{2,2,8}).boxed().collect(Collectors.toList());
        combinations[2] = IntStream.of(new int[]{4,4,4}).boxed().collect(Collectors.toList());
        combinations[3] = IntStream.of(new int[]{2,2,4,4}).boxed().collect(Collectors.toList());
        combinations[4] = IntStream.of(new int[]{2,2,2,2,4}).boxed().collect(Collectors.toList());
        //combinations[5] = Arrays.asList(new Integer[]{2,2,2,2,2}); with this the chance for a core to be used by a small vm would be too big

        //Create VMs
        int counter = 0;
        int createdVMs = 0;
        List<VM> vms = new ArrayList<>();

        for(int i= 0;i<amountOfRacks;i++){
            for(int j=0;j<serversPerRack;j++){
                int index = i*serversPerRack+j;
                Server server = racks[i].get(j);
                if(index%nthServerSleeps==0){
                    server.setState(Server.State.SLEEPING);
                }else{
                    int combinationsIndex = counter%combinations.length;
                    for(int k:combinations[combinationsIndex]){
                        switch(k){
                            case 2:
                                VM smallVm = new M4LargeVM(createdVMs);
                                vms.add(smallVm);
                                server.addVM(smallVm);
                                createdVMs++;
                                break;
                            case 4:
                                VM medVM = new M4XLargeVM(createdVMs);
                                vms.add(medVM);
                                server.addVM(medVM);
                                createdVMs++;
                                break;
                            case 8:
                                VM largeVM = new M42XLargeVM(createdVMs);
                                vms.add(largeVM);
                                server.addVM(largeVM);
                                createdVMs++;
                                break;
                            default:
                                break;
                        }
                    }
                    counter++;
                }
            }
        }
       // System.out.println("[INFO] Created VMs: "+createdVMs);

        //Create seeded RNG
        Random rng = new Random(0L);

        // Vms are linked
        for(int i=0;i<createdVMs*fractionInGroup;i+=groupSize){
            //create new group
            List<VM> group = new ArrayList<>();
            for(int j=0;j<groupSize;j++){
                //add a VM to group
                int index = rng.nextInt(vms.size());
                VM selectedVM = vms.get(index);
                group.add(selectedVM);

                //remove VM from vms
                vms.remove(index);

                //connect vms in group to each other
                for(VM vm : vms){
                    selectedVM.connectToVM(vm);
                }
            }
        }

        // Return the cluster
        return new Cluster<Node, Cable>("Simple cloud", world, nodes, edges);
    }

    public static void main(String[] args) {
        // Build the cluster
        Cluster<Node, Cable> cluster = simpleCloud();

        // Create the simulation
        CloudSimulation simulation = new CloudSimulation(cluster, Params.MIGRATION_POLICY);

        // Setup logging
        simulation.setupLogging();

        //Set time
        int ticks = Params.TICK_COUNT;


        //List parameters
        Map<String, String> params = simulation.params();
        params.put("Fraction of servers in sleep mode", ""+(1/nthServerSleeps));
        params.put("Amount of server racks with TOR switches", ""+amountOfRacks);
        params.put("Amount of servers in a server rack", ""+serversPerRack);
        params.put("Fraction of VMs that belong to a VM group", ""+fractionInGroup);
        params.put("Size of VM groups", ""+groupSize);

        // Start
        simulation.run(ticks);

        //Create Graph
        simulation.getExcelLogger().makeGraph(simulation.getOutputFileName(), params);
    }
}
