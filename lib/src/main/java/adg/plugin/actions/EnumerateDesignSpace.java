package adg.plugin.actions;

import adg.plugin.graph.DesignsHelper;
import adg.plugin.packages.DiagramsPackage;
import com.google.gson.JsonArray;
import com.nomagic.magicdraw.ui.actions.DefaultDiagramAction;
import graph.Graph;

import javax.annotation.CheckForNull;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class EnumerateDesignSpace extends DefaultDiagramAction {


    private static final String ACTION_NAME = "Enumerate Decision Graph";


    public EnumerateDesignSpace()
    {
        super(ACTION_NAME, ACTION_NAME, KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK), null);
    }



    @Override
    public void actionPerformed(@CheckForNull ActionEvent e) {

        // --> 1. Get adg graph instance and validate it is built
        Graph graph = Graph.getInstance();
        if(!graph.is_built){
            JOptionPane.showMessageDialog(null, "ERROR: graph not built");
            return;
        }

        // --> 2. Enumerate design space
        try{
            graph.enumerateDesignSpace();
            JOptionPane.showMessageDialog(null, "SUCCESS: writing enumerated designs");

            // Clear any current designs in design space
            DiagramsPackage.cleanAdgDesignSpacePackage();

            ArrayList<JsonArray> designs = graph.getEnumeratedDesignObjects();
            int counter = 0;
            for(JsonArray design: designs){
                DesignsHelper.writeEnumeratedDesignOld(design, counter);
                counter += 1;
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }




    }



}
