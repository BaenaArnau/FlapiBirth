package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class GameScreen implements Screen {
    private final Bird game;
    private OrthographicCamera camera;
    private Stage stage;
    private Player player;
    private boolean dead;
    private Array<Pipe> obstacles;
    private Array<PowerUp> powerUps;
    private long lastObstacleTime;
    private long lastPowerUpTime;
    private float lastHoley;
    private float score;
    private float powerUpSpawnRate; // Tasa de aparición del power-up
    private boolean invulnerable; // Variable para controlar si el jugador está invulnerable
    private float invulnerabilityDuration; // Duración de la invulnerabilidad en segundos
    private float invulnerabilityTimer; // Temporizador para la invulnerabilidad
    private SpriteBatch batch;
    private float normalPowerUpSpawnRate;
    private float normalScoreIncrementRate;

    public GameScreen(final Bird gam) {
        this.game = gam;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);
        batch = new SpriteBatch();
        stage = new Stage();
        stage.getViewport().setCamera(camera);
        obstacles = new Array<>();
        powerUps = new Array<>();
        player = new Player();
        player.setManager(game.manager);
        stage.addActor(player);
        spawnObstacle();
        score = 0;
        powerUpSpawnRate = 10; // Establecemos la tasa de aparición inicial del power-up
        invulnerable = false;
        invulnerabilityDuration = 6; // Duración de la invulnerabilidad en segundos
        invulnerabilityTimer = 0;
        normalPowerUpSpawnRate = powerUpSpawnRate;
        normalScoreIncrementRate = 1.0f;
        spawnPowerUp();
    }

    @Override
    public void render(float delta) {
        boolean dead = false;
        ScreenUtils.clear(0.3f, 0.8f, 0.8f, 1);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(game.manager.get("background.png", Texture.class), 0, 0);
        game.smallFont.draw(batch, "Score: " + (int) score, 10, 470);
        batch.end();
        stage.getBatch().setProjectionMatrix(camera.combined);
        stage.draw();
        score += Gdx.graphics.getDeltaTime();
        if (Gdx.input.justTouched()) {
            player.impulso();
            game.manager.get("flap.wav", Sound.class).play();
        }
        stage.act();
        if (player.getBounds().y > 480 - 45) {
            player.setY(480 - 45);
        }
        if (player.getBounds().y < 0 - 45) {
            dead = true;
        }
        if (TimeUtils.nanoTime() - lastObstacleTime > 1500000000) {
            spawnObstacle();
        }
        if (TimeUtils.nanoTime() - lastPowerUpTime > powerUpSpawnRate * 1000000000) {
            spawnPowerUp();
        }
        Iterator<Pipe> iter = obstacles.iterator();
        while (iter.hasNext()) {
            Pipe pipe = iter.next();
            if (pipe.getBounds().overlaps(player.getBounds())) {
                if (!invulnerable) {
                    dead = true;
                }
            }
        }
        iter = obstacles.iterator();
        while (iter.hasNext()) {
            Pipe pipe = iter.next();
            if (pipe.getX() < -64) {
                obstacles.removeValue(pipe, true);
            }
        }
        // Dentro del bucle while que maneja la recolección del power-up en el método render de GameScreen
        Iterator<PowerUp> powerUpIterator = powerUps.iterator();
        while (powerUpIterator.hasNext()) {
            PowerUp powerUp = powerUpIterator.next();
            if (powerUp.getBounds().overlaps(player.getBounds())) {
                powerUp.collect(); // Marcar el power-up como recogido
                activateInvulnerability(); // Activar la invulnerabilidad
                player.setHasPowerUp(true); // Establecer que el jugador tiene un power-up activo
                player.setPowerUpTexture(game.manager.get("powerupbird.png", Texture.class)); // Cambiar la textura del jugador
            }
        }
        // Actualizar el temporizador de invulnerabilidad
        if (invulnerable) {
            invulnerabilityTimer += delta;
            if (invulnerabilityTimer >= invulnerabilityDuration) {
                invulnerable = false; // Desactivar la invulnerabilidad al pasar el tiempo
                invulnerabilityTimer = 0; // Reiniciar el temporizador
            }
        }
        if (dead) {
            game.manager.get("fail.wav", Sound.class).play();
            game.lastScore = (int) score;
            if (game.lastScore > game.topScore) game.topScore = game.lastScore;
            game.setScreen(new GameOverScreen(game));
            dispose();
        }
        // Aumentar la tasa de incremento del score mientras el power-up está activo
        if (player.hasPowerUp()) {
            score += Gdx.graphics.getDeltaTime() * normalScoreIncrementRate * 2f; // Por ejemplo, el doble de rápido
        } else {
            score += Gdx.graphics.getDeltaTime() * normalScoreIncrementRate; // Restablecer el incremento normal
        }

        // Actualizar el temporizador de invulnerabilidad
        if (invulnerable) {
            invulnerabilityTimer += delta;
            if (invulnerabilityTimer >= invulnerabilityDuration) {
                invulnerable = false; // Desactivar la invulnerabilidad al pasar el tiempo
                invulnerabilityTimer = 0; // Reiniciar el temporizador
                player.deactivatePowerUp(game.manager); // Desactivar el power-up cuando se agote el tiempo
            }
        }
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        batch.dispose();
        stage.dispose();
    }

    private void spawnObstacle() {
        lastHoley = MathUtils.random(50, 230);
        Pipe pipe1 = new Pipe();
        pipe1.setX(800);
        pipe1.setY(lastHoley - 230);
        pipe1.setUpsideDown(true);
        pipe1.setManager(game.manager);
        obstacles.add(pipe1);
        stage.addActor(pipe1);
        Pipe pipe2 = new Pipe();
        pipe2.setX(800);
        pipe2.setY(lastHoley + 200);
        pipe2.setUpsideDown(false);
        pipe2.setManager(game.manager);
        obstacles.add(pipe2);
        stage.addActor(pipe2);
        lastObstacleTime = TimeUtils.nanoTime();
    }

    private void spawnPowerUp() {
        // Calcular el espacio entre las tuberías
        float gapHeight = 200; // Esto puede variar dependiendo del diseño de tu juego

        // Calcular la posición Y del power-up para que esté en el espacio entre las tuberías
        float pipeTopY = lastHoley + gapHeight / 2f; // Posición Y de la parte superior de la tubería
        float pipeBottomY = lastHoley - gapHeight / 2f - PowerUp.DEFAULT_HEIGHT; // Posición Y de la parte inferior de la tubería
        float powerUpY = MathUtils.random(pipeBottomY, pipeTopY);

        // Verificar si la posición Y del power-up no colisiona con las tuberías
        for (Pipe pipe : obstacles) {
            if (powerUpY < pipe.getY() + pipe.getHeight() && powerUpY + PowerUp.DEFAULT_HEIGHT > pipe.getY()) {
                // La posición Y del power-up colisiona con esta tubería, ajustar la posición Y del power-up
                if (pipe.isUpsideDown()) {
                    powerUpY = pipe.getY() + pipe.getHeight();
                } else {
                    powerUpY = pipe.getY() - PowerUp.DEFAULT_HEIGHT;
                }
            }
        }

        // Obtener las dimensiones de la pantalla
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        // Calcular la posición X del power-up para que esté en el centro de la pantalla
        float powerUpX = (screenWidth - PowerUp.DEFAULT_WIDTH) / 2f;

        // Crear y agregar el power-up al juego
        PowerUp powerUp = new PowerUp(powerUpX, powerUpY);
        powerUp.setManager(game.manager);
        powerUps.add(powerUp);
        stage.addActor(powerUp);
        lastPowerUpTime = TimeUtils.nanoTime();

        // Reducir la tasa de aparición de power-ups mientras el power-up está activo
        if (player.hasPowerUp()) {
            powerUpSpawnRate = normalPowerUpSpawnRate / 2f; // Por ejemplo, la mitad del valor normal
        } else {
            powerUpSpawnRate = normalPowerUpSpawnRate; // Restablecer el valor normal
        }
    }

    private void activateInvulnerability() {
        invulnerable = true;
        invulnerabilityTimer = 0; // Reiniciar el temporizador de invulnerabilidad
    }
}