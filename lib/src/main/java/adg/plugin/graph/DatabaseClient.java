package adg.plugin.graph;

import org.neo4j.driver.Driver;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.GraphDatabase;

import javax.swing.*;
import java.util.ArrayList;

public class DatabaseClient {


    private static DatabaseClient instance = new DatabaseClient();

    public static DatabaseClient getInstance(){
        return instance;
    }

    private String uri;
    private String user;
    private String password;

    private Driver driver;


    private DatabaseClient(){
        this.uri = "neo4j://localhost:7687";
        this.user = "neo4j";
        this.password = "test";
    }

    public void connect(ArrayList<String> inputs){
        this.uri = inputs.get(0);
        this.user = inputs.get(1);
        this.password = inputs.get(2);


        JOptionPane.showMessageDialog(null, "CONNECTING TO NEO4J");
        try {
            this.driver = GraphDatabase.driver(this.uri, AuthTokens.basic(this.user, this.password));
            JOptionPane.showMessageDialog(null, "CONNECTED");
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }












}
