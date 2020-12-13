package sample.Connection;

import com.company.JFXOptionPane;
import com.sun.mail.imap.IMAPFolder;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

import javax.mail.*;
import javax.mail.UIDFolder.FetchProfileItem;
import javax.mail.event.ConnectionAdapter;
import javax.mail.event.ConnectionEvent;
import javax.mail.event.MessageCountAdapter;
import javax.mail.event.MessageCountEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Connect {
    String mail;
    String passwd;
    int port;
    String host;

    // boolean isConnectionLive = false;

    private SimpleObjectProperty<Store> storeSimpleObjectProperty;
    private SimpleObjectProperty<IMAPFolder> imapFolderSimpleObjectProperty;
    private SimpleObjectProperty<ObservableList<Message>> messageListObservableListSimpleObjectProperty;
    private SimpleObjectProperty<Message[]> lastReceivedMessageSimpleObjectProperty;
    private SimpleObjectProperty<Task<Object>> idleThreadSimpleObjectProperty;
    private SimpleObjectProperty<Task<Message[]>> fetchThreadSimpleObjectProperty;
    private SimpleObjectProperty<ConnectionSettings> connectionSettingsSimpleObjectProperty;

    public static boolean PARTIAL_FETCH_ENABLED = true;
    public static boolean AUTO_RETRY_ON_CONNECTION_FAILURE = true;
    public static boolean IS_SSL_ENABLED = true;
    public static FetchProfile FETCHPROFILE;

    static {
        FETCHPROFILE = new FetchProfile();

        FETCHPROFILE.add(FetchProfileItem.ENVELOPE);

        FETCHPROFILE.add(IMAPFolder.FetchProfileItem.ENVELOPE);

        FETCHPROFILE.add(IMAPFolder.FetchProfileItem.INTERNALDATE);
    }

    public Connect(String mail, String passwd, int port, String host) {
        this(new ConnectionSettings(mail, passwd, port, host));
    }

    public Connect(ConnectionSettings connectionSettings) {
        this.mail = Objects.requireNonNull(connectionSettings.mail, "Mail cannot be null!");
        this.passwd = Objects.requireNonNull(connectionSettings.passwd, "Password cannot be null!");
        this.host = Objects.requireNonNull(connectionSettings.host, "Host cannot be null!");
        this.port = Objects.requireNonNull(connectionSettings.port, "Port cannot be null!");

    }

    public void initiateConnection(int retries) {
        Properties properties = new Properties();
        properties.setProperty("mail.store.protocol", "imap");
        properties.setProperty("mail.imap.host", host);
        properties.setProperty("mail.imap.port", port + "");
        properties.setProperty("mail.imap.ssl.enable", IS_SSL_ENABLED + "");
        properties.setProperty("mail.imap.partialfetch", PARTIAL_FETCH_ENABLED + "");

        Session session = Session.getInstance(properties);
        session.setDebug(true);

        for (int i = 0; i < retries; i++) {
            try {
                Store store = session.getStore();
                store.connect(mail, passwd);

                IMAPFolder folder = (IMAPFolder) store.getFolder("INBOX");

                storeSimpleObjectProperty().set(store);
                imapFolderSimpleObjectProperty().set(folder);
                break;

            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
    }

    public Message getLastMessage() {
        return messagesObservableListSimpleObjectProperty().get().get(0);
    }

    public Message[] getLastReceivedMessage() {
        return lastReceivedMessageSimpleObjectProperty().get();
    }

    public boolean isConnected() {
        return (storeSimpleObjectProperty() != null && storeSimpleObjectProperty().isNotNull().get()
                && storeSimpleObjectProperty().get().isConnected());
    }

    public void disconnect() {
        if (isConnected()) {
            try {
                storeSimpleObjectProperty().get().close();
                imapFolderSimpleObjectProperty().get().close();
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Already disconnected");
        }
    }

    MessageCountAdapter listenForMessageMessageCountAdapter = new MessageCountAdapter() {
        @Override
        public void messagesAdded(MessageCountEvent e) {
            super.messagesAdded(e);
            System.out.println("Got " + e.getMessages().length + " new message/s!");

            if (fetchThreadSimpleObjectProperty().isNull().get()
                    || !fetchThreadSimpleObjectProperty().get().isRunning()) {
                try {
                    messagesObservableListSimpleObjectProperty().set(
                            FXCollections.observableArrayList(imapFolderSimpleObjectProperty().get().getMessages()));
                } catch (MessagingException e1) {
                    e1.printStackTrace();
                }
            }
            lastReceivedMessageSimpleObjectProperty().set(e.getMessages());
        }
    };

    ConnectionAdapter resetConnectionAdapter = new ConnectionAdapter() {
        @Override
        public void opened(ConnectionEvent e) {
            super.opened(e);

        }

        @Override
        public void disconnected(ConnectionEvent e) {
            super.disconnected(e);

            if (AUTO_RETRY_ON_CONNECTION_FAILURE) {
                initiateConnection(1);
            }
        }

        @Override
        public void closed(ConnectionEvent e) {
            super.closed(e);

            if (AUTO_RETRY_ON_CONNECTION_FAILURE) {
                initiateConnection(1);
            }
        }
    };

    public SimpleObjectProperty<ObservableList<Message>> messagesObservableListSimpleObjectProperty() {
        if (messageListObservableListSimpleObjectProperty == null) {
            Callable<SimpleObjectProperty<ObservableList<Message>>> call = () -> {

                SimpleObjectProperty<ObservableList<Message>> newObject = new SimpleObjectProperty<>(
                        FXCollections.observableArrayList(imapFolderSimpleObjectProperty().get().getMessages()));

                newObject.addListener((observable, oldValue, newValue) -> {
                    if (oldValue != null && Objects.deepEquals(oldValue, newValue)) {
                        System.out.println("Message stack updated!");
                    } else {
                        System.out.println("Update pushed to message stack, but no update was found!");
                    }

                });
                return newObject;
            };
            try {
                messageListObservableListSimpleObjectProperty = call.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return messageListObservableListSimpleObjectProperty;
    }

    public SimpleObjectProperty<Store> storeSimpleObjectProperty() {
        if (storeSimpleObjectProperty == null) {
            Callable<SimpleObjectProperty<Store>> call = () -> {
                SimpleObjectProperty<Store> newObject = new SimpleObjectProperty<>();
                newObject.addListener((observable, oldValue, newValue) -> {
                    newValue.addConnectionListener(resetConnectionAdapter);
                    if (oldValue != null) {
                        oldValue.removeConnectionListener(resetConnectionAdapter);
                        if (oldValue.isConnected()) {
                            try {
                                oldValue.close();
                            } catch (MessagingException e) {
                                e.printStackTrace();
                                Logger.getGlobal().log(Level.SEVERE, "Couldnt disconnect old connection!", e);
                            }
                        }
                        if (!oldValue.equals(newValue)) {
                            System.out
                                    .println("A new store has been registered, new: " + newValue + " old: " + oldValue);
                            System.out.println("Is new store connected? " + (newValue.isConnected() ? "Yes" : "No"));
                        } else {
                            System.out.println(
                                    "It seems an update was pushed to store variable, but no change was detected! If you see this for the first time ignore this :)");
                        }
                    }

                });
                return newObject;
            };
            try {
                storeSimpleObjectProperty = call.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return storeSimpleObjectProperty;
    }

    public SimpleObjectProperty<IMAPFolder> imapFolderSimpleObjectProperty() {
        if (imapFolderSimpleObjectProperty == null) {

            Callable<SimpleObjectProperty<IMAPFolder>> call = () -> {
                SimpleObjectProperty<IMAPFolder> newObject = new SimpleObjectProperty<>();

                newObject.addListener((observable, oldValue, newValue) -> {
                    try {
                        if (oldValue != null) {
                            if (oldValue.isOpen()) {
                                oldValue.close();
                                Logger.getGlobal().log(Level.INFO, "Closing connection of old folder: " + oldValue);
                            }
                            if (!Objects.deepEquals(oldValue, newObject)) {
                                System.out.println(
                                        "A new IMAPfolder has been registered, new: " + newValue + " old: " + oldValue);
                                System.out.println("Is new IMAPfolder open? " + (newValue.isOpen() ? "Yes" : "No"));
                            } else {
                                System.out.println(
                                        "It seems that an update was pushed to the IMAPfolder but no change was detected! If you see this for the first time ignore this :)");
                            }
                        }

                        newValue.open(IMAPFolder.READ_ONLY);
                        newValue.addMessageCountListener(listenForMessageMessageCountAdapter);
                        newValue.addConnectionListener(resetConnectionAdapter);
                        Logger.getGlobal().log(Level.FINE, "Added message count adapter as listener");
                        Logger.getGlobal().log(Level.FINE, "Opening imap folder: " + newValue);

                        Task<Object> idleTask = new Task<Object>() {
                            @Override
                            protected Object call() {
                                int cycles = 0;
                                while (!isCancelled()) {
                                    try {
                                        updateTitle(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")
                                                .format(LocalDateTime.now()));
                                        updateMessage("Cycle: " + (cycles++));
                                        newValue.idle();
                                        Thread.sleep(15000);
                                    } catch (InterruptedException | MessagingException e) {
                                        e.printStackTrace();
                                    }
                                }
                                return null;
                            }
                        };
                        Task<Message[]> fetchTask = new Task<Message[]>() {
                            @Override
                            protected Message[] call() throws Exception {
                                updateTitle(
                                        DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now()));
                                newValue.fetch(newValue.getMessages(), FETCHPROFILE);
                                return newValue.getMessages();
                            }
                        };

                        fetchThreadSimpleObjectProperty().set(fetchTask);
                        idleThreadSimpleObjectProperty().set(idleTask);

                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }
                });
                return newObject;
            };
            try {
                imapFolderSimpleObjectProperty = call.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return imapFolderSimpleObjectProperty;

    }

    public SimpleObjectProperty<Task<Object>> idleThreadSimpleObjectProperty() {
        if (idleThreadSimpleObjectProperty == null) {
            Callable<SimpleObjectProperty<Task<Object>>> call = () -> {
                SimpleObjectProperty<Task<Object>> newObject = new SimpleObjectProperty<>();
                newObject.addListener((observable, oldValue, newValue) -> {
                    if (newValue != null && !newValue.isRunning()) {
                        Thread thread = new Thread(newValue);
                        thread.setDaemon(true);
                        thread.start();
                    }

                    if (oldValue != null && oldValue.isRunning()) {
                        oldValue.cancel();
                    }
                });
                return newObject;
            };
            try {
                idleThreadSimpleObjectProperty = call.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return idleThreadSimpleObjectProperty;
    }

    public SimpleObjectProperty<Task<Message[]>> fetchThreadSimpleObjectProperty() {
        if (fetchThreadSimpleObjectProperty == null) {
            Callable<SimpleObjectProperty<Task<Message[]>>> call = () -> {
                SimpleObjectProperty<Task<Message[]>> newObject = new SimpleObjectProperty<>();
                newObject.addListener((observable, oldValue, newValue) -> {
                    if (oldValue != null && (oldValue.isRunning() || !oldValue.isDone())) {
                        oldValue.cancel();
                    }
                    if (newValue != null && !newValue.isRunning()) {
                        newValue.setOnSucceeded(workerStateEvent -> messagesObservableListSimpleObjectProperty()
                                .set(FXCollections.observableArrayList(newValue.getValue())));
                        Thread thread = new Thread(newValue);
                        thread.setDaemon(true);
                        thread.start();

                    } else {
                        JFXOptionPane.showMessageDialog("already running!");
                    }
                });
                return newObject;
            };
            try {
                fetchThreadSimpleObjectProperty = call.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return fetchThreadSimpleObjectProperty;
    }

    public SimpleObjectProperty<Message[]> lastReceivedMessageSimpleObjectProperty() {
        if (lastReceivedMessageSimpleObjectProperty == null) {
            lastReceivedMessageSimpleObjectProperty = new SimpleObjectProperty<>();
        }
        return lastReceivedMessageSimpleObjectProperty;
    }

    public SimpleObjectProperty<ConnectionSettings> connectionSettingsSimpleObjectProperty() {
        if (connectionSettingsSimpleObjectProperty == null) {
            connectionSettingsSimpleObjectProperty = new SimpleObjectProperty<>();
        }
        return connectionSettingsSimpleObjectProperty;
    }
}
