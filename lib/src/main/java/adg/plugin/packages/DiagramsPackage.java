package adg.plugin.packages;

import adg.plugin.ADG_Diagram;
import adg.plugin.ADG_Element;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.openapi.uml.ModelElementsManager;
import com.nomagic.magicdraw.openapi.uml.SessionManager;
import com.nomagic.magicdraw.uml.DiagramTypeConstants;
import com.nomagic.magicdraw.uml.Finder;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.uml2.ext.jmi.helpers.CoreHelper;
import com.nomagic.uml2.ext.magicdraw.classes.mddependencies.Dependency;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.*;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
import com.nomagic.magicdraw.sysml.util.SysMLConstants;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;


public class DiagramsPackage {

    // ------------------------
    // --- Diagrams Package ---
    // ------------------------

    public static Package getOrCreateDiagramsPackage(Project project){
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


    // -------------------
    // --- ADG Package ---
    // -------------------

    public static Package getOrCreateAdgPackage(Project project, Diagram adg_diagram){
        Package diagram_package = DiagramsPackage.getOrCreateDiagramsPackage(project);
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

            Package architecture_package = project.getElementsFactory().createPackageInstance();
            architecture_package.setName("Architecture");
            architecture_package.setOwner(adg_package);
        }
        return adg_package;
    }


    // --> Sub-Packages
    // --> 1. Decisions
    // --> 2. Generated Designs
    // --> 3. Design Space
    // --> 4. System Architecture


    // -------------------------
    // --- Decisions Package ---
    // -------------------------

    public static Package getAdgDecisionsPackage(Project project, Diagram adg_diagram){
        Package adg_package = DiagramsPackage.getOrCreateAdgPackage(project, adg_diagram);
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

    public static Class createDecisionElement(Diagram adg_diagram, String[] stereotypes){
        Project project = Application.getInstance().getProject();
        Class element = ADG_Element.createClassElement("ADGProfile", stereotypes);
        Package decision_pkg = DiagramsPackage.getAdgDecisionsPackage(project, adg_diagram);
        element.setName("Decision " + decision_pkg.getPackagedElement().size());
        element.setOwner(decision_pkg);
        return element;
    }


    // ---------------------------------
    // --- Generated Designs Package ---
    // ---------------------------------

    public static Package getAdgGeneratedDesignsPackage(Project project, Diagram adg_diagram){
        Package adg_package = DiagramsPackage.getOrCreateAdgPackage(project, adg_diagram);
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

    public static void cleanAdgGeneratedDesignsPackage(){
        Project project = Application.getInstance().getProject();
        Package generated_designs_pkg = DiagramsPackage.getAdgGeneratedDesignsPackage(project, ADG_Diagram.getActiveDiagram());
        Collection<PackageableElement> subpackages = generated_designs_pkg.getPackagedElement();
        Iterator<PackageableElement> pkg_iterator = subpackages.stream().iterator();
        ArrayList<PackageableElement> to_delete = new ArrayList<>();
        while(pkg_iterator.hasNext()){
            to_delete.add(pkg_iterator.next());
        }
        for(PackageableElement element: to_delete){
            ADG_Element.deleteElement(element);
        }
    }




    // ----------------------------
    // --- Design Space Package ---
    // ----------------------------

    public static Package getAdgDesignSpacePackage(Project project, Diagram adg_diagram){
        Package adg_package = DiagramsPackage.getOrCreateAdgPackage(project, adg_diagram);
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

    public static void cleanAdgDesignSpacePackage(){
        Project project = Application.getInstance().getProject();
        Package design_space_pkg = DiagramsPackage.getAdgDesignSpacePackage(project, ADG_Diagram.getActiveDiagram());
        Collection<PackageableElement> subpackages = design_space_pkg.getPackagedElement();
        Iterator<PackageableElement> pkg_iterator = subpackages.stream().iterator();
        ArrayList<PackageableElement> to_delete = new ArrayList<>();
        while(pkg_iterator.hasNext()){
            to_delete.add(pkg_iterator.next());
        }
        for(PackageableElement element: to_delete){
            ADG_Element.deleteElement(element);
        }
    }




    // -----------------------------------
    // --- System Architecture Package ---
    // -----------------------------------

    public static Package getAdgSystemArchitecturePackage(Diagram adg_diagram){
        Project project = Application.getInstance().getProject();
        Package adg_package = DiagramsPackage.getOrCreateAdgPackage(project, adg_diagram);
        Collection<Package> subpackages = adg_package.getNestedPackage();
        Iterator<Package> pkg_iterator = subpackages.stream().iterator();
        while(pkg_iterator.hasNext()){
            Package pkg = pkg_iterator.next();
            if(pkg.getName().equals("System Architecture")){
                return pkg;
            }
        }
        JOptionPane.showMessageDialog(null, "Adg SystemArchitecture Package Not Found");
        return null;
    }

    public static Class getAdgSystemArchitectureModel(Diagram adg_diagram){
        Class arch_model = null;
        Package sys_arch_pkg = DiagramsPackage.getAdgSystemArchitecturePackage(adg_diagram);
        Collection<PackageableElement> subpackages = sys_arch_pkg.getPackagedElement();
        Iterator<PackageableElement> pkg_iterator = subpackages.stream().iterator();
        while(pkg_iterator.hasNext()){
            PackageableElement element = pkg_iterator.next();
            if(ADG_Element.hasStereotype(element, "SystemArchitecture")){
                arch_model = (Class) element;
            }
        }
        return arch_model;
    }

    public static void cleanAdgSystemArchitectureModel(Diagram adg_diagram){

        // --> Delete System Architecture Elements
        Project project = Application.getInstance().getProject();
        Package sys_arch_pkg = DiagramsPackage.getAdgSystemArchitecturePackage(adg_diagram);
        Collection<PackageableElement> subpackages = sys_arch_pkg.getPackagedElement();
        Iterator<PackageableElement> pkg_iterator = subpackages.stream().iterator();
        ArrayList<PackageableElement> to_delete = new ArrayList<>();
        while(pkg_iterator.hasNext()){
            to_delete.add(pkg_iterator.next());
        }
        for(PackageableElement element: to_delete){
            ADG_Element.deleteElement(element);
        }

        // --> Delete System Architecture BDD
        ArrayList<DiagramPresentationElement> sys_arch_views = new ArrayList<>();
        for(DiagramPresentationElement sys_arch_view: project.getDiagrams()){
            if(sys_arch_view.getDiagram().getOwner().getID() == sys_arch_pkg.getID()){
                sys_arch_views.add(sys_arch_view);
            }
        }
        for(DiagramPresentationElement sys_arch_view: sys_arch_views){
            ADG_Element.deleteDiagram(sys_arch_view);
        }
    }

    public static Diagram createAdgSystemArchitectureDiagram(Diagram adg_diagram){
        Project project = Application.getInstance().getProject();
        Package sys_arch_pkg = DiagramsPackage.getAdgSystemArchitecturePackage(adg_diagram);
        Diagram diagram = null;
        SessionManager.getInstance().createSession(project, "Create a diagram");
        try {
            diagram = ModelElementsManager.getInstance().createDiagram(SysMLConstants.SYSML_BLOCK_DEFINITION_DIAGRAM, sys_arch_pkg);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        SessionManager.getInstance().closeSession(project);
        return diagram;
    }




}
