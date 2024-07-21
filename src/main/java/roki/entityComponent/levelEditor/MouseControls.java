package roki.entityComponent.levelEditor;

import roki.Window;
import roki.entityComponent.Component;
import roki.entityComponent.GameObject;
import roki.listeners.MouseListener;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static util.Settings.GRID_HEIGHT;
import static util.Settings.GRID_WIDTH;

public class MouseControls extends Component {
    GameObject holdingObject = null;

    public void pickupObject(GameObject go) {
        this.holdingObject = go;
        Window.getScene().addGameObjectToScene(go);
    }

    public void place() {
        this.holdingObject = null;
    }

    @Override
    public void update(float dt) {
        if (holdingObject != null) {
            holdingObject.transform.position.x = MouseListener.getOrthoX() - 16;
            holdingObject.transform.position.y = MouseListener.getOrthoY() - 16;
            holdingObject.transform.position.x = (int)(holdingObject.transform.position.x / GRID_WIDTH) * GRID_WIDTH;
            holdingObject.transform.position.y = (int)(holdingObject.transform.position.y / GRID_HEIGHT) * GRID_HEIGHT;

            if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
                place();
            }
        }
    }
}
