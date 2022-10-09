package adg.plugin.actions;

import adg.plugin.ADG_Diagram;
import adg.plugin.ADG_Element;
import adg.plugin.packages.DiagramsPackage;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.ui.actions.DefaultDiagramAction;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.*;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;

import javax.annotation.CheckForNull;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class GenerateSystemArchitectureModel extends DefaultDiagramAction {

    private static final String ACTION_NAME = "Generate System Architecture Model";

    public GenerateSystemArchitectureModel() {
        super(ACTION_NAME, ACTION_NAME, KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK), null);
    }



    @Override
    public void actionPerformed(@CheckForNull ActionEvent e) {
        Project project = Application.getInstance().getProject();

        // --> 1. Get ADG Diagram
        DiagramPresentationElement adg_diagram_view = ADG_Diagram.getActiveDiagramView();
        if(adg_diagram_view == null){
            ADG_Element.showMessage("getActiveDiagramView", "null");
            return;
        }
        Diagram adg_diagram = ADG_Diagram.getActiveDiagram();


        // --> 2. Check to see if previous generation exists
        Package sys_arch_pkg = DiagramsPackage.getAdgSystemArchitecturePackage(adg_diagram);
        Class arch_model = DiagramsPackage.getAdgSystemArchitectureModel(adg_diagram);
        if(arch_model != null){
            int dialogButton = JOptionPane.YES_NO_OPTION;
            int dialogResult = JOptionPane.showConfirmDialog (null, "Warning, this will overwrite the last generated architecture model... Continue?","Warning",dialogButton);
            if(dialogResult == JOptionPane.NO_OPTION){
                return;
            }
            else {
                DiagramsPackage.cleanAdgSystemArchitectureModel(adg_diagram);
            }
        }

        // --> 3. Create System Architecture BDD
        Diagram sys_arch_diagram = DiagramsPackage.createAdgSystemArchitectureDiagram(adg_diagram);
        if(sys_arch_diagram == null){
            ADG_Element.showMessage("createAdgSystemArchitectureDiagram", "null");
            return;
        }

        // --> 4. Create System Architecture Model
        GenerateSystemArchitectureModel.buildSystemArchitectureModel(adg_diagram_view, sys_arch_diagram);

    }



    public static Element buildSystemArchitectureModel(DiagramPresentationElement adg_diagram_view, Diagram sys_arch_diagram){
        Project project = Application.getInstance().getProject();

        // --> 1. Get System Architecture Package
        Package sys_arch_pkg = DiagramsPackage.getAdgSystemArchitecturePackage(adg_diagram_view.getDiagram());

        // --> 2. Create System Architecture root element
        Class arch_element = ADG_Element.createClassElement("ADGProfile", new String[]{"SystemArchitecture"});
        arch_element.setName("System Architecture");
        arch_element.setActive(true);
        arch_element.setOwner(sys_arch_pkg);

        // --> 3. Get diagram elements
        ArrayList<Element> decisions = ADG_Diagram.getDecisions();
        ArrayList<Element> element_sets = ADG_Diagram.getElementSets();


        // --> 4. Validate diagram elements
        for(Element ele: decisions){
            ADG_Element.showCameoElement("validating decision", ele);
            ADG_Element.validateDecision(ele);
        }
        for(Element ele: element_sets){
            ADG_Element.showCameoElement("validating element set", ele);
            ADG_Element.validateElementSet(ele);
        }

        // --> 4. Make sure each decision is dependent on either another decision or an element set



//        // --> .... Populate arch element
//        Collection<Element> elements = adg_diagram_view.getUsedModelElements();
//        for(Element element: elements){
//            if(!Decision.is_decision(element))
//                continue;
//            if(Decision.is_root_decision(element))
//                continue;
//
//
//
//
//
//
//        }


        return arch_element;
    }


}
