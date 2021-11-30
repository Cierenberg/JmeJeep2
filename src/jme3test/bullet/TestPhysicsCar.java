/*
 * Copyright (c) 2009-2021 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package jme3test.bullet;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.system.AppSettings;
import com.jme3.util.SkyFactory;
import de.hc.jme.gui.hud.Hud;
import de.hc.jme.jme.models.vehicle.Jeep2;
import de.hc.jme.jme.scene.controll.SceneControll;
import de.hc.jme.jme.utility.Utility;
import de.hc.jme.terrain.Island;

public class TestPhysicsCar extends SimpleApplication {

    private BulletAppState bulletAppState;
    private Jeep2 jeep; 
    private Island island;
    private int islandIndex = 1; 
    
    public static void main(String[] args) {
        TestPhysicsCar app = new TestPhysicsCar();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        this.setDisplayFps(true);
        AppSettings newSettings = new AppSettings(true);
        newSettings.setFrameRate(30);
        setSettings(newSettings);
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        bulletAppState.setDebugEnabled(false);
        this.island = new Island(this, false, this.islandIndex);
        this.getRootNode().attachChild(SkyFactory.createSky(getAssetManager(), "Textures/Sky/Bright/BrightSky.dds", SkyFactory.EnvMapType.CubeMap));
        this.setUpLight();
        RigidBodyControl islandRigidBodyControl = new RigidBodyControl(0.0f);
        this.island.getTerrain().addControl(islandRigidBodyControl);
        getPhysicsSpace().add(islandRigidBodyControl);
        setupKeys();
        this.jeep = new Jeep2(this, true, new Vector3f(-1580, 0, -1485), 45);
//        this.jeep = new Jeep2(this, true, new Vector3f(-1200, 0, -1000), 45);
//        this.jeep = new Jeep2(this, false, new Vector3f(0, 0, 0), 45);
        
        this.rootNode.attachChild(this.jeep.getVehicleNode());
        getPhysicsSpace().add(this.jeep.getVehicleControl());
        this.jeep.setCam(cam);
//        this.rootNode.attachChild(new Barrel(this, new Vector3f(-1496, 0, -1404), false).getNode());
        flyCam.setEnabled(false);
        
        SceneControll.getDefault().startGame(this);
        Hud.getDefault().setParent(this);
        this.settings.setFrameRate(30);
//        BarrelTower barrelTower = new BarrelTower(this, new Vector3f(-1496, 0, -1404), 10);
//        this.rootNode.attachChild(barrelTower.getNode());
//        this.jeep.setTarget(barrelTower.getTarget());

//        CameraNode camNode = new CameraNode("CamNode", cam);
//        camNode.setLocalTranslation(jeep.getVehicleNode().getLocalTranslation());
//        camNode.lookAt(jeep.getVehicleNode().getLocalTranslation().negate(), Vector3f.UNIT_Y);
    }
    
    public int getIsle() {
        return this.islandIndex;
    }
    
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
    @Override
    public AssetManager getAssetManager() {
        return this.assetManager;
    }
    
    public PhysicsSpace getPhysicsSpace(){
        return bulletAppState.getPhysicsSpace();
    }

    private final AnalogListener analogListener = new AnalogListener() {
        public void onAnalog(String binding, float value, float tpf) {
            if (binding.equals("Lefts")) {
                jeep.steerLeft();
            } 
            if (binding.equals("Rights")) {
                jeep.steerRight(); 
            }
            if (binding.equals("Ups")) {
                jeep.accelerate();
            }
            if (binding.equals("Downs")) {
               jeep.brake();
            }
            if (binding.equals("Space")) {
               jeep.jump();
            }
            if (binding.equals("Reset")) {
               jeep.turbo();
            }
            if (binding.equals("Gear")) {
               jeep.shift();
            }
        }
    };

    
    private void setupKeys() {
        this.inputManager.addMapping("Lefts", new KeyTrigger(KeyInput.KEY_LEFT));
        this.inputManager.addMapping("Rights", new KeyTrigger(KeyInput.KEY_RIGHT));
        this.inputManager.addMapping("Ups", new KeyTrigger(KeyInput.KEY_UP));
        this.inputManager.addMapping("Downs", new KeyTrigger(KeyInput.KEY_DOWN));
        this.inputManager.addMapping("Space", new KeyTrigger(KeyInput.KEY_SPACE));
        this.inputManager.addMapping("Reset", new KeyTrigger(KeyInput.KEY_RETURN));
        this.inputManager.addMapping("Gear", new KeyTrigger(KeyInput.KEY_RCONTROL));
        
        this.inputManager.addListener(this.analogListener, "Lefts");
        this.inputManager.addListener(this.analogListener, "Rights");
        this.inputManager.addListener(this.analogListener, "Ups");
        this.inputManager.addListener(this.analogListener, "Downs");
        this.inputManager.addListener(this.analogListener, "Space");
        this.inputManager.addListener(this.analogListener, "Reset");
        this.inputManager.addListener(this.analogListener, "Gear");
    }

    public Geometry getGeometry(Spatial spatial){
        return Utility.getGeometryFromSpatial(spatial);
    }

    public Jeep2 getJeep(){
        return this.jeep;
    }
    
    public AppSettings getAppSettings(){
        return this.settings;
    }
    
    
    public Float getEnvironmentHeight(Float x, Float z) {
        Vector3f location = new Vector3f(x, 1500, z);
        Vector3f direction = new Vector3f(0, -1, 0);
        Vector3f highestCut = Utility.firstRayCut(location, direction, this.rootNode);
        if (highestCut != null) {
            return highestCut.y;
        }
        return null;
    }
    

    @Override
    public void simpleUpdate(float tpf) {
        this.jeep.updateCam();
        Hud.getDefault().update();
    }
}