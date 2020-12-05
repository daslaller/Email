package sample;

import com.company.JFXOptionPane;
import com.company.Resource;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;
import sample.Connection.Connect;
import sample.Connection.ConnectionSettings;
import sample.ListCellFXML.ListCellController;
import sample.SettingsFXML.SettingsController;
import sample.epostTab.EpostController;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("WeakerAccess")
public class Main extends Application {
    public static Pair<Node, RootController> squibMain;
    public static ConnectionSettings currentConnectionSettings;
    public static Connect currentConnection;
    public static Pair<Node, EpostController> epostCellFXML;
    public static Pair<Node, SettingsController> settingsFXML;
    public static DateTimeFormatter PROJECT_DATE_FORMAT;

    static {
        PROJECT_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    }

    public Gson gson = new GsonBuilder().create();

    @Override
    public void start(Stage primaryStage) {
        squibMain = getMAIN();
        Parent root = (Parent) squibMain.getKey();
        primaryStage.setTitle("EmailPos80");
        primaryStage.setScene(new Scene(root, 1024, 768));
        primaryStage.show();
        primaryStage.setOnCloseRequest(windowEvent -> {
            if (currentConnectionSettings != null) {
                try {
                    gson.toJson(currentConnectionSettings, new FileWriter("settings.json", true));
                    System.out.println("Wrote new settings file!");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        });

        try {
            currentConnectionSettings = gson.fromJson(new FileReader("Settings.json"), ConnectionSettings.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            try {
                Pair<String, String> userPasswordPair = JFXOptionPane.showLoginDialog("Settings couldnt be found!",
                        "You need to create a settings file!", "");

                String host = JFXOptionPane.showInputDialog("Please provide host", "Host needed!", "Specify host here");
                String portString = JFXOptionPane.showInputDialog("Please provide port", "No port found",
                        "Leave empty for default port 993");

                int port = (portString == null || portString.isEmpty() ? 993 : Integer.parseInt(portString));

                currentConnectionSettings = new ConnectionSettings(userPasswordPair.getKey(),
                        userPasswordPair.getValue(), port, host);

                gson.toJson(currentConnectionSettings, new FileWriter("Settings.json"));
                Logger.getGlobal().log(Level.INFO, "User has input settings: " + currentConnectionSettings);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

        currentConnection = new Connect(currentConnectionSettings.mail, currentConnectionSettings.passwd,
                currentConnectionSettings.port, currentConnectionSettings.host);
        currentConnection.initiateConnection(1);

        epostCellFXML.getValue().setConnection(currentConnection);

    }

    public static void main(String[] args) {
        launch(args);
    }

    public static Pair<Node, RootController> getMAIN() {
        try {
            System.out.println("Root fxml found!");
            FXMLLoader loader = new FXMLLoader(Resource.load("root.fxml").toUri().toURL());
            Node load = loader.load();
            RootController controller = loader.getController();

            controller.setSettingsTab(Objects.requireNonNull((settingsFXML = getSettingsFXML())));
            controller.setEpostTab(Objects.requireNonNull((epostCellFXML = getEpostCellFXML())));
            return (squibMain = new Pair<>(load, controller));
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(-2);
        return null;
    }

    @SuppressWarnings("SpellCheckingInspection")
    public static Pair<Node, ListCellController> getListCellFXML() {
        try {
            FXMLLoader loader = new FXMLLoader(Resource.load("ListCell.fxml").toUri().toURL());
            return new Pair<>(loader.load(), loader.getController());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.err.println("Couldn't find List Cell!");
        System.exit(-2);
        return null;
    }

    public static Pair<Node, SettingsController> getSettingsFXML() {
        try {
            FXMLLoader loader = new FXMLLoader(Resource.load("Settings.fxml").toUri().toURL());
            return new Pair<>(loader.load(), loader.getController());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.err.println("Couldn't find Settings!");
        System.exit(-2);
        return null;
    }

    public static Pair<Node, EpostController> getEpostCellFXML() {
        try {
            FXMLLoader loader = new FXMLLoader(Resource.load("epostCell.fxml").toUri().toURL());
            return new Pair<>(loader.load(), loader.getController());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.err.println("Couldn't find Settings!");
        System.exit(-2);
        return null;
    }
}
