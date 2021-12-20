/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hc.jme.jme.utility;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import de.hc.jme.gui.hud.Hud;
import fe.hc.jme.models.Barrel;
import fe.hc.jme.models.BarrelPyramid;
import fe.hc.jme.models.BarrelTower;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import de.hc.jme.scene.Jeep2Scene;

/**
 *
 * @author hendrik
 */
public class TargetData {
    public static enum TargetType 
    {
       PYRAMID_NORMAL, PYRAMID_INTERLACED, TOWER
    }
    private static Map<Integer, List<TargetData>> map = new HashMap<>();
    private static int[] level = {-1, -1, -1};
    private static Node currentNode = null;
    private static RigidBodyControl currentTarget;
    private static float treshold;
    public static void add(int isle, TargetData targetData) {
        List<TargetData> list = TargetData.map.get(isle);
        if (list == null) {
            list = new ArrayList<>();
            TargetData.map.put(isle, list);
        }
        list.add(targetData);
    }
    
    public static void restart() {
        TargetData.level = new int[] {-1, -1, -1};
        TargetData.currentNode = null;
        TargetData.currentTarget = null;
    }
    
    public static float getCurrentTresHold() {
        return TargetData.treshold;
    }
    
    public static RigidBodyControl initNext(Jeep2Scene parent) {
        TargetData.treshold = Float.MIN_VALUE;
        TargetData.level[parent.getIsle()] ++;
        System.out.println("level :" + TargetData.level[parent.getIsle()]);
        if (TargetData.currentNode != null) {
            parent.getRootNode().detachChild(TargetData.currentNode);
            Barrel.removeAllControls(parent);
            if (!Hud.getDefault().isTagetTimeStarted()) {
                Hud.getDefault().startTargetTime();
            } 
        } 
//        System.out.println(parent.getIsle());
        if (TargetData.map.get(parent.getIsle()) != null && TargetData.map.get(parent.getIsle()).size() > TargetData.level[parent.getIsle()]) {
            TargetData data = TargetData.map.get(parent.getIsle()).get(TargetData.level[parent.getIsle()]);
            ITarget target = null;
            if (data.getType().equals(TargetData.TargetType.PYRAMID_NORMAL)) {
                target = new BarrelPyramid(parent, data.getPosition(), data.getCount(), false); 
            } else if(data.getType().equals(TargetData.TargetType.PYRAMID_INTERLACED)) {
                target = new BarrelPyramid(parent, data.getPosition(), data.getCount(), true);
            } else if(data.getType().equals(TargetData.TargetType.TOWER)) {
                target = new BarrelTower(parent, data.getPosition(), data.getCount());
            } 
            if (target != null) {
                TargetData.currentNode = target.getNode();
                TargetData.currentTarget = target.getTarget();
                TargetData.treshold = target.getTreshold();
                
                parent.getRootNode().attachChild(TargetData.currentNode);
                parent.getJeep().setTarget(TargetData.currentTarget);
                return TargetData.currentTarget;
            } else {
                System.out.println("de.hc.jme.jme.utility.TargetData.initNext()");
                parent.getJeep().setCongratulation();
            }
        }
        return null;
    }
    
    private TargetData.TargetType type = TargetData.TargetType.PYRAMID_NORMAL;
    private int count;
    private Vector3f position;

    public TargetData(TargetData.TargetType type, int count, Vector3f position) {
        this.type = type;
        this.count = count;
        this.position = position;
    }

    public TargetType getType() {
        return this.type;
    }

    public int getCount() {
        return this.count;
    }
    
    public Vector3f getPosition() {
        return this.position;
    }
    
    
    
    
}
