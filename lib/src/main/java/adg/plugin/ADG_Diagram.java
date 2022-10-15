package adg.plugin;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Diagram;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;

public class ADG_Diagram {

    public static ArrayList<Diagram> getDiagrams(){
        Project project = Application.getInstance().getProject();
        Collection<DiagramPresentationElement> diagrams_view = project.getDiagrams();
        ArrayList<Diagram> adg_diagrams = new ArrayList<>();
        for(DiagramPresentationElement diagram_view: diagrams_view){
            if(diagram_view.getDiagramType().getType().equals(ADG_Descriptor.ARCHITECTURE_DECISION_GRAPH)){
                adg_diagrams.add(diagram_view.getDiagram());
            }
        }
        return adg_diagrams;
    }

    public static Diagram getActiveDiagram(){
        Project project = Application.getInstance().getProject();
        DiagramPresentationElement adg_diagram_view = project.getActiveDiagram();
        if(!adg_diagram_view.getDiagramType().getType().equals(ADG_Descriptor.ARCHITECTURE_DECISION_GRAPH)){
            JOptionPane.showMessageDialog(null, "ERROR: active diagram must be of type ADG");
            return null;
        }
        return adg_diagram_view.getDiagram();
    }

    public static DiagramPresentationElement getActiveDiagramView(){
        Project project = Application.getInstance().getProject();
        DiagramPresentationElement adg_diagram_view = project.getActiveDiagram();
        if(!adg_diagram_view.getDiagramType().getType().equals(ADG_Descriptor.ARCHITECTURE_DECISION_GRAPH)){
            return null;
        }
        return adg_diagram_view;
    }
    
    public static ArrayList<Element> getElementSets(){
        ArrayList<Element> element_sets = new ArrayList<>();
        DiagramPresentationElement adg_diagram_view = ADG_Diagram.getActiveDiagramView();
        Collection<Element> elements = adg_diagram_view.getUsedModelElements();
        for(Element element: elements){
            if(ADG_Element.isElementSet(element))
                element_sets.add(element);
        }
        return element_sets;
    }

    public static ArrayList<Element> getDecisions(){
        ArrayList<Element> decisions = new ArrayList<>();
        DiagramPresentationElement adg_diagram_view = ADG_Diagram.getActiveDiagramView();
        Collection<Element> elements = adg_diagram_view.getUsedModelElements();
        for(Element element: elements){
            if(ADG_Element.isDecision(element))
                decisions.add(element);
        }
        return decisions;
    }

    public static Element findDecision(ArrayList<Element> decisions, String search_name){
        for(Element decision: decisions){
            String decision_name = ADG_Element.getElementName(decision);
            if(decision_name == search_name){
                return decision;
            }
        }
        return null;
    }




}
