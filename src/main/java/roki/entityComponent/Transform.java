package roki.entityComponent;

import org.joml.Vector2f;

public class Transform {
    public Vector2f position = new Vector2f();
    public Vector2f scale = new Vector2f();

    public Transform() {
        init(new Vector2f(), new Vector2f());
    }

    public Transform(Vector2f position) {
        init(position, new Vector2f());
    }

    public Transform(Vector2f position, Vector2f scale) {
        init(position, scale);
    }

    public void init(Vector2f position, Vector2f scale) {
        this.position.set(position);
        this.scale.set(scale);
    }
}