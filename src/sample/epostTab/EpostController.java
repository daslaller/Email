package sample.epostTab;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.web.WebView;
import sample.Main;
import sample.Connection.Connect;

import javax.mail.Message;
import javax.mail.MessagingException;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;

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
        emailList.itemsProperty()
                .addListener((oldValue, newValue, currentValue) -> statusLabel.setText(newValue.size() + ""));
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
                        getSubjectTask.setOnSucceeded(workerStateEvent -> {
                            setText(getSubjectTask.getValue());
                        });

                        Thread getSubjectTaskThread = new Thread(getSubjectTask);
                        getSubjectTaskThread.setDaemon(true);

                        if (!Main.currentConnection.fetchThreadSimpleObjectProperty().get().isRunning()) {
                            getSubjectTaskThread.start();
                        } else {
                            Main.currentConnection.fetchThreadSimpleObjectProperty().get()
                                    .setOnSucceeded(workerStateEvent -> {
                                        getSubjectTaskThread.start();
                                    });
                            setText("Populating list!!");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void setConnection(Connect connect) {
        connectSimpleObjectProperty().set(connect);
    }

    SimpleObjectProperty<Connect> connectSimpleObjectProperty;

    public SimpleObjectProperty<Connect> connectSimpleObjectProperty() {
        if (connectSimpleObjectProperty == null) {
          connectSimpleObjectProperty = new SimpleObjectProperty<>();
          connectSimpleObjectProperty.addListener((currentValue, oldValue, newValue) -> {
            emailList.itemsProperty().bind(newValue.messagesObservableListSimpleObjectProperty());
          });
        }
        return connectSimpleObjectProperty;
    }
}
