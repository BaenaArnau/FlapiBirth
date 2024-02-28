package com.mygdx.game;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class StarUp extends Actor {
    static final float DEFAULT_WIDTH = 32; // Ancho de la imagen del power-up
    static final float DEFAULT_HEIGHT = 32; // Alto de la imagen del power-up
    private Rectangle bounds;
    private AssetManager manager;
    private boolean collected;

    public StarUp(float x, float y) {
        setX(x);
        setY(y);
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        bounds = new Rectangle(getX(), getY(), getWidth(), getHeight());
        collected = false;
    }

    @Override
    public void act(float delta) {
        moveBy(-200 * delta, 0); // Mueve el PowerUp hacia la izquierda
        bounds.set(getX(), getY(), getWidth(), getHeight()); // Actualiza los límites del PowerUp
        if (!isVisible()) // Si el PowerUp no es visible, se hace visible
            setVisible(true);
        if (getX() < -64) // Si el PowerUp sale de la pantalla por la izquierda, se elimina
            remove();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        if (!collected) {
            Texture powerUpTexture = manager.get("powerup.png", Texture.class);
            batch.draw(powerUpTexture, getX(), getY(), getWidth(), getHeight()); // Dibujar el power-up con el tamaño ajustado
        }
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void setManager(AssetManager manager) {
        this.manager = manager;
    }

    public boolean isCollected() {
        return collected;
    }

    public void collect() {
        collected = true;
    }
}
