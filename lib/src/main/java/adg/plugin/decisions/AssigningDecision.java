package adg.plugin.decisions;

import adg.plugin.ADG_Element;
import adg.plugin.events.DiagramEvents;
import adg.plugin.packages.DiagramsPackage;
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
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdports.Port;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Profile;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;

import javax.swing.*;
import java.awt.event.KeyEvent;

public class AssigningDecision extends DrawShapeDiagramAction {

    public static final String DRAW_ASSIGNING_ACTION = "DRAW_ASSIGNING_ACTION";

    public AssigningDecision() {
        super(DRAW_ASSIGNING_ACTION, "Assigning", KeyStroke.getKeyStroke(KeyEvent.VK_M, 0));
        setLargeIcon(SquareIcon.fitOrCenter(new ScalableImageIcon(getClass(), "icons/myclass.svg"), 16));
    }

    @Override
    protected Element createElement() {
        Project project = Application.getInstance().getProject();

        // --> 1. Create element
        Diagram adg_diagram = project.getActiveDiagram().getDiagram();
        Class element = DiagramsPackage.createDecisionElement(adg_diagram, new String[]{ "Decision", "Assigning" });
        element.setActive(true);

        // --> 2. Add ports
        Port port_to = project.getElementsFactory().createPortInstance();
        port_to.setName("to");
        port_to.setOwner(element);
        Port port_from = project.getElementsFactory().createPortInstance();
        port_from.setName("from");
        port_from.setOwner(element);
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
