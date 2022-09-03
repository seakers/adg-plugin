package adg.plugin.actions;

import adg.plugin.graph.AlgorithmParameters;
import adg.plugin.graph.ConnectionParameters;
import com.nomagic.magicdraw.ui.actions.DefaultDiagramAction;

import javax.annotation.CheckForNull;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class SetAlgorithm extends DefaultDiagramAction {


    private static final String ACTION_NAME = "Algorithm Parameters";

    public SetAlgorithm()
    {
        super(ACTION_NAME, ACTION_NAME, KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK), null);
    }

    @Override
    public void actionPerformed(@CheckForNull ActionEvent e) {
        AlgorithmParameters algorithm = AlgorithmParameters.getInstance();
        algorithm.setAlgorithmParameters();
    }


}
