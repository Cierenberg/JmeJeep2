
package de.hc.jeep2;

import com.jme3.system.AppSettings;
import de.hc.jme.scene.Jeep2Scene;

public class Main extends Jeep2Scene {
    public static void main(String[] args) {
        Jeep2Scene app = (Jeep2Scene) new Main();
        app.setShowSettings(false);
        AppSettings settings = new AppSettings(true);
        settings.setFrameRate(30);
        app.setSettings(settings);
        app.setDisplayStatView(false);
        app.setDisplayFps(false);
        app.start();
    }
}