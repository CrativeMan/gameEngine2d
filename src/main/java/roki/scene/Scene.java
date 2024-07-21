package roki.scene;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import imgui.ImGui;
import roki.entityComponent.Component;
import roki.entityComponent.GameObject;
import roki.renderer.Camera;
import roki.renderer.Renderer;
import serilization.ComponentTypeAdapter;
import serilization.GameObjectDeserializer;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public abstract class Scene {

    protected Renderer renderer = new Renderer();
    protected Camera camera;
    private boolean isRunning = false;
    protected List<GameObject> gameObjects = new ArrayList<>();
    protected GameObject activeGameObject = null;
    protected boolean levelLoaded = false;

    public Scene() {
    }

    public void init() {

    }

    public void start() {
        // when scene is started, start all game objects
        // and add them to the renderer to render
        for (GameObject go : gameObjects) {
            go.start();
            this.renderer.add(go);
        }
        isRunning = true;
    }

    public void addGameObjectToScene(GameObject go) {
        if (!isRunning) {
            gameObjects.add(go); // if not running, just add
        } else {
            gameObjects.add(go); // if running, add and start, add to renderer
            go.start();
            this.renderer.add(go);
        }
    }

    public abstract void update(float dt);

    public Camera camera() {
        return this.camera;
    }

    public void sceneImgui() {
        if (activeGameObject != null) {
            ImGui.begin("Inspector");
            activeGameObject.imgui();
            ImGui.end();
        }

        imgui();
    }

    public void imgui() {

    }

    public void saveExit() {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Component.class, new ComponentTypeAdapter())
                .registerTypeAdapter(GameObject.class, new GameObjectDeserializer())
                .create();
        try {
            FileWriter writer = new FileWriter("level.txt");
            writer.write(gson.toJson(this.gameObjects));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Component.class, new ComponentTypeAdapter())
                .registerTypeAdapter(GameObject.class, new GameObjectDeserializer())
                .create();
        String inFile = "";

        try {
            inFile = new String(Files.readAllBytes(Paths.get("level.txt")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!inFile.isEmpty()) {
            GameObject[] objs = gson.fromJson(inFile, GameObject[].class);
            for (GameObject obj : objs) {
                addGameObjectToScene(obj);
            }
            this.levelLoaded = true;
        }
    }
}
