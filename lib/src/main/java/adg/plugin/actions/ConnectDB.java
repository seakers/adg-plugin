package adg.plugin.actions;

import adg.plugin.graph.ConnectionParameters;
import com.nomagic.magicdraw.ui.actions.DefaultDiagramAction;

import javax.annotation.CheckForNull;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class ConnectDB extends DefaultDiagramAction {

    private static final String ACTION_NAME = "Connect Neo4j";

    public ConnectDB()
    {
        super(ACTION_NAME, ACTION_NAME, KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK), null);
    }

    @Override
    public void actionPerformed(@CheckForNull ActionEvent e) {
        ConnectionParameters conn = ConnectionParameters.getInstance();
        conn.setConnectionParameters();
        JOptionPane.showMessageDialog(null, conn.verifyConnection());
    }





}
