package adg.plugin.actions;

import adg.plugin.graph.AlgorithmParameters;
import adg.plugin.graph.DesignsHelper;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.ui.actions.DefaultDiagramAction;
import graph.Graph;

import javax.annotation.CheckForNull;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class CrossoverDesigns extends DefaultDiagramAction {

    private static final String ACTION_NAME = "Crossover Designs";

    public CrossoverDesigns()
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

        // --> 2. Validate graph has more than 1 design
        if(graph.designs.size() < 2){
            JOptionPane.showMessageDialog(null, "ERROR: must have more than 1 generated designs");
            return;
        }

        // --> 3. Get designs to crossover
        ArrayList<Integer> parents = this.getParentDesigns(graph.designs.size());
        if(parents.isEmpty() || parents.size() < 2){
            JOptionPane.showMessageDialog(null, "WARNING: no parents were selected, aborting");
            return;
        }

        // --> 4. Crossover designs and write
        try{
            double mutation_prob = (AlgorithmParameters.getInstance().mutation_probability * 1.0) / 100;
            int papa = parents.get(0);
            int mama = parents.get(1);
            int design_idx = graph.crossoverDesigns(papa, mama, mutation_prob);
            JOptionPane.showMessageDialog(null, "SUCCESS: child design id " + design_idx + " - writing design to project");
            DesignsHelper.writeDesign(graph.designs.get(design_idx).getAsJsonObject(), design_idx);
        }
        catch (Exception ex){
            ex.printStackTrace();
        }



    }

    public ArrayList<Integer> getParentDesigns(int num_designs){
        ArrayList<Integer> parents = new ArrayList<>();

        // --> 1. Create panel and set layout
        JPanel myPanel = new JPanel();
        myPanel.setLayout(new GridLayout(2, 2));

        // --> Parent comboboxes
        JLabel papa_label = new JLabel("Parent Design 1");
        JLabel mama_label = new JLabel("Parent Design 2");
        JComboBox papa_design_box = new JComboBox();
        JComboBox mama_design_box = new JComboBox();
        for(int x = 0; x < num_designs; x++){
            papa_design_box.addItem(x);
            mama_design_box.addItem(x);
        }
        myPanel.add(papa_label);
        myPanel.add(papa_design_box);
        myPanel.add(mama_label);
        myPanel.add(mama_design_box);


        int result = JOptionPane.showConfirmDialog(null, myPanel,
                "Algorithm Parameters", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            parents.add(Integer.parseInt(papa_design_box.getSelectedItem().toString()));
            parents.add(Integer.parseInt(mama_design_box.getSelectedItem().toString()));
        }

        return parents;
    }




}