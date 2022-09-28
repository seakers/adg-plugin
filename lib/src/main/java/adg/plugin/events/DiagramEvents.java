package adg.plugin.events;

import adg.plugin.ADG_Descriptor;
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


public class DiagramEvents implements PropertyChangeListener {

    public HashMap<Diagram, Package> diagram_map;


    // --> Called every time a project is opened
    public DiagramEvents initialize(Project project){
        this.diagram_map = new HashMap<>();
        Package primary_model = project.getPrimaryModel();

        // --> 1. Get all ADGs in the current project
        ArrayList<Diagram> adg_diagrams = DiagramEvents.getAdgDiagrams(project);

        // --> 2. If any ADGs exist, ensure diagram package
        if(!adg_diagrams.isEmpty()){
            DiagramEvents.getOrCreateDiagramPackage(project);

            // --> 3. Ensure each ADG has a corresponding design folder and relationship
            for(Diagram adg_diagram: adg_diagrams){
                this.diagram_map.put(adg_diagram, DiagramEvents.getOrCreateAdgPackage(project, adg_diagram));
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
                        Package adg_package = DiagramEvents.getOrCreateAdgPackage(project, adg_diagram);
                        this.diagram_map.put(adg_diagram, adg_package);
                        adg_diagram.setOwner(adg_package);
                    }
                }
            }
        }
    }






    public static ArrayList<Diagram> getAdgDiagrams(Project project){
        Collection<DiagramPresentationElement> diagrams_view = project.getDiagrams();
        ArrayList<Diagram> adg_diagrams = new ArrayList<>();
        for(DiagramPresentationElement diagram_view: diagrams_view){
            if(diagram_view.getDiagramType().getType().equals(ADG_Descriptor.ARCHITECTURE_DECISION_GRAPH)){
                adg_diagrams.add(diagram_view.getDiagram());
            }
        }
        return adg_diagrams;
    }

    public static Package getOrCreateDiagramPackage(Project project){
        String diagram_package_name = "Decision Graphs";
        Package diagram_package;

        Package primary_model = project.getPrimaryModel();

        // --> 1. Check to see if already exists
        java.lang.Class<?>[] types = new java.lang.Class<?>[]{Package.class};
        Collection<Package> elements = Finder.byTypeRecursively().find(primary_model, types, false);
        for(Package pkg: elements){
            if(pkg.getName().equals(diagram_package_name)){
                return pkg;
            }
        }

        // --> 2. Create new design package
        diagram_package = project.getElementsFactory().createPackageInstance();
        diagram_package.setName(diagram_package_name);
        diagram_package.setOwner(primary_model);
        return diagram_package;
    }

    public static Package getOrCreateAdgPackage(Project project, Diagram adg_diagram){
        Package diagram_package = DiagramEvents.getOrCreateDiagramPackage(project);
        Package adg_package;

        // --> 1. Get all diagram relationships
        Collection<DirectedRelationship> relationships = adg_diagram.get_directedRelationshipOfSource();
        if(relationships.size() > 1){
            JOptionPane.showMessageDialog(null, "ERROR: ADG Diagram should have only one relationship");
        }

        // --> 2. If a relationship exists, extract package from first element
        if(relationships.size() > 0){
            // --> Get relationship
            DirectedRelationship relationship = relationships.iterator().next();

            // --> Get package
            Collection<Element> relationship_endpoints = relationship.getTarget();
            adg_package = (Package) relationship_endpoints.iterator().next();
        }
        // --> 3. Else, create package and relationship
        else{
            // --> Create Package
            adg_package = project.getElementsFactory().createPackageInstance();
            adg_package.setName(adg_diagram.getName());
            adg_package.setOwner(diagram_package);

            // --> Create Relationship
            Dependency diagram_relationship = project.getElementsFactory().createDependencyInstance();
            CoreHelper.setSupplierElement(diagram_relationship, adg_package);
            CoreHelper.setClientElement(diagram_relationship, adg_diagram);
            diagram_relationship.setOwner(adg_package);

            // --> Create Subpackages
            Package designs_package = project.getElementsFactory().createPackageInstance();
            designs_package.setName("Generated Designs");
            designs_package.setOwner(adg_package);

            Package decisions_package = project.getElementsFactory().createPackageInstance();
            decisions_package.setName("Decisions");
            decisions_package.setOwner(adg_package);

            Package design_space_package = project.getElementsFactory().createPackageInstance();
            design_space_package.setName("Design Space");
            design_space_package.setOwner(adg_package);
        }
        return adg_package;
    }

    public static Package getAdgDecisionPackage(Project project, Diagram adg_diagram){
        Package adg_package = DiagramEvents.getOrCreateAdgPackage(project, adg_diagram);
        Collection<Package> subpackages = adg_package.getNestedPackage();
        Iterator<Package> pkg_iterator = subpackages.stream().iterator();
        while(pkg_iterator.hasNext()){
            Package pkg = pkg_iterator.next();
            if(pkg.getName().equals("Decisions")){
                return pkg;
            }
        }
        JOptionPane.showMessageDialog(null, "Adg Decision Package Not Found");
        return null;
    }

    public static Package getAdgDesignPackage(Project project, Diagram adg_diagram){
        Package adg_package = DiagramEvents.getOrCreateAdgPackage(project, adg_diagram);
        Collection<Package> subpackages = adg_package.getNestedPackage();
        Iterator<Package> pkg_iterator = subpackages.stream().iterator();
        while(pkg_iterator.hasNext()){
            Package pkg = pkg_iterator.next();
            if(pkg.getName().equals("Generated Designs")){
                return pkg;
            }
        }
        JOptionPane.showMessageDialog(null, "Adg Design Package Not Found");
        return null;
    }

    public static Package getAdgDesignSpacePackage(Project project, Diagram adg_diagram){
        Package adg_package = DiagramEvents.getOrCreateAdgPackage(project, adg_diagram);
        Collection<Package> subpackages = adg_package.getNestedPackage();
        Iterator<Package> pkg_iterator = subpackages.stream().iterator();
        while(pkg_iterator.hasNext()){
            Package pkg = pkg_iterator.next();
            if(pkg.getName().equals("Design Space")){
                return pkg;
            }
        }
        JOptionPane.showMessageDialog(null, "Adg Design Space Package Not Found");
        return null;
    }





}