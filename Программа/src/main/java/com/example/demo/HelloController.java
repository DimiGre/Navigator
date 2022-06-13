package com.example.demo;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import java.io.*;
import java.net.URL;
import java.util.*;

import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

public class HelloController implements Initializable {
    //h640 w738
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private VBox floors;
    @FXML
    private Button path;
    @FXML
    private TextField cabfrom;
    @FXML
    private TextField cabto;
    @FXML
    private TextField textSize;

    double fX = -1;
    double fY = -1;
    boolean flag = true;

    double pX, pY;
    ImageView imageView;

    private EventHandler<MouseEvent> eventEventHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent mouseEvent) {
            if (flag) {
                fX = ((Text) mouseEvent.getSource()).getLayoutX();
                fY = ((Text) mouseEvent.getSource()).getLayoutY();
                prevText = ((Text) mouseEvent.getSource());
                flag = false;
            } else {
                double nX = ((Text) mouseEvent.getSource()).getLayoutX();
                double nY = ((Text) mouseEvent.getSource()).getLayoutY();
                anchorPane.getChildren().add(new Line(fX, fY, nX, nY));
                flag = true;
                DefaultWeightedEdge dwe = new DefaultWeightedEdge();
                g.addEdge(hashMap.get(prevText), hashMap.get((Text) mouseEvent.getSource()), dwe);
                g.setEdgeWeight(dwe, Math.sqrt((fX - nX) * (fX - nX) + (fY - nY) * (fY - nY)));
                System.out.println(g.getEdgeWeight(dwe));
                System.out.println(g);
            }
        }
    };
    private int currentID;
    Graph<Point, DefaultWeightedEdge> g = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
    Map<Text, Point> hashMap = new HashMap<>();
    static Map<Button, FragmentInfo> buttonImageMap = new HashMap<>();
    int c = 0;
    Text prevText;
    static String deWay = System.getProperty("user.dir") +"\\files\\";
    String fontStyle = "-fx-font: 7 arial;";

    public static void setOnClose(Stage stage) {
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                try {
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(deWay + "bim.ser"));
                    ArrayList<FragmentInfo> arrayList = new ArrayList(buttonImageMap.values());
                    objectOutputStream.writeObject(arrayList);
                    objectOutputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }



    @Override
    public void initialize(URL url, ResourceBundle rb) {
        HelloApplication.hc = this;
        File theDir = new File(deWay);
        if (!theDir.exists()){
            theDir.mkdirs();
        }
        try {
            boolean flag = (new File(deWay + "bim.ser")).exists();
            if(flag) {
                ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(deWay + "bim.ser"));
                ArrayList<FragmentInfo> arrayList = (ArrayList<FragmentInfo>) objectInputStream.readObject();
                for (FragmentInfo fragment :
                        arrayList) {
                    addFragment(fragment);
                }
                objectInputStream.close();
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

    @FXML
    public void LoadFromFile(MouseEvent event) {
        FileInputStream inputStream = null;
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(deWay + "gr.ser"));
            g = (Graph<Point, DefaultWeightedEdge>) objectInputStream.readObject();
            objectInputStream.close();
            hashMap = new HashMap<>();
            c = 0;
            for (Point p:
                 g.vertexSet()) {
                if(Integer.parseInt(p.getID()) > c) c = Integer.parseInt(p.getID()) + 1;
            }
            showPoints();
            System.out.println(g);
            System.out.println(hashMap);
            objectInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        showPoints();
    }

    public void showPoints(){
        for (Point p :
                g.vertexSet()) {
            if(p.getPos(currentID) != null) {
                Text text = new Text(p.getName() + " ID: " + p.getID());
                text.setStyle(fontStyle);
                text.setLayoutX(p.getPos(currentID).getFirst() * imageView.getFitWidth());
                text.setLayoutY(p.getPos(currentID).getSecond() * imageView.getFitHeight());
                text.setId(p.getID());
                text.setOnMouseClicked(eventEventHandler);
                anchorPane.getChildren().add(text);
                hashMap.put(text, p);
            }
        }

        for (DefaultWeightedEdge defaultEdge :
                g.edgeSet()) {
            Point source = g.getEdgeSource(defaultEdge);
            Point target = g.getEdgeTarget(defaultEdge);
            if((source.getPos(currentID) != null) && (target.getPos(currentID) != null))
                anchorPane.getChildren().add(new Line(source.getPos(currentID).getFirst() * imageView.getFitWidth(), source.getPos(currentID).getSecond() * imageView.getFitHeight(), target.getPos(currentID).getFirst() * imageView.getFitWidth(), target.getPos(currentID).getSecond() * imageView.getFitHeight()));
        }
    }

    @FXML
    public void SaveToFile(MouseEvent event) {
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(deWay + "gr.ser"));
            objectOutputStream.writeObject(g);
            objectOutputStream.close();
            objectOutputStream = new ObjectOutputStream(new FileOutputStream(deWay + "toandroid.ser"));
            objectOutputStream.writeObject(g.toString());
            objectOutputStream.close();
            objectOutputStream = new ObjectOutputStream(new FileOutputStream(deWay + "toandroidpoints.ser"));
            HashMap<String, Point> androidHashMap = new HashMap<>();

            for (Point p:
                    g.vertexSet()) {
                androidHashMap.put(p.getID(), p);
            }

            objectOutputStream.writeObject(androidHashMap);
            objectOutputStream.close();
            objectOutputStream = new ObjectOutputStream(new FileOutputStream(deWay + "toandroidfragments.ser"));
            ArrayList<FragmentInfo> arrayList = new ArrayList<FragmentInfo>(buttonImageMap.values());
            objectOutputStream.writeObject(arrayList);
            objectOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setImageView(String url) {
        anchorPane.getChildren().clear();
        imageView = new ImageView(new Image(url));
        imageView.setFitWidth(anchorPane.getWidth());
        imageView.setFitHeight(anchorPane.getHeight());
        imageView.setPickOnBounds(true);
        imageView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                pX = mouseEvent.getX() / imageView.getFitWidth();
                pY = mouseEvent.getY() / imageView.getFitHeight();
                Parent root;
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader();
                    URL url = getClass().getResource("dialog.fxml");
                    root = fxmlLoader.load(url);
                    Stage stage = new Stage();
                    stage.setTitle("My New Stage Title");
                    stage.setScene(new Scene(root));
                    stage.sizeToScene();
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        anchorPane.getChildren().add(imageView);
    }

    @FXML
    public void OpenFloor(MouseEvent event) {
        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent root = null;
        try {
            root = fxmlLoader.load(getClass().getResource("dialogFragment.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.sizeToScene();
        stage.show();
    }

    public void addFragment(FragmentInfo fragmentInfo) {
        fragmentInfo.URL = deWay + fragmentInfo.URL.substring(fragmentInfo.URL.indexOf("pictures\\"));
        setImageView(fragmentInfo.URL);
        Button btn = new Button(fragmentInfo.name);
        btn.setId(fragmentInfo.ID);
        currentID = Integer.parseInt(fragmentInfo.ID);
        buttonImageMap.put(btn, fragmentInfo);
        btn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Button me = (Button) event.getSource();
                anchorPane.getChildren().removeAll();
                currentID = Integer.parseInt(me.getId());
                setImageView(buttonImageMap.get(me).URL);
                showPoints();
            }
        });
        floors.getChildren().add(btn);
    }

    @FXML
    public void RemoveFloor(MouseEvent event) {
        Button btn = (Button) floors.lookup("#" + currentID);
        buttonImageMap.remove(btn);
        floors.getChildren().remove(btn);
        currentID = -1;
        anchorPane.getChildren().clear();
        imageView = null;
    }

    public void addPoint(Point p) {
        p.addXY(currentID, new Pair<Double, Double>(pX, pY));
        p.setID(c);
        c++;
        g.addVertex(p);
        Text rd = new Text();
        rd.setId(p.getID());
        rd.setStyle(fontStyle);
        rd.setText(p.getName() + " ID: " + p.getID());
        hashMap.put(rd, p);
        rd.setLayoutX(pX * imageView.getFitWidth());
        rd.setLayoutY(pY * imageView.getFitHeight());
        rd.setOnMouseClicked(eventEventHandler);
        anchorPane.getChildren().add(rd);
    }

    List<Line> lineList = new ArrayList<>();

    @FXML
    public void FindPath(MouseEvent event) {
        if (lineList.size() == 0) {
            Point from = findPoint(cabfrom.getText());
            Point to = findPoint(cabto.getText());
            DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(g);
            GraphPath<Point, DefaultWeightedEdge> graphPathList = dijkstraShortestPath.getPath(from, to);
            System.out.println(graphPathList.getWeight());
            for (DefaultWeightedEdge defaultEdge :
                    graphPathList.getEdgeList()) {
                Point source = g.getEdgeSource(defaultEdge);
                Point target = g.getEdgeTarget(defaultEdge);

                if((source.getPos(currentID) != null) && (target.getPos(currentID) != null)) {
                    Line line = new Line(source.getPos(currentID).getFirst() * imageView.getFitWidth(), source.getPos(currentID).getSecond() * imageView.getFitHeight(), target.getPos(currentID).getFirst() * imageView.getFitWidth(), target.getPos(currentID).getSecond() * imageView.getFitHeight());
                    line.setStroke(Color.RED);
                    anchorPane.getChildren().add(line);
                    lineList.add(line);
                }
            }
            path.setText("Скрыть маршрут");
        } else {
            anchorPane.getChildren().removeAll(lineList);
            lineList.removeAll(lineList);
            path.setText("Найти маршрут");
        }
    }

    private Point findPoint(String name) {
        for (Point point :
                g.vertexSet()) {
            if (point.getName().equals(name)) return point;
        }
        return null;
    }

    private Point findPointByID(String name) {
        for (Point point :
                g.vertexSet()) {
            if (point.getID().equals(name)) return point;
        }
        return null;
    }

    @FXML
    public void RemovePoint(){
        anchorPane.getChildren().remove(prevText);
        Point p = hashMap.get(prevText);
        p.removeXY(currentID);
        if(p.positions.size() == 0) {
            g.removeVertex(p);
            hashMap.remove(prevText);
        }
        prevText = null;
        flag = true;
    }

    @FXML
    public void ChangeTextSize(){
        fontStyle = "-fx-font: " + textSize.getText() + " arial;";
    }

    public void addNewPosToExistPoint(String text) {
        Point p = findPointByID(text);
        p.addXY(currentID, new Pair<>(pX, pY));
        Text rd = new Text();
        rd.setId(p.getID());
        rd.setText(p.getName() + " ID: " + p.getID());
        rd.setStyle(fontStyle);
        rd.setLayoutX(pX * imageView.getFitWidth());
        rd.setLayoutY(pY * imageView.getFitHeight());
        rd.setOnMouseClicked(eventEventHandler);
        hashMap.put(rd, p);
        anchorPane.getChildren().add(rd);
    }
}