package sample.SettingsFXML;

import com.company.DisplayInfo;
import com.company.JFXOptionPane;
import com.company.RandomImage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jfoenix.controls.*;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import sample.BitonalEnum;
import sample.ListCellFXML.ListCellController;
import sample.Main;
import sample.PrintObjects;
import sample.RootFXML.RootController;
import sample.Settings;

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
    private ObservableList<Node> createdCellList = FXCollections.observableArrayList();
    private SimpleObjectProperty<ObservableList<Pair<Region, ListCellController>>> initiatedListCellsPairSimpleObjectProperty;
    private final ObservableList<PrintService> availablePrintersList = FXCollections
            .observableArrayList(Objects.requireNonNull(getAvailablePrinters(), "Cant find any printers"));
    // private SimpleObjectProperty<Connect> connectSimpleObjectProperty;


    private static final double rollWidthInches = 3.14961; // Inches, 80mm = 3.14961 inc
    private static final double rollWidthPixels = rollWidthInches * DisplayInfo.getMonitorBitDepth();

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
    private VBox/* <Node> */ printerDocumentNodeList_JFXNodeList;

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
    private JFXTextField epostTextField;

    @FXML
    private JFXPasswordField passwordPasswordField;

    @FXML
    private JFXTextField hostTextField;

    @FXML
    private JFXTextField portTextField;

    @FXML
    void addEmailButton_JFXButtonActionPerformed(ActionEvent event) {

    }

    @FXML
    void addImageButton_JFXButtonActionPerformed(ActionEvent event) throws IOException {

        File file = JFXOptionPane.showSpecificFileChooser(JFXOptionPane.Filters.ALL,
                JFXOptionPane.SaveOption.OPEN_OPTION, ((Node) event.getTarget()).getScene().getWindow());
        BufferedImage read = ImageIO.read(file);

        BitonalEnum bild_typ = JFXOptionPane.showChoiceDialog(BitonalEnum.values(), "Bild typ", "Välj en bild typ!",
                "Bild typen, skrivaren ska använda vid utskrift.");
        ButtonBar.ButtonData buttonType = JFXOptionPane.showThreeOptionAlert(
                "Vill du att programmet väljer en automatisk storlek?", "Storlek", "Förändra storlek av bild",
                "Automatisk", "Ingen ändring", "Egen");

        if (file.isFile()) {
            System.out.println("YES " + (buttonType == ButtonBar.ButtonData.YES));
            switch (buttonType) {
                case YES:
                    System.out.println("Valde Yes");
                    double imgWidth = (int) rollWidthPixels;
                    double imgHeight = (imgWidth / read.getWidth()) * read.getHeight();
                    read = RandomImage.resize(read, (int) imgWidth, (int) imgHeight);
                    break;
                case CANCEL_CLOSE:
                    System.out.println("Valde cancel close");
                    int width;
                    int height;
                    String string_width = JFXOptionPane.showInputDialog("Bildens bredd", "Bildens storlek",
                            "Skriv in din bredd du vill ha på bilden, om du skriver " + '"' + "r" + '"'
                                    + "Anpassas bredden till skrivarens pappers storlek");
                    String string_height = JFXOptionPane.showInputDialog("Bildens höjd", "Bildens storlek",
                            "Skriv in din höjd du vill ha på bilden, om du skriver" + '"' + "r" + '"'
                                    + "Anpassas höjden till skrivarens pappers storlek");
                    if (string_width.equals("r")) {
                        width = (int) rollWidthPixels;
                    } else {
                        width = Integer.parseInt(string_width);
                    }
                    if (string_height.equals("r")) {
                        height = (int) ((rollWidthPixels / read.getWidth()) * read.getHeight());
                    } else {
                        height = Integer.parseInt(string_height);
                    }
                    read = RandomImage.resize(read, (width <= 0 ? read.getWidth() : width),
                            (height <= 0 ? read.getHeight() : height));
                    break;
                default:
                    System.out.println("Valde annan");
                    break;
            }
            createdCellList
                    .add(createCell("(IMAGE)", file.getName(), new PrintObjects.PosImg(bild_typ.image(read))).getKey());
        } else {
            System.err.println("File not available: " + file);
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
        String epost = epostTextField.getText();
        String password = passwordPasswordField.getText();
        int parseInt = Integer.parseInt(portTextField.getText());
        String host = hostTextField.getText();

        if (epost != null && !epost.isEmpty() && password != null && !password.isEmpty() && host != null
                && !host.isEmpty() && !portTextField.getText().isEmpty()) {

            Settings settings = new Settings(epost, password, parseInt, host);
            Main.currentSettingsSimpleObjectProperty()
                    .set(settings
                            /*new ConnectionSettings(epost, password, parseInt, host)*/);

            JFXOptionPane.showMessageDialog("Restart the program for the change to take effect!\nWrote:\n"
                    + Main.currentSettingsSimpleObjectProperty().get() + "\nTo file.");

        } else {
            JFXOptionPane.showMessageDialog("All fields are mandatory!");
        }
        if (initiatedListCellsPairSimpleObjectProperty() != null && initiatedListCellsPairSimpleObjectProperty().isNotNull().get() && !initiatedListCellsPairSimpleObjectProperty().get().isEmpty()) {
            for (Pair<Region, ListCellController> pair : initiatedListCellsPairSimpleObjectProperty().get()) {
                Gson gson1 = new GsonBuilder().create();
                String s = gson1.toJson(pair.getValue().printAbles);
                JFXOptionPane.showMessageDialog(s);

                Gson gson = new GsonBuilder().create();
                for (RootController.PRINTEROPTIONS printeroptions : pair.getValue().printAbles) {
                    String s1 = gson.toJson(printeroptions);
                    JFXOptionPane.showMessageDialog(s1);
                }
            }


        }

    }

    @FXML
    void addTextButton_JFXButtonActionPerformed(ActionEvent event) {
        String description = JFXOptionPane.showInputDialog("Skriv in din text som du vill ska hamna på kvittot", "Text",
                "Text tillägg för utskrift");
        if (description != null && !description.isEmpty()) {
            createdCellList.add(createCell("(TEXT)", description, null).getKey());
            System.out.println("Node added!");
        }
    }

    @FXML
    void initialize() {
        assert settingsGrid_GridPane != null
                : "fx:id=\"settingsGrid_GridPane\" was not injected: check your FXML file 'Settings.fxml'.";
        assert printerCombBox_JFXComboBox != null
                : "fx:id=\"printerCombBox_JFXComboBox\" was not injected: check your FXML file 'Settings.fxml'.";
        assert rightSettingsPane_StackPane != null
                : "fx:id=\"rightSettingsPane_StackPane\" was not injected: check your FXML file 'Settings.fxml'.";
        assert printerDocumentNodeList_JFXNodeList != null
                : "fx:id=\"printerDocumentNodeList_JFXListView\" was not injected: check your FXML file 'Settings.fxml'.";
        assert documentButtonGroup_Vbox != null
                : "fx:id=\"documentButtonGroup_Vbox\" was not injected: check your FXML file 'Settings.fxml'.";
        assert addImageButton_JFXButton != null
                : "fx:id=\"addImageButton_JFXButton\" was not injected: check your FXML file 'Settings.fxml'.";
        assert addEmailButton_JFXButton != null
                : "fx:id=\"addEmailButton_JFXButton\" was not injected: check your FXML file 'Settings.fxml'.";
        assert addTextButton_JFXButton != null
                : "fx:id=\"addTextButton_JFXButton\" was not injected: check your FXML file 'Settings.fxml'.";
        assert previewButton_JFXButton != null
                : "fx:id=\"previewButton_JFXButton\" was not injected: check your FXML file 'Settings.fxml'.";
        assert saveButton_JFXButton != null
                : "fx:id=\"saveButton_JFXButton\" was not injected: check your FXML file 'Settings.fxml'.";
        createdCellList = printerDocumentNodeList_JFXNodeList.getChildren();
        printerCombBox_JFXComboBox.setItems(availablePrintersList);
        printerCombBox_JFXComboBox.getSelectionModel().select(printerDialog());

        Main.currentSettingsSimpleObjectProperty().addListener((currentValue, oldValue, newValue) -> {
            epostTextField.setText(newValue.mail);
            passwordPasswordField.setText(newValue.passwd);
            portTextField.setText(newValue.port + "");
            hostTextField.setText(newValue.host);
        });


    }

    public ObjectProperty<ObservableList<PrintService>> getAvailablePrinterProperty() {
        return printerCombBox_JFXComboBox.itemsProperty();
    }

    public PrintService getSelectedPrinter() {
        return printerCombBox_JFXComboBox.getSelectionModel().getSelectedItem();
    }

    public ObservableList<Node> getCreatedListCellNodes() {
        return createdCellList;
    }


    private Pair<Region, ListCellController> createCell(String title, String description, PrintObjects.PosImg image) {
        Pair<Node, ListCellController> nodeListCellControllerPair = Objects.requireNonNull(Main.getListCellFXML());
        Pair<Region, ListCellController> regionListCellControllerPair = new Pair<>((Region) nodeListCellControllerPair.getKey(), nodeListCellControllerPair.getValue());
        ListCellController controller = regionListCellControllerPair.getValue();

        if (title != null) {
            controller.setTitle((createdCellList.size() + 1) + ' ' + title);
        }
        if (description != null) {
            controller.setDescription(new PrintObjects.PosText(description));
        }
        if (image != null) {
            controller.setImage(image);
        }

        controller.setExitAction(() -> {
            System.out.println("Exit pressed for node " + controller.toString());
            initiatedListCellsPairSimpleObjectProperty().get().remove(regionListCellControllerPair);
            return createdCellList.remove(nodeListCellControllerPair.getKey());
        });

        initiatedListCellsPairSimpleObjectProperty().get().add(regionListCellControllerPair);
        return regionListCellControllerPair;
    }

    private List<PrintService> getAvailablePrinters() {
        return Arrays.asList(PrintServiceLookup.lookupPrintServices(DocFlavor.INPUT_STREAM.AUTOSENSE, null));
    }

    private PrintService printerDialog() {
        if (!availablePrintersList.isEmpty()) {
            PrintService printService = JFXOptionPane.showChoiceDialog(availablePrintersList.toArray(new PrintService[0]), "Skrivare",
                    "Skrivare", "Använd någon av följande skrivare för utskrift");
            printService = (printService != null && !printService.getName().isEmpty()) ? printService : PrintServiceLookup.lookupDefaultPrintService();
            return printService;
        } else {
            System.err.println("No printers found on system!");
            System.exit(-1);
            return null;
        }
    }

    public SimpleObjectProperty<ObservableList<Pair<Region, ListCellController>>> initiatedListCellsPairSimpleObjectProperty() {
        if (initiatedListCellsPairSimpleObjectProperty == null) {
            initiatedListCellsPairSimpleObjectProperty = new SimpleObjectProperty<>(FXCollections.observableArrayList());
        }
        return initiatedListCellsPairSimpleObjectProperty;
    }
}
