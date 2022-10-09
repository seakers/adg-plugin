package adg.plugin.events;

import adg.plugin.ADG_Descriptor;
import adg.plugin.ADG_Diagram;
import adg.plugin.ADG_Element;
import adg.plugin.packages.DiagramsPackage;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.ui.DiagramWindow;
import com.nomagic.magicdraw.uml.Finder;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.uml2.ext.jmi.helpers.CoreHelper;
import com.nomagic.uml2.ext.magicdraw.classes.mddependencies.Dependency;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.*;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
public class DiagramEvents implements PropertyChangeListener {

    public HashMap<Diagram, Package> diagram_map;


    // --> Called every time a project is opened
    public DiagramEvents initialize(Project project){
        this.diagram_map = new HashMap<>();
        Package primary_model = project.getPrimaryModel();

        // --> 1. Get all ADGs in the current project
        ArrayList<Diagram> adg_diagrams = ADG_Diagram.getDiagrams();

        // --> 2. If any ADGs exist, ensure diagram package
        if(!adg_diagrams.isEmpty()){
            DiagramsPackage.getOrCreateDiagramsPackage(project);

            // --> 3. Ensure each ADG has a corresponding design folder and relationship
            for(Diagram adg_diagram: adg_diagrams){
                this.diagram_map.put(adg_diagram, DiagramsPackage.getOrCreateAdgPackage(project, adg_diagram));
            }
        }
        return this;
    }

    public void propertyChange(PropertyChangeEvent evt) {
        Project project = Application.getInstance().getProject();

        // --------------------
        // --- HANDLE EVENT ---
        // --------------------
        String propertyName = evt.getPropertyName();
        // System.out.println("--> EVENT "+propertyName+": " + evt.getNewValue().getClass().toString());

        // --> EVENT: ADG Diagram Opened / Created
        if (Project.DIAGRAM_OPENED.equals(propertyName)) {
            Object new_value = evt.getNewValue();
            if (new_value instanceof DiagramWindow){
                DiagramPresentationElement diagram_view = ((DiagramWindow) new_value).getDiagramPresentationElement();
                if(diagram_view.getDiagramType().getType().equals(ADG_Descriptor.ARCHITECTURE_DECISION_GRAPH)){

                    // --> If the diagram has no relationships it is new
                    Diagram adg_diagram = diagram_view.getDiagram();
                    if(adg_diagram.get_directedRelationshipOfSource().isEmpty()){
                        String temp_name = "ADG" + (this.diagram_map.keySet().size()+1);
                        adg_diagram.setName(temp_name);
                        Package adg_package = DiagramsPackage.getOrCreateAdgPackage(project, adg_diagram);
                        this.diagram_map.put(adg_diagram, adg_package);
                        adg_diagram.setOwner(adg_package);
                    }
                }
            }
        }
    }

}