package roki.scene.scenes;

import org.joml.Vector2f;
import org.joml.Vector4f;
import roki.entityComponent.GameObject;
import roki.entityComponent.Transform;
import roki.entityComponent.components.Sprite;
import roki.entityComponent.components.SpriteRenderer;
import roki.entityComponent.components.SpriteSheet;
import roki.renderer.Camera;
import roki.scene.Scene;
import util.AssetPool;

public class LevelEditorScene extends Scene {

    private SpriteRenderer obj1Sprite;

    public LevelEditorScene() {
    }

    @Override
    public void init() {
        loadResources();
        this.camera = new Camera(new Vector2f(-250, 0));
        if (levelLoaded) {
            return;
        }

        SpriteSheet sprites = AssetPool.getSpriteSheet("assets/textures/spritesheet.png");

        GameObject obj1 = new GameObject("Object 1", new Transform(new Vector2f(200, 100), new Vector2f(256, 256)), 2);
        obj1Sprite = new SpriteRenderer();
        obj1Sprite.setColor(new Vector4f(1, 0, 0, 1));
        obj1.addComponent(obj1Sprite);
        this.addGameObjectToScene(obj1);
        this.activeGameObject = obj1;

        GameObject obj2 = new GameObject("Object 2", new Transform(new Vector2f(400, 100), new Vector2f(256, 256)), 2);
        SpriteRenderer obj2SpriteRenderer = new SpriteRenderer();
        Sprite obj2Sprite = new Sprite();
        obj2Sprite.setTexture(AssetPool.getTexture("assets/textures/blendImage2.png"));
        obj2SpriteRenderer.setSprite(obj2Sprite);
        obj2.addComponent(obj2SpriteRenderer);
        this.addGameObjectToScene(obj2);



    }

    private void loadResources() {
        AssetPool.getShader("assets/shaders/default.glsl");
        AssetPool.addSpriteSheet("assets/textures/spritesheet.png",
                new SpriteSheet(AssetPool.getTexture("assets/textures/spritesheet.png"),
                        16, 16, 26, 0));
    }


    @Override
    public void update(float dt) {

        for (GameObject go : this.gameObjects) {
            go.update(dt);
        }

        this.renderer.render();
    }

    @Override
    public void imgui() {

    }
}
