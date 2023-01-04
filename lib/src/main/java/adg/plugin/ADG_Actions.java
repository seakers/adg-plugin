package adg.plugin;

import adg.plugin.decisions.*;
import com.nomagic.actions.ActionsCategory;
import com.nomagic.actions.ActionsManager;
import com.nomagic.actions.NMAction;
import com.nomagic.magicdraw.actions.ActionsCreator;
import com.nomagic.magicdraw.actions.ActionsProvider;
import com.nomagic.magicdraw.actions.MDActionsManager;

import com.nomagic.magicdraw.actions.ActionsID;






/*
    --> High Level Actions Class <--

    Purpose
    - Builds cameo plugin actions when class is loaded into memory

    Returns
    - MDActionsManager

    1. Creates actions
    2. Puts actions in categories
    3. Adds categories to manager






 */
public class ADG_Actions {

    private static final MDActionsManager ACTIONS = new MDActionsManager();

    // --> Static blocks are run only once when class is loaded into memory
    static {

        // --> 1. Create custom actions for diagram
        ActionsCreator actionsCreator      = ActionsProvider.getInstance().getCreator();
        ActionsManager classDiagramActions = actionsCreator.createClassDiagramActions().clone();

        NMAction addStereotypeAction     = classDiagramActions.getActionFor(ActionsID.ADD_STEREOTYPE);
        NMAction drawDownSelectingAction = new DownSelectingDecision();
        NMAction drawPartitioningAction  = new PartitioningDecision();
        NMAction drawAssigningAction     = new AssigningDecision();
        NMAction drawStandardFormAction  = new StandardFormDecision();
        NMAction drawPermutingAction     = new PermutingDecision();
        NMAction drawElementSetAction    = new ElementSet();


        // --> 2. Put into categories
        ActionsCategory category = new ActionsCategory();

        // --> Add all common diagram actions to category
        actionsCreator.createCommonDiagramsActions(category);

        // --> Add custom diagram actions to category
        category.addAction(addStereotypeAction);
        category.addAction(drawDownSelectingAction);
        category.addAction(drawPartitioningAction);
        category.addAction(drawAssigningAction);
        category.addAction(drawStandardFormAction);
        category.addAction(drawPermutingAction);
        category.addAction(drawElementSetAction);




        // --> 3. Add categories to manager
        ACTIONS.addCategory(category);
    }

    public static MDActionsManager getActions()
    {
        return ACTIONS;
    }
}
