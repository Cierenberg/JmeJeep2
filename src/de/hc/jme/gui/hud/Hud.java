/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hc.jme.gui.hud;

import com.jme3.audio.AudioData;
import com.jme3.audio.AudioNode;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.scene.Node;
import com.jme3.ui.Picture;
import de.hc.jme.scene.Jeep2Scene;

/**
 *
 * @author hendrik
 */
public class Hud {
    private final static Hud instance = new Hud();
    private Node guiNode;
    private Jeep2Scene parent;
    private BitmapFont arialFont;
    private BitmapText guiText[] = new BitmapText[5];
    private float[] padPosition = {0, 0, 0, 0};
    private long targetTime = Long.MAX_VALUE;
    private AudioNode audioGameOver = null;
    private AudioNode audioCongratulation = null;
    private boolean hide = true; 
    private float[] displayDimension; 
    private final Picture picPad = new Picture("Pad Picture");
    private final Picture picHappy = new Picture("congratulation");
    private final Picture picForward = new Picture("Gear Picture forward");
    private final Picture picBackward = new Picture("Gear Picture backward");
    private Picture pic = new Picture("Gear Picture");
    private final Picture picSad = new Picture("Game over");
    
    private final Picture picTacho = new Picture("Tacho back");
    private Object[] updateable = new Object[50];
    private long lastUpdate = System.currentTimeMillis();
    
    
    private Hud() {
    }
    
    public static Hud getDefault() {
        return Hud.instance;
    }
    
    public void setHide(boolean hide) {
        this.hide = hide;
    }
    
    public void setParent(Jeep2Scene parent) {
        if (this.parent == null) {
            
            this.parent = parent;
            this.displayDimension = new float[]{this.parent.getAppSettings().getWidth(), this.parent.getAppSettings().getHeight()}; 
            this.guiNode = this.parent.getGuiNode();
            this.arialFont = this.parent.getAssetManager().loadFont("Interface/Fonts/digital.fnt");
            this.audioGameOver = new AudioNode(this.parent.getAssetManager(), "Sounds/sadtrombone.wav", AudioData.DataType.Buffer);
            this.audioGameOver.setPositional(false);
            this.audioGameOver.setLooping(false);
            this.audioGameOver.setVolume(2);
            this.parent.getRootNode().attachChild(this.audioGameOver);

            this.audioCongratulation = new AudioNode(this.parent.getAssetManager(), "Sounds/Beifahl.wav", AudioData.DataType.Buffer);
            this.audioCongratulation.setPositional(false);
            this.audioCongratulation.setLooping(false);
            this.audioCongratulation.setVolume(2);
            this.parent.getRootNode().attachChild(this.audioCongratulation);
            
            this.picPad.setImage(this.parent.getAssetManager(), "Textures/keypad.png", true);
            this.picHappy.setImage(this.parent.getAssetManager(), "Textures/congratulation.png", true);
            this.picForward.setImage(this.parent.getAssetManager(), "Textures/forward.png", true);
            this.picBackward.setImage(this.parent.getAssetManager(), "Textures/backward.png", true);
            this.picTacho.setImage(this.parent.getAssetManager(), "Textures/tacho.png", true);
            this.picSad.setImage(this.parent.getAssetManager(), "Textures/gameover.png", true);
            
            this.parent.getRootNode().attachChild(this.audioGameOver);
            this.parent.getRootNode().attachChild(this.audioCongratulation);
        }
    }
    
    public void playGameOverSound() {
        this.audioGameOver.play();
    }
    
    public void playCongratulationSound() {
        this.audioCongratulation.play();
    }
    
    public void startTargetTime() {
        this.targetTime = System.currentTimeMillis() + 1000 * 60 * 7;
//        this.targetTime = System.currentTimeMillis() + 1000 * 10;
    }
    
    public boolean isTagetTimeStarted() {
        return this.targetTime != Long.MAX_VALUE;
    }
    
    public void setText(String text, ColorRGBA color) {
        if (this.guiText[0] != null) {
            this.guiText[0].setColor(color);
            this.guiText[0].setText(text);
        }
    }
    
    public void clean() {
        if (this.guiNode != null) {
            this.guiNode.detachAllChildren();
        }
    }
    
