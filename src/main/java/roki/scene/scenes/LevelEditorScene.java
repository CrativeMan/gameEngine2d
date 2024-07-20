package roki.scene.scenes;

import org.joml.Vector2f;
import roki.entityComponent.GameObject;
import roki.entityComponent.Transform;
import roki.entityComponent.components.SpriteRenderer;
import roki.listeners.KeyListener;
import roki.renderer.Camera;
import roki.scene.Scene;
import util.AssetPool;

import static org.lwjgl.glfw.GLFW.*;

public class LevelEditorScene extends Scene {

    public LevelEditorScene() {
    }

    @Override
    public void init() {
        this.camera = new Camera(new Vector2f(-250, 0));

        int asd = 10;
        for (int x = 0; x < 100; x++) {
            for (int y = 0; y < 100; y++) {
                GameObject go = new GameObject("Object "+x+" "+y, new Transform(new Vector2f(x * asd, y * asd), new Vector2f(asd, asd)));
                go.addComponent(new SpriteRenderer(AssetPool.getTexture("assets/textures/testImage.png")));
                this.renderer.add(go);
            }
        }


        loadResources();
    }

    private void loadResources() {
        AssetPool.getShader("assets/shaders/default.glsl");
    }

    @Override
    public void update(float dt) {
        System.out.println("FPS: " + 1/dt);

        if (KeyListener.isKeyPressed(GLFW_KEY_RIGHT)) {
            camera.position.x += 100f * dt;
        } else if (KeyListener.isKeyPressed(GLFW_KEY_LEFT)) {
            camera.position.x -= 100f * dt;
        }
        if (KeyListener.isKeyPressed(GLFW_KEY_UP)) {
            camera.position.y += 100f * dt;
        } else if (KeyListener.isKeyPressed(GLFW_KEY_DOWN)) {
            camera.position.y -= 100f * dt;
        }

        for(GameObject go : this.gameObjects) {
            go.update(dt);
        }

        this.renderer.render();
    }
}
