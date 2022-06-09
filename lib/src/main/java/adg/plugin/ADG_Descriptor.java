package adg.plugin;


import com.nomagic.actions.AMConfigurator;
import com.nomagic.magicdraw.actions.DiagramContextAMConfigurator;
import com.nomagic.magicdraw.actions.MDActionsManager;
import com.nomagic.magicdraw.icons.IconsFactory;
import com.nomagic.magicdraw.ui.actions.BaseDiagramContextAMConfigurator;
import com.nomagic.magicdraw.ui.actions.ClassDiagramShortcutsConfigurator;
import com.nomagic.magicdraw.uml.DiagramDescriptor;
import com.nomagic.magicdraw.uml.DiagramType;
import com.nomagic.ui.ResizableIcon;

import java.io.File;
import java.net.URL;

public class ADG_Descriptor extends DiagramDescriptor{


    public static final String ARCHITECTURE_DECISION_GRAPH = "Architecture Decision Graph";
    public static final String ICON_PATH = "C:\\Program Files\\Cameo Systems Modeler Demo\\plugins\\adg\\icons\\addicon1.svg";

    @Override
    public String getDiagramTypeId()
    {
        return ARCHITECTURE_DECISION_GRAPH;
    }

    @Override
    public String getSingularDiagramTypeHumanName()
    {
        return ARCHITECTURE_DECISION_GRAPH;
    }

    @Override
    public String getPluralDiagramTypeHumanName()
    {
        return "Decision Graphs";
    }

    private URL getSVGIconURL()
    {
        try{
            URL icon_url = new File(ADG_Descriptor.ICON_PATH).toURI().toURL();
            return icon_url;
        }
        catch (Exception ex){
            ex.printStackTrace();
        }

        // --> Backup return if url fails
        return getClass().getResource("icons/myclass.svg");
    }

    @Override
    public ResizableIcon getSVGIcon()
    {
        return IconsFactory.getNotScaledIcon(getSVGIconURL());
    }

    @Override
    public URL getSmallIconURL()
    {
        return getSVGIconURL();
    }

    @Override
    public String getSuperType()
    {
        return DiagramType.UML_CLASS_DIAGRAM;
    }

    @Override
    public boolean isCreatable()
    {
        return true;
    }


    //                    _    _
    //        /\         | |  (_)
    //       /  \    ___ | |_  _   ___   _ __   ___
    //      / /\ \  / __|| __|| | / _ \ | '_ \ / __|
    //     / ____ \| (__ | |_ | || (_) || | | |\__ \
    //    /_/    \_\\___| \__||_| \___/ |_| |_||___/

    @Override
    public MDActionsManager getDiagramActions()
    {
        return ADG_Actions.getActions();
    }



    //     _______             _  _
    //    |__   __|           | || |
    //       | |  ___    ___  | || |__    __ _  _ __
    //       | | / _ \  / _ \ | || '_ \  / _` || '__|
    //       | || (_) || (_) || || |_) || (_| || |
    //       |_| \___/  \___/ |_||_.__/  \__,_||_|

    @Override
    public AMConfigurator getDiagramToolbarConfigurator()
    {
        return new ADG_ToolbarConfigurator(getSuperType());
    }


    //      _____  _                   _                _
    //     / ____|| |                 | |              | |
    //    | (___  | |__    ___   _ __ | |_  ___  _   _ | |_  ___
    //     \___ \ | '_ \  / _ \ | '__|| __|/ __|| | | || __|/ __|
    //     ____) || | | || (_) || |   | |_| (__ | |_| || |_ \__ \
    //    |_____/ |_| |_| \___/ |_|    \__|\___| \__,_| \__||___/

    @Override
    public AMConfigurator getDiagramShortcutsConfigurator()
    {
        return new ClassDiagramShortcutsConfigurator();
    }


    //      _____               _               _     __  __
    //     / ____|             | |             | |   |  \/  |
    //    | |      ___   _ __  | |_  ___ __  __| |_  | \  / |  ___  _ __   _   _
    //    | |     / _ \ | '_ \ | __|/ _ \\ \/ /| __| | |\/| | / _ \| '_ \ | | | |
    //    | |____| (_) || | | || |_|  __/ >  < | |_  | |  | ||  __/| | | || |_| |
    //     \_____|\___/ |_| |_| \__|\___|/_/\_\ \__| |_|  |_| \___||_| |_| \__,_|

    @Override
    public DiagramContextAMConfigurator getDiagramContextConfigurator()
    {
        return new BaseDiagramContextAMConfigurator();
    }

}
