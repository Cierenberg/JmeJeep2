package de.hc.jme.gui.controller;


import java.util.ArrayList;
import java.util.List;

import com.jme3.niftygui.NiftyJmeDisplay;
import de.hc.jme.scene.Jeep2Scene;


import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.RadioButton;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;


public class GuiController implements ScreenController {
    
    private Nifty nifty;
    
    public GuiController() {
        super();
        this.init();
    }
    
    private void init() {
        NiftyJmeDisplay niftyDisplay = NiftyJmeDisplay.newNiftyJmeDisplay(Jeep2Scene.getCurrent().getAssetManager(), 
        Jeep2Scene.getCurrent().getInputManager(), 
        Jeep2Scene.getCurrent().getAudioRenderer(), 
        Jeep2Scene.getCurrent().getViewPort());
        this.nifty = niftyDisplay.getNifty();
        
        Jeep2Scene.getCurrent().getViewPort().addProcessor(niftyDisplay);
        this.nifty.fromXml("Interface/screen.xml", "start", this);
        this.nifty.getScreen("start").findNiftyControl("cb2", RadioButton.class).select();
        this.nifty.getScreen("start").findNiftyControl("cb4", RadioButton.class).select();
    }
    
    public void start() {
        this.nifty.gotoScreen("start");
    }
    
    public void startGame(String nextScreen) {
        Screen screen = this.nifty.getScreen("start");
        int island = 0;
        boolean jeep = false;
        if (screen.findNiftyControl("cb1", RadioButton.class).isActivated()) {
            jeep = false;
        }
        if (screen.findNiftyControl("cb2", RadioButton.class).isActivated()) {
            jeep = true;
        }
        if (screen.findNiftyControl("cb3", RadioButton.class).isActivated()) {
            island = 0;
        }
        if (screen.findNiftyControl("cb4", RadioButton.class).isActivated()) {
            island = 1;
        }
        System.out.println("de.hc.jme.gui.controller.GuiController.startGame()");
        System.out.println(island + " / " + jeep);
 
        this.nifty.gotoScreen("hud");
        Jeep2Scene.getCurrent().init(island, jeep);
    }
    
    public void quitGame() {
        System.exit(0);
    }
    
    public void resetGui() {
        this.start();
    }
    
    @Override
    public void bind(Nifty nifty, Screen screen) {
    }

    @Override
    public void onStartScreen() {
    }

    @Override
    public void onEndScreen() {
    }
}

