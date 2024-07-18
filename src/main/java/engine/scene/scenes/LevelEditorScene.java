package engine.scene.scenes;

import engine.Window;
import engine.input.KeyListener;
import engine.scene.Scene;

import java.awt.event.KeyEvent;

public class LevelEditorScene extends Scene {
    private boolean changingScene = false;
    private float timeToChangeScene = 2.0f;

    public LevelEditorScene() {
        System.out.println("Level Editor Scene");
    }

    @Override
    public void update(float dt) {
        if (!changingScene && KeyListener.isKeyPressed(KeyEvent.VK_SPACE)) {
            changingScene = true;
        }

        if (changingScene && timeToChangeScene > 0) {
            timeToChangeScene -= dt;
            Window.get().r -= dt * 0.5f;
            Window.get().g -= dt * 0.5f;
            Window.get().b -= dt * 0.5f;
        } else if (changingScene) {
            Window.changeScene(1);
        }
    }
}
