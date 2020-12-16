package sample;

import com.company.JFXOptionPane;
import com.company.Resource;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;
import sample.Connection.Connect;
import sample.Connection.ConnectionSettings;
import sample.ListCellFXML.ListCellController;
import sample.RootFXML.RootController;
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
    public static Pair<Node, RootController> epostFXML;
    // public static ConnectionSettings currentConnectionSettings;
//    public static Connect currentConnection;
    public static Pair<Node, EpostController> epostCellFXML;
    public static Pair<Node, SettingsController> settingsFXML;
    public static DateTimeFormatter PROJECT_DATE_FORMAT;

    static {
        // Project dateformat, should be used for all date prints.
        PROJECT_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    }

    public static Gson gson = new GsonBuilder().create();

    private static SimpleObjectProperty<ConnectionSettings> CURRENT_CONNECTION_SETTINGS;
    private static SimpleObjectProperty<Connect> CURRENT_CONNECTION;

    @Override
    public void start(Stage primaryStage) {

        epostFXML = getMAIN();
        Parent root = (Parent) epostFXML.getKey();
        primaryStage.setTitle("EmailPos80");
        primaryStage.setScene(new Scene(root, 1280, 1024));
        primaryStage.show();
        primaryStage.setOnCloseRequest(windowEvent -> {
            onExit();
        });
        try {
            currentConnectionSettingsSimpleObjectProperty()
                    .set(gson.fromJson(new FileReader("Settings.json"), ConnectionSettings.class));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            currentConnectionSettingsSimpleObjectProperty().set(showConnectionSettingsDialog());
            Logger.getGlobal().log(Level.INFO,
                    "User has input settings: " + currentConnectionSettingsSimpleObjectProperty().get());


        }

        currentConnectionSimpleObjectProperty().set(new Connect(currentConnectionSettingsSimpleObjectProperty().get()));
//        epostCellFXML.getValue().setConnection(currentConnection);
//        settingsFXML.getValue().setConnection(currentConnection);
    }

    private void onExit() {
        try {
            gson.toJson(currentConnectionSettingsSimpleObjectProperty().get(),
                    new FileWriter("settings.json", true));
            System.out
                    .println("Wrote new settings file!\n" + currentConnectionSettingsSimpleObjectProperty().get());
        } catch (IOException e) {
            e.printStackTrace();
        }

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
            return (epostFXML = new Pair<>(load, controller));
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

    public static ConnectionSettings showConnectionSettingsDialog() {
        Pair<String, String> userPasswordPair = JFXOptionPane.showLoginDialog("Settings couldnt be found!",
                "You need to create a settings file!", "");

        String host = JFXOptionPane.showInputDialog("Please provide host", "Host needed!", "Specify host here");
        String portString = JFXOptionPane.showInputDialog("Please provide port", "No port found",
                "Leave empty for default port 993");

        int port = ((portString == null || portString.isEmpty()) ? 993 : Integer.parseInt(portString));

        return new ConnectionSettings(userPasswordPair.getKey(), userPasswordPair.getValue(), port, host);
    }

    public static SimpleObjectProperty<ConnectionSettings> currentConnectionSettingsSimpleObjectProperty() {

        if (CURRENT_CONNECTION_SETTINGS == null) {

            CURRENT_CONNECTION_SETTINGS = new SimpleObjectProperty<>();

            CURRENT_CONNECTION_SETTINGS.addListener((currentValue, oldValue, newValue) -> {

                if (newValue != null && !Objects.deepEquals(newValue, oldValue)) {
                    // try {
                    String json = gson.toJson(newValue);
//                    JFXOptionPane.showMessageDialog(json);
                    try (FileWriter fileWriter = new FileWriter("Settings.json")) {
                        fileWriter.write(json);
                        Logger.getGlobal().log(Level.INFO, "Update of settingsfile pushed.");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Logger.getGlobal().log(Level.INFO, ("No update of settingsfile has been done, they should be equal."
                            + "\nOldvalue: " + oldValue.toString() + "\nNewvalue: " + newValue.toString()));
                    JFXOptionPane.showMessageDialog(
                            "Throw setting\nOldvalue: " + oldValue.toString() + "\nNewvalue: " + newValue.toString());
                }
            });
        }
        return CURRENT_CONNECTION_SETTINGS;
    }


    public static SimpleObjectProperty<Connect> currentConnectionSimpleObjectProperty() {
        if (CURRENT_CONNECTION == null) {
            CURRENT_CONNECTION = new SimpleObjectProperty<>();
            CURRENT_CONNECTION.addListener((observableValue, oldConnect, t1) -> {
                if (t1 != null && !t1.isConnected()) {

                    t1.initiateConnection(3);
                    if (!t1.isConnected()) {
                        JFXOptionPane.showMessageDialog("Couldnt connect with these settings." +
                                "\nGo to settings pane and change login information!");
                    }
                }
                if (oldConnect != null && oldConnect.isConnected()) {
                    oldConnect.disconnect();
                }
            });
        }
        return CURRENT_CONNECTION;
    }
}
