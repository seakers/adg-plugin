package adg.plugin.actions;

import adg.plugin.ADG_Descriptor;
import adg.plugin.ADG_Diagram;
import adg.plugin.ADG_Element;
import adg.plugin.graph.AlgorithmParameters;
import adg.plugin.graph.ConnectionParameters;
import adg.plugin.packages.DiagramsPackage;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.nomagic.esi.common.a.J;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.ui.actions.DefaultDiagramAction;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.*;
import graph.Graph;

import javax.annotation.CheckForNull;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;

public class BuildGraph extends DefaultDiagramAction {

    private static final String ACTION_NAME = "Build Graph";

    public BuildGraph() {
        super(ACTION_NAME, ACTION_NAME, KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK), null);
    }

    @Override
    public void actionPerformed(@CheckForNull ActionEvent e) {
        BuildGraph.buildAdgGraph();
    }


    public static Graph getAdgGraph(){
        Graph curr_graph = Graph.getInstance();
        if(!curr_graph.is_built){
            int dialogButton = JOptionPane.YES_NO_OPTION;
            int dialogResult = JOptionPane.showConfirmDialog (null, "Warning, no graph has been built. Build graph?","Warning",dialogButton);
            if(dialogResult == JOptionPane.YES_OPTION){
                return BuildGraph.buildAdgGraph();
            }
            else{
                return null;
            }
        }
        else{
            return curr_graph;
        }
    }

    public static Graph buildAdgGraph(){
        Project project = Application.getInstance().getProject();

        // --> 1. Check if graph already exists
        Graph curr_graph = Graph.getInstance();
        if(curr_graph.is_built){
            int dialogButton = JOptionPane.YES_NO_OPTION;
            int dialogResult = JOptionPane.showConfirmDialog (null, "Warning, a graph is already built. Use the existing one?","Warning",dialogButton);
            if(dialogResult == JOptionPane.YES_OPTION){
                return curr_graph;
            }
            else{
                System.out.println("--> CLEAN ADG PACKAGE FOR NEW GRAPH...");
                DiagramsPackage.cleanAdgDesignSpacePackage();
                DiagramsPackage.cleanAdgSystemArchitectureModel(ADG_Diagram.getActiveDiagram());
                DiagramsPackage.cleanAdgGeneratedDesignsPackage();
            }
        }

        // --> 2. Get diagram and validate correct type
        DiagramPresentationElement adg_diagram_view = ADG_Diagram.getActiveDiagramView();
        Diagram adg_diagram = ADG_Diagram.getActiveDiagram();

        // --> 3. Get algorithm parameters
        AlgorithmParameters algo = AlgorithmParameters.getInstance();
        ConnectionParameters conn = ConnectionParameters.getInstance();
        String formulation = adg_diagram.getName();
        String problem = adg_diagram.getName();
        boolean reset_nodes = true;
        boolean reset_graphs = true;

        // --> 4. Get graph specifications
        // JsonObject adg_specs = BuildGraph.buildAdgSpecsExample();
        // JsonObject adg_specs = BuildGraph.buildAdgSpecs();
        JsonObject adg_specs = BuildGraph.buildAdgOldSpecs();
        ADG_Element.showJsonElement("GRAPH OBJECT", adg_specs.get("graph"));
        ADG_Element.showJsonElement("ROOT OBJECT", adg_specs.get("root"));

        // --> 5. Build graph
        Graph graph = new Graph.Builder(formulation, problem, adg_specs)
                .buildDatabaseClient(conn.uri, conn.user, conn.password, true, true)
                .indexGraph()
                .buildTopologicalOrdering()
                .projectGraph()
                .build();
        if(graph.is_built){
            JOptionPane.showMessageDialog(null, "SUCCESS: graph build passed");
            return graph;
        }
        else{
            JOptionPane.showMessageDialog(null, "ERROR: while building graph - build unsuccessful");
            return null;
        }
    }


    // ---------------
    // --- ADG 1.0 ---
    // ---------------

    public static JsonObject buildAdgOldSpecs(){
        JsonObject final_obj = new JsonObject();



        JsonObject root_obj = new JsonObject();
        JsonArray root_dep_array = new JsonArray();
        root_obj.add(ADG_Diagram.getActiveDiagram().getName(), root_dep_array);



        // --> 1. Build Graph JsonObject
        JsonObject graph_object = new JsonObject();
        JsonArray decisions = new JsonArray();
        JsonArray edges = new JsonArray();
        graph_object.add("decisions", decisions);
        graph_object.add("edges", edges);

        // --> 2. Populate Decisions / Edges
        ArrayList<Element> elements = ADG_Diagram.getDecisions();
        for(Element decision_element: elements){

            // --> Get decision object
            String decision_type = ADG_Element.getDecisionType(decision_element);
            String decision_name = ADG_Element.getElementName(decision_element);

            // --> Enter decision object
            JsonObject decision = new JsonObject();
            decision.addProperty("name", decision_name);
            decision.addProperty("type", decision_type);
            decisions.add(decision);

            if(ADG_Element.isLeafNode(decision_element)){
                JsonObject fedge = new JsonObject();
                fedge.addProperty("parent", ADG_Element.getElementName(decision_element));
                fedge.addProperty("child", "Design");
                fedge.addProperty("type", "FINAL_DEPENDENCY");
                edges.add(fedge);
            }

            // --> Iterate over edges
            ArrayList<Element> dependencies = ADG_Element.getDecisionDependencies(decision_element);
            for(Element related_element: dependencies){

                // --> Create edge
                JsonObject edge = new JsonObject();
                edge.addProperty("child", decision_name);
                if(ADG_Element.isDecision(related_element)){
                    DirectedRelationship relationship = ADG_Element.getRelationship(related_element, decision_element);
                    edge.addProperty("parent", ADG_Element.getElementName(related_element));
                    edge.addProperty("type", "DEPENDENCY");




                }
                else if(ADG_Element.isElementSet(related_element)){
                    edge.addProperty("parent", "Root");
                    edge.addProperty("type", "ROOT_DEPENDENCY");

                    JsonObject decision_root_obj = new JsonObject();
                    decision_root_obj.addProperty("child_name", decision_name);
                    decision_root_obj.addProperty("child_type", decision_type);
                    decision_root_obj.add("elements", BuildGraph.getRootDeps(related_element));
                    root_dep_array.add(decision_root_obj);

                }
                else{
                    continue;
                }
                edges.add(edge);
            }
        }

        final_obj.add("graph", graph_object);
        final_obj.add("root", root_obj);
        return final_obj;
    }

    public static JsonArray getRootDeps(Element element_set){
        JsonArray values = new JsonArray();
        ArrayList<Element> element_set_items = ADG_Element.getElementSetDependencies(element_set);
        int counter = 0;
        for(Element element_set_item: element_set_items){
            JsonObject value = new JsonObject();
            value.addProperty("name", ADG_Element.getElementSetItemName(element_set_item));
            value.addProperty("id", counter);
            value.addProperty("type", "item");
            value.addProperty("active", Boolean.TRUE);
            values.add(value);

            counter += 1;
        }
        return values;
    }



    // ---------------
    // --- ADG 2.0 ---
    // ---------------

    public static JsonObject buildAdgSpecsExample(){
        JsonObject adg_specs = new JsonObject();

        String graph_file = "C:\\Program Files\\Cameo Systems Modeler Demo\\plugins\\adg\\formulations\\EOSS\\graph.json";
        String problem_file = "C:\\Program Files\\Cameo Systems Modeler Demo\\plugins\\adg\\formulations\\EOSS\\problems\\base.json";

        Gson gson = new Gson();
        JsonObject graph_object = new JsonObject();
        JsonObject context_object = new JsonObject();
        try {
            graph_object = gson.fromJson(new FileReader(graph_file), JsonObject.class);
            context_object = gson.fromJson(new FileReader(problem_file), JsonObject.class);
        }
        catch (Exception ex){
            ex.printStackTrace();
        }

        adg_specs.add("graph", graph_object);
        adg_specs.add("inputs", context_object);

        return adg_specs;
    }

    public static JsonObject buildAdgSpecs(){
        JsonObject adg_specs = new JsonObject();

        // --> 1. Create graph object
        adg_specs.add("graph", BuildGraph.buildAdgGraphObject());

        // --> 2. Create context object
        adg_specs.add("inputs", BuildGraph.buildAdgContextObject());

        // --> 3. Return specs
        return adg_specs;
    }

    public static JsonObject buildAdgGraphObject(){

        // --> 1. Build Graph JsonObject
        JsonObject graph_object = new JsonObject();
        JsonArray decisions = new JsonArray();
        JsonArray edges = new JsonArray();
        graph_object.add("decisions", decisions);
        graph_object.add("edges", edges);


        // --> 2. Populate Decisions / Edges
        ArrayList<Element> elements = ADG_Diagram.getDecisions();
        for(Element decision_element: elements){

            // --> Get decision object
            String decision_type = ADG_Element.getDecisionType(decision_element);
            String decision_name = ADG_Element.getElementName(decision_element);

            // --> Enter decision object
            JsonObject decision = new JsonObject();
            decision.addProperty("name", decision_name);
            decision.addProperty("type", decision_type);
            decisions.add(decision);

            // --> Iterate over edges
            ArrayList<Element> dependencies = ADG_Element.getDecisionDependencies(decision_element);
            for(Element related_element: dependencies){

                // --> Create edge
                JsonObject edge = new JsonObject();
                edge.addProperty("child", decision_name);
                if(ADG_Element.isDecision(related_element)){
                    DirectedRelationship relationship = ADG_Element.getRelationship(related_element, decision_element);
                    edge.addProperty("parent", ADG_Element.getElementName(related_element));
                    edge.addProperty("operates_on", ADG_Element.getElementName(relationship));
                }
                else if(ADG_Element.isElementSet(related_element)){
                    edge.addProperty("parent", "Root");
                    edge.addProperty("operates_on", ADG_Element.getElementName(related_element));
                }
                else{
                    continue;
                }
                edges.add(edge);
            }
        }
        ADG_Element.showJsonElement("GRAPH OBJECT", graph_object);
        return graph_object;
    }

    public static JsonObject buildAdgContextObject(){

        // --> 1. Create context object
        JsonObject context_object = new JsonObject();


        // --> 2. For each element set, get key / values
        ArrayList<Element> element_sets = ADG_Diagram.getElementSets();
        for(Element element_set: element_sets){

            // --> Key
            String key = ADG_Element.getElementName(element_set);

            // --> Values
            JsonArray values = new JsonArray();
            ArrayList<Element> element_set_items = ADG_Element.getElementSetDependencies(element_set);
            for(Element element_set_item: element_set_items){
                JsonObject value = new JsonObject();
                value.addProperty("name", ADG_Element.getElementSetItemName(element_set_item));
                value.addProperty("multiplicity", ADG_Element.getElementSetItemMultiplicity(element_set_item));
                values.add(value);
            }

            context_object.add(key, values);
        }

        // --> 3. Return
        ADG_Element.showJsonElement("CONTEXT OBJECT", context_object);
        return context_object;
    }

}
