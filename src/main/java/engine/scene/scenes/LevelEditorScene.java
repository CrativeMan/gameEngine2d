package engine.scene.scenes;

import engine.scene.Scene;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class LevelEditorScene extends Scene {

    private String vertexShaderSrc = "#version 330 core\n" +
            "layout(location = 0) in vec3 aPos;\n" +
            "layout(location = 1) in vec4 aColor;\n" +
            "\n" +
            "out vec4 fColor;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    fColor = aColor;\n" +
            "    gl_Position = vec4(aPos, 1.0);\n" +
            "}";

    private String fragmentShaderSrc = "#version 330 core\n" +
            "\n" +
            "in vec4 fColor;\n" +
            "\n" +
            "out vec4 color;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    color = fColor;\n" +
            "}\n";

    private int vertexID, fragmentID, shaderProgram;

    private float[] vertexArray = {
            //Position               //color
            0.5f, -0.5f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, // Bottom right
            -0.5f, 0.5f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, // Top left
            0.5f, 0.5f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f,  // Top right
            -0.5f, -0.5f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f // Bottom left

    };

    //? must be in counterclockwise order
    private int[] elementArray = {
            /*
                X   X

                X   X
             */
            0, 1, 2, // top right triangle
            0, 1, 3 // bottom left triangle
    };

    private int vaoID, vboID, eboID;

    public LevelEditorScene() {
        System.out.println("Level Editor Scene");
    }

    @Override
    public void init() {
        //? ==================================================================
        //? compile and link shaders
        //? ==================================================================
        // load and compile vertex
        vertexID = glCreateShader(GL_VERTEX_SHADER);
        // pass source code to gpu
        glShaderSource(vertexID, vertexShaderSrc);
        // compile shader
        glCompileShader(vertexID);
        // check for errors in comp
        int success = glGetShaderi(vertexID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: 'default.glsl'\n\tVertex shader compilation failed.");
            System.out.println(glGetShaderInfoLog(vertexID, len));
            assert false : "";
        }

        // load and compile fragment
        fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
        // pass source code to gpu
        glShaderSource(fragmentID, fragmentShaderSrc);
        // compile shader
        glCompileShader(fragmentID);
        // check for errors in comp
        success = glGetShaderi(fragmentID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: 'default.glsl'\n\tFragment shader compilation failed.");
            System.out.println(glGetShaderInfoLog(fragmentID, len));
            assert false : "";
        }

        // link shaders check errors
        shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexID);
        glAttachShader(shaderProgram, fragmentID);
        glLinkProgram(shaderProgram);

        success = glGetProgrami(shaderProgram, GL_LINK_STATUS);
        if (success == GL_FALSE) {
            int length = glGetProgrami(shaderProgram, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: 'default.glsl'\n\tLinking of shaders failed.");
            System.out.println(glGetProgramInfoLog(shaderProgram, length));
            assert false : "";
        }

        //? ==================================================================
        //? create vao, vbo, ebo and send to gpu
        //? ==================================================================
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);
        // create float buffer of vertices
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray).flip();

        // create vbo upload vertex buffer
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        //create indices and upload
        IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
        elementBuffer.put(elementArray).flip();
        eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

        // vertex attrib pointers
        int positionsSize = 3;
        int colorSize = 4;
        int floatSizeBytes = Float.BYTES;
        int vertexSizeBytes = (positionsSize + colorSize) * floatSizeBytes;

        glVertexAttribPointer(0, positionsSize, GL_FLOAT, false, vertexSizeBytes, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeBytes, positionsSize * floatSizeBytes);
        glEnableVertexAttribArray(1);
    }

    @Override
    public void update(float dt) {
        // bind shader progrm
        glUseProgram(shaderProgram);
        // bind vao
        glBindVertexArray(vaoID);

        // enable vertex atrib pointrs
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);

        // unbind
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);

        glBindVertexArray(0);
        glUseProgram(0);
    }
}
