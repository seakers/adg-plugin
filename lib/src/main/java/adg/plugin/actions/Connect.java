package adg.plugin.actions;

import adg.plugin.graph.DatabaseClient;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.ui.MainFrame;
import com.nomagic.magicdraw.ui.actions.DefaultDiagramAction;
import com.nomagic.magicdraw.uml.Finder;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.magicdraw.uml.symbols.PresentationElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.*;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;

import javax.annotation.CheckForNull;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Connect extends DefaultDiagramAction {

    private static final String ACTION_NAME = "Connect Neo4j";

    public Connect()
    {
        super(ACTION_NAME, ACTION_NAME, KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK), null);
    }

    @Override
    public void actionPerformed(@CheckForNull ActionEvent e) {

        ArrayList<String> inputs = this.getConnectDialog();

        DatabaseClient.getInstance().connect(inputs);

    }


    public ArrayList<String> getConnectDialog(){
        ArrayList<String> inputs = new ArrayList<>();

        JTextField xField = new JTextField(10);
        JTextField yField = new JTextField(10);
        JTextField zField = new JTextField(10);
        xField.setText("neo4j://localhost:7687");
        yField.setText("neo4j");
        zField.setText("test");

        JPanel myPanel = new JPanel();
        myPanel.add(new JLabel("uri:"));
        myPanel.add(xField);
        myPanel.add(Box.createHorizontalStrut(15)); // a spacer
        myPanel.add(new JLabel("user:"));
        myPanel.add(yField);
        myPanel.add(Box.createHorizontalStrut(15)); // a spacer
        myPanel.add(new JLabel("password:"));
        myPanel.add(zField);

        int result = JOptionPane.showConfirmDialog(null, myPanel,
                "Neo4j Connection", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            inputs.add(xField.getText());
            inputs.add(yField.getText());
            inputs.add(zField.getText());
        }
        return inputs;
    }





}
