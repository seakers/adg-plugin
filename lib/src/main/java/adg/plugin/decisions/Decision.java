package adg.plugin.decisions;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.DirectedRelationship;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;

import javax.swing.*;
import java.util.*;

public class Decision {

    public static String[] types = new String[]{"DownSelecting", "Assigning", "Partitioning", "Permuting", "Connecting", "StandardForm"};

    public static ArrayList<String> get_stereotypes(Element element){
        ArrayList<String> return_list = new ArrayList<>();
        List<Stereotype> element_stereotypes = element.getAppliedStereotype();
        for(Stereotype stereo: element_stereotypes){
            return_list.add(stereo.getName());
        }
        return return_list;
    }

    public static String get_decision_type(Element element){
        if(Decision.is_decision(element)){
            ArrayList<String> ele_types = Decision.get_stereotypes(element);
            for(String ele_type: ele_types){
                if(Arrays.asList(Decision.types).contains(ele_type)){
                    return ele_type;
                }
            }
        }
        return "";
    }

    public static String get_element_name(Element element){
        String human_name = element.getHumanName();
        String[] arr = human_name.split(" ", 2);
        return arr[1];
    }


    public static Element get_relationship_element_target(DirectedRelationship relationship){
        Collection<Element> target_elements = relationship.getTarget();
        Element inner_element = target_elements.iterator().next();
        return inner_element;
    }

    public static Element get_relationship_element_source(DirectedRelationship relationship){
        Collection<Element> target_elements = relationship.getSource();
        Element inner_element = target_elements.iterator().next();
        return inner_element;
    }



    public static boolean is_decision(Element element){
        ArrayList<String> ele_types = Decision.get_stereotypes(element);
        return ele_types.contains("Decision");
    }

    public static boolean is_root_decision(Element element) {
        ArrayList<String> ele_types = Decision.get_stereotypes(element);
        return (ele_types.contains("Decision") && ele_types.contains("Root"));
    }



    public static void showJsonElement(String description, JsonElement element){
        JOptionPane.showMessageDialog(null, "---> " + description + " | " + new Gson().toJson(element));
    }
}
