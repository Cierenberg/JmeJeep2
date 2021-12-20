/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hc.jme.terrain;

import com.jme3.material.Material;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.heightmap.ImageBasedHeightMap;
import com.jme3.texture.Texture;
import de.hc.jme.scene.Jeep2Scene;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import fe.hc.jme.models.Loop;

/**
 *
 * @author hendrik
 */
public class Island {
    private TerrainQuad terrain;
    Material mat_terrain;
    private Jeep2Scene parent;
    private boolean addControl = false;
    private int isle = 0;
    private String[][] isles = 
    {
        {"Textures/Terrain/Heightmap/height_small.png","Textures/Terrain/Heightmap/alpha_0_small.jpg", "Textures/Terrain/Heightmap/alpha_1.jpg"},
        {"Textures/Terrain/Heightmap/two/height_small.png","Textures/Terrain/Heightmap/two/alpha_0_small.jpg", "Textures/Terrain/Heightmap/two/alpha_1.jpg"}        
    };
    private Loop loop;
    
    
    public Island(Jeep2Scene parent, boolean control, int isle) {
        this.parent = parent;
        this.addControl = control;
        this.isle = isle;
        this.init();
        this.loop = new Loop(parent, new Vector3f(-1388, 0, -1280));
    }
    
    
    private void init() {
        mat_terrain = new Material(this.parent.getAssetManager(),
                "Common/MatDefs/Terrain/TerrainLighting.j3md");

        /** 1.1) Add ALPHA map (for red-blue-green coded splat textures) */
        mat_terrain.setTexture("AlphaMap", this.parent.getAssetManager().loadTexture(
                this.isles[this.isle][1]));

        Texture grass = this.parent.getAssetManager().loadTexture(
                "Textures/Terrain/grass.png");
        grass.setWrap(Texture.WrapMode.Repeat);
        Texture water = this.parent.getAssetManager().loadTexture(
                "Textures/Terrain/water.png");
        water.setWrap(Texture.WrapMode.Repeat);
        Texture dirt = this.parent.getAssetManager().loadTexture(
                "Textures/Terrain/stone.png");
        dirt.setWrap(Texture.WrapMode.Repeat);
        Texture rock = this.parent.getAssetManager().loadTexture(
                "Textures/Terrain/street.png");
        rock.setWrap(Texture.WrapMode.Repeat);

        mat_terrain.setTexture("DiffuseMap", grass);
        mat_terrain.setFloat("DiffuseMap_0_scale", 128f);
        mat_terrain.setTexture("DiffuseMap_1", dirt);
        mat_terrain.setFloat("DiffuseMap_1_scale", 64f);
        mat_terrain.setTexture("DiffuseMap_2", rock);
        mat_terrain.setFloat("DiffuseMap_2_scale", 128f);

        mat_terrain.setTexture("AlphaMap_1", this.parent.getAssetManager().loadTexture(
                this.isles[this.isle][2]));
        mat_terrain.setTexture("DiffuseMap_4", water);
        mat_terrain.setFloat("DiffuseMap_4_scale", 128f);
        mat_terrain.setTexture("DiffuseMap_5", rock);
        mat_terrain.setFloat("DiffuseMap_5_scale", 128f);

        /** 2. Create the height map */
        AbstractHeightMap heightmap = null;
        Texture heightMapImage = this.parent.getAssetManager().loadTexture(
        this.isles[this.isle][0]);
        heightmap = new ImageBasedHeightMap(heightMapImage.getImage());
        heightmap.load();
        heightmap.smooth(1f);

        /** 3. We have prepared material and heightmap.
         * Now we create the actual terrain:
         * 3.1) Create a TerrainQuad and name it "my terrain".
         * 3.2) A good value for terrain tiles is 64x64 -- so we supply 64+1=65.
         * 3.3) We prepared a heightmap of size 512x512 -- so we supply 512+1=513.
         * 3.4) As LOD step scale we supply Vector3f(1,1,1).
         * 3.5) We supply the prepared heightmap itself.
         */
        int patchSize = 8;
        terrain = new TerrainQuad("my terrain", patchSize, 257, heightmap.getHeightMap());

        /** 4. We give the terrain its material, position & scale it, and attach it. */
        terrain.setMaterial(mat_terrain);
        terrain.setLocalTranslation(0, -12, 0);
        terrain.setLocalScale(16f, 1.8f, 16f);
        this.parent.getRootNode().attachChild(terrain);

        /** 5. The LOD (level of detail) depends on were the camera is: */
        if (this.addControl) {
            TerrainLodControl control = new TerrainLodControl(terrain, this.parent.getCamera());
            terrain.addControl(control);
        } else {
            terrain.addControl(new RigidBodyControl(0));
        }
        terrain.setShadowMode(RenderQueue.ShadowMode.Receive);
    }
    
    public TerrainQuad getTerrain() {
        return this.terrain;
    }
}

