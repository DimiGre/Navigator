package com.example.demo;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class DialogController implements Initializable {
    @FXML
    private VBox l1;

    @FXML
    private RadioButton r1;
    @FXML
    private RadioButton r2;
    @FXML
    private RadioButton r3;
    @FXML
    private RadioButton r4;
    @FXML
    private RadioButton r5;

    @FXML
    private TextField t1;

    @FXML
    private CheckBox doesItExist;

    @FXML
    private Button save;
    @FXML
    private Button decline;

    Point p = new Point();

    @Override
    public void initialize(URL url, ResourceBundle rb){

        r1.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                p.type = 1;
            }
        });


        r2.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                p.type = 2;
            }
        });


        r3.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                p.type = 3;
            }
        });


        r4.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                p.type = 4;
            }
        });
    }
    @FXML
    public void Save(MouseEvent event){
        if(!(doesItExist.isSelected())) {
            p.setName(t1.getText());
            HelloApplication.hc.addPoint(p);
            ((Stage) save.getScene().getWindow()).close();
        } else {
            HelloApplication.hc.addNewPosToExistPoint(t1.getText());
            ((Stage) save.getScene().getWindow()).close();
        }
    }
    @FXML
    public void Decline(MouseEvent event){
        ((Stage)save.getScene().getWindow()).close();
    }
}
