package entity;

import client.GameBoardPane;
import javafx.scene.image.*;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;

public class Tank {
    private ImageView tankIcon;
    private Image[] tankImages;
    private Image[] enemyTankImages;
    private final String IMAGES_DIR = "C:\\Users\\ACER\\Documents\\Projects\\Java projects\\Tanks-2D-Game\\GameClient\\Images\\";
    private final int IMG_SIZE = 43;
    private boolean isEnemy;

    private Bomb[] bombs = new Bomb[1000];
    private int curBomb = 0;
    private int tankID = -1;
    private int posX = -1, posY = -1;
    private int direction = 2;
    private int speed = 10;
    private int width = 497, height = 500;


    public Tank() throws MalformedURLException {
        while(posX < 0 | posY < 0 | posY > height - 70 | posX > width - 30) {
            posX = (int)(Math.random()*width);
            posY = (int)(Math.random()*height);
        }
        loadImages();
        tankIcon.relocate(posX, posY);
        posX = (int)tankIcon.getLayoutX();
        posY = (int)tankIcon.getLayoutY();

    }
    public Tank (int x, int y, int dir, int id) throws MalformedURLException {
        posX = x;
        posY = y;
        tankID = id;
        direction = dir;
        loadEnemyImages();
        tankIcon.relocate(posX, posY);
        isEnemy = true;
    }

    public void loadImages() throws MalformedURLException {
        tankImages = new Image[4];

        for (int i = 4; i < tankImages.length + 4; i++) {
            tankImages[i - 4] = new Image(new File(IMAGES_DIR + i + ".PNG").toURI().toURL().toExternalForm());
        }

        tankIcon = new ImageView(tankImages[1]);
    }

    public void loadEnemyImages() throws MalformedURLException {
        enemyTankImages = new Image[4];

        for (int i = 0; i < enemyTankImages.length; i++) {
            enemyTankImages[i] = new Image(new File(IMAGES_DIR + i + ".PNG").toURI().toURL().toExternalForm());
        }

        tankIcon = new ImageView(enemyTankImages[1]);
    }

    public int getXPosition() {
        return posX;
    }
    public int getYPosition() {
        return posY;
    }
    public void setXPosition(int x) {
        posX = x;
    }
    public void setYPosition(int y) {
        posY = y;
    }
    public void updatePosition(){
        tankIcon.relocate(posX, posY);
    }

    public ImageView getTankIcon() {
        return tankIcon;
    }


    public void moveLeft() {
        if(direction == 1 | direction == 3) {
            setDirection(4);
        }
        else if (direction == 4) {

            int temp = posX - speed;
            if (!checkCollision(temp, posY) && temp < 0) {
                posX = 0;
            }
            else if (!checkCollision(temp, posY)) {
                posX = temp;
            }
        }
    }

    public void moveRight() {
        if(direction == 1 | direction == 3) {
            setDirection(2);
        }
        else if (direction == 2) {
            int temp = posX + speed;
            if(!checkCollision(temp, posY) && temp > width - 30) {
                posX = width - 30;
            }
            else if(!checkCollision(temp, posY)) {
                posX=temp;
            }
        }
    }

    public void moveForward() {
        if(direction == 2 | direction == 4) {
            setDirection(1);
        }
        else if (direction == 1) {
            int temp = posY - speed;
            if(!checkCollision(posX, temp) && temp < 0) {
                posY = 0;
            }
            else if(!checkCollision(posX, temp)) {
                posY = temp;
            }
        }
    }

    public void moveBackward() {
        if(direction == 2 | direction == 4) {
            setDirection(3);
        }
        else if (direction == 3) {
            int temp = posY + speed;
            if(!checkCollision(posX, temp) && temp > height - 70) {
                posY = height - 70;
            }
            else if(!checkCollision(posX, temp)) {
                posY = temp;
            }
        }
    }

    public void shot() throws MalformedURLException {
        bombs[curBomb]= new Bomb(this.getXPosition() + IMG_SIZE/2, this.getYPosition() + IMG_SIZE/2, direction);
        curBomb++;
    }

    public Bomb getCurBomb() {return bombs[curBomb - 1];}
    public void setTankID(int id) {
        tankID = id;
    }
    public int getTankID() {
        return tankID;
    }
    public void setDirection(int dir) {
        if (dir < 0 || dir > 4) {
            return;
        }

        if (isEnemy) {
            tankIcon.setImage(enemyTankImages[dir - 1]);
        } else {
            tankIcon.setImage(tankImages[dir - 1]);
        }
        direction = dir;
    }
    public int getDirection() {
        return direction;
    }


    public boolean checkCollision(int xP, int yP) {

        ArrayList<Tank> clientTanks = GameBoardPane.getClients();
        int x, y;
        for(int i = 1; i < clientTanks.size(); i++) {
            if(clientTanks.get(i) != null) {
                x = clientTanks.get(i).getXPosition();
                y = clientTanks.get(i).getYPosition();
                if(direction == 1) {
                    if (((yP <= y + IMG_SIZE) && yP >= y) && ((xP <= x + IMG_SIZE&&xP >= x)||(xP + IMG_SIZE >= x&&xP + IMG_SIZE <= x + IMG_SIZE))) {
                        return true;
                    }
                }
                else if(direction == 2) {
                    if (((xP + IMG_SIZE >= x)&&(xP + IMG_SIZE <= x + IMG_SIZE))&&((yP <= y + IMG_SIZE&&yP >= y)||(yP + IMG_SIZE >=y&&yP + IMG_SIZE <= y + IMG_SIZE))) {
                        return true;
                    }
                }
                else if(direction == 3) {
                    if (((yP + IMG_SIZE >= y)&&(yP + IMG_SIZE <= y + IMG_SIZE))&&((xP <= x + IMG_SIZE&&xP >= x)||(xP + IMG_SIZE >=x&&xP + IMG_SIZE <= x + IMG_SIZE))) {
                        return true;
                    }
                }
                else if(direction == 4) {
                    if (((xP <= x + IMG_SIZE)&&xP >= x)&&((yP <= y + IMG_SIZE&&yP >= y)||(yP + IMG_SIZE >=y&&yP + IMG_SIZE <= y + IMG_SIZE))) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

}
