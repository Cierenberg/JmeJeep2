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
import jme3test.bullet.TestPhysicsCar;

/**
 *
 * @author hendrik
 */
public class BarrelPyramid implements ITarget{
    private static int pyramidInstancesCount = 0;
    private Node barrelPyramid = new Node("BarrelPyramid_" + (++ BarrelPyramid.pyramidInstancesCount));
    private TestPhysicsCar parent;
    private Vector3f position;
    private RigidBodyControl barrelPhysikAlias;
    private float treshold;

    public BarrelPyramid(TestPhysicsCar parent, Vector3f position, int count, boolean interlaced) {
        this.parent = parent;
        this.position = position;
        Float offY = this.parent.getEnvironmentHeight(this.position.x, this.position.z);
        if (offY != null) {
            this.position.y = offY + Barrel.HEIGHT / 2f;
        }
        this.init(count, interlaced);
        this.treshold = this.position.y;
        
    }
    
    private void init(int count, boolean interlaced) {
        for (int i = count; i > 1; --i) {
            if (interlaced && i % 2 == 0 || !interlaced) {
                this.barrelPyramid.attachChild(new BarrelPane(parent, new Vector3f(this.position.x, this.position.y, this.position.z), i, false).getNode());
//                System.out.print(this.position.y + " / ");
                this.position.y += Barrel.HEIGHT;
            }
        }
        Barrel target = new Barrel(parent, new Vector3f(this.position.x, this.position.y, this.position.z), true, false);
        this.barrelPyramid.attachChild(target.getNode());
        this.barrelPhysikAlias = target.getTarget();
    }
    
    public Node getNode() {
        return this.barrelPyramid;
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
