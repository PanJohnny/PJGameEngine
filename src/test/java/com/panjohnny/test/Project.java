package com.panjohnny.test;

import com.panjohnny.pjge.PJGE;
import com.panjohnny.pjgl.adapt.lwjgl.GLFWKeyboard;
import com.panjohnny.pjgl.adapt.lwjgl.GLFWWindow;
import com.panjohnny.pjgl.adapt.lwjgl.LWJGLInitializer;
import com.panjohnny.pjgl.api.PJGL;
import com.panjohnny.pjgl.api.PJGLEvents;
import com.panjohnny.pjgl.api.asset.SpriteRegistry;
import com.panjohnny.pjgl.core.EngineOptions;
import org.lwjgl.glfw.GLFW;

import java.util.concurrent.atomic.AtomicReference;

public class Project {
    public static void main(String[] args) {
        EngineOptions.logFPS = true;
        PJGE.startDebugger();
        PJGL.init(new LWJGLInitializer("Apple!", 750, 750));
        PJGL pjgl = PJGL.getInstance();

        AtomicReference<Apple> apple = new AtomicReference<>();


        PJGLEvents.VISIBLE.listen(() -> {
            SpriteRegistry.registerTextureSprite("apple", "./apple.png");

            apple.set(new Apple());

            pjgl.getManager().queueAddition(apple.get());
            for (int i = 0; i < 20; i++) {
                pjgl.getManager().queueAddition(new Apple());
            }
        });

        GLFWKeyboard keyboard = pjgl.getKeyboard();
        GLFWWindow window = pjgl.getWindow();

        PJGLEvents.TICK.listen(() -> {
            if (keyboard.isKeyDown(GLFW.GLFW_KEY_ESCAPE)) {
                window.close();
            }

            if (keyboard.isKeyDown(GLFW.GLFW_KEY_LEFT)) {
                apple.get().move(-1,0);
            }

            if (keyboard.isKeyDown(GLFW.GLFW_KEY_RIGHT)) {
                apple.get().move(1,0);
            }

            if (keyboard.isKeyDown(GLFW.GLFW_KEY_UP)) {
                apple.get().move(0,-1);
            }

            if (keyboard.isKeyDown(GLFW.GLFW_KEY_DOWN)) {
                apple.get().move(0,1);
            }
        });
        pjgl.start();
    }
}
