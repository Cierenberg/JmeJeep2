/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fe.hc.jme.models;

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import jme3test.bullet.TestPhysicsCar;

/**
 *
 * @author hendrik
 */
public class BarrelRow {
    private static int rowInstancesCount = 0;
    private Node barrelRow = new Node("BarrelRow_" + (++ BarrelRow.rowInstancesCount));
    private TestPhysicsCar parent;
    private Vector3f position;
    

    public BarrelRow(TestPhysicsCar parent, Vector3f position, int count, boolean calculateY) {
        this.parent = parent;
        this.position = position;
        if (calculateY) {
            Float offY = this.parent.getEnvironmentHeight(this.position.x, this.position.z);
            if (offY != null) {
                this.position.y = offY + Barrel.HEIGHT / 2f;
            }
        }
        this.init(count);
        
    }
    
    private void init(int count) {
        float rowWidth = count * Barrel.WIDTH;
        float startX = this.position.x - (rowWidth/2f);
        for (int i = 0; i < count; ++i) {
            this.barrelRow.attachChild(new Barrel(parent, new Vector3f(startX + i * Barrel.WIDTH, this.position.y, this.position.z), false, false).getNode());
        }
    }
    
    public Node getNode() {
        return this.barrelRow;
    }
    
}
