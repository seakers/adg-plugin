package adg.plugin.actions;

import adg.plugin.ADG_Descriptor;
import adg.plugin.decisions.Decision;
import adg.plugin.graph.AlgorithmParameters;
import adg.plugin.graph.ConnectionParameters;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.nomagic.ci.persistence.local.a.J;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.ui.MainFrame;
import com.nomagic.magicdraw.ui.actions.DefaultDiagramAction;
import com.nomagic.magicdraw.uml.Finder;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.magicdraw.uml.symbols.PresentationElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.*;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;
import com.teamdev.jxbrowser.js.Json;
import graph.Graph;

import javax.annotation.CheckForNull;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

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
        JsonObject adg_specs = this.buildAdgSpecs(adg_diagram_view);
        JOptionPane.showMessageDialog(null, "EARLY RETURN");


        // --> 3. Build graph
        if(!Graph.buildInstance(conn.uri, conn.user, conn.password, formulation, problem, reset_nodes, reset_graphs, adg_diagram_view, adg_specs)){
            JOptionPane.showMessageDialog(null, "ERROR: while building graph (check database connection)");
            return;
        }

        // --> 4. Get graph instance / validate
        Graph graph = Graph.getInstance();
        if(!graph.isBuilt){
            JOptionPane.showMessageDialog(null, "ERROR: while building graph - build unsuccessful");
            return;
        }

        JOptionPane.showMessageDialog(null, "SUCCESS: graph build passed");
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







    public JsonObject buildAdgSpecs(DiagramPresentationElement adg_diagram_view){
        JsonObject adg_specs = new JsonObject();

        // --> 1. Create graph object
        adg_specs.add("graph", this.buildAdgGraphObject(adg_diagram_view));

        // --> 2. Create context object
        adg_specs.add("inputs", this.buildAdgContextObject(adg_diagram_view));

        // --> 3. Return specs
        return adg_specs;
    }


    public JsonObject buildAdgGraphObject(DiagramPresentationElement adg_diagram_view){
        JsonObject graph_object = new JsonObject();
        JsonArray decisions = new JsonArray();
        JsonArray edges = new JsonArray();



        Collection<Element> elements = adg_diagram_view.getUsedModelElements();

        // --> Build: decisions
        for(Element element: elements){
            ArrayList<String> element_types = Decision.get_stereotypes(element);
            if(!element_types.contains("Decision"))
                continue;
            if(element_types.contains("Root"))
                continue;

            String decision_type = Decision.get_decision_type(element);
            String decision_name = Decision.get_decision_name(element);
            String name = "|" + decision_type + "|" + decision_name + "|";
            JOptionPane.showMessageDialog(null, name);

            JsonObject decision = new JsonObject();
            decision.addProperty("name", decision_name);
            decision.addProperty("type", decision_type);
            decisions.add(decision);

//
//            Collection<Element> inner_elements = element.getOwnedElement();
//
//            for(Element inner_element: inner_elements){
//                Property property = (Property) inner_element;
//            }
        }

        // --> Build: edges
        JsonObject edge = new JsonObject();
        edge.addProperty("parent", "Root");
        edge.addProperty("child", "Instrument Selection");
        edge.addProperty("operates_on", "instruments");
        edges.add(edge);




        graph_object.add("decisions", decisions);
        graph_object.add("edges", edges);
        return graph_object;
    }

    public JsonObject buildAdgContextObject(DiagramPresentationElement adg_diagram_view){
        JsonObject context_object = new JsonObject();

        JsonArray insts = new JsonArray();


        Collection<Element> elements = adg_diagram_view.getUsedModelElements();
        for(Element element: elements) {
            ArrayList<String> element_types = Decision.get_stereotypes(element);
            if (!element_types.contains("Decision"))
                continue;
            if (element_types.contains("Root"))
                continue;

            Collection<DirectedRelationship> relationships_source = element.get_directedRelationshipOfSource();
            String decision_type = Decision.get_decision_type(element);
            String decision_name = Decision.get_decision_name(element);

            for(DirectedRelationship relationship: relationships_source){
                Collection<Element> target_elements = relationship.getTarget();
                Element inner_element = target_elements.iterator().next();
                String inner_name = Decision.get_decision_name(inner_element);
                JOptionPane.showMessageDialog(null, inner_name);
                if(!inner_name.equals("Root")){
                    JsonObject inner_json = new JsonObject();
                    inner_json.addProperty("name", inner_name);
                    insts.add(inner_json);
                }
            }
        }


        context_object.add("instruments", insts);
        return context_object;
    }


}
