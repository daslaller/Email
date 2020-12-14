package sample.RootFXML;

import com.jfoenix.controls.JFXListView;
import escpos.EscPos;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebView;
import javafx.util.Pair;
import output.PrinterOutputStream;
import sample.PrintObjects;
import sample.SettingsFXML.SettingsController;
import sample.epostTab.EpostController;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

public class RootController {

    ObservableList<Node> printerOutputOptions = FXCollections.observableArrayList();
    Pair<Node, SettingsController> settings;
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TabPane main_TabPane;

    @FXML
    private Tab epostTab_Tab;

    @FXML
    private BorderPane epostView_BorderPane;

    @FXML
    private JFXListView<?> epostList_JFXListView;

    @FXML
    private WebView emailDisplay_WebView;

    @FXML
    private Tab settingsTab_Tab;
//
//    @FXML
//    private GridPane settingsGrid_GridPane;
//
//    @FXML
//    private JFXComboBox<PrintService> printerCombBox_JFXComboBox;
//
//    @FXML
//    private JFXButton previewButton_JFXButton;
//
//    @FXML
//    private JFXButton saveButton_JFXButton;
//
//    @FXML
//    private VBox documentButtonGroup_Vbox;
//
//    @FXML
//    private StackPane rightSettingsPane_StackPane;
//
////    @FXML
////    private JFXListView<Node> printerDocumentNodeList_JFXListView;
//
//    //    @FXML
////    private JFXNodesList printerDocumentNodeList_JFXListView;
//    @FXML
//    private ListView<Node> printerDocumentNodeList_JFXListView;

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
    void addEmailButton_JFXButtonActionPerformed(ActionEvent event) {

    }


    @FXML
    void initialize() {
        assert main_TabPane != null : "fx:id=\"main_TabPane\" was not injected: check your FXML file 'root.fxml'.";
        assert epostTab_Tab != null : "fx:id=\"epostTab_Tab\" was not injected: check your FXML file 'root.fxml'.";
        assert epostView_BorderPane != null : "fx:id=\"epostView_BorderPane\" was not injected: check your FXML file 'root.fxml'.";
        assert epostList_JFXListView != null : "fx:id=\"epostList_JFXListView\" was not injected: check your FXML file 'root.fxml'.";
        assert emailDisplay_WebView != null : "fx:id=\"emailDisplay_WebView\" was not injected: check your FXML file 'root.fxml'.";
        assert settingsTab_Tab != null : "fx:id=\"settingsTab_Tab\" was not injected: check your FXML file 'root.fxml'.";
//        assert settingsGrid_GridPane != null : "fx:id=\"settingsGrid_GridPane\" was not injected: check your FXML file 'root.fxml'.";
//        assert printerCombBox_JFXComboBox != null : "fx:id=\"printerCombBox_JFXComboBox\" was not injected: check your FXML file 'root.fxml'.";
//        assert previewButton_JFXButton != null : "fx:id=\"previewButton_JFXButton\" was not injected: check your FXML file 'root.fxml'.";
//        assert saveButton_JFXButton != null : "fx:id=\"saveButton_JFXButton\" was not injected: check your FXML file 'root.fxml'.";
//

        /*        setSettingsTab(Main.getSETTINGS().getKey());*/
    }

    public void setSettingsTab(Pair<Node, SettingsController> pair) {
        settingsTab_Tab.setContent(pair.getKey());
        settings = pair;
//        JFXOptionPane.showChoiceDialog(pair.getValue().getAvailablePrinters().toArray(new PrintService[0]), "Skrivare", "Skrivare", "Använd någon av följande skrivare för utskrift");
    }

    public void setEpostTab(Pair<Node, EpostController> pair) {
        epostTab_Tab.setContent(pair.getKey());

    }


    public enum PRINTEROPTIONS {
        IMAGE() {
            @Override
            public <T> void save(T image) {
                if (image instanceof PrintObjects.PosImg) {
                    commit(image);
                }
            }

            @Override
            public void print() throws IOException {
                PrintObjects.PosImg posImage = (PrintObjects.PosImg) load();
                if (posImage != null) {
                    escPos.write(posImage.imageWrapperInterface, posImage.escPosImage);
                    escPos.flush();
                }
            }
        },
        EMAIL,
        TEXT() {
            @Override
            public <T> void save(T text) {
                if (text instanceof PrintObjects.PosText) {
                    commit(text);
                }
            }

            @Override
            public void print() throws IOException {
                PrintObjects.PosText posText = (PrintObjects.PosText) load();
                if (posText != null) {
                    escPos.write(posText.escPosStyle, posText.text);
                    escPos.flush();
                }
            }
        };

        private Object savedT;
        private static EscPos escPos;

        static {
            try {
                escPos = new EscPos(new PrinterOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public boolean isSaved() {
            return savedT != null;
        }

        public <T> void save(T type) {
            throw new UnsupportedOperationException();
        }

        public <T> void commit(T newCommit) {
            this.savedT = Objects.requireNonNull(newCommit, "Cant commit null!");
        }

        public Object load() {
            return Optional.of(savedT).orElseThrow(NullPointerException::new);
        }

        public void print() throws IOException {
            throw new UnsupportedOperationException();
        }

        public OutputStream hijackStream() {
            return escPos.getOutputStream();
        }

        public void changePrinterOutputStream(PrinterOutputStream printerOutputStream) {
            escPos = new EscPos(printerOutputStream);
        }
    }
}
