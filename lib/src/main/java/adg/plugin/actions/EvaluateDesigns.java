package adg.plugin.actions;

import adg.plugin.ADG_Descriptor;
import adg.plugin.ADG_Diagram;
import adg.plugin.ADG_Element;
import adg.plugin.ADG_Plugin;
import adg.plugin.packages.DiagramsPackage;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.ui.actions.DefaultDiagramAction;

import javax.annotation.CheckForNull;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.PackageableElement;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Profile;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;
import graph.Graph;

public class EvaluateDesigns extends DefaultDiagramAction {


    private static final String ACTION_NAME = "Evaluate Designs";

    public EvaluateDesigns() {
        super(ACTION_NAME, ACTION_NAME, KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK), null);
    }

    @Override
    public void actionPerformed(@CheckForNull ActionEvent e) {

        // --> 1. Get all valid designs
        Project project = Application.getInstance().getProject();
        Profile adm_profile = StereotypesHelper.getProfile(project, ADG_Element.adg_profile);
        Stereotype design_stereotype = StereotypesHelper.getStereotype(project, "Design", adm_profile);
        Package evaluator_pkg = DiagramsPackage.getAdgEvaluatorPackage(ADG_Diagram.getActiveDiagram());
        Collection<PackageableElement> design_packages = evaluator_pkg.getPackagedElement();
        Iterator<PackageableElement> pkg_iterator = design_packages.stream().iterator();
        ArrayList<PackageableElement> to_evaluate = new ArrayList<>();
        while(pkg_iterator.hasNext()){
            PackageableElement design_element = pkg_iterator.next();
            if(ADG_Element.hasStereotype(design_element, "Design")){
                to_evaluate.add(design_element);
            }
        }

        // --> 2. Evaluate
        String model_folder_path = Paths.get(ADG_Plugin.getPluginPath().toString(), "models").toString();
        for(PackageableElement design: to_evaluate){

            // --> Evaluate design / set
            String design_string = StereotypesHelper.getStereotypePropertyValue(design, design_stereotype, "DesignString").get(0).toString();
            JsonObject result = Graph.getInstance().evaluateDesignString(model_folder_path, design_string);
            result.addProperty("name", design.getName());
            String result_str = (new GsonBuilder().setPrettyPrinting().create()).toJson(result);
            StereotypesHelper.setStereotypePropertyValue(design, design_stereotype, "Objectives", result_str);
            // ADG_Element.showJsonElement("EVALUATED DESIGN", result);

            // --> Move design to processed folder
            Package processed_designs = DiagramsPackage.getAdgProcessedDesignsPackage(ADG_Diagram.getActiveDiagram());
            design.setOwner(processed_designs);
        }

    }
}