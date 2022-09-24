package adg.plugin.decisions;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Decision {

    public static ArrayList<String> get_stereotypes(Element element){
        ArrayList<String> return_list = new ArrayList<>();
        List<Stereotype> element_stereotypes = element.getAppliedStereotype();
        for(Stereotype stereo: element_stereotypes){
            return_list.add(stereo.getName());
        }
        return return_list;
    }

    public static String get_decision_type(Element element){
        ArrayList<String> ele_types = Decision.get_stereotypes(element);
        for(String ele_type: ele_types){
            if(!Objects.equals(ele_type, "Decision")){
                return ele_type;
            }
        }
        JOptionPane.showMessageDialog(null, "--> NO DECISION SPECIFIED IN ELEMENT " + element.getHumanName());
        return "empty";
    }

    public static String get_decision_name(Element element){
        String human_name = element.getHumanName();
        String[] arr = human_name.split(" ", 2);
        return arr[1];
    }



}
