package adg.plugin;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.openapi.uml.ModelElementsManager;
import com.nomagic.magicdraw.openapi.uml.PresentationElementsManager;
import com.nomagic.magicdraw.openapi.uml.ReadOnlyElementException;
import com.nomagic.magicdraw.openapi.uml.SessionManager;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.DirectedRelationship;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Profile;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import javax.swing.*;
import java.util.*;

public class ADG_Element {

    public static String[] types = new String[]{"DownSelecting", "Assigning", "Partitioning", "Permuting", "Connecting", "StandardForm"};


    // --------------
    // --- Create ---
    // --------------

    public static Class createClassElement(String profile, String[] stereotypes){
        Project project = Application.getInstance().getProject();
        Class element = project.getElementsFactory().createClassInstance();
        Profile element_profile = StereotypesHelper.getProfile(project, profile);
        for(String stereotype: stereotypes){
            Stereotype element_stereotype = StereotypesHelper.getStereotype(project, stereotype, element_profile);
            StereotypesHelper.addStereotype(element, element_stereotype);
        }
        return element;
    }



    // --------------
    // --- Delete ---
    // --------------

    public static void deleteElement(Element element){
        Project project = Application.getInstance().getProject();
        SessionManager.getInstance().createSession(project, "Remove class");
        try {
            ModelElementsManager.getInstance().removeElement(element);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        SessionManager.getInstance().closeSession(project);
    }

    public static void deleteDiagram(DiagramPresentationElement diagram_view){
        Project project = Application.getInstance().getProject();
        SessionManager.getInstance().createSession(project, "Remove class");
        try {
            PresentationElementsManager.getInstance().deletePresentationElement(diagram_view);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        SessionManager.getInstance().closeSession(project);
    }


    // ---------------
    // --- Getters ---
    // ---------------

    public static ArrayList<String> getStereotypes(Element element){
        ArrayList<String> return_list = new ArrayList<>();
        List<Element> element_list = Arrays.asList(element);
        Set<Stereotype> element_stereotypes = StereotypesHelper.getAllAssignedStereotypes(element_list);
        for(Stereotype stype: element_stereotypes){
            return_list.add(stype.getName());
        }
        // ADG_Element.showMessage("ELEMENT STEREOTYPES", String.join(", ", return_list));
        return return_list;
    }

    public static String getDecisionType(Element element){
        if(ADG_Element.isDecision(element)){
            ArrayList<String> ele_types = ADG_Element.getStereotypes(element);
            for(String ele_type: ele_types){
                if(Arrays.asList(ADG_Element.types).contains(ele_type)){
                    return ele_type;
                }
            }
        }
        return "";
    }

    public static String getElementName(Element element){
        String human_name = element.getHumanName();
        String[] arr = human_name.split(" ", 2);
        return arr[1];
    }

    public static Element getRelationshipElementTarget(DirectedRelationship relationship){
        Collection<Element> target_elements = relationship.getTarget();
        Element inner_element = target_elements.iterator().next();
        return inner_element;
    }

    public static Element getRelationshipElementSource(DirectedRelationship relationship){
        Collection<Element> target_elements = relationship.getSource();
        Element inner_element = target_elements.iterator().next();
        return inner_element;
    }


    // ------------------
    // --- Properties ---
    // ------------------

    public static boolean hasStereotype(Element element, String stereotype){
        ArrayList<String> ele_types = ADG_Element.getStereotypes(element);
        return ele_types.contains(stereotype);
    }

    public static boolean isDecision(Element element){
        ArrayList<String> ele_types = ADG_Element.getStereotypes(element);
        return ele_types.contains("Decision");
    }

    public static boolean validateDecision(Element element){
        ArrayList<String> ele_types = ADG_Element.getStereotypes(element);
        if(!ele_types.contains("Decision")){
            return false;
        }

        // --> Ensure each decision is dependent on either another decision or an element set


        return true;
    }

    public static boolean isElementSet(Element element){
        ArrayList<String> ele_types = ADG_Element.getStereotypes(element);
        return ele_types.contains("ElementSet");
    }

    public static boolean validateElementSet(Element element){
        ArrayList<String> ele_types = ADG_Element.getStereotypes(element);
        if(!ele_types.contains("ElementSet")){
            return false;
        }

        // --> Ensure each element set has objects in the set


        return true;
    }

    public static boolean isRootDecision(Element element) {
        ArrayList<String> ele_types = ADG_Element.getStereotypes(element);
        return (ele_types.contains("Decision") && ele_types.contains("Root"));
    }


    // -----------------------
    // --- Debug Functions ---
    // -----------------------

    public static void showJsonElement(String description, JsonElement element){
        JOptionPane.showMessageDialog(null, "---> " + description + " | " + new Gson().toJson(element));
    }

    public static void showMessage(String description, String message){
        JOptionPane.showMessageDialog(null, "---> " + description + " | " + message);
    }

    public static void showCameoElement(String description, Element message){
        JOptionPane.showMessageDialog(null, "---> " + description + " | " + message.getHumanName() + " | " + message.getHumanType());
    }



}
