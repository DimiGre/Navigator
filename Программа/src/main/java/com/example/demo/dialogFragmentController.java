package com.example.demo;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class dialogFragmentController implements Initializable {

    @FXML
    private TextField name;
    @FXML
    private TextField ID;
    @FXML
    private Text path;

    private String url;

    public void ChooseFile(MouseEvent event){
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Open File");
        File file = chooser.showOpenDialog(path.getScene().getWindow());
        if (file != null) {
                url = file.getPath();
                path.setText("Выбранный файл: " + url);
        }
    }
    @FXML
    public void Accept(MouseEvent event){
        HelloApplication.hc.addFragment(new FragmentInfo(name.getText(),ID.getText() , url));
        Decline(event);
    }
    @FXML
    public void Decline(MouseEvent event){
        ((Stage)path.getScene().getWindow()).close();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

}
