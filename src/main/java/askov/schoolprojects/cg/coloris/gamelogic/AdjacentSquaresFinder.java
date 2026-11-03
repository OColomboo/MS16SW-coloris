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

import askov.schoolprojects.cg.coloris.sprites.Square;
import askov.schoolprojects.cg.coloris.sprites.SquareMatrix;
import java.util.ArrayList;
import java.util.List;

public class AdjacentSquaresFinder {

    private final SquareMatrix squareMatrix;

    public AdjacentSquaresFinder(SquareMatrix squareMatrix) {
        this.squareMatrix = squareMatrix;
    }

    public List<Square> findAdjacentSquares() {
        List<Square> adjacentSquares = new ArrayList<>();

        for (int i = 0; i < squareMatrix.getNumRows(); i++) {
            for (int j = 0; j < squareMatrix.getNumColumns(); j++) {
                Square currentSquare = squareMatrix.getSquare(i, j);

                if (currentSquare.getSquareColor() == Square.DEFAULT_SQUARE_COLOR) {
                    continue;
                }

                // Try to find same-colored squares in currentSquare's row
                int k = j + 1;
                int numAdjacentSquares = 1;
                while (k < squareMatrix.getNumColumns()) {
                    if (squareMatrix.getSquare(i, k).getSquareColor() == currentSquare.getSquareColor()) {
                        numAdjacentSquares++;
                    } else {
                        break;
                    }
                    k++;
                }

                if (numAdjacentSquares > 2) {
                    for (k = j; k < j + numAdjacentSquares; k++) {
                        addIfAbsent(adjacentSquares, squareMatrix.getSquare(i, k));
                    }
                }

                // Try to find same-colored squares in currentSquare's column
                k = i + 1;
                numAdjacentSquares = 1;
                while (k < squareMatrix.getNumRows()) {
                    if (squareMatrix.getSquare(k, j).getSquareColor() == currentSquare.getSquareColor()) {
                        numAdjacentSquares++;
                    } else {
                        break;
                    }
                    k++;
                }

                if (numAdjacentSquares > 2) {
                    for (k = i; k < i + numAdjacentSquares; k++) {
                        addIfAbsent(adjacentSquares, squareMatrix.getSquare(k, j));
                    }
                }
            }
        }

        return adjacentSquares;
    }

    private static void addIfAbsent(List<Square> squares, Square square) {
        if (!squares.contains(square)) {
            squares.add(square);
        }
    }

}
