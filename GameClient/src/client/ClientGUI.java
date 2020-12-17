package client;

import entity.Bomb;
import entity.Tank;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import logic.InputManager;
import protocol.Message;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.Socket;

public class ClientGUI extends Application {
    private Stage mainStage;
    private Scene mainScene;
    private static final double W = 875, H = 600;
    private Group group;

    private static Label statusLabel;
    private GameBoardPane gameBoardPane;
    private boolean isRunning = true;

    private Tank clientTank;
    private Client client;

    private static int score = 0;

    @Override
    public void start(Stage stage) throws MalformedURLException {
        mainStage = stage;
        startGame();
    }

    public void startGame() throws MalformedURLException {
        Label label = new Label("Tanks 2D Multiplayer Game");
        label.setTextFill(Color.CHARTREUSE);
        label.setFont(new Font("Comic Sans MS", 25));
        label.relocate(150, 15);

        group = new Group(label);

        client = Client.getGameClient();
        clientTank = new Tank();
        gameBoardPane = new GameBoardPane(clientTank, false);
        gameBoardPane.relocate(30, 70);

        group.getChildren().add(gameBoardPane);
        addRegisterForm();
        addStatusPane();

        mainScene = new Scene(group, W, H, Color.BLACK);

        mainScene.setOnKeyPressed(new InputManager(clientTank));

        mainStage.setOnCloseRequest(windowEvent -> {
            Client.getGameClient().sendToServer(new Message().exitMessagePacket(clientTank.getTankID()));
            Platform.exit();
            System.exit(0);
        });

        mainStage.setScene(mainScene);
        mainStage.setTitle("Tanks 2D");
        mainStage.show();
    }

    private void addRegisterForm() {
        final GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Label ipLabel = new Label("Ip address:");
        grid.add(ipLabel, 0, 1);

        final TextField ipText = new TextField("localhost");
        grid.add(ipText, 1, 1);

        Label portLabel = new Label("Port:");
        grid.add(portLabel, 0, 2);

        final TextField portText = new TextField("11111");
        grid.add(portText, 1, 2);

        final Button button = new Button("Register");
        button.setOnAction(actionEvent -> {
            grid.setDisable(true);

            try {
                client.register(ipText.getText(), Integer.parseInt(portText.getText()), clientTank.getXPosition(),clientTank.getYPosition());

                gameBoardPane.setGameStatus(true);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                new ClientReceivingThread(client.getSocket()).start();
            } catch (IOException ex) {
                showModalWindow("The Server is not running!");
            } catch (IllegalArgumentException ex) {
                System.out.println("This server does not exist!");
            }
        });
        grid.add(button, 1, 3);

        BackgroundFill background_fill = new BackgroundFill(Color.YELLOW, CornerRadii.EMPTY, Insets.EMPTY);

        grid.setBackground(new Background(background_fill));
        grid.relocate(575, 70);

        group.getChildren().add(grid);
    }

    private void addStatusPane() {
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(75, 167, 200, 25));

        statusLabel = new Label("Score: 0");
        statusLabel.setPrefWidth(75);
        statusLabel.setMaxWidth(75);
        vbox.getChildren().add(statusLabel);

