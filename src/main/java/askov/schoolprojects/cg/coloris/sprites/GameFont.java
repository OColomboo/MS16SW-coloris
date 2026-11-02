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


import javafx.scene.text.Font;


/**
 *
 * @author Danijel Askov
 */
public class GameFont {

    private static final String SEGMENTED_DISPLAY_FONT_PATH = "fonts/digital-7-mono.ttf";
    private static final String MAIN_GAME_FONT_PATH = "fonts/computerfont.ttf";
    private static final String FALLBACK_FONT_FAMILY = "Consolas";

    private final Font font;

    private GameFont(Font font) {
        this.font = font;
    }

    public static GameFont segmentedDisplay(double size) {
        return new GameFont(loadFont(SEGMENTED_DISPLAY_FONT_PATH, size));
    }

    public static GameFont mainGameFont(double size) {
        return new GameFont(loadFont(MAIN_GAME_FONT_PATH, size));
    }

    private static Font loadFont(String resourcePath, double size) {
        try {
            return Font.loadFont(Thread.currentThread().getContextClassLoader().getResource(resourcePath).toExternalForm(), size);
        } catch (Exception exception) {
            return Font.font(FALLBACK_FONT_FAMILY, size);
        }
    }

    public Font getFont() {
        return font;
    }

}