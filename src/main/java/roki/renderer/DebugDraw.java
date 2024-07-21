package roki.renderer;

import org.joml.Vector2f;
import org.joml.Vector3f;
import roki.Window;
import roki.renderer.components.Line2D;
import roki.renderer.components.Shader;
import util.AssetPool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.lwjgl.opengl.GL30.*;

public class DebugDraw {
    private static final int MAX_LINES = 500;

    private static List<Line2D> lines = new ArrayList<>();
    // 6 floats per vertex, 2 vertices per line
    private static float[] vertexArray = new float[MAX_LINES * 6 * 2];
    private static Shader shader = AssetPool.getShader("assets/shaders/debugLine2d.glsl");

    private static int vaoID;
    private static int vboID;

    private static boolean started = false;

    public static void start() {
        // gen vao
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        // vbo
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, (long) vertexArray.length * Float.BYTES, GL_DYNAMIC_DRAW);

        // enable vert attribs
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 6 * Float.BYTES, 0);
        glEnableVertexAttribArray(0); // position
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);
        glEnableVertexAttribArray(1); // color

        glLineWidth(2.0f);
    }

    public static void beginFrame() {
        if (!started) {
            start();
            started = true;
        }

        // remove dead-lines
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).beginFrame() < 0) {
                lines.remove(i);
                i--;
            }
        }
    }

    public static void draw() {
        if (lines.size() <= 0) return;

        int index = 0;
        for (Line2D line : lines) {
            for (int i = 0; i < 2; i++) {
                Vector2f pos = i == 0 ? line.getFrom() : line.getTo();
                Vector3f col = line.getColor();

                // load pos into float array
                vertexArray[index] = pos.x;
                vertexArray[index + 1] = pos.y;
                vertexArray[index + 2] = -10.0f;

                // load color
                vertexArray[index + 3] = col.x;
                vertexArray[index + 4] = col.y;
                vertexArray[index + 5] = col.z;

                index += 6;
            }
        }

        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferSubData(GL_ARRAY_BUFFER, 0, Arrays.copyOfRange(vertexArray, 0, lines.size() * 6 * 2));

        // use shader
        shader.use();
        shader.uploadMat4f("uProjection", Window.getScene().camera().getProjectionMatrix());
        shader.uploadMat4f("uView", Window.getScene().camera().getViewMatrix());

        // bind vao
        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        // draw the batch
        glDrawArrays(GL_LINES, 0, lines.size() * 6 * 2);

        // unbind
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);
        shader.detach();
    }

    // ==================================================================================
    // Add line2D methods
    // ==================================================================================
    public static void addLine2D(Vector2f from, Vector2f to) {
        // TODO: add constants for common colors
        addLine2D(from, to, new Vector3f(0, 1, 0), 1);
    }

    public static void addLine2D(Vector2f from, Vector2f to, Vector3f color) {
        // TODO: add constants for common colors
        addLine2D(from, to, color, 1);
    }

    public static void addLine2D(Vector2f from, Vector2f to, Vector3f color, int lifetime) {
        if (lines.size() >= MAX_LINES) return;
        DebugDraw.lines.add(new Line2D(from, to, color, lifetime));
    }

}
