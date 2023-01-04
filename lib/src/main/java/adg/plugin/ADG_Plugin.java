package adg.plugin;


import adg.plugin.actions.*;
import adg.plugin.events.ProjectEvents;
import com.nomagic.actions.AMConfigurator;
import com.nomagic.actions.ActionsCategory;
import com.nomagic.actions.ActionsManager;
import com.nomagic.magicdraw.actions.ActionsConfiguratorsManager;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.plugins.Plugin;
import com.nomagic.magicdraw.ui.actions.DefaultDiagramAction;

import javax.swing.*;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;


public class ADG_Plugin extends Plugin{

    /**
     * Initializing the plugin.
     */
    @Override
    public void init()
    {
        JOptionPane.showMessageDialog(null, "INSTALLING ADG PLUGIN");

        // -------------------
        // ----- DIAGRAM -----
        // -------------------

        // --> Add stereotypes for ADGs
        // this.init_stereotypes();

        // --> Add new diagram type, handles decision actions
        Application.getInstance().addNewDiagramType(new ADG_Descriptor());




        // ---------------------------
        // ----- DIAGRAM ACTIONS -----
        // ---------------------------
        ActionsConfiguratorsManager.getInstance().addDiagramCommandBarConfigurator(
                ADG_Descriptor.ARCHITECTURE_DECISION_GRAPH,
                new AbstractActionConfigurator()
        );


        // --------------------------
        // ----- EVENT HANDLING -----
        // --------------------------
        ProjectEvents event_handler = new ProjectEvents();
        Application.getInstance().getProjectsManager().addProjectListener(event_handler);



        // --------------------
        // ----- FINISHED -----
        // --------------------
        JOptionPane.showMessageDialog(null, "INSTALLATION FINISHED");

    }

    public void init_stereotypes(){
        System.out.println("--> INIT ADG STEREOTYPES");




        Project project = Application.getInstance().getProject();
        System.out.println((project == null));

    }


    @Override
    public boolean close()
    {
        return true;
    }

    @Override
    public boolean isSupported()
    {
        return true;
    }

    public static Path getPluginPath(){
        Path plugin_path = null;
        try{
            String path = new File(".").getCanonicalPath();
            Path path_obj = Paths.get(path);
            plugin_path = Paths.get(path_obj.toString(), "plugins", "adg");
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        return plugin_path;
    }



//                    _    _
//        /\         | |  (_)
//       /  \    ___ | |_  _   ___   _ __   ___
//      / /\ \  / __|| __|| | / _ \ | '_ \ / __|
//     / ____ \| (__ | |_ | || (_) || | | |\__ \
//    /_/    \_\\___| \__||_| \___/ |_| |_||___/

    private static final class AbstractActionConfigurator implements AMConfigurator {

        private final DefaultDiagramAction set_algorithm;
        private final DefaultDiagramAction validate_graph;
        private final DefaultDiagramAction build_graph;
        private final DefaultDiagramAction connect;
        private final DefaultDiagramAction random_design;
        private final DefaultDiagramAction crossover_designs;
        private final DefaultDiagramAction gen_design_space;

        private final DefaultDiagramAction enumerate;
        private final DefaultDiagramAction visualizer;
        private final DefaultDiagramAction evaluator;



        AbstractActionConfigurator() {
            this.set_algorithm = new SetAlgorithm();
            this.validate_graph = new ValidateGraph();
            this.build_graph = new BuildGraph();
            this.connect = new ConnectDB();
            this.random_design = new RandomDesign();
            this.crossover_designs = new CrossoverDesigns();
            this.gen_design_space = new GenerateSystemArchitectureModel();
            this.enumerate = new EnumerateDesignSpace();
            this.visualizer = new OpenVisualizer();
            this.evaluator = new EvaluateDesigns();
        }

        @Override
        public void configure(ActionsManager mngr)
        {
            ActionsCategory category = new ActionsCategory();
            mngr.addCategory(category);
            // category.addAction(this.validate_graph);
            category.addAction(this.set_algorithm);
            category.addAction(this.connect);
            category.addAction(this.build_graph);

            ActionsCategory category2 = new ActionsCategory();
            mngr.addCategory(category2);
            category2.addAction(this.gen_design_space);
            category2.addAction(this.random_design);
            category2.addAction(this.crossover_designs);
            category2.addAction(this.enumerate);
            category2.addAction(this.visualizer);
            category2.addAction(this.evaluator);
        }

        @Override
        public int getPriority()
        {
            return HIGH_PRIORITY;
        }
    }


}
