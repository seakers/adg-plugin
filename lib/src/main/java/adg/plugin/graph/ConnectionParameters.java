package adg.plugin.graph;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;

import javax.swing.*;

public class ConnectionParameters {

    private static ConnectionParameters instance = new ConnectionParameters();

    public static ConnectionParameters getInstance() { return instance; }

    public String uri = "neo4j://localhost:7687";
    public String user = "neo4j";
    public String password = "test";

    public void setConnectionParameters(){

        JTextField xField = new JTextField(20);
        JTextField yField = new JTextField(10);
        JTextField zField = new JTextField(10);
        xField.setText(this.uri);
        yField.setText(this.user);
        zField.setText(this.password);

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
            this.uri = xField.getText();
            this.user = yField.getText();
            this.password = zField.getText();
        }
    }

    public String verifyConnection(){

        // --> 1. Create driver object
        Driver driver = GraphDatabase.driver(this.uri, AuthTokens.basic(this.user, this.password));

        // --> 2. Validate connection
        try{
            driver.verifyConnectivity();
        }
        catch(Exception ex){
            driver.close();
            return ("ERROR: " + ex.getMessage());
        }
        driver.close();
        return "SUCCESS: connection successful";
    }




}
