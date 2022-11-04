package adg.plugin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.openapi.uml.ModelElementsManager;
import com.nomagic.magicdraw.openapi.uml.PresentationElementsManager;
import com.nomagic.magicdraw.openapi.uml.ReadOnlyElementException;
import com.nomagic.magicdraw.openapi.uml.SessionManager;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.activities.mdfundamentalactivities.Activity;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.DirectedRelationship;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Profile;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;
import javax.swing.*;
import java.lang.reflect.Array;
import java.util.*;

public class ADG_Element {

    public static String adg_profile = "ADM Profile";  // ADGProfile | AMD Profile

    public static List<String> types = Arrays.asList("General Decision", "Down-selecting Decision", "Assigning Decision", "Partitioning Decision", "Combining Decision", "Permuting Decision", "Connecting Decision", "Decision");

    public static Map<String, String> type_map = new HashMap<String, String>() {{
        put("General Decision", "StandardForm");
        put("Down-selecting Decision", "DownSelecting");
        put("Assigning Decision", "Assigning");
        put("Partitioning Decision", "Partitioning");
        put("Combining Decision", "Combining");
        put("Permuting Decision", "Permuting");
        put("Connecting Decision", "Connecting");
    }};


    // --------------
    // --- Create ---
    // --------------

    public static Class createClassElement(String[] stereotypes){
        Project project = Application.getInstance().getProject();
        Class element = project.getElementsFactory().createClassInstance();
        Profile element_profile = StereotypesHelper.getProfile(project, ADG_Element.adg_profile);
        for(String stereotype: stereotypes){
            Stereotype element_stereotype = StereotypesHelper.getStereotype(project, stereotype, element_profile);
            StereotypesHelper.addStereotype(element, element_stereotype);
        }
        return element;
    }

    public static Activity createActivityElement(String[] stereotypes){
        Project project = Application.getInstance().getProject();
        Activity element = project.getElementsFactory().createActivityInstance();
        Profile element_profile = StereotypesHelper.getProfile(project, ADG_Element.adg_profile);
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
                if(ADG_Element.type_map.keySet().contains(ele_type)){
                    return ADG_Element.type_map.get(ele_type);
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

    public static ArrayList<Element> getDecisionDependencies(Element decision){
        ArrayList<Element> dependencies = new ArrayList<>();
        Collection<DirectedRelationship> relationships_source = decision.get_directedRelationshipOfSource();
        for(DirectedRelationship relationship: relationships_source){
            Element related_element = ADG_Element.getRelationshipElementTarget(relationship);
            if(ADG_Element.isDecision(related_element) || ADG_Element.isElementSet(related_element)){
                dependencies.add(related_element);
            }
        }
        return dependencies;
    }

    public static ArrayList<Element> getElementSetDependencies(Element element_set){
        ArrayList<Element> dependencies = new ArrayList<>();
        Collection<Element> owned_elements = element_set.getOwnedElement();
        for(Element ele: owned_elements){
            if(ele instanceof Property){
                Property prop = (Property) ele;
                dependencies.add(prop);
            }
        }
        return dependencies;
    }

    public static String getElementSetItemName(Element element){
        Property prop = (Property) element;
        return prop.getType().getName();
    }

    public static String getElementSetItemMultiplicity(Element element){
        // --> Property Multiplicity
        // [*] = (0, -1)
        // [0...1] = (0, 1)
        // [1] = (1, 1)
        // [1...*] = (1, -1)
        // [0...*] = (0, -1) -- Redundant -- not included
        Property prop = (Property) element;
        int lower = prop.getLower();
        int upper = prop.getUpper();
        if(lower == 0 && upper == -1){
            return "[*]";
        }
        else if(lower == 0 && upper == 1){
            return "[0...1]";
        }
        else if(lower == 1 && upper == 1){
            return "[1]";
        }
        else if(lower == 1 && upper == -1){
            return "[1...*]";
        }
        return "undefined";
    }

    public static DirectedRelationship getRelationship(Element parent, Element child){
        Collection<DirectedRelationship> relationships_source = child.get_directedRelationshipOfSource();
        for(DirectedRelationship relationship: relationships_source) {
            Element related_element = ADG_Element.getRelationshipElementTarget(relationship);
            if(related_element.getID() == parent.getID()){
                return relationship;
            }
        }
        return null;
    }



    // ------------------
    // --- Properties ---
    // ------------------

    public static boolean hasStereotype(Element element, String stereotype){
        ArrayList<String> ele_types = ADG_Element.getStereotypes(element);
        return ele_types.contains(stereotype);
    }

    public static boolean isDecision(Element element){
        for(String ele_type: ADG_Element.getStereotypes(element)){
            if(ADG_Element.types.contains(ele_type)){
                return true;
            }
        }
        return false;
    }

    public static boolean isLeafNode(Element decision){
        Collection<DirectedRelationship> relationships_source = decision.get_directedRelationshipOfTarget();
        return relationships_source.isEmpty();
    }


    public static boolean isElementSet(Element element){
        ArrayList<String> ele_types = ADG_Element.getStereotypes(element);
        return ele_types.contains("ElementSet");
    }

    public static boolean isRootDecision(Element element) {
        ArrayList<String> ele_types = ADG_Element.getStereotypes(element);
        return (ele_types.contains("Decision") && ele_types.contains("Root"));
    }




    // ------------------
    // --- Validation ---
    // ------------------

    public static boolean validateDecision(Element element){
        ArrayList<String> ele_types = ADG_Element.getStereotypes(element);
        if(!ADG_Element.isDecision(element)){
            return false;
        }

        // --> Must depend on: decision or element set
        ArrayList<Element> dependencies = ADG_Element.getDecisionDependencies(element);
        return !dependencies.isEmpty();
    }

    public static boolean validateElementSet(Element element){
        ArrayList<String> ele_types = ADG_Element.getStereotypes(element);
        if(!ADG_Element.isElementSet(element)){
            return false;
        }

        // --> Must have a set of dependency elements
        ArrayList<Element> dependencies = ADG_Element.getElementSetDependencies(element);
        return !dependencies.isEmpty();
    }


    // -----------------------
    // --- Debug Functions ---
    // -----------------------

    public static void showJsonElement(String description, JsonElement element){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JOptionPane.showMessageDialog(null, "---> " + description + "\n" + gson.toJson(element));
    }

    public static void showMessage(String description, String message){
        JOptionPane.showMessageDialog(null, "---> " + description + "\n" + message);
    }

    public static void showCameoElement(String description, Element message){
        JOptionPane.showMessageDialog(null, "---> " + description +
                "\n --> getHumanName() " + message.getHumanName() +
                "\n --> getHumanType() " + message.getHumanType());
    }

    public static void showResultsElement(HashMap<String, Double> results){
        String results_str = "";
        for(String key: results.keySet()){
            results_str += ("\n " + key + ": " + results.get(key));
        }
        JOptionPane.showMessageDialog(null, results_str);
    }
}
