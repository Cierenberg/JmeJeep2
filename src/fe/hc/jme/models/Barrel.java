/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fe.hc.jme.models;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import de.hc.jme.jme.utility.ITarget;

import java.util.ArrayList;
import java.util.List;
import jme3test.bullet.TestPhysicsCar;

/**
 *
 * @author hendrik
 */
public class Barrel implements ITarget{
    private static int count = 0;
    private static final List<RigidBodyControl> rigidBodyControols = new ArrayList<>();
    private Node barrel = new Node("Barrel_" + (++ Barrel.count));
    private Material[] materials;
    private TestPhysicsCar parent;
    private Vector3f position;
    private boolean isMarked = false;
    public static final float WIDTH = 1.5f;
    public static final float HEIGHT = 1.87f;
    private RigidBodyControl barrelPhysikAlias;
    private float treshold;
    
    public Barrel(TestPhysicsCar parent, Vector3f position, boolean isMarked, boolean calculateY) {
        this.parent = parent;
        this.position = position;
        this.isMarked = isMarked;
        if (this.materials == null) {
            this.materials = new Material[] {
                new Material(this.parent.getAssetManager(),"Common/MatDefs/Misc/Unshaded.j3md"),
                new Material(this.parent.getAssetManager(),"Common/MatDefs/Misc/Unshaded.j3md")
            };
            this.materials[0].setTexture("ColorMap", this.parent.getAssetManager().loadTexture("Textures/fass.png"));
            this.materials[1].setTexture("ColorMap", this.parent.getAssetManager().loadTexture("Textures/fass_kreuz.png"));            
        }
        if (calculateY) {
        Float offY = this.parent.getEnvironmentHeight(this.position.x, this.position.z);
            if (offY != null) {
                this.position.y = offY + Barrel.HEIGHT / 2f;
            }
        }
        this.treshold = this.position.y;
        this.init();
    }
    
    private void init() {
        Spatial barrelSpatial = this.parent.getAssetManager().loadModel("Models/fass.j3o");
        if (this.isMarked) {
            barrelSpatial.setMaterial(this.materials[1]);
        } else {
            barrelSpatial.setMaterial(this.materials[0]);
        }
//        this.barrel.setLocalTranslation(this.position);
        barrelSpatial.setLocalTranslation(this.position);
        barrelSpatial.scale(.7f, .7f, .7f);

        this.barrelPhysikAlias = new RigidBodyControl(2f);
        barrelSpatial.addControl(barrelPhysikAlias);
        this.parent.getPhysicsSpace().add(barrelPhysikAlias);
        Barrel.rigidBodyControols.add(barrelPhysikAlias);
        barrelSpatial.setShadowMode(RenderQueue.ShadowMode.Cast);

        this.barrel.attachChild(barrelSpatial);
    }
    
    public Node getNode(TestPhysicsCar parent) {
        return this.barrel;
    }

    @Override
    public RigidBodyControl getTarget() {
        return this.barrelPhysikAlias;
    }
    
    public static void removeAllControls(TestPhysicsCar parent) {
        for (RigidBodyControl control : Barrel.rigidBodyControols) {
            parent.getPhysicsSpace().remove(control);
        }
        Barrel.rigidBodyControols.clear();
    }

    @Override
    public Node getNode() {
        return this.barrel;
    }

    @Override
    public float getTreshold() {
        return treshold;
    }
    
}
