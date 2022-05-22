package adg.plugin.actions;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.ui.MainFrame;
import com.nomagic.magicdraw.ui.actions.DefaultDiagramAction;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;

import javax.annotation.CheckForNull;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class TestingAction extends DefaultDiagramAction {

    private static final String ACTION_NAME = "Test Action";

    public TestingAction()
    {
        super(ACTION_NAME, ACTION_NAME, KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK), null);
    }

    @Override
    public void actionPerformed(@CheckForNull ActionEvent e)
    {
        MainFrame mainFrame = Application.getInstance().getMainFrame();
        JOptionPane.showMessageDialog(mainFrame, "Executing action...");

        // --> 1. Get current project
        Project project = Application.getInstance().getProject();

        // --> 2. Get root model
        Package model = project.getPrimaryModel();

        // --> TODO: determine how to traverse elements by type for validation


        JOptionPane.showMessageDialog(mainFrame, "Action finished...");
    }

}
