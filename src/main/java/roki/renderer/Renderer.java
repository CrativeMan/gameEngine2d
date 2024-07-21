package roki.renderer;

import roki.entityComponent.GameObject;
import roki.entityComponent.components.SpriteRenderer;
import roki.renderer.components.Texture;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Renderer {
    private final int MAX_BATCH_SIZE = 1000;
    private List<RenderBatch> batches;

    public Renderer() {
        this.batches = new ArrayList<>();
    }

    public void add(GameObject go) { // add called by other classes
        SpriteRenderer spr = go.getComponent(SpriteRenderer.class);
        if (spr != null) {
            add(spr);
        }
    }

    private void add(SpriteRenderer spr) {
        boolean added = false;
        for (RenderBatch batch : batches) {
            if (batch.hasRoom() && batch.getzIndex() == spr.gameObject.getzIndex()) { // if current batch has room, add sprite to batch
                Texture tex = spr.getTexture();
                if ((batch.hasTexture(tex) || batch.hasTextureRoom()) || tex == null) {
                    batch.addSprite(spr);
                    added = true;
                    break;
                }
            }
        }

        if (!added) { // if not create a new batch
            RenderBatch newBatch = new RenderBatch(MAX_BATCH_SIZE, spr.gameObject.getzIndex());
            newBatch.start();
            batches.add(newBatch);
            newBatch.addSprite(spr);
            Collections.sort(batches);
        }
    }

    public void render() {
        for (RenderBatch batch : batches) {
            batch.render();
        }
    }
}