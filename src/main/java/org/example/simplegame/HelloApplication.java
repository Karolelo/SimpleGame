package org.example.simplegame;
import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
@Slf4j
public class HelloApplication extends Application {

    private Label percentageLabel = new Label("0%");
    private AnimationTimer timer;
    private Pane root;
    private Stage primaryStage;
    private List<Node> blocks = new ArrayList<>();
    private int winCounter;
    private int allBlocksInGame;
    private double frequencyOfFallingBlocks=0.075;

    private Scene createMenuScene() {
        Button playButton = new Button("Play");
        playButton.getStyleClass().add("button-style");
        playButton.setOnAction(e -> startGame());

        Button optionsButton = new Button("Options");
        optionsButton.getStyleClass().add("button-style");
        optionsButton.setOnAction(e->turnOptions());

        Button exitButton = new Button("Exit");
        exitButton.getStyleClass().add("button-style");
        exitButton.setOnAction(e -> Platform.exit());

        VBox container = new VBox(playButton, optionsButton, exitButton);
        container.setAlignment(Pos.CENTER);
        container.setSpacing(10);

        BorderPane layout = new BorderPane();
        layout.setCenter(container);

        Scene scene = new Scene(layout, 800, 600);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        return scene;
    }
    private Parent optionsMenu(){

        BorderPane pane=new BorderPane();

        Label label=new Label("Choose difficulty level");
        label.getStyleClass().add("label");


        ComboBox<String> gameDifficulty=new ComboBox<>();
        gameDifficulty.getItems().addAll("Normal","Hard","Easy");
        gameDifficulty.setValue("Normal");
        gameDifficulty.setOnAction(e -> {
            String selectedDifficulty = gameDifficulty.getValue();
            updateBlockFallingFrequency(selectedDifficulty);
        });
        gameDifficulty.getStyleClass().add("combo-box");


        Button back=new Button("Back");
        back.setOnAction(e->primaryStage.setScene(createMenuScene()));
        back.getStyleClass().add("button-style");


        VBox options=new VBox();
        options.getChildren().addAll(label,gameDifficulty,back);
        options.setAlignment(Pos.CENTER);
        options.setSpacing(10);

        pane.setCenter(options);
        pane.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        return pane;
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
        allBlocksInGame=200;
        winCounter=0;

        Scene gameScene = new Scene(createContent(), 800, 600);
        primaryStage.setScene(gameScene);
    }
    private void turnOptions(){
        Scene gameScene = new Scene(optionsMenu(), 800, 600);
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
    private void updateBlockFallingFrequency(String difficulty){

        switch (difficulty) {
            case "Easy":
                frequencyOfFallingBlocks=0.05;
                break;
            case "Normal":
                frequencyOfFallingBlocks=0.075;
                break;
            case "Hard":
                frequencyOfFallingBlocks=0.15;
                break;
            default:
                frequencyOfFallingBlocks=0.075;
                break;
        }

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

        if (Math.random() < frequencyOfFallingBlocks&&allBlocksInGame>0) {
            blocks.add(spawnBlock());
            allBlocksInGame--;
        }

        checkState();
    }

    private void checkState() {
        if (allBlocksInGame == 0 && blocks.isEmpty()) {
            displayEndGameMessage("YOU LOSE");
        }

        if (winCounter >= 100) {
            displayEndGameMessage("YOU WIN");
        }
    }


    private void displayEndGameMessage(String message) {
        timer.stop();

        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);
        layout.setTranslateX(300);
        layout.setTranslateY(250);

        HBox hBox = new HBox();
        for (int i = 0; i < message.toCharArray().length; i++) {
            char letter = message.charAt(i);

            Text text = new Text(String.valueOf(letter));
            text.setFont(Font.font(48));
            text.setOpacity(0);

            hBox.getChildren().add(text);

            FadeTransition ft = new FadeTransition(Duration.seconds(0.66), text);
            ft.setToValue(1);
            ft.setDelay(Duration.seconds(i * 0.15));
            ft.play();
        }

        Button restartButton = new Button("Restart");
        restartButton.setOnAction(e -> startGame());
        restartButton.getStyleClass().add("button-style");


        Button exitButton = new Button("Exit");
        exitButton.setOnAction(e -> Platform.exit());
        exitButton.getStyleClass().add("button-style");

        layout.getChildren().addAll(hBox, restartButton, exitButton);

        root.getChildren().add(layout);
        root.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
    }

    @Override
    public void start(Stage stage){
        this.primaryStage=stage;
        Scene menuScene = createMenuScene();
        primaryStage.setScene(menuScene);
        primaryStage.setTitle("Falling Blocks");
        Image myImage=new Image(getClass().getResource("img.png").toExternalForm());
        primaryStage.getIcons().add(myImage);
        stage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}