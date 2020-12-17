package client;

import entity.Bomb;
import entity.Tank;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;

public class GameBoardPane extends Pane {
    private Tank tank;
    private static ArrayList<Tank> tanks;
    private Node bg;
    private final String IMAGES_DIR = "C:\\Users\\ACER\\Documents\\Projects\\Java projects\\Tanks-2D-Game\\GameClient\\Images\\";

    private boolean gameStatus;

    public GameBoardPane(Tank tank, boolean gameStatus) throws MalformedURLException {
        this.tank = tank;
        this.gameStatus = gameStatus;

        bg = new ImageView(new Image(new File(IMAGES_DIR + "bg.JPG").toURI().toURL().toExternalForm(), true));

        tanks = new ArrayList<Tank>(100);
        for (int i = 0; i < 100; i++) {
            tanks.add(null);
        }

        getChildren().add(0, bg);
    }

    public void registerClientTank() {
        Platform.runLater(() -> getChildren().add(tank.getTankIcon()));
    }
    public void registerNewTank(final Tank newTank) {
        tanks.set(newTank.getTankID(), newTank);

        Platform.runLater(new Runnable(){
            @Override
            public void run() {
                //System.out.println(newTank.getTankID());
                getChildren().add(newTank.getTankIcon());
            }
        });
    }

    public void registerBomb(final Bomb bomb) {
        Platform.runLater(() -> getChildren().add(bomb.getBombIcon()));
    }

    public void removeTank(final int tankID) {

        Platform.runLater(() -> {
            Tank removingTank = tanks.get(tankID);
            tanks.set(tankID,null);
            getChildren().remove(removingTank.getTankIcon());
        });

    }

    public void removeBomb(final Bomb bomb) {
        Platform.runLater(() -> {
            if (getChildren().contains(bomb.getBombIcon())) {
                getChildren().remove(bomb.getBombIcon());
            }
        });

    }

    public Tank getTank(int id) {
        return tanks.get(id);
    }
    public void setGameStatus(boolean status) {
        gameStatus = status;
    }
    public static ArrayList<Tank> getClients() {
        return tanks;
    }
}
