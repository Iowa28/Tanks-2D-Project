import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.SocketException;

public class ServerGUI extends Application {
    private Server server;

    private Button startButton;
    private Button stopButton;
    private Label statusLabel;

    @Override
    public void start(Stage stage) throws Exception {

        statusLabel = new Label("");

        startButton = new Button("Start Server");
        startButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    server = new Server();
                    server.start();

                    startButton.setDisable(true);
                    statusLabel.setText("Server is running...");
                } catch (SocketException e) {
                    e.printStackTrace();
                }

            }
        });

        stopButton = new Button("Stop Server");
        stopButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    if (server != null) {
                        server.stopServer();
                    }
                } catch (IOException e) {
                    statusLabel.setText("Failed to stop the server. Please try again.");
                    return;
                }

                statusLabel.setText("Server is stopping...");
                stopButton.setDisable(true);

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.exit(0);
            }
        });

        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER);
        hbox.setSpacing(20);
        hbox.getChildren().addAll(startButton, stopButton);

        BorderPane pane = new BorderPane();
        pane.setPadding(new Insets(10, 10, 10, 10));
        pane.setCenter(hbox);
        pane.setBottom(statusLabel);

        stage.setScene(new Scene(pane, 300, 200));
        stage.setTitle("Tanks 2D Server");
        stage.setOnCloseRequest(windowEvent -> System.exit(0));
        stage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}
