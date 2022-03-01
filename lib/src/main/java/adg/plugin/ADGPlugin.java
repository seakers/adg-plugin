package adg.plugin;

import adg.plugin.menu.AbstractAction;
import adg.plugin.menu.AddDecisionAction;
import com.nomagic.actions.ActionsCategory;
import com.nomagic.actions.ActionsManager;
import com.nomagic.magicdraw.plugins.Plugin;

import com.nomagic.magicdraw.dependencymatrix.configuration.DependencyMatrixConfigurator;
import com.nomagic.magicdraw.actions.ActionsConfiguratorsManager;
import com.nomagic.actions.AMConfigurator;


import javax.swing.*;


public class ADGPlugin extends Plugin{

    public final String ADG_DEPENDENCY_MATRIX = "Architecture Decision Graph";


    @Override
    public void init()
    {
        System.out.println("--> INITIALIZING PLUGIN");

        // --> 1. Register a new type of matrix
        DependencyMatrixConfigurator.registerConfiguration(new ADGConfigurator(ADG_DEPENDENCY_MATRIX));


        // --> 2. Add actions to the command bar


        ActionsConfiguratorsManager.getInstance().addDiagramCommandBarConfigurator(
                ADG_DEPENDENCY_MATRIX,
                new AbstractActionConfigurator("Abstract Action")
        );

        ActionsConfiguratorsManager.getInstance().addDiagramCommandBarConfigurator(
                ADG_DEPENDENCY_MATRIX,
                new AbstractActionConfigurator("Add Decision")
        );









        JOptionPane.showMessageDialog(null, "FINISHED ADG PLUGIN INITIALIZATION");
    }

    @Override
    public boolean close()
    {
        System.out.println("--> CLOSING PLUGIN");
        return true;
    }

    @Override
    public boolean isSupported()
    {
        //plugin can check here for specific conditions
        //if false is returned plugin is not loaded.
        return true;
    }


//                    _    _
//        /\         | |  (_)
//       /  \    ___ | |_  _   ___   _ __   ___
//      / /\ \  / __|| __|| | / _ \ | '_ \ / __|
//     / ____ \| (__ | |_ | || (_) || | | |\__ \
//    /_/    \_\\___| \__||_| \___/ |_| |_||___/


    private static final class AbstractActionConfigurator implements AMConfigurator{

        private final String mText;

        private AbstractActionConfigurator(String text)
        {
            mText = text;
        }

        @Override
        public void configure(ActionsManager mngr)
        {
            ActionsCategory category = new ActionsCategory();
            mngr.addCategory(category);

            if(mText.equals("Abstract Action")){
                category.addAction(new AbstractAction(mText));
            }
            else if(mText.equals("Add Decision")){
                category.addAction(new AddDecisionAction(mText));
            }
        }

        @Override
        public int getPriority()
        {
            return HIGH_PRIORITY;
        }
    }





}
