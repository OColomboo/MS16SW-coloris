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

package askov.schoolprojects.cg.coloris.gamelogic;

/**
 * Estados explicitos do ciclo do jogo.
 */
public enum GameState {
    /** O bloco esta caindo e o jogador pode controla-lo. */
    ACTIVE,
    /** Casas estao sendo destruidas (animacao em andamento). */
    DESTROYING_SQUARES,
    /** Fim de jogo. */
    GAME_OVER
}