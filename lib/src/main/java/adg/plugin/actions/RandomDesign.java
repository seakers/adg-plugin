package adg.plugin.actions;

import adg.plugin.ADG_Descriptor;
import adg.plugin.events.DiagramEvents;
import adg.plugin.graph.AlgorithmParameters;
import adg.plugin.graph.ConnectionParameters;
import adg.plugin.graph.DesignsHelper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.nomagic.ci.persistence.local.a.J;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.ui.MainFrame;
import com.nomagic.magicdraw.ui.actions.DefaultDiagramAction;
import com.nomagic.magicdraw.uml.Finder;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.magicdraw.uml.symbols.PresentationElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Diagram;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;
import com.teamdev.jxbrowser.js.Json;
import graph.Graph;

import javax.annotation.CheckForNull;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RandomDesign extends DefaultDiagramAction {

    private static final String ACTION_NAME = "Generate Random Design";

    public RandomDesign()
    {
        super(ACTION_NAME, ACTION_NAME, KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK), null);
    }

    @Override
    public void actionPerformed(@CheckForNull ActionEvent e) {
        Project project = Application.getInstance().getProject();

        // --> 1. Get adg graph instance and validate it is built
        Graph graph = Graph.getInstance();
        if(!graph.isBuilt){
            JOptionPane.showMessageDialog(null, "ERROR: graph not built");
            return;
        }

        // --> 2. Generate random design
        try{
            int design_idx = graph.generateRandomDesign();
            JOptionPane.showMessageDialog(null, "SUCCESS: random design id " + design_idx + " - writing design to project");
            DesignsHelper.writeDesign(graph.designs.get(design_idx).getAsJsonObject(), design_idx);
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }


}
