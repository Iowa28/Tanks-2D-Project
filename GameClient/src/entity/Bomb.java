package entity;

import client.Client;
import client.ClientGUI;
import client.GameBoardPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import protocol.Message;
import sound.SimpleSoundPlayer;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;

public class Bomb {
    private Image bombImage;
    private ImageView bombIcon;
    private final String IMAGES_DIR = "C:\\Users\\ACER\\Documents\\Projects\\Java projects\\Tanks-2D-Game\\GameClient\\Images\\";
    private final String SOUNDS_DIR = "C:\\Users\\ACER\\Documents\\Projects\\Java projects\\Tanks-2D-Game\\GameClient\\Sounds\\";

    private int xPos;
    private int yPos;
    private int direction;
    public boolean stop;
    public int speed = 10;

    public Bomb(int x, int y, int direction) throws MalformedURLException {
        final SimpleSoundPlayer soundBoom = new SimpleSoundPlayer(SOUNDS_DIR + "boom.wav");
        final InputStream streamBoom =new ByteArrayInputStream(soundBoom.getSamples());
        xPos = x;
        yPos = y;
        this.direction = direction;
        stop = false;
        bombImage = new Image(new File( IMAGES_DIR + "bomb.png").toURI().toURL().toExternalForm());

        bombIcon = new ImageView(bombImage);
        bombIcon.relocate(xPos, yPos);

        Thread t= new Thread(new Runnable() {
            public void run() {
                soundBoom.play(streamBoom);
            }
        });
        t.start();
    }
    public int getPosX() {
        return xPos;
    }
    public int getPosY() {
        return yPos;
    }
    public boolean getBombStatus() {return stop;}
    public ImageView getBombIcon() {
        return bombIcon;
    }

    public boolean checkCollision() {

        ArrayList<Tank> clientTanks = GameBoardPane.getClients();
        int x, y;
        for(int i = 1; i < clientTanks.size(); i++) {
            if(clientTanks.get(i) != null) {
                x = clientTanks.get(i).getXPosition();
                y = clientTanks.get(i).getYPosition();

                if ((yPos >= y && yPos <= y + 43)&&(xPos >= x && xPos <= x + 43)) {

                    ClientGUI.setScore(50);

                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    if (clientTanks.get(i) != null) {
                        Client.getGameClient().sendToServer(new Message().removeClientPacket(clientTanks.get(i).getTankID()));
                    }

                    return true;
                }
            }
        }
        return false;
    }

    public void startBombThread(boolean chekCollision) {

        new BombShotThread(chekCollision).start();

    }

    private class BombShotThread extends Thread {
        boolean checkCollis;
        public BombShotThread(boolean chCollision) {
            checkCollis = chCollision;
        }
        public void run() {
            if (checkCollis) {

                if(direction == 1) {
                    while (yPos > 5) {
                        yPos -= speed;
                        bombIcon.relocate(xPos, yPos);
                        if(checkCollision()) {
                            break;
                        }
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
                else if(direction == 2) {
                    while(xPos < 495) {
                        xPos += speed;
                        bombIcon.relocate(xPos, yPos);
                        if(checkCollision()) {
                            break;
                        }
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }

                    }
                }
                else if(direction == 3) {
                    while(yPos < 455) {
                        yPos += speed;
                        bombIcon.relocate(xPos, yPos);
                        if(checkCollision()) {
                            break;
                        }
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }

                    }
                }
                else if(direction == 4) {
                    while(xPos > 5) {
                        xPos -= speed;
                        bombIcon.relocate(xPos, yPos);
                        if(checkCollision()) {
                            break;
                        }
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }

                    }
                }
                stop = true;

            } else {
                if(direction == 1) {
                    while (yPos > 5) {
                        yPos -= speed;
                        bombIcon.relocate(xPos, yPos);
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
                else if(direction == 2) {
                    while(xPos < 495) {
                        xPos += speed;
                        bombIcon.relocate(xPos, yPos);
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
                else if(direction == 3) {
                    while(yPos < 455) {
                        yPos += speed;
                        bombIcon.relocate(xPos, yPos);
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }

                    }
                }
                else if(direction == 4) {
                    while(xPos > 5) {
                        xPos -= speed;
                        bombIcon.relocate(xPos, yPos);
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }

                    }
                }
                stop = true;
            }

        }
    }
}
