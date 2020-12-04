package sample.epostTab;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.web.WebView;
import sample.Connection.Connect;

import javax.mail.Message;
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
    void initialize() {
        assert emailWebView != null : "fx:id=\"emailWebView\" was not injected: check your FXML file 'epostCell.fxml'.";
        assert emailList != null : "fx:id=\"emailList\" was not injected: check your FXML file 'epostCell.fxml'.";
    }

    public void setConnection(Connect connect) {
        emailList.itemsProperty().bind(connect.messagesObservableListSimpleObjectProperty());
        emailList.setCellFactory(messageListView -> new ListCell<Message>() {
            public void updateItem(Message item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    try {
                        setText("Loading...");
//                        setDisabled(true);
                        Task<String> getSubjectTask = new Task<String>() {
                            @Override
                            protected String call() throws Exception {
                                return item.getSubject() + " " + item.getSentDate();
                            }
                        };
                        getSubjectTask.setOnSucceeded(workerStateEvent -> {
                            setText(getSubjectTask.getValue());
//                            setDisabled(false);
                        });
                        Thread getSubjectTaskThread = new Thread(getSubjectTask);
                        getSubjectTaskThread.setDaemon(true);
                        getSubjectTaskThread.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                        setText(null);
                    }
                }
            }
        });

    }
}
