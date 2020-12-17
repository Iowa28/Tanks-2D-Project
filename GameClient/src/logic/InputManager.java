package logic;

import client.Client;
import entity.Tank;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import protocol.Message;

import java.net.MalformedURLException;

public class InputManager implements EventHandler<KeyEvent> {
    private Tank tank;
    private Client client;

    public InputManager(Tank tank) {
        this.tank = tank;
        this.client = Client.getGameClient();
    }

    @Override
    public void handle(KeyEvent keyEvent) {
        switch (keyEvent.getCode()) {
            case UP:
                tank.moveForward();
                client.sendToServer(new Message().updatePacket(tank.getXPosition(), tank.getYPosition(), tank.getTankID(), tank.getDirection()));

                break;
            case DOWN:
                tank.moveBackward();
                client.sendToServer(new Message().updatePacket(tank.getXPosition(), tank.getYPosition(), tank.getTankID(), tank.getDirection()));

                break;
            case RIGHT:
                tank.moveRight();
                client.sendToServer(new Message().updatePacket(tank.getXPosition(), tank.getYPosition(), tank.getTankID(), tank.getDirection()));

                break;
            case LEFT:
                tank.moveLeft();
                client.sendToServer(new Message().updatePacket(tank.getXPosition(), tank.getYPosition(), tank.getTankID(), tank.getDirection()));

                break;
            case SPACE:
                try {
                    tank.shot();
                    client.sendToServer(new Message().shotPacket(tank.getTankID()));
                } catch (MalformedURLException e) {
                    try {
                        tank.shot();
                    } catch (MalformedURLException malformedURLException) {
                        System.err.println("Got exception with tank shooting. Error message:");
                        System.err.println(e.getMessage());
                    }
                }
                break;
        }
        tank.updatePosition();
    }
}
