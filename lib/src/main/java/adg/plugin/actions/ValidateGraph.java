package adg.plugin.actions;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.ui.MainFrame;
import com.nomagic.magicdraw.ui.actions.DefaultDiagramAction;
import com.nomagic.magicdraw.uml.Finder;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.magicdraw.uml.symbols.PresentationElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.*;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;

import javax.annotation.CheckForNull;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ValidateGraph extends DefaultDiagramAction {

    private static final String ACTION_NAME = "Validate Graph";

    public ValidateGraph()
    {
        super(ACTION_NAME, ACTION_NAME, KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK), null);
    }

    @Override
    public void actionPerformed(@CheckForNull ActionEvent e) {
        MainFrame mainFrame = Application.getInstance().getMainFrame();
        JOptionPane.showMessageDialog(mainFrame, "Executing action...");

        // --> 1. Get current project
        Project project = Application.getInstance().getProject();

        // --> 2. Validate graph
        this.validate(project);


    }

    public void validate(Project project){
        MainFrame mainFrame = Application.getInstance().getMainFrame();

        // --> 1. Get ADG Diagram
        DiagramPresentationElement diagram = project.getActiveDiagram();
        Collection<Element> diagram_elements = diagram.getUsedModelElements();
        // List<PresentationElement>  diagram_elements = diagram.getPresentationElements();

        // --> 2. Extract design decisions




    }


}
