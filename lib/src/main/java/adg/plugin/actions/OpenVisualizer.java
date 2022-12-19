package adg.plugin.actions;

import adg.plugin.viz.Visualizer;
import com.nomagic.magicdraw.ui.actions.DefaultDiagramAction;

import javax.annotation.CheckForNull;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class OpenVisualizer extends DefaultDiagramAction {


    private static final String ACTION_NAME = "Open Visualizer";

    public OpenVisualizer()
    {
        super(ACTION_NAME, ACTION_NAME, KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK), null);
    }

    @Override
    public void actionPerformed(@CheckForNull ActionEvent e) {
        Visualizer vis = Visualizer.getInstance();
        vis.open_visualizer();
    }
}