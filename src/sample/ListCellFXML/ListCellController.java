package sample.ListCellFXML;

//import com.company.CycleBackground;
import com.jfoenix.controls.JFXButton;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import sample.PrintObjects;
import sample.RootFXML.RootController;

import java.io.IOException;
import java.util.concurrent.Callable;

public class ListCellController {
    public Callable<Object> defaultOnDeleteAction = () -> {
        root().setVisible(false);
        return null;
    };
    public ObservableList<RootController.PRINTEROPTIONS> printAbles = FXCollections.observableArrayList();
    public RootController.PRINTEROPTIONS printText = RootController.PRINTEROPTIONS.TEXT;
    public RootController.PRINTEROPTIONS printImage = RootController.PRINTEROPTIONS.IMAGE;

//
 
    @FXML
    private ImageView nodeImage_ImageView;

    @FXML
    private JFXButton deleteButton_JFXButton;

    @FXML
    private Label nodeIdentifierLabel_Label;

    @FXML
    private Label descriptionLabel_Label;

    @FXML
    private StackPane rootStackPane_StackPane;

    @FXML
    void deleteButton_JFXButtonActionPerformed(ActionEvent event) throws Exception {
        if (deleteButton_JFXButtonActionPerformedProperty().isNotNull().get()) {
            deleteButton_JFXButtonActionPerformedProperty().get().call();
        }
    }

    @FXML
    void initialize() {
        assert deleteButton_JFXButton != null : "fx:id=\"deleteButton_JFXButton\" was not injected: check your FXML file 'ListCell.fxml'.";
        assert nodeIdentifierLabel_Label != null : "fx:id=\"nodeIdentifierLabel_Label\" was not injected: check your FXML file 'ListCell.fxml'.";
        assert descriptionLabel_Label != null : "fx:id=\"descriptionLabel_Label\" was not injected: check your FXML file 'ListCell.fxml'.";
        nodeImage_ImageView.fitWidthProperty().bind(rootStackPane_StackPane.prefWidthProperty());
        nodeImage_ImageView.fitHeightProperty().bind(rootStackPane_StackPane.prefHeightProperty());
//        CycleBackground.cycle(rootStackPane_StackPane, Duration.millis(15000), CycleBackground.CYCLE_MODE.COLOR);
//        rootStackPane_StackPane.setBackground(CycleBackground.createColorBackground(CycleBackground.randomColor(1f, 1f, 1f, 1f, 2)));

    }

    public Node root() {
        return rootStackPane_StackPane;
    }

    public void setTitle(String text) {
        nodeIdentifierLabel_Label.setText(text);
    }

    public void setDescription(PrintObjects.PosText posText) {
        descriptionLabel_Label.setText(posText.text);
        printText.save(posText);
        printAbles.add(printText);
    }

    public void setImage(PrintObjects.PosImg posImg) {

        int width = posImg.escPosImage.getWidthOfImageInBits();
        int height = posImg.escPosImage.getHeightOfImageInBits();
        System.out.println("Width img: " + width + " height: " + height);
        WritableImage image = new WritableImage(width, height);
        Image writableImage = SwingFXUtils.toFXImage(posImg.escPosImage.getBufferedImageCopy(), image);
        nodeImage_ImageView.setImage(writableImage);

        System.out.println("Comparable: iView w " + nodeImage_ImageView.getFitWidth() + " iView h " + nodeImage_ImageView.getFitHeight() +
                " wImage w: " + writableImage.getWidth() + " h: " + writableImage.getHeight());
        printImage.save(posImg);
        printAbles.add(printImage);
    }


    public void print() {
        if(printAbles != null && !printAbles.isEmpty()){
            for (RootController.PRINTEROPTIONS printAble : printAbles) {
                try {
                    printAble.print();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public <T> void setExitAction(Callable<T> exitAction) {
        deleteButton_JFXButtonActionPerformedProperty().set(exitAction);
    }


    private  ObjectProperty<Callable<?>> deleteButton_JFXButtonActionPerformedProperty;

    public <T> ObjectProperty<Callable<?>> deleteButton_JFXButtonActionPerformedProperty() {
        if (deleteButton_JFXButtonActionPerformedProperty == null) {
            deleteButton_JFXButtonActionPerformedProperty = new SimpleObjectProperty<>(defaultOnDeleteAction);
        }
        return deleteButton_JFXButtonActionPerformedProperty;
    }

    public StringProperty titleProperty() {
        return nodeIdentifierLabel_Label.textProperty();
    }

    public StringProperty descriptionProperty() {
        return descriptionLabel_Label.textProperty();
    }

    public ObjectProperty<Image> imageProperty() {
        return nodeImage_ImageView.imageProperty();
    }

}