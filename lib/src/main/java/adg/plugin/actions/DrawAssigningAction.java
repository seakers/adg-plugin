package adg.plugin.actions;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.properties.PropertyID;
import com.nomagic.magicdraw.properties.PropertyPool;
import com.nomagic.magicdraw.ui.actions.DrawShapeDiagramAction;
import com.nomagic.magicdraw.uml.symbols.PresentationElement;
import com.nomagic.ui.ScalableImageIcon;
import com.nomagic.ui.SquareIcon;
import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdports.Port;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Profile;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.util.Collection;

public class DrawAssigningAction extends DrawShapeDiagramAction {

    public static final String DRAW_ASSIGNING_ACTION = "DRAW_ASSIGNING_ACTION";

    public DrawAssigningAction()
    {
        super(DRAW_ASSIGNING_ACTION, "Assigning", KeyStroke.getKeyStroke(KeyEvent.VK_M, 0));
        //noinspection OverridableMethodCallDuringObjectConstruction,SpellCheckingInspection
        setLargeIcon(SquareIcon.fitOrCenter(new ScalableImageIcon(getClass(), "icons/myclass.svg"), 16));
    }

    /**
     * Creates model element
     *
     * @return created model element
     */
    @Override
    protected Element createElement()
    {
        Project project = Application.getInstance().getProject();

        // --> 1. Instantiate UML element / ports
        com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class element = project.getElementsFactory().createClassInstance();

        Port port_to   = project.getElementsFactory().createPortInstance();
        port_to.setName("assign to");
        port_to.setOwner(element);

        Port port_from = project.getElementsFactory().createPortInstance();
        port_from.setName("assign from");
        port_from.setOwner(element);

        // --> 2. Get ADG profile
        Profile adg_profile = StereotypesHelper.getProfile(project, "ADGProfile");

        // --> 3. Get appropriate stereotype for profile
        Stereotype decision_type = StereotypesHelper.getStereotype(project, "Decision", adg_profile);
        Stereotype root_type     = StereotypesHelper.getStereotype(project, "Assigning", adg_profile);

        // --> 4. Apply the stereotype to the element
        StereotypesHelper.addStereotype(element, decision_type);
        StereotypesHelper.addStereotype(element, root_type);

        // --> 5. Set element to active
        element.setActive(true);
        return element;
    }

    /**
     * Creates presentation element.
     *
     * @return created presentation element
     */
    @Override
    protected PresentationElement createPresentationElement()
    {
        PresentationElement presentationElement = super.createPresentationElement();
        presentationElement.addProperty(PropertyPool.getBooleanProperty(PropertyID.SUPPRESS_CLASS_ATTRIBUTES, true, "ATTRIBUTES"));
        presentationElement.addProperty(PropertyPool.getBooleanProperty(PropertyID.SUPPRESS_CLASS_OPERATIONS, true, "OPERATIONS"));
        return presentationElement;
    }

}
