package adg.plugin.graph;

import adg.plugin.ADG_Diagram;
import adg.plugin.ADG_Element;
import adg.plugin.packages.DiagramsPackage;
import com.google.gson.*;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Profile;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;

public class DesignsHelper {


    public static void writeEnumeratedDesignOld(JsonArray design, int number){
        Project project = Application.getInstance().getProject();

        // --> 1. Get design element
        Element design_element = DiagramsPackage.createDesignElement(ADG_Diagram.getActiveDiagram(), number);

        // --> 2. Get design string and add as tag
        Profile adm_profile = StereotypesHelper.getProfile(project, ADG_Element.adg_profile);
        Stereotype design_stereotype = StereotypesHelper.getStereotype(project, "Design", adm_profile);
        String design_str = (new GsonBuilder().setPrettyPrinting().create()).toJson(design);
        StereotypesHelper.setStereotypePropertyValue(design_element, design_stereotype, "DesignString", design_str);

        // --> 3. Recursively fill design
        for(JsonElement element: design){
            if(element.isJsonObject()){
                DesignsHelper.writeDesignRecursiveOld(design_element, element.getAsJsonObject(), project);
            }
        }
    }

    public static void writeDesignOld(JsonArray design, int number){
        ADG_Element.showJsonElement("WRITING DESIGN", design);

        Project project = Application.getInstance().getProject();

        // --> 1. Get Adg Design Package
        Package designs_package = DiagramsPackage.getAdgGeneratedDesignsPackage(project, project.getActiveDiagram().getDiagram());

        // --> 2. Create new package for new design (maybe ensure this doesn't exist first)
        Package design_pkg = project.getElementsFactory().createPackageInstance();
        design_pkg.setName("design-" + number);
        design_pkg.setOwner(designs_package);

        for(JsonElement element: design){
            if(element.isJsonObject()){
                DesignsHelper.writeDesignRecursiveOld(design_pkg, element.getAsJsonObject(), project);
            }
        }
    }


    public static void writeDesignRecursiveOld(Element owner, JsonObject data_source, Project project){
        boolean active = data_source.get("active").getAsBoolean();
        if(!active){
            return;
        }
        String type = data_source.get("type").getAsString();

        String name = "group-" + owner.getOwnedElement().size();
        if(data_source.has("id")){
            name += (" (id: " + data_source.get("id").getAsString() + ")");
        }


        if(data_source.has("name")){
            name = data_source.get("name").getAsString();
        }


        com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class cameo_element = project.getElementsFactory().createClassInstance();
        cameo_element.setName(name);
        cameo_element.setOwner(owner);
        if(type == "list" && data_source.has("elements")){
            JsonArray elements = data_source.getAsJsonArray("elements");
            for(JsonElement element: elements){
                if(element.isJsonObject()){
                    DesignsHelper.writeDesignRecursiveOld(cameo_element, element.getAsJsonObject(), project);
                }
            }
        }
    }







    public static void writeDesign(JsonObject design, int number){
        Project project = Application.getInstance().getProject();

        // --> 1. Get Adg Design Package
        Package designs_package = DiagramsPackage.getAdgGeneratedDesignsPackage(project, project.getActiveDiagram().getDiagram());

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
