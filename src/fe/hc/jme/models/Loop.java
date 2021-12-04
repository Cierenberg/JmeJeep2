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
import com.jme3.scene.Spatial;
import de.hc.jme.scene.Jeep2Scene;

/**
 *
 * @author hendrik
 */
public class Loop{
    private Jeep2Scene parent;
    private Spatial loopSpatial;
    public static final float HEIGHT = 25f;
    private RigidBodyControl loopPhysikAlias;
    
    public Loop(Jeep2Scene parent, Vector3f position) {
        this.parent = parent;
        this.init(position);
    }

    public Spatial getSpatial() {
        return this.loopSpatial;
    }
              
    private void init(Vector3f position) {
        Material loopMaterial = new Material(this.parent.getAssetManager(),"Common/MatDefs/Misc/Unshaded.j3md");
        loopMaterial.setTexture("ColorMap", this.parent.getAssetManager().loadTexture("Textures/steel.png"));
        
        Float offY = this.parent.getEnvironmentHeight(position.x, position.z);
        if (offY != null) {
            position.y = offY + Loop.HEIGHT / 2f;
        }     
        this.loopSpatial = this.parent.getAssetManager().loadModel("Models/loop.j3o");
        
        this.loopSpatial.scale(4.5f);
        this.loopSpatial.rotate(0f, (float) Math.toRadians(-20), 0f);
        this.loopSpatial.setLocalTranslation(position);
        loopSpatial.setMaterial(loopMaterial);
        this.loopPhysikAlias = new RigidBodyControl(0f);
        loopSpatial.addControl(this.loopPhysikAlias);
        this.parent.getPhysicsSpace().add(this.loopPhysikAlias);
        this.loopSpatial.setShadowMode(RenderQueue.ShadowMode.Cast);

        this.parent.getRootNode().attachChild(this.loopSpatial);
    }
    
    
    private void updatePosition(Vector3f position) {
        this.loopSpatial.setLocalTranslation(position);
    }
}
