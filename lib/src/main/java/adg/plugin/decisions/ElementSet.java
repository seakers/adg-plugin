package adg.plugin.decisions;

import adg.plugin.ADG_Element;
import adg.plugin.events.DiagramEvents;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.properties.PropertyID;
import com.nomagic.magicdraw.properties.PropertyPool;
import com.nomagic.magicdraw.ui.actions.DrawShapeDiagramAction;
import com.nomagic.magicdraw.uml.symbols.PresentationElement;
import com.nomagic.ui.ScalableImageIcon;
import com.nomagic.ui.SquareIcon;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Diagram;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
import adg.plugin.packages.DiagramsPackage;

import javax.swing.*;
import java.awt.event.KeyEvent;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;


public class ElementSet extends DrawShapeDiagramAction {

    public static final String DRAW_ELEMENT_SET_ACTION = "DRAW_ELEMENT_SET_ACTION";

    public ElementSet() {
        super(DRAW_ELEMENT_SET_ACTION, "Element Set", KeyStroke.getKeyStroke(KeyEvent.VK_M, 0));
        //noinspection OverridableMethodCallDuringObjectConstruction,SpellCheckingInspection
        setLargeIcon(SquareIcon.fitOrCenter(new ScalableImageIcon(getClass(), "icons/myclass.svg"), 16));
    }

    @Override
    protected Element createElement(){
        Project project = Application.getInstance().getProject();

        // --> 1. Create element
        Diagram adg_diagram = project.getActiveDiagram().getDiagram();
        Class element = DiagramsPackage.createDecisionElement(adg_diagram, new String[]{ "ElementSet" });
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
