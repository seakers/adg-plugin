package adg.plugin;

import com.nomagic.magicdraw.plugins.Plugin;





public class ArchitectureDecisionGraph extends Plugin{


    @Override
    public void init()
    {
        System.out.println("--> INITIALIZING PLUGIN");




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


}
