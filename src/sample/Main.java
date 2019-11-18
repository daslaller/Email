package sample;

import com.company.Resource;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;
import sample.ListCellFXML.ListCellController;
import sample.SettingsFXML.SettingsController;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

public class Main extends Application {
    public static Pair<Node, RootController> squibMain = getMAIN();
    public static Pair<Node, SettingsController> squibSettings = getSETTINGS();

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = (Parent) squibMain.getKey();
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 1024, 768));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

    public static Pair<Node, RootController> getMAIN() {
        try {
            FXMLLoader loader = new FXMLLoader(Resource.load("root.fxml").toUri().toURL());
            Node load = loader.load();
            RootController controller = loader.getController();
//            controller.setSettingsTab(Objects.requireNonNull(getSETTINGS()));
            return (squibMain = new Pair<>(load, controller));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            System.err.println("Couldn't find root! " + Resource.load("root.fxml").toUri().toURL());
        } catch (MalformedURLException | FileNotFoundException e) {
            e.printStackTrace();
        }
        System.exit(-2);
        return null;
    }

    @SuppressWarnings("SpellCheckingInspection")
    public static Pair<Node, ListCellController> getLISTCELL() {
        try {
            FXMLLoader loader = new FXMLLoader(Resource.load("ListCell.fxml").toUri().toURL());
            return new Pair<>(loader.load(), loader.getController());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.err.println("Couldn't find List Cell!");
        return null;
    }

    public static Pair<Node, SettingsController> getSETTINGS() {
        try {
            FXMLLoader loader = new FXMLLoader(Resource.load("Settings.fxml").toUri().toURL());
            return (squibSettings = new Pair<>(loader.load(), loader.getController()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.err.println("Couldn't find Settings!");
        return null;
    }
}
