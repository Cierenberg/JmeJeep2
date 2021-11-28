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
import com.jme3.input.controls.ActionListener;
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
import fe.hc.jme.models.BarrelPyramid;
import fe.hc.jme.models.BarrelTower;

public class TestPhysicsCar extends SimpleApplication implements ActionListener {

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

    private void setupKeys() {
        inputManager.addMapping("Lefts", new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addMapping("Rights", new KeyTrigger(KeyInput.KEY_RIGHT));
        inputManager.addMapping("Ups", new KeyTrigger(KeyInput.KEY_UP));
        inputManager.addMapping("Downs", new KeyTrigger(KeyInput.KEY_DOWN));
        inputManager.addMapping("Space", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("Reset", new KeyTrigger(KeyInput.KEY_RETURN));
        inputManager.addMapping("Gear", new KeyTrigger(KeyInput.KEY_RCONTROL));
        
        inputManager.addListener(this, "Lefts");
        inputManager.addListener(this, "Rights");
        inputManager.addListener(this, "Ups");
        inputManager.addListener(this, "Downs");
        inputManager.addListener(this, "Space");
        inputManager.addListener(this, "Reset");
        inputManager.addListener(this, "Gear");
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

    @Override
    public void onAction(String binding, boolean value, float tpf) {
        if (binding.equals("Lefts")) {
            this.jeep.steerLeft(value);
        } else if (binding.equals("Rights")) {
            this.jeep.steerRight(value);
        } else if (binding.equals("Ups")) {
                this.jeep.accelerate(value);
        } else if (binding.equals("Downs")) {
           this.jeep.brake(value);
        } else if (binding.equals("Space")) {
            this.jeep.jump();
        } else if (binding.equals("Reset")) {
            this.jeep.resetRotation();
        } else if (binding.equals("Gear")) {
            this.jeep.shift(value);
        }
    }
}