    public void update() {
        if (this.guiNode != null) {
            if (!this.hide) {
                boolean updateHud = false;
                if (this.parent.getAppSettings().getWidth() != this.displayDimension[0]); {
                    this.displayDimension = new float[]{this.parent.getAppSettings().getWidth(), this.parent.getAppSettings().getHeight()};
                    updateHud = true;
                }
                if (this.parent != null && this.parent.getJeep() != null) {
                    if (this.updateable[0] == null) {
                        this.updateable[0] = this.parent.getJeep().isCongratulation();
                        this.updateable[1] = this.parent.getJeep().isGameOver();
                        this.updateable[2] = this.parent.getJeep().isForward();
                        this.updateable[3] = this.parent.getJeep().getSpeed();
                    }
                    
                    if (this.parent.getJeep().isCongratulation() != (boolean) this.updateable[0]) {
                        this.updateable[0] = this.parent.getJeep().isCongratulation();
                        updateHud = true;
                    }
                    if (this.parent.getJeep().isGameOver() != (boolean) this.updateable[1]) {
                        this.updateable[1] = this.parent.getJeep().isGameOver();
                        updateHud = true;
                    }
                    if (this.parent.getJeep().isForward() != (boolean) this.updateable[2]) {
                        this.updateable[2] = this.parent.getJeep().isForward();
                        updateHud = true;
                    }
                    if (this.parent.getJeep().getSpeed() != (int) this.updateable[3]) {
                        this.updateable[3] = this.parent.getJeep().getSpeed();
                        updateHud = true;
                    }
                    if (!updateHud) {
                        if (System.currentTimeMillis() - this.lastUpdate > 1000) {
                            updateHud = true;
                        }
                    }
                }

                if (updateHud) {
                    this.lastUpdate = System.currentTimeMillis();
                    this.guiNode.detachAllChildren();

                    if (!this.parent.shouldBeonDesktop()) {        
                        this.padPosition = new float[] {this.displayDimension[0] - this.displayDimension[0]/3.5f, this.displayDimension[1]/14, this.displayDimension[0]/4, displayDimension[0]/4}; 
                        this.picPad.setWidth(this.padPosition[2]);
                        this.picPad.setHeight(this.padPosition[3]);
                        this.picPad.setPosition(this.padPosition[0], this.padPosition[1]);
                        this.guiNode.attachChild(picPad);
                    }

                    if (this.parent.getJeep() != null) {
                        if (this.parent.getJeep().isCongratulation()) {                             
                            float picWidth = this.displayDimension[0] - 50;
                            this.picHappy.setWidth(picWidth);
                            this.picHappy.setHeight(picWidth / 2);
                            this.picHappy.setPosition(20, (this.displayDimension[1] - (picWidth / 2)) / 2);
                            this.guiNode.attachChild(picHappy);     
                        } else if (!this.parent.getJeep().isGameOver()) {  
                            if (this.parent.getJeep().isForward()) {
                                this.pic = this.picForward;
                            } else {
                                this.pic = this.picBackward;
                            }
                            float width = this.displayDimension[0] / 10;
                            float margin = width / 8;
                            this.pic.setWidth(width);
                            this.pic.setHeight(width / 2);
                            this.pic.setPosition(this.displayDimension[0] - width - margin, this.displayDimension[1] - (width / 2) - margin);
                            this.guiNode.attachChild(this.pic); 
                           Picture picNadel = new Picture("Tacho pin");
                            picNadel.setImage(this.parent.getAssetManager(), "Textures/zeiger.png", true);
                            picNadel.rotate(0, 0,(float) ((190 - (this.parent.getJeep().getSpeed() * 2)) * Math.PI / 180.0));
                            
                            width = displayDimension[0] / 10;
                            margin = width / 8;
                            picNadel.setWidth(width);
                            picNadel.setHeight(2);
                            picNadel.setPosition(displayDimension[0] - width - margin, displayDimension[1] - 2 * width - margin );
                            this.guiNode.attachChild(picNadel); 
                            this.picTacho.setWidth(2 * width);
                            this.picTacho.setHeight(1.5f * width);
                            this.picTacho.setPosition(this.displayDimension[0] - 2 * width - margin, this.displayDimension[1] - 2 * width - 3 * margin);
                            this.guiNode.attachChild(this.picTacho); 
                        } else {
                            
                            
                            float picWwidth = displayDimension[0] - 50;
                            this.picSad.setWidth(picWwidth);
                            this.picSad.setHeight(picWwidth / 2);
                            this.picSad.setPosition(20, (displayDimension[1] - (picWwidth / 2)) / 2);
                            this.guiNode.attachChild(picSad);     
                        }
                    }
                    long moveDuration = this.parent.getJeep().getLastMoveDuration();

                    if (this.isTagetTimeStarted() && !this.parent.getJeep().isCongratulation()) {
                        long milliesLeft = this.targetTime - System.currentTimeMillis();
                        if (milliesLeft < 0 && !this.parent.getJeep().isGameOver()) {
                            this.parent.getJeep().setGameOver();
                        } else {
                            long seconds = milliesLeft / 1000;
                            long minute = seconds / 60;
                            seconds -= minute * 60;
                            String textTime = minute + "Min " + seconds + "Sek";
                            BitmapText textDisplayTime = new BitmapText(this.arialFont, false);

                            textDisplayTime.setSize(this.arialFont.getCharSet().getRenderedSize() * 2); 
                            textDisplayTime.setColor((milliesLeft < 60000)?ColorRGBA.Red:ColorRGBA.Green);
                            textDisplayTime.setText(textTime);
                            textDisplayTime.setLocalTranslation(10f, 50f, 10f);

                            if (!this.parent.getJeep().isGameOver()) {
                                this.guiNode.attachChild(textDisplayTime);    
                           }
                        }
                    }

                    if (moveDuration > 3000 && !this.parent.getJeep().isGameOver()) {
                        Picture pic = new Picture("misc");
                        float width = (Float) (displayDimension[0] / 3);
                        pic = new Picture("Count Down");
                        pic.setWidth(width);
                        pic.setHeight(width * 1.2f);
                        pic.setImage(this.parent.getAssetManager(), "Textures/0.png", true);
                        if (moveDuration < 4000) {
                            pic.setImage(this.parent.getAssetManager(), "Textures/5.png", true);
                        } else if (moveDuration < 5000) {
                            pic.setImage(this.parent.getAssetManager(), "Textures/4.png", true);
                        } else if (moveDuration < 6000) {
                            pic.setImage(this.parent.getAssetManager(), "Textures/3.png", true);
                        } else if (moveDuration < 7000) {
                            pic.setImage(this.parent.getAssetManager(), "Textures/2.png", true);
                        } else if (moveDuration < 8000) {
                            pic.setImage(this.parent.getAssetManager(), "Textures/1.png", true);
                        } else {
                            this.parent.getJeep().setGameOver();
                        } 
                        pic.setPosition(displayDimension[0] / 2 - width / 2, displayDimension[1] / 2 - (width / 2));
                        this.guiNode.attachChild(pic); 
                    }
                }
            }
        }
    }
    
