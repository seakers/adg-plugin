package adg.plugin.viz;

import adg.plugin.ADG_Diagram;
import adg.plugin.ADG_Element;
import adg.plugin.ADG_Plugin;
import adg.plugin.packages.DiagramsPackage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.PackageableElement;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Profile;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;
import graph.Graph;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.Plot;
import tech.tablesaw.plotly.api.BubblePlot;
import tech.tablesaw.plotly.api.ScatterPlot;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.components.Marker;
import tech.tablesaw.plotly.components.Symbol;
import tech.tablesaw.plotly.traces.BarTrace;
import tech.tablesaw.plotly.traces.HistogramTrace;
import tech.tablesaw.plotly.traces.ScatterTrace;
import tech.tablesaw.plotly.traces.Trace;

import javax.swing.*;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

import static tech.tablesaw.aggregate.AggregateFunctions.sum;



public class Visualizer {


    private static Visualizer instance = new Visualizer();

    public static Visualizer getInstance() { return instance; }


    public void open_visualizer(){

        // --> 1. Get visualization data from ADG
//        Graph graph = Graph.getInstance();
//        if(!graph.is_built){
//            JOptionPane.showMessageDialog(null, "ERROR: graph not built");
//            return;
//        }
//        ArrayList<JsonObject> designs = graph.evaluated_designs;
//        if(designs.isEmpty()){
//            JOptionPane.showMessageDialog(null, "ERROR: no evaluated designs exist");
//            return;
//        }

        // --> 1. Get visualization data from Processed Designs package
        ArrayList<JsonObject> designs = new ArrayList<>();
        Project project = Application.getInstance().getProject();
        Profile adm_profile = StereotypesHelper.getProfile(project, ADG_Element.adg_profile);
        Stereotype design_stereotype = StereotypesHelper.getStereotype(project, "Design", adm_profile);
        Package processed_designs_pkg = DiagramsPackage.getAdgProcessedDesignsPackage(ADG_Diagram.getActiveDiagram());
        Collection<PackageableElement> processed_designs = processed_designs_pkg.getPackagedElement();
        for(PackageableElement design_pkg: processed_designs){
            String objectives_str = StereotypesHelper.getStereotypePropertyValue(design_pkg, design_stereotype, "Objectives").get(0).toString();
            JsonObject objectives = (new JsonParser()).parse(objectives_str).getAsJsonObject();
            designs.add(objectives);
        }





        // --> 2. Create table
        double[] values1 = {};
        double[] values2 = {};
        double[] values3 = {};
        String[] values4 = {};
        String[] values5 = {};
        DoubleColumn column1 = DoubleColumn.create("Science", values1);
        DoubleColumn column2 = DoubleColumn.create("Cost", values2);
        DoubleColumn column3 = DoubleColumn.create("Data Continuity", values3);
        StringColumn column4 = StringColumn.create("Design Info", values4);
        StringColumn column5 = StringColumn.create("Name", values5);
        for(JsonObject design: designs){
            column1.append(design.get("science").getAsDouble());
            column2.append(design.get("cost").getAsDouble());
            column3.append(design.get("data_continuity").getAsDouble());
            column4.append(design.get("design_info").getAsString());
            column5.append(design.get("name").getAsString());
        }
        Table data_table = Table.create("Design Space").addColumns(column1, column2, column3);

//        AtomicInteger order = new AtomicInteger(0);
//        Trace[] traces =
//                data_table.numericColumns().stream()
//                        .map(
//                                f -> {
//                                    int i = order.incrementAndGet();
//                                    return ScatterTrace.builder(f.asDoubleArray())
//                                            .name(f.name())
//                                            .xAxis("x" + i)
//                                            .yAxis("y" + i)
//                                            .build();
//                                })
//                        .toArray(Trace[]::new);


        // --> 3. Visualize table
//        Figure figure = BubblePlot.create(
//                        "Design Space",
//                        data_table, // table name
//                        "Science",  // x variable column name
//                        "Cost",     // y variable column name
//                        "Data Continuity" // bubble size
//                );

//        Layout layout = Layout.builder().title("Design Space").build();
//        ScatterTrace trace1 = ScatterTrace.builder(column1, column2)
//                .name("Science vs. Cost")
//                .mode(ScatterTrace.Mode.MARKERS)
//                .marker(Marker.builder().symbol(Symbol.DIAMOND).color("red").size(10).build())
//                .build();
//        ScatterTrace trace2 = ScatterTrace.builder(column3, column2)
//                .name("Data Continuity vs. Cost")
//                .mode(ScatterTrace.Mode.MARKERS)
//                .marker(Marker.builder().symbol(Symbol.SQUARE).color("blue").size(10).build())
//                .build();
//        Figure figure = new Figure(layout, trace1, trace2);






        Figure figure = ScatterPlot.create("Design Space", data_table, "Cost", "Science");


        Path fig_path = Paths.get(ADG_Plugin.getPluginPath().toString(), "figures", "fig.html");
        File fig_file = fig_path.toFile();
        Plot.show(figure, fig_file);


//        Path data_path = Paths.get(ADG_Plugin.getPluginPath().toString(), "data", "tornado_data.csv");
//        Table table = Table.read().csv(data_path.toString());
//        table = table.where(table.numberColumn("Fatalities").isGreaterThan(3));
//        Table t2 = table.summarize("fatalities", sum).by("State");
//        t2 = t2.sortDescendingOn(t2.column(1).name());
//        Layout layout = Layout.builder().title("Tornado Fatalities by State").build();
//        BarTrace trace = BarTrace.builder(t2.categoricalColumn(0), t2.numberColumn(1)).build();
//        Plot.show(new Figure(layout, trace), fig_file);

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
