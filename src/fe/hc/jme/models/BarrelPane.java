/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fe.hc.jme.models;

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import de.hc.jme.scene.Jeep2Scene;
/**
 *
 * @author hendrik
 */
public class BarrelPane {
    private static int paneInstancesCount = 0;
    private Node barrelPane = new Node("BarrelPane_" + (++ BarrelPane.paneInstancesCount));
    private Jeep2Scene parent;
    private Vector3f position;
    

    public BarrelPane(Jeep2Scene parent, Vector3f position, int count, boolean calculateY) {
        this.parent = parent;
        this.position = position;        
        this.init(count, calculateY);
        
    }
    
    private void init(int count, boolean calculateY) {
        float paneWidth = count * Barrel.WIDTH;
        float startZ = this.position.z - (paneWidth/2f);
        for (int i = 0; i < count; ++i) {
            this.barrelPane.attachChild(new BarrelRow(parent, new Vector3f(this.position.x, this.position.y, startZ + i * Barrel.WIDTH), count, calculateY).getNode());
        }
    }
    
    public Node getNode() {
        return this.barrelPane;
    }
    
}