        BackgroundFill background_fill = new BackgroundFill(Color.YELLOW, CornerRadii.EMPTY, Insets.EMPTY);
        vbox.setBackground(new Background(background_fill));
        vbox.relocate(575, 250);
        group.getChildren().add(vbox);
    }

    public static void setScore(final int scoreSum) {
        Platform.runLater(() -> {
            score += scoreSum;
            statusLabel.setText("Score: " + score);
        });
    }

    private void showModalWindow(String message) {
        Platform.runLater(() -> {
            Stage mwStage = new Stage();

            VBox vbox = new VBox();
            vbox.setAlignment(Pos.CENTER);
            vbox.setPadding(new Insets(10, 10, 10, 10));

            Label loseLabel = new Label(message);
            vbox.getChildren().add(loseLabel);

            HBox hbox = new HBox();
            hbox.setAlignment(Pos.CENTER);
            hbox.setPadding(new Insets(10, 10, 10, 10));
            hbox.setSpacing(20);

            Button okButton = new Button("OK");
            okButton.setOnAction(actionEvent -> {
                try {
                    mwStage.setOnCloseRequest(null);
                    mwStage.close();
                    startGame();
                } catch (MalformedURLException e) {
                    System.exit(0);
                }
            });
            Button closeButton = new Button("Close");
            closeButton.setOnAction(actionEvent -> System.exit(0));
            hbox.getChildren().addAll(okButton, closeButton);

            vbox.getChildren().add(hbox);

            Group mwGroup = new Group(vbox);

            mwStage.setScene(new Scene(mwGroup, 300, 150));
            mwStage.setTitle("Oops!");
            mwStage.setOnCloseRequest(windowEvent -> System.exit(0));
            mwStage.initModality(Modality.WINDOW_MODAL);
            mwStage.initOwner(mainScene.getWindow());
            mwStage.show();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }

    public class ClientReceivingThread extends Thread {
        Socket clientSocket;
        DataInputStream reader;

        public ClientReceivingThread(Socket clientSocket) {
            this.clientSocket = clientSocket;
            try {
                reader = new DataInputStream(clientSocket.getInputStream());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        public void run() {
            while (isRunning) {
                String sentence = "";
                try {
                    sentence = reader.readUTF();
                } catch (EOFException e) {
                    showModalWindow("Sorry, but server is not working. Connect later.");
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                } catch (IOException ex) {
                    showModalWindow("Sorry, but server is not working. Connect later.");
                }

                if(sentence.startsWith("ID")) {
                    int id = Integer.parseInt(sentence.substring(2));
                    clientTank.setTankID(id);
                    gameBoardPane.registerClientTank();
                }
                else if(sentence.startsWith("NewClient")) {
                    int pos1 = sentence.indexOf(',');
                    int pos2 = sentence.indexOf('-');
                    int pos3 = sentence.indexOf('|');
                    int x = Integer.parseInt(sentence.substring(9, pos1));
                    int y = Integer.parseInt(sentence.substring(pos1 + 1, pos2));
                    int dir = Integer.parseInt(sentence.substring(pos2 + 1, pos3));
                    int id = Integer.parseInt(sentence.substring(pos3 + 1));
                    if(id != clientTank.getTankID()) {
                        try {
                            gameBoardPane.registerNewTank(new Tank(x, y, dir, id));
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                    }
                }
                else if(sentence.startsWith("Update")) {
                    int pos1 = sentence.indexOf(',');
                    int pos2 = sentence.indexOf('-');
                    int pos3 = sentence.indexOf('|');
                    int x = Integer.parseInt(sentence.substring(6, pos1));
                    int y = Integer.parseInt(sentence.substring(pos1 + 1, pos2));
                    int dir = Integer.parseInt(sentence.substring(pos2 + 1, pos3));
                    int id = Integer.parseInt(sentence.substring(pos3 + 1));

                    if (id != clientTank.getTankID()) {
                        gameBoardPane.getTank(id).setXPosition(x);
                        gameBoardPane.getTank(id).setYPosition(y);
                        gameBoardPane.getTank(id).setDirection(dir);
                        gameBoardPane.getTank(id).updatePosition();
                        //System.out.println(sentence);
                    }
                }
                else if (sentence.startsWith("Shot")) {
                    int id = Integer.parseInt(sentence.substring(4));

                    Bomb bomb = null;
                    if (id == clientTank.getTankID()) {
                        bomb = clientTank.getCurBomb();
                        gameBoardPane.registerBomb(bomb);
                        bomb.startBombThread(true);
                    } else {
                        try {
                            gameBoardPane.getTank(id).shot();

                            bomb = gameBoardPane.getTank(id).getCurBomb();
                            gameBoardPane.registerBomb(bomb);
                            bomb.startBombThread(false);
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                    }

                    final Bomb finalBomb = bomb;
                    new Thread(() -> {
                        try {
                            while (!finalBomb.getBombStatus()) {
                                Thread.sleep(15);
                            }
                            gameBoardPane.removeBomb(finalBomb);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }).start();
                }
                else if (sentence.startsWith("Remove")) {
                    int id=Integer.parseInt(sentence.substring(6));

                    if(id == clientTank.getTankID()) {
                        showModalWindow("You lose! Do you want to try again?");
                    } else {
                        gameBoardPane.removeTank(id);
                    }
                }
                else if (sentence.startsWith("Exit")) {
                    int id = Integer.parseInt(sentence.substring(4));

                    if(id != clientTank.getTankID()) {
                        gameBoardPane.removeTank(id);
                    }
                }

            }
        }
    }
}
