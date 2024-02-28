package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class ScoreMultiplierPowerUp extends Actor {
    static final float DEFAULT_SIZE = 32; // Nuevo tamaño predeterminado del power-up
    private Rectangle bounds;
    private TextureRegion textureRegion;
    private boolean collected;

    public ScoreMultiplierPowerUp(float x, float y) {
        Texture powerUpTexture = new Texture(Gdx.files.internal("scoremultiplier.png"));
        this.textureRegion = new TextureRegion(powerUpTexture);
        setX(x);
        setY(y);
        setSize(DEFAULT_SIZE, DEFAULT_SIZE); // Establecer el nuevo tamaño predeterminado
        bounds = new Rectangle(x, y, getWidth(), getHeight());
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
        if (!collected){
            batch.draw(textureRegion, getX(), getY(), getWidth(), getHeight());
        }
    }

    public Rectangle getBounds() {
        return bounds;
    }

    // Método para recolectar el power-up
    public void collect() {
        collected = true;
    }

    public boolean isCollected() {
        return collected;
    }

    public void setCollected(boolean collected) {
        this.collected = collected;
    }
}

