/*
 * Copyright (C) 2019  Danijel Askov
 *
 * This file is part of Coloris.
 *
 * Coloris is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Coloris is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package askov.schoolprojects.cg.coloris.sprites;

import java.util.Random;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author Danijel Askov
 */
@SuppressWarnings("unused")
public class Universe extends Sprite {

    private static final Color UNIVERSE_COLOR = Color.BLACK;
    private static final int NUM_STARS = 50;
    private static final Random RANDOM = new Random();
    private final double width;
    private final double height;

    public Universe(double width, double height) {
        Rectangle background = new Rectangle(this.width = width, this.height = height);
        background.setFill(UNIVERSE_COLOR);
        super.getChildren().add(background);

        for (int i = 0; i < NUM_STARS; i++) {
            double posX = RANDOM.nextDouble() * width;
            double posY = RANDOM.nextDouble() * height;

            StarSprite star = new StarSprite(
                    RANDOM.nextDouble() * 1.2 * StarSprite.CRITICAL_STAR_SIZE,
                    RANDOM
            );

            star.setTranslateX(posX);
            star.setTranslateY(posY);

            super.getChildren().add(star);
        }
    }

    @Override
    public void update() {
    }
    
    @Override
    public double getWidth() {
        return width;
    }

    @Override
    public double getHeight() {
        return height;
    }

}
