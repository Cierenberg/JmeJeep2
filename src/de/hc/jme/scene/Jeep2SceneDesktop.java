/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hc.jme.scene;

import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.system.AppSettings;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

/**
 *
 * @author hendrik
 */
public class Jeep2SceneDesktop extends Jeep2Scene{
    /**
     * Inits the display.
     *
     * @return the app settings
     */
    private static AppSettings initDisplay() {
        GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        DisplayMode[] modes = device.getDisplayModes();
        DisplayMode biggest = modes[modes.length - 1];
        AppSettings settings = new AppSettings(true);
        settings.put("Width", biggest.getWidth());
        settings.put("Height", biggest.getHeight());
        settings.put("Title", "JeepScene");
        for (DisplayMode m : modes) {
            System.out.println(m.getWidth() + "/" + m.getHeight());
        }
        settings.setFrameRate(30);
        return settings;
    }

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        Jeep2Scene.CURRENT = (Jeep2Scene) new Jeep2SceneDesktop();
        Jeep2Scene.CURRENT.setShowSettings(false);
        Jeep2Scene.CURRENT.setSettings(Jeep2SceneDesktop.initDisplay());
        Jeep2Scene.CURRENT.start();
        System.out.println("######");
    }
 
    /* (non-Javadoc)
     * @see de.hc.custom.scene.PoolScene#setUpLight()
     */
    @Override
    protected void setUpLight() {
        AmbientLight ambient = new AmbientLight();
        ambient.setColor(ColorRGBA.White.mult(0.04f));
        this.rootNode.addLight(ambient);

        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.1f, -0.7f, -1.0f));
        this.rootNode.addLight(sun);

        final int SHADOWMAP_SIZE = 512;
        DirectionalLightShadowRenderer dlsr = new DirectionalLightShadowRenderer(assetManager, SHADOWMAP_SIZE, 3);
        dlsr.setLight(sun);
        dlsr.setShadowIntensity(0.7f);
        this.viewPort.addProcessor(dlsr);
    }
    
    /* (non-Javadoc)
     * @see de.hc.custom.scene.PoolScene#shouldBeonDesktop()
     */
    @Override
    public boolean shouldBeonDesktop() {
        return true;
    }   
}
