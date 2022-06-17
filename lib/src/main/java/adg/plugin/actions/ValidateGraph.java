package adg.plugin.actions;

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

import javax.annotation.CheckForNull;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ValidateGraph extends DefaultDiagramAction {

    private static final String ACTION_NAME = "Validate Graph";

    public ValidateGraph()
    {
        super(ACTION_NAME, ACTION_NAME, KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK), null);
    }

    @Override
    public void actionPerformed(@CheckForNull ActionEvent e) {
        MainFrame mainFrame = Application.getInstance().getMainFrame();
        JOptionPane.showMessageDialog(mainFrame, "Executing action...");

        // --> 1. Get current project
        Project project = Application.getInstance().getProject();

        // --> 2. Validate graph
        this.validate(project);



//        // --> 3. Get all Enumeration elements from model root
//        java.lang.Class<?>[] types = new java.lang.Class<?>[]{Enumeration.class};
//        Collection<Enumeration> elements = Finder.byType().find(model, types, false);
//
//        // --> 4. Iterate over enumeration elements and handle
//        JOptionPane.showMessageDialog(mainFrame, Integer.toString(elements.size()) + " Enum Elements");
//        for(Enumeration element: elements){
//            String element_name = element.getName();
//            String element_type = element.getHumanType();
//            String element_info = element_name + ": ";
//            List<EnumerationLiteral> options = element.getOwnedLiteral();
//            for(EnumerationLiteral option: options){
//                element_info += (option.getName() + " ");
//            }
//            JOptionPane.showMessageDialog(mainFrame, element_info);
//
////            List<Property> properties = element.getOwnedAttribute();
////            for(Property prop: properties){
////                String prop_info = prop.getName() + " - " + prop.getDefaultValue().toString();
////                JOptionPane.showMessageDialog(mainFrame, prop_info);
////            }
//
//        }
    }

    public void validate(Project project){
        MainFrame mainFrame = Application.getInstance().getMainFrame();

        // --> 1. Get ADG Diagram
        DiagramPresentationElement diagram = project.getActiveDiagram();
        Collection<Element> diagram_elements = diagram.getUsedModelElements();
        // List<PresentationElement>  diagram_elements = diagram.getPresentationElements();

        // --> 2. Extract design decisions
        this.extractDecisions(diagram_elements);




    }


    public ArrayList<Element> extractDecisions(Collection<Element> diagram_elements){
        MainFrame mainFrame = Application.getInstance().getMainFrame();

        ArrayList<Element> decision_elements = new ArrayList<>();
        for(Element element: diagram_elements){
            List<Stereotype> element_types = element.getAppliedStereotype();
            for(Stereotype element_type: element_types){
                String element_type_name = element_type.getName();
                if(element_type_name.equalsIgnoreCase("Decision")){
                    decision_elements.add(element);
                    break;
                }
            }
        }

        String info = "Number of Decisions: " + decision_elements.size();
        JOptionPane.showMessageDialog(mainFrame, info);
        return decision_elements;
    }



}
