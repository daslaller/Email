package sample.SettingsFXML;

import com.company.DisplayInfo;
import com.company.JFXOptionPane;
import com.company.RandomImage;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListCell;
import com.jfoenix.controls.JFXNodesList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import sample.BitonalEnum;
import sample.ListCellFXML.ListCellController;
import sample.Main;
import sample.PrintObjects;

import javax.imageio.ImageIO;
import javax.print.DocFlavor;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class SettingsController {
    public ObservableList<Node> printerOutputOptions = FXCollections.observableArrayList();
    ObservableList<PrintService> availablePrintersList = FXCollections.observableArrayList(Objects.requireNonNull(getAvailablePrinters(), "Cant find any printers"));

    public static double rollWidthInches = 3.14961; //Inches, 80mm = 3.14961 inc
    public static double rollWidthPixels = rollWidthInches * DisplayInfo.getMonitorBitDepth();

    JFXListCell<Node> testCell = new JFXListCell<>();
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private GridPane settingsGrid_GridPane;

    @FXML
    private JFXComboBox<PrintService> printerCombBox_JFXComboBox;

    @FXML
    private StackPane rightSettingsPane_StackPane;

    @FXML
    private JFXNodesList/*<Node>*/ printerDocumentNodeList_JFXNodeList;

    @FXML
    private VBox documentButtonGroup_Vbox;

    @FXML
    private JFXButton addImageButton_JFXButton;

    @FXML
    private JFXButton addEmailButton_JFXButton;

    @FXML
    private JFXButton addTextButton_JFXButton;

    @FXML
    private JFXButton previewButton_JFXButton;

    @FXML
    private JFXButton saveButton_JFXButton;

    @FXML
    void addEmailButton_JFXButtonActionPerformed(ActionEvent event) {

    }

    @FXML
    void addImageButton_JFXButtonActionPerformed(ActionEvent event) throws IOException {
        Pair<Node, ListCellController> nodeListCellControllerPair = Objects.requireNonNull(Main.getLISTCELL());
        Region load = (Region) nodeListCellControllerPair.getKey();
        ListCellController controller = nodeListCellControllerPair.getValue();

        File file = JFXOptionPane.showSpecificFileChooser(JFXOptionPane.Filters.ALL, JFXOptionPane.SaveOption.OPEN_OPTION, ((Node) event.getTarget()).getScene().getWindow());
        BufferedImage read = ImageIO.read(file);

        BitonalEnum bild_typ = JFXOptionPane.showChoiceDialog(BitonalEnum.values(), "Bild typ", "Välj en bild typ!", "Bild typen, skrivaren ska använda vid utskrift.");
        ButtonType buttonType = JFXOptionPane.showConfirmationDialog("Vill du att programmet väljer en automatisk storlek?", "Storlek", "Förändra storlek av bild");
        if (!JFXOptionPane.defaultFile.equals(file)) {
            if (buttonType.equals(ButtonType.APPLY) || buttonType.equals(ButtonType.OK)) {
                double imgWidth = (int) rollWidthPixels;
//            double imgHeight = (imgWidth / read.getWidth()) * read.getHeight();
                double imgHeight = read.getHeight();
                read = RandomImage.resize(read, (int) imgWidth, (int) imgHeight);
            }
            controller.setImage(new PrintObjects.PosImg(bild_typ.image(read)));
            controller.setTitle(printerOutputOptions.size() + 1 + " (TEXT)");
            controller.setExitAction(() -> {
                System.out.println("Exit pressed for node " + controller.toString());
                return printerOutputOptions.remove(controller.root());
            });
//            printerDocumentNodeList_JFXListView.prefWidthProperty().bind(load.widthProperty());
            printerOutputOptions.add(load);
        }
    }


    @FXML
    void previewButton_JFXButtonActionPerformed(ActionEvent event) {

    }

    @FXML
    void printerCombBox_JFXComboBoxActionPerformed(ActionEvent event) {

    }

    @FXML
    void saveButton_JFXButtonActionPerformed(ActionEvent event) {

    }

    @FXML
    void addTextButton_JFXButtonActionPerformed(ActionEvent event) {
        Pair<Node, ListCellController> nodeListCellControllerPair = Objects.requireNonNull(Main.getLISTCELL());
        Region load = (Region) nodeListCellControllerPair.getKey();
        ListCellController controller = nodeListCellControllerPair.getValue();

        String description = JFXOptionPane.showInputDialog("Skriv in din text som du vill ska hamna på kvittot", "Text", "Text tillägg för utskrift");

        if (!description.isEmpty() && !description.isBlank()) {
            controller.setDescription(new PrintObjects.PosText(description));
            controller.setTitle(printerOutputOptions.size() + 1 + " (TEXT)");
            controller.setExitAction(() -> {
                System.out.println("Exit pressed for node " + controller.toString());
                return printerOutputOptions.remove(nodeListCellControllerPair.getKey());
            });

            printerOutputOptions.add(load);
            System.out.println("Node added!");
        }
    }

    @FXML
    void initialize() {
        assert settingsGrid_GridPane != null : "fx:id=\"settingsGrid_GridPane\" was not injected: check your FXML file 'Settings.fxml'.";
        assert printerCombBox_JFXComboBox != null : "fx:id=\"printerCombBox_JFXComboBox\" was not injected: check your FXML file 'Settings.fxml'.";
        assert rightSettingsPane_StackPane != null : "fx:id=\"rightSettingsPane_StackPane\" was not injected: check your FXML file 'Settings.fxml'.";
        assert printerDocumentNodeList_JFXNodeList != null : "fx:id=\"printerDocumentNodeList_JFXListView\" was not injected: check your FXML file 'Settings.fxml'.";
        assert documentButtonGroup_Vbox != null : "fx:id=\"documentButtonGroup_Vbox\" was not injected: check your FXML file 'Settings.fxml'.";
        assert addImageButton_JFXButton != null : "fx:id=\"addImageButton_JFXButton\" was not injected: check your FXML file 'Settings.fxml'.";
        assert addEmailButton_JFXButton != null : "fx:id=\"addEmailButton_JFXButton\" was not injected: check your FXML file 'Settings.fxml'.";
        assert addTextButton_JFXButton != null : "fx:id=\"addTextButton_JFXButton\" was not injected: check your FXML file 'Settings.fxml'.";
        assert previewButton_JFXButton != null : "fx:id=\"previewButton_JFXButton\" was not injected: check your FXML file 'Settings.fxml'.";
        assert saveButton_JFXButton != null : "fx:id=\"saveButton_JFXButton\" was not injected: check your FXML file 'Settings.fxml'.";
        printerOutputOptions = printerDocumentNodeList_JFXNodeList.getChildren();
        printerCombBox_JFXComboBox.setItems(availablePrintersList);
        printerCombBox_JFXComboBox.getSelectionModel().select(printerDialog());


//        rightSettingsPane_StackPane.prefWidthProperty().bind(printerDocumentNodeList_JFXNodeList.widthProperty());

    }

    public List<PrintService> getAvailablePrinters() {
        return Arrays.asList(PrintServiceLookup.lookupPrintServices(DocFlavor.INPUT_STREAM.AUTOSENSE, null));
    }

    public PrintService printerDialog() {
        if (!availablePrintersList.isEmpty()) {
            return JFXOptionPane.showChoiceDialog(availablePrintersList.toArray(new PrintService[0]), "Skrivare", "Skrivare", "Använd någon av följande skrivare för utskrift");
        } else {
            System.err.println("No printers found on system!");
            System.exit(-1);
            return null;
        }
    }


}
