package adg.plugin.events;

import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.core.project.ProjectEventListenerAdapter;
import com.nomagic.magicdraw.uml.symbols.DiagramListenerAdapter;

import javax.swing.*;

public class ProjectEvents extends ProjectEventListenerAdapter {

    public DiagramEvents diagram_listener;
    public DiagramListenerAdapter diagram_listener_adapter;

    @Override
    public void projectOpened(Project project) {
        // JOptionPane.showMessageDialog(null, "OPENING PROJECT");

        // --> 1. Setup Diagram Event Listener
        this.diagram_listener = new DiagramEvents().initialize(project);
        this.diagram_listener_adapter = new DiagramListenerAdapter(this.diagram_listener);
        this.diagram_listener_adapter.install(project);
    }

    @Override
    public void projectClosed(Project project) {
        // JOptionPane.showMessageDialog(null, "CLOSING PROJECT");

        // --> 1. Remove Event Listeners
        this.diagram_listener_adapter.uninstall(project);
    }

}
