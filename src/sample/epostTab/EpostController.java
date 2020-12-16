package sample.epostTab;

import com.company.JFXOptionPane;
import com.company.Resource;
import com.jfoenix.controls.JFXButton;
import escpos.EscPos;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.WritableImage;
import javafx.scene.web.WebView;
import output.PrinterOutputStream;
import sample.Connection.Connect;
import sample.Main;

import javax.imageio.ImageIO;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.print.PrintService;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ResourceBundle;

public class EpostController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private WebView emailWebView;

    @FXML
    private ListView<Message> emailList;

    @FXML
    private Label statusLabel;

    @FXML
    private JFXButton printJFXButton;

    @FXML
    void printJFXButton_ActionPerformed(ActionEvent event) {
//        PrinterJob job = PrinterJob.createPrinterJob();
//        job.showPrintDialog(((Node) event.getTarget()).getScene().getWindow());
//        statusLabel.textProperty().bind(job.jobStatusProperty().asString());
//
//        Paper pos80 = PrintHelper.createPaper("Pos80", 80, Double.MAX_VALUE, Units.MM);
//        PageLayout pageLayout = job.getPrinter().createPageLayout(pos80, PageOrientation.PORTRAIT, 5, 5, 5, 5);
//
//        Set<Paper> supportedPapers = job.getPrinter().getPrinterAttributes().getSupportedPapers();
//        Paper defaultPaper = job.getPrinter().getPrinterAttributes().getDefaultPaper();
//
//        JFXOptionPane.showMessageDialog("Supported papers: " + supportedPapers);
//        JFXOptionPane.showMessageDialog("Selected paper: " + defaultPaper);
//        JFXOptionPane.showMessageDialog("Pos80: " + pos80);
//
//        List<Paper> list = new ArrayList<>(supportedPapers);
//        list.add(pos80);
//        list.forEach(paper -> {
//            JFXOptionPane.showMessageDialog("Current print:\n" + paper);
//            PageLayout pageLayout1 = job.getPrinter().createPageLayout(paper, PageOrientation.PORTRAIT, 0, 0, 0, 0);
//            Dimension2D widthHeightAvailable = new Dimension2D(pageLayout1.getPrintableWidth(), pageLayout1.getPrintableHeight());
//
//            TextArea text = new TextArea(paper.getName() + " Width: " + widthHeightAvailable.getWidth() + " height: " + widthHeightAvailable.getHeight());
//            text.setPrefRowCount(10);
//            text.setPrefColumnCount(20);
//            text.setWrapText(true);
//
//            job.printPage(text);
//            job.printPage(pageLayout1, emailWebView);
//        });
//        job.endJob();


//        ObservableList<Pair<Region, ListCellController>> pairs = Main.settingsFXML.getValue().initiatedListCellsPairSimpleObjectProperty().get();


        //        if (job.printPage(pageLayout, emailWebView)) {
//            job.endJob();
//        }

        try {
            printText(emailList.getSelectionModel().getSelectedItem().getContent().toString());
        } catch (IOException | MessagingException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void initialize() {
        assert printJFXButton != null : "fx:id=\"printJFXButton\" was not injected: check your FXML file 'epostCell.fxml'.";
        assert emailWebView != null : "fx:id=\"emailWebView\" was not injected: check your FXML file 'epostCell.fxml'.";
        assert emailList != null : "fx:id=\"emailList\" was not injected: check your FXML file 'epostCell.fxml'.";
        assert statusLabel != null : "fx:id=\"statusLabel\" was not injected: check your FXML file 'epostCell.fxml'.";

        Main.currentConnectionSimpleObjectProperty().addListener((observableValue, connect, t1) -> emailList.itemsProperty().bind(t1.messagesObservableListSimpleObjectProperty()));
        emailList.selectionModelProperty().get().selectedItemProperty().addListener((observableValue, messageMultipleSelectionModel, t1) -> {
            try {

                String content = (String) t1.getContent();
                System.err.println("Content of selection:\n" + content);
                emailWebView.getEngine().loadContent(content);

                WritableImage snapshot = emailWebView.snapshot(null, null);
                BufferedImage bufferedImage = new BufferedImage((int) snapshot.getWidth(), (int) snapshot.getHeight(), BufferedImage.TYPE_INT_ARGB);
                ImageIO.write(SwingFXUtils.fromFXImage(snapshot, bufferedImage), "png", new File("snapshot.png"));
            } catch (IOException | MessagingException e) {
                e.printStackTrace();
            }

        });


        emailList.setCellFactory(messageListView -> new ListCell<Message>() {
            public void updateItem(Message item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    try {

                        setText("Loading...");
                        Task<String> getSubjectTask = new Task<String>() {
                            @Override
                            protected String call() throws Exception {
                                return item.getSubject() + " " + item.getSentDate();
                            }
                        };
                        getSubjectTask.setOnSucceeded(workerStateEvent -> setText(getSubjectTask.getValue()));
                        Thread getSubjectTaskThread = new Thread(getSubjectTask);
                        getSubjectTaskThread.setDaemon(true);


                        if (!Main.currentConnectionSimpleObjectProperty().get().fetchThreadSimpleObjectProperty().get().isRunning()) {
                            getSubjectTaskThread.start();
                        } else {
                            Main.currentConnectionSimpleObjectProperty().get().fetchThreadSimpleObjectProperty().get()
                                    .setOnSucceeded(workerStateEvent -> getSubjectTaskThread.start());
                            setText("Populating list!");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void printText(String printAbleText) {
        PrintService selectedPrinter = Main.settingsFXML.getValue().getSelectedPrinter();
        if (selectedPrinter != null) {
            System.out.println("Printing to selected printer: " + selectedPrinter.getName());
            Runnable run = () -> {
                try {
                    Path load = Resource.load("snapshot.png");

                    if (load.toFile().exists() && load.toFile().isFile()) {
//                        BitonalEnum bitonalEnum = BitonalEnum.BITONAL_DITHER_MATRIX;
//                        EscPosImage image = bitonalEnum.image(ImageIO.read(load.toFile()));

                        PrinterOutputStream printerOutputStream = new PrinterOutputStream(selectedPrinter);
                        EscPos escPos = new EscPos(printerOutputStream);
                        escPos.write(printAbleText);
//                        escPos.write(new RasterBitImageWrapper(), image);
                        escPos.flush();
                        escPos.close();
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }
            };
            Thread startRun = new Thread(run);
            startRun.setDaemon(true);
            startRun.start();
        } else {
            JFXOptionPane.showMessageDialog("No printer selected?");
        }
    }

    public void setConnection(Connect connect) {
//        connect.addListener((currentValue, oldValue, newValue) -> emailList.itemsProperty().bind(newValue.messagesObservableListSimpleObjectProperty()));

    }

//    SimpleObjectProperty<Connect> connectSimpleObjectProperty;
//
//    public SimpleObjectProperty<Connect> connectSimpleObjectProperty() {
//        if (connectSimpleObjectProperty == null) {
//            connectSimpleObjectProperty = new SimpleObjectProperty<>();
//            connectSimpleObjectProperty.addListener((currentValue, oldValue, newValue) -> emailList.itemsProperty().bind(newValue.messagesObservableListSimpleObjectProperty()));
//        }
//        return connectSimpleObjectProperty;
//    }
}