        /**
     * Touch.
     */
    public void touch() {
        if (this.parent != null) {
            if (!this.parent.shouldBeonDesktop()) {
                float touchWidthX = this.padPosition[2] / 3;
                float touchWidthY = touchWidthX;
                Vector2f position = this.parent.getInputManager().getCursorPosition();

                if (position.getX() > this.padPosition[0] && position.getX() < (this.padPosition[0] + this.padPosition[2]) && position.getY() > this.padPosition[1] && position.getY() < (this.padPosition[1] + this.padPosition[3])) {
                        float iX = position.getX() - this.padPosition[0];
                        float iY = position.getY() - this.padPosition[1];
                        boolean steering = false;
                        if (iX < touchWidthX) {
                                this.parent.getJeep().steerLeft();
                                steering = true;
                        }
                        if (iX > this.padPosition[2] - touchWidthX) {
                                this.parent.getJeep().steerRight();
                                steering = true;
                        }
                        if (iY < touchWidthY) {
                                this.parent.getJeep().brake();
                                steering = true;
                        }
                        if (iY > this.padPosition[3] - touchWidthY) {
                                this.parent.getJeep().accelerate();
                                steering = true;
                        }
                        if (!steering) {
                            this.parent.getJeep().horn();
                        }
                    }
            }
        }
    }

}
