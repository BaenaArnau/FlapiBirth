package com.mygdx.game;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Player extends Actor {
    private Rectangle bounds;
    private AssetManager manager;
    private float speedy, gravity;
    private Texture baseTexture;
    private TextureRegion baseTextureRegion;
    private Texture powerUpTexture;
    private TextureRegion powerUpTextureRegion;
    private boolean hasPowerUp; // Indica si el jugador tiene activado un power-up

    // Constructor
    public Player() {
        setX(200);
        setY(280 / 2 - 64 / 2);
        setSize(64, 45);
        bounds = new Rectangle();
        speedy = 0;
        gravity = 850f;
        hasPowerUp = false; // Por defecto, el jugador no tiene un power-up activado
        // Cargar las texturas por defecto
        baseTexture = new Texture("bird.png");
        baseTextureRegion = new TextureRegion(baseTexture, 0, 0, 64, 45);
        powerUpTexture = new Texture("powerupbird.png");
        powerUpTextureRegion = new TextureRegion(powerUpTexture, 0, 0, 64, 45);
    }

    @Override
    public void act(float delta) {
        moveBy(0, speedy * delta);
        speedy -= gravity * delta;
        bounds.set(getX(), getY(), getWidth(), getHeight());
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        TextureRegion currentTextureRegion; // Usaremos TextureRegion para poder dibujar subregiones de las texturas

        // Si el jugador tiene un power-up activo, usamos la textura del power-up, de lo contrario, usamos la textura base
        if (hasPowerUp) {
            currentTextureRegion = new TextureRegion(powerUpTexture);
        } else {
            currentTextureRegion = new TextureRegion(baseTexture);
        }

        batch.draw(currentTextureRegion, getX(), getY(), getWidth(), getHeight()); // Dibujamos la textura actual
    }

    // Método para activar o desactivar el power-up
    public void setHasPowerUp(boolean hasPowerUp) {
        this.hasPowerUp = hasPowerUp;
    }

    // Método para establecer el AssetManager
    public void setManager(AssetManager manager) {
        this.manager = manager;
    }

    // Método para aplicar un impulso al jugador
    public void impulso() {
        speedy = 400f;
    }

    // Método para obtener los límites del jugador
    public Rectangle getBounds() {
        return bounds;
    }

    public void setPowerUpTexture(Texture texture) {
        this.powerUpTexture = texture;
        this.powerUpTextureRegion = new TextureRegion(texture, 0, 0, 64, 45);
    }

    public void deactivatePowerUp(AssetManager manager) {
        this.hasPowerUp = false;
        // Restaurar la textura base
        baseTexture = manager.get("bird.png", Texture.class);
        baseTextureRegion.setRegion(baseTexture);
    }
}
