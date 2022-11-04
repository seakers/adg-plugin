package adg.plugin.decisions;


import adg.plugin.events.DiagramEvents;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.properties.PropertyID;
import com.nomagic.magicdraw.properties.PropertyPool;
import com.nomagic.magicdraw.ui.actions.DrawShapeDiagramAction;
import com.nomagic.magicdraw.uml.symbols.PresentationElement;
import com.nomagic.ui.ScalableImageIcon;
import com.nomagic.ui.SquareIcon;
import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Diagram;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdports.Port;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Profile;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;
import adg.plugin.packages.DiagramsPackage;

import javax.swing.*;
import java.awt.event.KeyEvent;

/**
 * Action for drawing entity element.
 *
 * @author Gabe Apaza
 */
public class StandardFormDecision extends DrawShapeDiagramAction {
    public static final String DRAW_STANDARD_FORM_ACTION = "DRAW_STANDARD_FORM_ACTION";

    public StandardFormDecision()
    {
        super(DRAW_STANDARD_FORM_ACTION, "Standard Form", KeyStroke.getKeyStroke(KeyEvent.VK_M, 0));
        setLargeIcon(SquareIcon.fitOrCenter(new ScalableImageIcon(getClass(), "icons/myclass.svg"), 16));
    }

    @Override
    protected Element createElement()
    {
        Project project = Application.getInstance().getProject();

        // --> 1. Create element
        Diagram adg_diagram = project.getActiveDiagram().getDiagram();
        Class element = DiagramsPackage.createDecisionElement(adg_diagram, new String[]{ "General Decision" });
        element.setActive(true);
        return element;
    }

    @Override
    protected PresentationElement createPresentationElement()
    {
        PresentationElement presentationElement = super.createPresentationElement();
        presentationElement.addProperty(PropertyPool.getBooleanProperty(PropertyID.SUPPRESS_CLASS_ATTRIBUTES, true, "ATTRIBUTES"));
        presentationElement.addProperty(PropertyPool.getBooleanProperty(PropertyID.SUPPRESS_CLASS_OPERATIONS, true, "OPERATIONS"));
        return presentationElement;
    }
}
