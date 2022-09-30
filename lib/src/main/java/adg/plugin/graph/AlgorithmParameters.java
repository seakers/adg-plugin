package adg.plugin.graph;


import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.Arrays;

public class AlgorithmParameters {

    private static AlgorithmParameters instance = new AlgorithmParameters();

    public static AlgorithmParameters getInstance() { return instance; }
    public int mutation_probability = 5;
    public int crossover_probability = 5;


    public void setAlgorithmParameters(){

        // --> 1. Create panel and set layout
        JPanel myPanel = new JPanel();
        myPanel.setLayout(new GridLayout(2, 2));

        // --> Mutation Probability
        String mutation_prob_label_txt = "Mutation Probability: ";
        JLabel mutation_prob_label = new JLabel(mutation_prob_label_txt + this.mutation_probability + "%");
        JSlider mutation_prob_slide = new JSlider(0, 100, this.mutation_probability);
        mutation_prob_slide.setMajorTickSpacing(25);
        mutation_prob_slide.setMinorTickSpacing(5);
        mutation_prob_slide.setPaintTicks(true);
        mutation_prob_slide.setPaintLabels(true);
        mutation_prob_slide.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                mutation_prob_label.setText(mutation_prob_label_txt + mutation_prob_slide.getValue() + "%");
            }
        });
        myPanel.add(mutation_prob_label);
        myPanel.add(mutation_prob_slide);

        // --> Crossover Probability
        String crossover_prob_label_txt = "Crossover Probability: ";
        JLabel crossover_prob_label = new JLabel(crossover_prob_label_txt + this.crossover_probability + "%");
        JSlider crossover_prob_slide = new JSlider(0, 100, this.crossover_probability);
        crossover_prob_slide.setMajorTickSpacing(25);
        crossover_prob_slide.setMinorTickSpacing(5);
        crossover_prob_slide.setPaintTicks(true);
        crossover_prob_slide.setPaintLabels(true);
        crossover_prob_slide.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                crossover_prob_label.setText(crossover_prob_label_txt + crossover_prob_slide.getValue() + "%");
            }
        });
        myPanel.add(crossover_prob_label);
        myPanel.add(crossover_prob_slide);

        // --> Display and get results
        int result = JOptionPane.showConfirmDialog(null, myPanel,
                "Algorithm Parameters", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            this.mutation_probability = mutation_prob_slide.getValue();
            this.crossover_probability = crossover_prob_slide.getValue();
        }
    }







}
