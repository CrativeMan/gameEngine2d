package roki.renderer;

import org.joml.Vector4f;
import roki.Window;
import roki.ec.components.SpriteRenderer;

import static org.lwjgl.opengl.ARBVertexArrayObject.glBindVertexArray;
import static org.lwjgl.opengl.ARBVertexArrayObject.glGenVertexArrays;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;

public class RenderBatch {
    // Vertex
    // =======
    // Pos                 Color
    // float, float,       float, float, float, float
    private final int POS_SIZE = 2;
    private final int COLOR_SIZE = 4;

    private final int POS_OFFSET = 0;
    private final int COLOR_OFFSET = POS_OFFSET + POS_SIZE * Float.BYTES;
    private final int VERTEX_SIZE = 6;
    private final int VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.BYTES;


    private SpriteRenderer[] sprites;
    private int numSprites;
    private boolean hasRoom;
    private float[] vertices;

    private int vaoID, vboID;
    private int maxBatchSize;
    private Shader shader;

    public RenderBatch(int maxBatchSize) {
        shader = new Shader("assets/shaders/default.glsl");
        shader.compileAndLink();

        this.sprites = new SpriteRenderer[maxBatchSize];
        this.maxBatchSize = maxBatchSize;

        vertices = new float[maxBatchSize * 4 * VERTEX_SIZE]; // 4 vertices per quad

        this.numSprites = 0;
        this.hasRoom = true;
    }

    public void start() {
        // generate, bind vao
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);
        // allocate space
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);

        // create upload ebo
        int eboID = glGenBuffers();
        int[] indices = generateIndices();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        // enable buffer attrib pointers
        glVertexAttribPointer(0, POS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, POS_OFFSET);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(1, COLOR_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, COLOR_OFFSET);
        glEnableVertexAttribArray(1);
    }

    private void loadElementIndices(int[] elements, int i) {
        int offsetArrayIndex = 6 * i;
        int offset = 4 * i;

        // triangle 1
        elements[offsetArrayIndex + 0] = offset + 3;
        elements[offsetArrayIndex + 1] = offset + 2;
        elements[offsetArrayIndex + 2] = offset + 0;
        // triangle 2
        elements[offsetArrayIndex + 3] = offset + 0;
        elements[offsetArrayIndex + 4] = offset + 2;
        elements[offsetArrayIndex + 5] = offset + 1;
    }

    public void addSprite(SpriteRenderer spr) {
        // get index add spr
        int index = this.numSprites;
        this.sprites[index] = spr;
        this.numSprites++;

        // add properties to local array
        loadVertexProperties(index);
        if(numSprites >= this.maxBatchSize){
            this.hasRoom = false;
        }
    }

    public void render() {
        // rebuffer all data each frame (FOR NOW)
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferSubData(GL_ARRAY_BUFFER, 0, vertices); // upload all vertices starting at 0

        // shader
        shader.use();
        shader.uploadMat4f("uProjection", Window.getScene().camera().getProjectionMatrix());
        shader.uploadMat4f("uView", Window.getScene().camera().getViewMatrix());

        // bind vao
        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, numSprites * 6, GL_UNSIGNED_INT, 0); // 6 indices per quad

        // unbind/detach/disable
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);
        shader.detach();
    }

    public void loadVertexProperties(int index) {
        SpriteRenderer sprite = this.sprites[index];

        // 4 vertices per sprite
        int offset = index * 4 * VERTEX_SIZE;

        Vector4f color = sprite.getColor();

        // add vertices with properties
        /*
            *      *
            *      *
         */
        float xAdd = 1.0f;
        float yAdd = 1.0f;
        for (int i = 0; i < 4; i++) {
            if (i == 1)
                yAdd = 0.0f;
            else if (i == 2)
                xAdd = 0.0f;
            else if (i == 3)
                yAdd = 1.0f;

            // load pos
            vertices[offset] = sprite.gameObject.transform.position.x + (xAdd * sprite.gameObject.transform.scale.x);
            vertices[offset + 1] = sprite.gameObject.transform.position.y + (yAdd * sprite.gameObject.transform.scale.y);

            // load color
            vertices[offset + 2] = color.x;
            vertices[offset + 3] = color.y;
            vertices[offset + 4] = color.z;
            vertices[offset + 5] = color.w;

            offset += VERTEX_SIZE;

        }
    }

    public int[] generateIndices() {
        // 6 indices per quad
        int[] elements = new int[6 * maxBatchSize];

        for(int i = 0; i < maxBatchSize; i++) {
            loadElementIndices(elements, i);
        }

        return elements;
    }

    public boolean hasRoom() {
        return this.hasRoom;
    }
}
