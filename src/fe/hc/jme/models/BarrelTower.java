/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fe.hc.jme.models;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import de.hc.jme.jme.utility.ITarget;
import de.hc.jme.scene.Jeep2Scene;

/**
 *
 * @author hendrik
 */
public class BarrelTower implements ITarget{
    private static int towerInstancesCount = 0;
    private Node barrelTower = new Node("BarrelTower_" + (++ BarrelTower.towerInstancesCount));
    private Jeep2Scene parent;
    private Vector3f position;
    private RigidBodyControl barrelPhysikAlias;
    private float treshold;

    public BarrelTower(Jeep2Scene parent, Vector3f position, int count) {
        this.parent = parent;
        this.position = position;
        Float offY = this.parent.getEnvironmentHeight(this.position.x, this.position.z);
        if (offY != null) {
            this.position.y = offY + Barrel.HEIGHT / 2f;
        }
        this.treshold = this.position.y + Barrel.HEIGHT;
        this.init(count);
        
    }
    
    private void init(int count) {
        for (int i = count; i > 1; --i) {
            this.barrelTower.attachChild(new Barrel(parent, new Vector3f(this.position.x, this.position.y, this.position.z), false,false).getNode());
            this.position.y += Barrel.HEIGHT;            
        }
        Barrel target = new Barrel(parent, new Vector3f(this.position.x, this.position.y, this.position.z), true, false);
        this.barrelTower.attachChild(target.getNode());
        this.barrelPhysikAlias = target.getTarget();
    }
    
    public Node getNode() {
        return this.barrelTower;
    }
    
    @Override
    public RigidBodyControl getTarget() {
        return this.barrelPhysikAlias;
    }
    
    @Override
    public float getTreshold() {
        return treshold;
    }
}
