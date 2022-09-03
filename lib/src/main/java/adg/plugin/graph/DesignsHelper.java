package adg.plugin.graph;

import adg.plugin.events.DiagramEvents;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;

public class DesignsHelper {


    public static void writeDesign(JsonObject design, int number){
        Project project = Application.getInstance().getProject();

        // --> 1. Get Adg Design Package
        Package designs_package = DiagramEvents.getAdgDesignPackage(project, project.getActiveDiagram().getDiagram());

        // --> 2. Create new package for new design (maybe ensure this doesn't exist first)
        Package design_pkg = project.getElementsFactory().createPackageInstance();
        design_pkg.setName("design-" + number);
        design_pkg.setOwner(designs_package);

        // --> 3. Recursively add design to its package
        DesignsHelper.writeDesignRecursive(design_pkg, design, project);
    }

    public static void writeDesignRecursive(Element owner, JsonObject data_source, Project project){
        for(String key: data_source.keySet()){
            if(key.equals("name") || key.equals("uid")){continue;}
            if(!data_source.get(key).isJsonArray()){continue;}

            // --> Each key links to a JsonArray; iterate over array and add elements
            JsonArray elements            = data_source.getAsJsonArray(key);
            String    elements_stereotype = key;
            // Package   elements_package    = project.getElementsFactory().createPackageInstance();
            // elements_package.setName(elements_stereotype);
            // elements_package.setOwner(owner);
            for(JsonElement element: elements){
                if(!element.isJsonObject()){continue;}
                JsonObject element_obj = element.getAsJsonObject();
                String element_name = element_obj.get("name").getAsString();
                com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class cameo_element = project.getElementsFactory().createClassInstance();
                cameo_element.setName(element_name);
                cameo_element.setOwner(owner);
                // cameo_element.setOwner(elements_package); // Class cannot own package !!!
                DesignsHelper.writeDesignRecursive(cameo_element, element_obj, project);
            }
        }
    }











}
