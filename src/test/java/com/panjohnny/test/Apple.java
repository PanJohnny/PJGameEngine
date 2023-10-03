package com.panjohnny.test;

import com.panjohnny.pjgl.api.object.GameObject;
import com.panjohnny.pjgl.api.object.components.Position;
import com.panjohnny.pjgl.api.object.components.Size;
import com.panjohnny.pjgl.api.object.components.SpriteRenderer;

@SuppressWarnings("unused")
public class Apple extends GameObject {
    public final Position position = addComponent(new Position(this, 10, 10));
    public final Size size = addComponent(new Size(this, 100, 100));
    public final SpriteRenderer renderer = addComponent(new SpriteRenderer(this, "apple"));

    public void move(int x, int y) {
        position.add(x, y);
    }

}
