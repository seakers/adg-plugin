package adg.plugin.actions;


import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.properties.PropertyID;
import com.nomagic.magicdraw.properties.PropertyPool;
import com.nomagic.magicdraw.ui.actions.DrawShapeDiagramAction;
import com.nomagic.magicdraw.uml.symbols.PresentationElement;
import com.nomagic.ui.ScalableImageIcon;
import com.nomagic.ui.SquareIcon;
import com.nomagic.uml2.StandardProfile;
import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;

import javax.swing.*;
import java.awt.event.KeyEvent;

/**
 * Action for drawing entity element.
 *
 * @author Mindaugas Ringys
 */
public class DrawDownSelectingAction extends DrawShapeDiagramAction {
    public static final String DRAW_DOWN_SELECTING_ACTION = "DRAW_DOWN_SELECTING_ACTION";

    public DrawDownSelectingAction()
    {
        super(DRAW_DOWN_SELECTING_ACTION, "Down Selecting", KeyStroke.getKeyStroke(KeyEvent.VK_M, 0));
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

        // --> 1. Instantiate UML element
        com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class elementClass = Application.getInstance().getProject().getElementsFactory().createClassInstance();

        // --> 2. Apply stereotype
        // Project project = Application.getInstance().getProject();
        // StereotypesHelper.addStereotype(elementClass, StandardProfile.getInstance(project).getEntity());


        // --> 3. Set element to active
        elementClass.setActive(true);
        return elementClass;
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
