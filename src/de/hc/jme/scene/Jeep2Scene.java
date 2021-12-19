package de.hc.jme.scene;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.input.MouseInput;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.system.AppSettings;
import com.jme3.util.SkyFactory;
import de.hc.jme.gui.controller.GuiController;
import de.hc.jme.gui.hud.Hud;
import de.hc.jme.jme.models.vehicle.Jeep2;
import de.hc.jme.jme.scene.controll.SceneControll;
import de.hc.jme.jme.utility.Utility;
import de.hc.jme.terrain.Island;

public class Jeep2Scene extends SimpleApplication {
    private BulletAppState bulletAppState;
    private Jeep2 jeep; 
    private Island island;
    private int islandIndex = 1;
    private boolean firstInit = true;
    protected static Jeep2Scene CURRENT;
    
    
    public static Jeep2Scene getCurrent() {
        return Jeep2Scene.CURRENT;
    }
    
    private GuiController guiController;    
    
    public GuiController getGuiController() {
        return this.guiController;
    }
    
    public void reInit() {
        Hud.getDefault().setHide(true);
        Hud.getDefault().clean();
        this.simpleInitApp();
    }
    
    public void init(int islandIndex, boolean jeep) {
        Hud.getDefault().setHide(false);
        this.islandIndex = islandIndex;
        if (!this.firstInit) {
            this.rootNode.detachAllChildren();
            this.bulletAppState.cleanup();
            this.bulletAppState.getPhysicsSpace().destroy();
            this.bulletAppState.getPhysicsSpace().create();
            this.island = null;
            this.jeep = null;
            System.gc();
        } else {
            Jeep2Scene.CURRENT = this;
            this.setUpLight();
            this.firstInit = false;
        }

        this.island = new Island(this, false, this.islandIndex);
        this.getRootNode().attachChild(SkyFactory.createSky(getAssetManager(), "Textures/Sky/Bright/BrightSky.dds", SkyFactory.EnvMapType.CubeMap));        
        RigidBodyControl islandRigidBodyControl = new RigidBodyControl(0.0f);
        this.island.getTerrain().addControl(islandRigidBodyControl);
        this.getPhysicsSpace().add(islandRigidBodyControl);
//        this.jeep = new Jeep2(this, jeep, new Vector3f(-1580, 0, -1485), 45);
//        this.jeep = new Jeep2(this, true, new Vector3f(-1200, 0, -1000), 45);
//        this.jeep = new Jeep2(this, jeep, new Vector3f(0, 0, 0), 45);
        this.jeep = new Jeep2(this, jeep, new Vector3f(-1580, 0, -1485), 45);
        
        this.rootNode.attachChild(this.jeep.getVehicleNode());
        this.getPhysicsSpace().add(this.jeep.getVehicleControl());
        this.jeep.setCam(cam);
        
        
        SceneControll.getDefault().startGame(this);
    }
    @Override
    public void simpleInitApp() {
        if (this.guiController == null) {
            Jeep2Scene.CURRENT = this;
            this.setupKeys();
            Hud.getDefault().setParent(this);
            this.flyCam.setEnabled(false);
            this.bulletAppState = new BulletAppState();
            this.stateManager.attach(bulletAppState);
            this.bulletAppState.setDebugEnabled(false);        
            this.guiController = new GuiController();
        } else {
            this.guiController.start();
        }
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
               jeep.horn();
            }
            if (binding.equals("Reset")) {
               jeep.turbo();
            }
            if (binding.equals("Gear")) {
               jeep.shift();
            }
            if (binding.equals("Touch")) {
               Hud.getDefault().touch();
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
        this.inputManager.addMapping("Touch", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));

        this.inputManager.addListener(this.analogListener, "Lefts");
        this.inputManager.addListener(this.analogListener, "Rights");
        this.inputManager.addListener(this.analogListener, "Ups");
        this.inputManager.addListener(this.analogListener, "Downs");
        this.inputManager.addListener(this.analogListener, "Space");
        this.inputManager.addListener(this.analogListener, "Reset");
        this.inputManager.addListener(this.analogListener, "Gear");
        this.inputManager.addListener(this.analogListener, "Touch");
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
        if (this.jeep != null) {
            this.jeep.update();
            Hud.getDefault().update();
        } else {
            Hud.getDefault().clean();;
        }
    }
    
    public boolean shouldBeonDesktop() {
        return false;
    }
}