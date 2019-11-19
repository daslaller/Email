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

import java.io.IOException;
import java.util.Objects;

@SuppressWarnings("WeakerAccess")
public class Main extends Application {
    public static Pair<Node, RootController> squibMain = getMAIN();

    @Override
    public void start(Stage primaryStage) {
        Parent root = (Parent) squibMain.getKey();
        primaryStage.setTitle("EmailPos80");
        primaryStage.setScene(new Scene(root, 1024, 768));
        primaryStage.show();
//        primaryStage.setOnCloseRequest(windowEvent -> Platform.setImplicitExit(true));

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
            controller.setSettingsTab(Objects.requireNonNull(getSETTINGS()));
            return (squibMain = new Pair<>(load, controller));
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(-2);
        return null;
    }

    @SuppressWarnings("SpellCheckingInspection")
    public static Pair<Node, ListCellController> getLISTCELL() {
        try {
            FXMLLoader loader = new FXMLLoader(Resource.load("ListCell.fxml").toUri().toURL());
//            Node load = loader.load();
//            ListCellController controller = loader.getController();
//            System.out.println("List cell fxml found!");
//            return new Pair<>(load, controller);
            return new Pair<>(loader.load(), loader.getController());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.err.println("Couldn't find List Cell!");
        System.exit(-2);
        return null;
    }

    public static Pair<Node, SettingsController> getSETTINGS() {
        try {
            FXMLLoader loader = new FXMLLoader(Resource.load("Settings.fxml").toUri().toURL());
//            Node load = loader.load();
//            SettingsController controller = loader.getController();
//            System.out.println("Settings fxml found!");
//            return new Pair<>(load, controller);
            return new Pair<>(loader.load(), loader.getController());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.err.println("Couldn't find Settings!");
        System.exit(-2);
        return null;
    }
}
