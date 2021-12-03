/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hc.jme.jme.scene.controll;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import de.hc.jme.jme.utility.TargetData;
import jme3test.bullet.TestPhysicsCar;

/**
 *
 * @author hendrik
 */
public class SceneControll {
    private static SceneControll instanz = new SceneControll();
    private RigidBodyControl target;
    private long barrelWait = Long.MIN_VALUE;
    
    
    private SceneControll() {
        TargetData.add(0, new TargetData(TargetData.TargetType.PYRAMID_NORMAL, 3, new Vector3f(-1496, 0, -1404)));
        TargetData.add(0, new TargetData(TargetData.TargetType.PYRAMID_NORMAL, 5, new Vector3f(-578, 0, -275)));
        TargetData.add(0, new TargetData(TargetData.TargetType.TOWER, 7, new Vector3f(441, 0, 1226)));
        TargetData.add(0, new TargetData(TargetData.TargetType.PYRAMID_INTERLACED, 10, new Vector3f(-965, 0, 1930)));
        
        TargetData.add(1, new TargetData(TargetData.TargetType.TOWER, 7, new Vector3f(-1496, 0, -1404)));
        TargetData.add(1, new TargetData(TargetData.TargetType.PYRAMID_NORMAL, 5, new Vector3f(-1497, 0, -1405)));        
//        TargetData.add(1, new TargetData(TargetData.TargetType.PYRAMID_NORMAL, 5, new Vector3f(0, 0, 0)));        
    }
    
    public static SceneControll getDefault() {
        return SceneControll.instanz;
    }
    
    public void startGame(TestPhysicsCar parent) {
        TargetData.restart();
        this.target = TargetData.initNext(parent);
    }
    
    public void checkTarget(TestPhysicsCar parent) {
        if (this.target != null) {
            if(TargetData.getCurrentTresHold() > this.target.getPhysicsLocation().y) {
                if (this.barrelWait ==  Long.MIN_VALUE) {
                    this.barrelWait = System.currentTimeMillis();
                } else if (System.currentTimeMillis() - 5000 > this.barrelWait) {
                    this.target = null;
                    this.barrelWait = Long.MIN_VALUE;
                    this.target = TargetData.initNext(parent);
                }
            }
        } 

    };
}
