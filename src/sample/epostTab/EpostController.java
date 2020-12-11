package sample.epostTab;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.web.WebView;
import sample.Connection.Connect;
import sample.Main;

import javax.mail.Message;
import javax.mail.MessagingException;
import java.io.IOException;
import java.net.URL;
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
    void initialize() {
        assert emailWebView != null : "fx:id=\"emailWebView\" was not injected: check your FXML file 'epostCell.fxml'.";
        assert emailList != null : "fx:id=\"emailList\" was not injected: check your FXML file 'epostCell.fxml'.";
        assert statusLabel != null : "fx:id=\"statusLabel\" was not injected: check your FXML file 'epostCell.fxml'.";

        Main.currentConnectionSimpleObjectProperty().addListener((observableValue, connect, t1) -> emailList.itemsProperty().bind(t1.messagesObservableListSimpleObjectProperty()));
        emailList.selectionModelProperty().get().selectedItemProperty().addListener((observableValue, messageMultipleSelectionModel, t1) -> {
            try {
                String content = (String) t1.getContent();
                System.err.println("Content of selection:\n" + content);
                emailWebView.getEngine().loadContent(content);

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
