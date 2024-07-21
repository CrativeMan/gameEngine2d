package roki.scene.scenes;

import imgui.ImGui;
import imgui.ImVec2;
import org.joml.Vector2f;
import roki.entityComponent.GameObject;
import roki.entityComponent.Prefabs;
import roki.entityComponent.Transform;
import roki.entityComponent.components.Sprite;
import roki.entityComponent.components.SpriteRenderer;
import roki.entityComponent.components.SpriteSheet;
import roki.entityComponent.levelEditor.GridLines;
import roki.entityComponent.levelEditor.MouseControls;
import roki.renderer.components.Camera;
import roki.scene.Scene;
import util.AssetPool;

public class LevelEditorScene extends Scene {

    private SpriteRenderer obj1Sprite;
    private SpriteSheet sprites;

    GameObject levelEditor = new GameObject("LevelEditor", new Transform(new Vector2f()), 0);

    public LevelEditorScene() {
    }

    @Override
    public void init() {
        levelEditor.addComponent(new MouseControls());
        levelEditor.addComponent(new GridLines());
        loadResources();
        this.camera = new Camera(new Vector2f(-250, 0));
        sprites = AssetPool.getSpriteSheet("assets/textures/spritesheets/decorationsAndBlocks.png");
        if (levelLoaded) {
            this.activeGameObject = gameObjects.getFirst();
            return;
        }

    }

    private void loadResources() {
        AssetPool.getShader("assets/shaders/default.glsl");
        AssetPool.addSpriteSheet("assets/textures/spritesheets/decorationsAndBlocks.png",
                new SpriteSheet(AssetPool.getTexture("assets/textures/spritesheets/decorationsAndBlocks.png"),
                        16, 16, 81, 0));
        AssetPool.getTexture("assets/textures/blendImage2.png");
    }

    @Override
    public void update(float dt) {
        levelEditor.update(dt);

        for (GameObject go : this.gameObjects) {
            go.update(dt);
        }

        this.renderer.render();
    }

    @Override
    public void imgui() {
        ImGui.begin("Blocks");

        ImVec2 windowPos = new ImVec2();
        ImGui.getWindowPos(windowPos);
        ImVec2 windowSize = new ImVec2();
        ImGui.getWindowSize(windowSize);
        ImVec2 itemSpacing = new ImVec2();
        ImGui.getStyle().getItemSpacing(itemSpacing);

        float windowX2 = windowPos.x + windowSize.x;
        for (int i = 0; i < sprites.size(); i++) {
            Sprite sprite = sprites.getSprite(i);
            float spriteWidth = sprite.getWidth() * 2;
            float spriteHeight = sprite.getHeight() * 2;
            int id = sprite.getTexId();
            Vector2f[] texCoords = sprite.getTexCoords();

            ImGui.pushID(i);
            if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
                GameObject obj = Prefabs.generateSpriteObject(sprite, 32, 32);
                // attach to cursor
                levelEditor.getComponent(MouseControls.class).pickupObject(obj);
            }
            ImGui.popID();

            ImVec2 lastButtonPos = new ImVec2();
            ImGui.getItemRectMax(lastButtonPos);
            float lastButtonX2 = lastButtonPos.x;
            float nextButtonX2 = lastButtonX2 + itemSpacing.x + spriteWidth;
            if (i + 1 < sprites.size() && nextButtonX2 < windowX2) {
                ImGui.sameLine();
            }
        }

        ImGui.end();
    }
}
