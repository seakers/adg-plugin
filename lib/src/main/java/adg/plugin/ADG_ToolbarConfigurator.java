package adg.plugin;


import adg.plugin.actions.*;
import com.nomagic.actions.ActionsManager;
import com.nomagic.magicdraw.actions.DiagramInnerToolbarConfiguration;
import com.nomagic.magicdraw.ui.actions.BaseDiagramToolbarConfigurator;


public class ADG_ToolbarConfigurator extends BaseDiagramToolbarConfigurator {

    // --> superType: Architecture Decision Diagram
    private final String superType;

    public ADG_ToolbarConfigurator(String superType)
    {
        this.superType = superType;
    }

    @Override
    public void configure(ActionsManager manager){

        // --> 1. Configure Actions Manager (add ActionsCategory)
        final ActionsManager actions = ADG_Actions.getActions();

        // --> Toolbar: Selection
        this.configureSelectionToolbar(manager, actions);

        // --> Toolbar: Tools
        this.configureToolsToolbar(manager, actions);

        // --> Toolbar: Common
        this.configureCommonToolbar(manager, actions);

        // --> Toolbar: Inner
        this.configureInnerToolbar(manager, actions);
    }


    public void configureSelectionToolbar(ActionsManager manager, ActionsManager actions){
        manager.addCategory(createSelectionToolbar(actions, superType));
    }

    public void configureToolsToolbar(ActionsManager manager, ActionsManager actions){
        manager.addCategory(createToolsToolbar(actions, superType));
    }

    public void configureCommonToolbar(ActionsManager manager, ActionsManager actions){
        manager.addCategory(createCommonToolbarConfiguration(actions, superType));
    }








    public void configureInnerToolbar(ActionsManager manager, ActionsManager actions){
        final DiagramInnerToolbarConfiguration category = new DiagramInnerToolbarConfiguration("SPECIFIC_DIAGRAM_ELEMENTS", null, "Specific Diagram Elements", true);
        manager.addCategory(category);

        category.addAction(actions.getActionFor(DrawDownSelectingAction.DRAW_DOWN_SELECTING_ACTION));
        category.addAction(actions.getActionFor(DrawPartitioningAction.DRAW_PARTITIONING_ACTION));
        category.addAction(actions.getActionFor(DrawAssigningAction.DRAW_ASSIGNING_ACTION));
        category.addAction(actions.getActionFor(DrawRootAction.DRAW_ROOT_ACTION));
        category.addAction(actions.getActionFor(DrawStandardFormAction.DRAW_STANDARD_FORM_ACTION));
    }





}
