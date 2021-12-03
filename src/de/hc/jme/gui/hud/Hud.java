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
import com.jme3.scene.Node;
import com.jme3.ui.Picture;
import jme3test.bullet.TestPhysicsCar;

/**
 *
 * @author hendrik
 */
public class Hud {
    private final static Hud instance = new Hud();
    private Node guiNode;
    private TestPhysicsCar parent;
    private BitmapFont arialFont;
    private BitmapText guiText[] = new BitmapText[5];
    private float[] padPosition = {0, 0, 0, 0};
    private long targetTime = Long.MAX_VALUE;
    private AudioNode audioGameOver = null;
    private AudioNode audioCongratulation = null;
    
    private Hud() {
    }
    
    public static Hud getDefault() {
        return Hud.instance;
    }
    
    public void setParent(TestPhysicsCar parent) {
        if (this.parent == null) {
            this.parent = parent;
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
        } else {
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
    
    public void update() {
        if (this.guiNode != null) {
                              
            float[] displayDimension = {this.parent.getAppSettings().getWidth(), this.parent.getAppSettings().getHeight()}; 
            this.guiNode.detachAllChildren();
            
            if (this.parent.getJeep().isCongratulation()) {  
                Picture picHappy = new Picture("congratulation");
                picHappy.setImage(this.parent.getAssetManager(), "Textures/congratulation.png", true);
                float picWidth = displayDimension[0] - 50;
                picHappy.setWidth(picWidth);
                picHappy.setHeight(picWidth / 2);
                picHappy.setPosition(20, (displayDimension[1] - (picWidth / 2)) / 2);
                this.guiNode.attachChild(picHappy);     
            } else if (!this.parent.getJeep().isGameOver()) {  
                Picture pic = new Picture("Gear Picture");
                if (this.parent.getJeep().isForward()) {
                    pic.setImage(this.parent.getAssetManager(), "Textures/forward.png", true);
                } else {
                    pic.setImage(this.parent.getAssetManager(), "Textures/backward.png", true);
                }
                float width = displayDimension[0] / 10;
                float margin = width / 8;
                pic.setWidth(width);
                pic.setHeight(width / 2);
                pic.setPosition(displayDimension[0] - width - margin, displayDimension[1] - (width / 2) - margin);
                this.guiNode.attachChild(pic); 




                Picture picNadel = new Picture("Tacho pin");
                picNadel.setImage(this.parent.getAssetManager(), "Textures/zeiger.png", true);
                picNadel.rotate(0, 0,(float) ((190 - this.parent.getJeep().getSpeed()) * Math.PI / 180.0));
                width = displayDimension[0] / 10;
                margin = width / 8;
                picNadel.setWidth(width);
                picNadel.setHeight(2);
                picNadel.setPosition(displayDimension[0] - width - margin, displayDimension[1] - 2 * width - margin );
                this.guiNode.attachChild(picNadel); 

                Picture picTacho = new Picture("Tacho back");
                picTacho.setImage(this.parent.getAssetManager(), "Textures/tacho.png", true);
                picTacho.setWidth(2 * width);
                picTacho.setHeight(1.5f * width);
                picTacho.setPosition(displayDimension[0] - 2 * width - margin, displayDimension[1] - 2 * width - 3 * margin);
                this.guiNode.attachChild(picTacho); 
            } else {
                Picture picSad = new Picture("Game over");
                picSad.setImage(this.parent.getAssetManager(), "Textures/gameover.png", true);
                float picWwidth = displayDimension[0] - 50;
                picSad.setWidth(picWwidth);
                picSad.setHeight(picWwidth / 2);
                picSad.setPosition(20, (displayDimension[1] - (picWwidth / 2)) / 2);
                this.guiNode.attachChild(picSad);     
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
