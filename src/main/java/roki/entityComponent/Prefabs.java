package roki.entityComponent;

import org.joml.Vector2f;
import roki.entityComponent.components.Sprite;
import roki.entityComponent.components.SpriteRenderer;

public class Prefabs {
    public static GameObject generateSpriteObject(Sprite sprite, float sizeX, float sizeY) {
        GameObject obj = new GameObject("Sprite_Object_Gen",
                new Transform(new Vector2f(), new Vector2f(sizeX, sizeY)), 0);
        SpriteRenderer renderer = new SpriteRenderer();
        renderer.setSprite(sprite);
        obj.addComponent(renderer);

        return obj;
    }
}
