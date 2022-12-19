package adg.plugin.viz;

import com.google.gson.JsonObject;
import graph.Graph;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.Plot;
import tech.tablesaw.plotly.api.BubblePlot;
import tech.tablesaw.plotly.api.ScatterPlot;
import tech.tablesaw.plotly.components.Figure;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;

public class Visualizer {


    private static Visualizer instance = new Visualizer();

    public static Visualizer getInstance() { return instance; }








    public void open_visualizer(){

        // --> 1. Get visualization data from ADG
        Graph graph = Graph.getInstance();
        if(!graph.is_built){
            JOptionPane.showMessageDialog(null, "ERROR: graph not built");
            return;
        }
        ArrayList<JsonObject> designs = graph.evaluated_designs;
        if(designs.isEmpty()){
            JOptionPane.showMessageDialog(null, "ERROR: no evaluated designs exist");
            return;
        }

        // --> 2. Create table
        double[] values1 = {};
        double[] values2 = {};
        double[] values3 = {};
        String[] values4 = {};
        DoubleColumn column1 = DoubleColumn.create("Science", values1);
        DoubleColumn column2 = DoubleColumn.create("Cost", values2);
        DoubleColumn column3 = DoubleColumn.create("Data Continuity", values3);
        StringColumn column4 = StringColumn.create("Design Info", values4);
        for(JsonObject design: designs){
            column1.append(design.get("science").getAsDouble());
            column2.append(design.get("cost").getAsDouble());
            column3.append(design.get("data_continuity").getAsDouble());
            column4.append(design.get("design_info").getAsString());
        }
        Table data_table = Table.create("name", column1, column2);


//        // --> 3. Visualize table
//        Figure figure =
//                BubblePlot.create(
//                        "Design Space",
//                        data_table, // table name
//                        "Data Continuity", // x variable column name
//                        "Cost", // y variable column name
//                        "Science" // bubble size
//                );

        Figure figure = ScatterPlot.create("Design Space", data_table, "Cost", "Science");


        File fig_file = new File("C:\\Users\\apaza\\repos\\seakers\\adgplugin\\adg\\figures\\fig.html");
        Plot.show(figure, fig_file);
    }


    public void testVisualization(){
        String data_path = "C:\\Users\\apaza\\repos\\seakers\\adgplugin\\adg\\data\\test_wines.csv";
        Table wines = Table.read().csv(data_path);
        Table champagne =
                wines.where(
                        wines
                                .stringColumn("wine type")
                                .isEqualTo("Champagne & Sparkling")
                                .and(wines.stringColumn("region").isEqualTo("California")));

        Figure figure =
                BubblePlot.create(
                        "Design Space",
                        champagne, // table name
                        "Science", // x variable column name
                        "Cost", // y variable column name
                        "Data Continuity" // bubble size
                );


        File fig_file = new File("C:\\Users\\apaza\\repos\\seakers\\adgplugin\\adg\\figures\\fig.html");

        Plot.show(figure, fig_file);
    }




}
