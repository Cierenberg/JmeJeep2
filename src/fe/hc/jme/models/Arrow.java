/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fe.hc.jme.models;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import de.hc.jme.scene.Jeep2Scene;

/**
 *
 * @author hendrik
 */
public class Arrow {
    private Jeep2Scene parent;
    private Spatial arrowSpatial;

    public Arrow(Jeep2Scene parent, Vector3f position) {
        this.parent = parent;
        this.init(position);
    }

    public Spatial getSpatial() {
        return this.arrowSpatial;
    }
              
    private void init(Vector3f position) {
        this.arrowSpatial = this.parent.getAssetManager().loadModel("Models/arrow.j3o");
        this.arrowSpatial.scale(.5f);
        //this.arrowSpatial.rotate((float) Math.toRadians(90), 0f, 0f);
        this.updatePosition(position);
    }
    
    public void updatePosition(Vector3f position) {
        this.arrowSpatial.setLocalTranslation(position);
    }
    
    public void lookAt(RigidBodyControl target) {
        this.arrowSpatial.lookAt(target.getPhysicsLocation(), Vector3f.UNIT_Y);
        this.arrowSpatial.rotate((float) Math.toRadians(90), 0f, 0f);
    }
}
