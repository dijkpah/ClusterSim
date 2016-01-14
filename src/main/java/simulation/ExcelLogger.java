package simulation;

import lombok.Data;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

@Data
public class ExcelLogger {

    private List<Integer> serverConsumption = new ArrayList<Integer>();
    private List<Integer> externalNetworkConsumption = new ArrayList<Integer>();
    private List<Integer> internalNetworkConsumption = new ArrayList<Integer>();
    private List<Integer> migrationNetworkConsumption = new ArrayList<Integer>();
    private List<Integer> baseSwitchConsumption = new ArrayList<Integer>();
    private static final String SEPARATOR = ",";

    public void addTick(int server, int switchBase, int external, int internal, int migration){
        this.serverConsumption.add(server);
        this.baseSwitchConsumption.add(switchBase);
        this.externalNetworkConsumption.add(external);
        this.internalNetworkConsumption.add(internal);
        this.migrationNetworkConsumption.add(migration);
    }

    public void makeGraph(){
        int ticks = serverConsumption.size();

        //Create file
        PrintWriter writer = null;
        try {
            writer = new PrintWriter("simulation.log", "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //Add headers
        writer.println("Servers"+SEPARATOR);
        writer.println("Switch Base"+SEPARATOR);
        writer.println("External Traffic"+SEPARATOR);
        writer.println("Internal Traffic"+SEPARATOR);
        writer.println("Migrations"+SEPARATOR);

        //Add ticks
        for(int i=0;i<ticks;i++){
            writer.println(serverConsumption.get(i)+SEPARATOR);
            writer.println(baseSwitchConsumption.get(i)+SEPARATOR);
            writer.println(externalNetworkConsumption.get(i)+SEPARATOR);
            writer.println(internalNetworkConsumption.get(i)+SEPARATOR);
            writer.println(migrationNetworkConsumption.get(i)+SEPARATOR);
        }
        writer.close();
    }

   public static void main(String[] args){
       ExcelLogger logger = new ExcelLogger();
       logger.makeGraph();
   }

}