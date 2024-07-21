package roki.renderer.components;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Camera {
    private Matrix4f projectionMatrix, viewMatrix, inverseprojection, inverseView;
    public Vector2f position;
    private Vector2f projectionSize = new Vector2f( 32.0f * 40.0f, 32.0f * 21.0f);

    public Camera(Vector2f position) {
        this.position = position;
        this.projectionMatrix = new Matrix4f();
        this.viewMatrix = new Matrix4f();
        this.inverseprojection = new Matrix4f();
        this.inverseView = new Matrix4f();
        adjustProjection();
    }

    public void adjustProjection() {
        projectionMatrix.identity();
        projectionMatrix.ortho(0.0f, projectionSize.x, 0.0f, projectionSize.y, 0.0f, 100.0f);
        projectionMatrix.invert(inverseprojection);
    }

    public Matrix4f getViewMatrix() {
        Vector3f cameraFront = new Vector3f(0.0f, 0.0f, -1.0f); // facing the z axis
        Vector3f cameraUp = new Vector3f(0.0f, 1.0f, 0.0f); // up is y axis
        this.viewMatrix.identity();
        // eye z axis, center z axis, up y axis
        viewMatrix.lookAt(new Vector3f(position.x, position.y, 20.0f), cameraFront.add(position.x, position.y, 0.0f), cameraUp);
        this.viewMatrix.invert(inverseView);

        return this.viewMatrix;
    }

    public Matrix4f getProjectionMatrix() {
        return this.projectionMatrix;
    }

    public Matrix4f getInverseprojection() {
        return this.inverseprojection;
    }

    public Matrix4f getInverseView() {
        return this.inverseView;
    }

    public Vector2f getProjectionSize() {
        return projectionSize;
    }
}
