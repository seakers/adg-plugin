package adg.plugin.actions;

import adg.plugin.ADG_Descriptor;
import adg.plugin.decisions.Decision;
import adg.plugin.graph.AlgorithmParameters;
import adg.plugin.graph.ConnectionParameters;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.ui.actions.DefaultDiagramAction;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.*;
import com.teamdev.jxbrowser.js.Json;
import graph.Graph;

import javax.annotation.CheckForNull;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.FileReader;
import java.util.Collection;

public class BuildGraph extends DefaultDiagramAction {

    private static final String ACTION_NAME = "Build Graph";

    public BuildGraph() {
        super(ACTION_NAME, ACTION_NAME, KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK), null);
    }

    @Override
    public void actionPerformed(@CheckForNull ActionEvent e) {
        Project project = Application.getInstance().getProject();

        // --> 1. Get diagram and validate correct type
        DiagramPresentationElement adg_diagram_view = project.getActiveDiagram();
        if(!adg_diagram_view.getDiagramType().getType().equals(ADG_Descriptor.ARCHITECTURE_DECISION_GRAPH)){
            JOptionPane.showMessageDialog(null, "ERROR: active diagram must be of type ADG");
            return;
        }
        Diagram adg_diagram = project.getActiveDiagram().getDiagram();

        // --> 2. Get graph parameters / specs
        AlgorithmParameters algo = AlgorithmParameters.getInstance();
        ConnectionParameters conn = ConnectionParameters.getInstance();
        String formulation = adg_diagram.getName();
        String problem = adg_diagram.getName();
        boolean reset_nodes = true;
        boolean reset_graphs = true;


        // JsonObject adg_specs = this.buildAdgSpecsExample(adg_diagram_view);
        JsonObject adg_specs = BuildGraph.buildAdgSpecs(adg_diagram_view);
        Decision.showJsonElement("ADG SPECS", adg_specs);


        // --> 3. Build graph
//        Graph graph = new Graph.Builder(formulation, problem, adg_specs)
//                .buildDatabaseClient(conn.uri, conn.user, conn.password, true, true)
//                .indexGraph()
//                .buildTopologicalOrdering()
//                .projectGraph()
//                .build();
//        if(graph.is_built){
//            JOptionPane.showMessageDialog(null, "SUCCESS: graph build passed");
//        }
//        else{
//            JOptionPane.showMessageDialog(null, "ERROR: while building graph - build unsuccessful");
//        }

    }



    public JsonObject buildAdgSpecsExample(DiagramPresentationElement adg_diagram_view){
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







    public static JsonObject buildAdgSpecs(DiagramPresentationElement adg_diagram_view){
        JsonObject adg_specs = new JsonObject();

        // --> 1. Create graph object
        adg_specs.add("graph", BuildGraph.buildAdgGraphObject(adg_diagram_view));

        // --> 2. Create context object
        adg_specs.add("inputs", BuildGraph.buildAdgContextObject(adg_diagram_view));

        // --> 3. Return specs
        return adg_specs;
    }


    public static JsonObject buildAdgGraphObject(DiagramPresentationElement adg_diagram_view){

        // --> 1. Build Graph JsonObject
        JOptionPane.showMessageDialog(null, "--> BUILDING GRAPH OBJECT");
        JsonObject graph_object = new JsonObject();
        JsonArray decisions = new JsonArray();
        JsonArray edges = new JsonArray();
        graph_object.add("decisions", decisions);
        graph_object.add("edges", edges);


        // --> 2. Populate Decisions / Edges
        Collection<Element> elements = adg_diagram_view.getUsedModelElements();
        for(Element element: elements){
            if(!Decision.is_decision(element))
                continue;
            if(Decision.is_root_decision(element))
                continue;

            // --> Get decision object
            Element decision_element = element;
            String decision_type = Decision.get_decision_type(decision_element);
            String decision_name = Decision.get_element_name(decision_element);

            // --> Enter decision object
            JsonObject decision = new JsonObject();
            decision.addProperty("name", decision_name);
            decision.addProperty("type", decision_type);
            decisions.add(decision);
            JOptionPane.showMessageDialog(null, "- " + decision_name + " " + decision_type);

            // --> Enter decision edges
            Collection<DirectedRelationship> relationships_source = element.get_directedRelationshipOfSource();
            for(DirectedRelationship relationship: relationships_source){
                Element related_element = Decision.get_relationship_element_target(relationship);
                if(Decision.is_root_decision(related_element))
                    continue;

                // --> Create edge
                JsonObject edge = new JsonObject();
                edge.addProperty("child", decision_name);
                if(Decision.is_decision(related_element)){
                    edge.addProperty("parent", Decision.get_element_name(related_element));
                    edge.addProperty("operates_on", "NULL");  // This will somehow be extracted from the dependent decision
                }
                else{
                    edge.addProperty("parent", "Root");
                    edge.addProperty("operates_on", Decision.get_element_name(related_element));

                    // --> Get items the related item generalizes

                }
                edges.add(edge);
            }
        }
        Decision.showJsonElement("GRAPH OBJECT", graph_object);
        return graph_object;
    }

    public static JsonObject buildAdgContextObject(DiagramPresentationElement adg_diagram_view){

        // --> 1. Create context object
        JOptionPane.showMessageDialog(null, "--> BUILDING CONTEXT OBJECT");
        JsonObject context_object = new JsonObject();

        // --> 2. Iterate over non-root decisions
        Collection<Element> elements = adg_diagram_view.getUsedModelElements();
        for(Element element: elements) {
            if(!Decision.is_decision(element))
                continue;
            if(Decision.is_root_decision(element))
                continue;

            // --> 3. Iterate over decision dependencies (dep must not be decision)
            Collection<DirectedRelationship> relationships_source = element.get_directedRelationshipOfSource();
            for(DirectedRelationship relationship: relationships_source) {
                Element dependency_element = Decision.get_relationship_element_target(relationship);
                if(Decision.is_decision(dependency_element))
                    continue;

                // --> 4. Extract key and items for decision context
                String key = Decision.get_element_name(dependency_element);
                JsonArray values = new JsonArray();
                Collection<DirectedRelationship> dependency_element_relationships = dependency_element.get_directedRelationshipOfTarget();
//                JOptionPane.showMessageDialog(null, "SIZE: " + dependency_element_relationships.size());
                for(DirectedRelationship dependency_relationship: dependency_element_relationships) {
                    String relationship_type = dependency_relationship.getHumanType();
                    if(!relationship_type.equalsIgnoreCase("Generalization"))
                        continue;

                    Element dependency_inner_element = Decision.get_relationship_element_source(dependency_relationship);
                    String dependency_inner_element_name = Decision.get_element_name(dependency_inner_element);

                    JsonObject dependency_inner_object = new JsonObject();
                    dependency_inner_object.addProperty("name", dependency_inner_element_name);
                    values.add(dependency_inner_object);

//                    JOptionPane.showMessageDialog(null, "INNER ELEMENT: " + dependency_inner_element_name);
                }
                context_object.add(key, values);
            }
        }
        Decision.showJsonElement("CONTEXT OBJECT", context_object);
        return context_object;
    }


}
