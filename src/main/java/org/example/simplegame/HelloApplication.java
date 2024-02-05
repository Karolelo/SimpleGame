package org.example.simplegame;

import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class HelloApplication extends Application {

    private Label percentageLabel = new Label("0%");
    private AnimationTimer timer;
    private Pane root;
    private Stage primaryStage;
    private List<Node> blocks = new ArrayList<>();
    private int winCounter=0;
    private int allBlocksInGame=200;

    private Scene createMenuScene() {
        Button playButton = new Button("Play");
        playButton.setOnAction(e -> startGame());

        Button optionsButton = new Button("Options");

        BorderPane layout = new BorderPane();
        VBox container=new VBox(playButton,optionsButton);
        container.setAlignment(Pos.CENTER);
        container.setSpacing(10);

        layout.setCenter(container);


        return new Scene(layout, 800, 600);
    }
    private Parent createContent() {

        root = new Pane();

        root.setPrefSize(800, 600);

        percentageLabel.setStyle("-fx-font-size: 24px;");
        percentageLabel.setPrefWidth(root.getPrefWidth());
        percentageLabel.setPrefHeight(50);
        percentageLabel.setLayoutY(root.getPrefHeight() - percentageLabel.getPrefHeight());
        percentageLabel.setLayoutX((root.getPrefWidth() - percentageLabel.getPrefWidth()) / 2);

        root.getChildren().add(percentageLabel);


        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                onUpdate();
            }
        };
        timer.start();

        return root;
    }

    private void startGame() {
        Scene gameScene = new Scene(createContent(), 800, 600);
        primaryStage.setScene(gameScene);
    }



    private Node spawnBlock() {
        Rectangle rect = new Rectangle(40, 40, Color.RED);
        rect.setTranslateY(0);
        double maxX = root.getWidth() - rect.getWidth();
        rect.setTranslateX((int)(Math.random() * maxX));


        rect.setOnMouseClicked(event -> {
            root.getChildren().remove(rect);
            blocks.remove(rect);
            winCounter++;
        });
        root.getChildren().add(rect);
        return rect;
    }

    private void onUpdate() {

        Iterator<Node> iterator = blocks.iterator();
        while (iterator.hasNext()) {
            Node block = iterator.next();
            block.setTranslateY(block.getTranslateY() + Math.random() * 5);
            if (block.getTranslateY() > root.getPrefHeight() + 40) {
                iterator.remove();
                root.getChildren().remove(block);
            }
        }
        percentageLabel.setText(String.valueOf(winCounter)+"%");

        if (Math.random() < 0.075&&allBlocksInGame>0) {
            blocks.add(spawnBlock());
            allBlocksInGame--;
        }

        checkState();
    }

    private void checkState() {

        if (allBlocksInGame==0&&blocks.isEmpty()) {

            timer.stop();
            String win = "YOU LOSE";

            HBox hBox = new HBox();
            hBox.setTranslateX(300);
            hBox.setTranslateY(250);
            root.getChildren().add(hBox);

            for (int i = 0; i < win.toCharArray().length; i++) {
                char letter = win.charAt(i);

                Text text = new Text(String.valueOf(letter));
                text.setFont(Font.font(48));
                text.setOpacity(0);

                hBox.getChildren().add(text);

                FadeTransition ft = new FadeTransition(Duration.seconds(0.66), text);
                ft.setToValue(1);
                ft.setDelay(Duration.seconds(i * 0.15));
                ft.play();
            }
        }

        if (winCounter>=100) {
            timer.stop();
            String win = "YOU WIN";

            HBox hBox = new HBox();
            hBox.setTranslateX(300);
            hBox.setTranslateY(250);
            root.getChildren().add(hBox);

            for (int i = 0; i < win.toCharArray().length; i++) {
                char letter = win.charAt(i);

                Text text = new Text(String.valueOf(letter));
                text.setFont(Font.font(48));
                text.setOpacity(0);

                hBox.getChildren().add(text);

                FadeTransition ft = new FadeTransition(Duration.seconds(0.66), text);
                ft.setToValue(1);
                ft.setDelay(Duration.seconds(i * 0.15));
                ft.play();
            }
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        this.primaryStage=stage;
        Scene menuScene = createMenuScene();
        primaryStage.setScene(menuScene);
        stage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}