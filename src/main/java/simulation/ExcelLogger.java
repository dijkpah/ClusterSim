package simulation;

import lombok.Data;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

@Data
public class ExcelLogger {

    private List<Integer> requestConsumption = new ArrayList<Integer>();
    private List<Integer> migrationConsumption = new ArrayList<Integer>();
    private List<Integer> groupedConsumption = new ArrayList<Integer>();
    private List<Integer> switchConsumption = new ArrayList<Integer>();
    private List<Integer> serverConsumption = new ArrayList<Integer>();
    private static final String SEPARATOR = ",";

    public void makeGraphs(){
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
        writer.println("Requests"+SEPARATOR);
        writer.println("Migrations"+SEPARATOR);
        writer.println("Communication"+SEPARATOR);
        writer.println("Switches"+SEPARATOR);
        writer.println("Servers"+SEPARATOR);

        //Add ticks
        for(int i=0;i<ticks;i++){
            writer.println(requestConsumption.get(i)+SEPARATOR);
            writer.println(migrationConsumption.get(i)+SEPARATOR);
            writer.println(groupedConsumption.get(i)+SEPARATOR);
            writer.println(switchConsumption.get(i)+SEPARATOR);
            writer.println(serverConsumption.get(i)+SEPARATOR);
        }
        writer.close();
    }

   public static void main(String[] args){
       ExcelLogger logger = new ExcelLogger();
       logger.makeGraphs();
   }

}
