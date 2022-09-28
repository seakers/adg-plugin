package adg.plugin.actions;

import adg.plugin.ADG_Descriptor;
import adg.plugin.decisions.Decision;
import adg.plugin.events.DiagramEvents;
import com.google.gson.JsonObject;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.ui.actions.DefaultDiagramAction;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Diagram;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;

import javax.annotation.CheckForNull;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Collection;

public class GenerateDesignSpace extends DefaultDiagramAction {

    private static final String ACTION_NAME = "Generate Design Space";

    public GenerateDesignSpace() {
        super(ACTION_NAME, ACTION_NAME, KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK), null);
    }



    @Override
    public void actionPerformed(@CheckForNull ActionEvent e) {
        Project project = Application.getInstance().getProject();

        // --> 1. Get ADG Design Space package
        DiagramPresentationElement adg_diagram_view = project.getActiveDiagram();
        if(!adg_diagram_view.getDiagramType().getType().equals(ADG_Descriptor.ARCHITECTURE_DECISION_GRAPH)){
            JOptionPane.showMessageDialog(null, "ERROR: active diagram must be of type ADG");
            return;
        }
        Diagram adg_diagram = project.getActiveDiagram().getDiagram();
        Package design_space_package = DiagramEvents.getAdgDesignSpacePackage(project, adg_diagram);
        JOptionPane.showMessageDialog(null, "BUILDING DESIGN SPACE 2");


        // --> 2. Build architecture
        Element arch_element = GenerateDesignSpace.buildDesignSpaceArchitecture(adg_diagram_view);
        arch_element.setOwner(design_space_package);


    }


    public static Element buildDesignSpaceArchitecture(DiagramPresentationElement adg_diagram_view){

        // --> 1. Create arch element
        Project project = Application.getInstance().getProject();
        com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class arch_element = project.getElementsFactory().createClassInstance();
        arch_element.setName("Architecture");



        // --> 2. Populate arch element
        Collection<Element> elements = adg_diagram_view.getUsedModelElements();
        for(Element element: elements){
            if(!Decision.is_decision(element))
                continue;
            if(Decision.is_root_decision(element))
                continue;






        }


        return arch_element;
    }












}
