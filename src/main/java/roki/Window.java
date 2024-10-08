package roki;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import roki.listeners.KeyListener;
import roki.listeners.MouseListener;
import roki.renderer.DebugDraw;
import roki.scene.Scene;
import roki.scene.scenes.LevelEditorScene;
import roki.scene.scenes.LevelScene;
import roki.ui.ImGuiLayer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    private static Window window = null;
    private static Scene currentScene;
    public float r, g, b, a;
    private int width, height;
    private String title;
    private long glfwWindow;
    private ImGuiLayer imGuiLayer;

    private Window() {
        this.width = 1920;
        this.height = 1080;
        this.title = "asd";
        r = 1;
        g = 1;
        b = 1;
        a = 1;
    }

    public static void changeScene(int newScene) {
        switch (newScene) {
            case 0:
                currentScene = new LevelEditorScene();
                break;
            case 1:
                currentScene = new LevelScene();
                break;
            default:
                assert false : "Unknown scene: " + newScene;
                break;
        }

        currentScene.load();
        currentScene.init();
        currentScene.start();
    }

    public static Window get() { // make sure only one instance is running at all times
        if (Window.window == null) {
            Window.window = new Window();
        }

        return Window.window;
    }

    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");
        init();
        loop();

        // freeing memory
        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);

        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    public void init() {
        // error callback
        GLFWErrorCallback.createPrint(System.err).set();

        // init glfw
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW.");
        }

        // config glfw
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);

        glfwWindow = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);
        if (glfwWindow == NULL) {
            throw new IllegalStateException("Failed to create the GLFW window.");
        }

        // set callbacks
        glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);
        glfwSetWindowSizeCallback(glfwWindow, (w, newWidth, newHeight) -> {
            Window.setWidth(newWidth);
            Window.setHeight(newHeight);
        });

        // opengl context current
        glfwMakeContextCurrent(glfwWindow);
        // Enable v-sync
        glfwSwapInterval(1);

        // show window
        glfwShowWindow(glfwWindow);

        // LWJGL COMMENT
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        // enabling alpha
        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
        this.imGuiLayer = new ImGuiLayer(glfwWindow);
        this.imGuiLayer.initImGui();

        Window.changeScene(0);
    }

    public void loop() {
        float beginTime = (float)glfwGetTime(); // set time at begin of frame
        float endTime;
        float dt = -1.0f;

        while (!glfwWindowShouldClose(glfwWindow)) {
            // get events
            glfwPollEvents();

            // debug stuff
            DebugDraw.beginFrame();

            // set background
            glClearColor(r, g, b, a);
            glClear(GL_COLOR_BUFFER_BIT);

            if (dt >= 0) {
                DebugDraw.draw();
                currentScene.update(dt);
            }

            this.imGuiLayer.update(dt, currentScene);
            glfwSwapBuffers(glfwWindow);

            endTime = (float)glfwGetTime();
            dt = endTime - beginTime; // calculate delta time
            beginTime = endTime;
        }

        currentScene.saveExit();
    }

    public static Scene getScene() {
        return get().currentScene;
    }
    public static int getWidth() {
        return get().width;
    }
    public static int getHeight() {
        return get().height;
    }
    public static void setWidth(int w) {
        get().width = w;
    }
    public static void setHeight(int h) {
        get().height = h;
    }
}
