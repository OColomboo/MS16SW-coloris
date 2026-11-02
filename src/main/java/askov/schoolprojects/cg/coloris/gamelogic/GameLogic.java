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
import askov.schoolprojects.cg.coloris.sprites.Block;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javafx.animation.ParallelTransition;

/**
 *
 * @author Danijel Askov
 */
public class GameLogic {

    public enum MoveDirection {LEFT, RIGHT}
    private static final double CRITICAL_DISTANCE = 5; // Pixels
    private static final double DEFAULT_FALLING_BLOCK_SPEED = 100; // Pixels per second
    private static final double REFRESH_RATE = 60; // Frames per second
    private static final double SPEED_UP_FACTOR = 3;
    private static final double MAX_SPEED = DEFAULT_FALLING_BLOCK_SPEED * SPEED_UP_FACTOR;

    private final SquareMatrix squareMatrix;
    private final List<Square> topBlocks = new ArrayList<>();
    private final List<Integer> topBlockRows = new ArrayList<>();

    private final Block fallingBlock;
    private int fallingBlockColumn;
    private double fallingBlockSpeed = DEFAULT_FALLING_BLOCK_SPEED;

    private final Block nextBlock;

    // [Refatoracao 4 - Replace Boolean State with State Object]
    // Antes: private boolean gameOver; private boolean squareDestruction;
    private GameState state = GameState.ACTIVE;
    private int numDestroyedSquares;

    // [Refatoracao 1 - Introduce Interface]
    // Antes: private final LabeledValue score;
    private final ScoreSink score;

    // [Refatoracao 1 - Introduce Interface]
    // Antes: public GameLogic(..., LabeledValue score)
    public GameLogic(SquareMatrix squareMatrix, Block fallingBlock, Block nextBlock, ScoreSink score) {
        this.squareMatrix = squareMatrix;
        this.fallingBlock = fallingBlock;
        this.nextBlock = nextBlock;
        this.score = score;

        fallingBlockColumn = squareMatrix.getNumColumns() / 2;

        for (int j = 0; j < squareMatrix.getNumColumns(); j++) {
            topBlocks.add(j, null);
            topBlockRows.add(j, 0);
        }

        fallingBlock.setTranslateX(this.squareMatrix.getTranslateX() + fallingBlock.getBoundsInParent().getWidth() * fallingBlockColumn);
        fallingBlock.setTranslateY(-fallingBlock.getBoundsInParent().getHeight());
    }

    public void moveFallingBlockHorizontally(MoveDirection fallingBlockMoveDirection) {
        Square topBlock;
        // [Refatoracao 2 - Move Method] calculo da borda inferior agora vive em Block
        double fallingBlockY = fallingBlock.getBottomEdgeY();
        switch (fallingBlockMoveDirection) {
            case LEFT -> {
                if (fallingBlockColumn > 0) {
                    topBlock = topBlocks.get(fallingBlockColumn - 1);
                    if (topBlock != null && fallingBlockY + CRITICAL_DISTANCE >= squareMatrix.getTranslateY() + topBlock.getTranslateY()) {
                        break;
                    }
                    fallingBlock.setTranslateX(fallingBlock.getTranslateX() - fallingBlock.getBoundsInParent().getWidth());
                    fallingBlockColumn--;
                }
            }
            case RIGHT -> {
                if (fallingBlockColumn < squareMatrix.getNumColumns() - 1) {
                    topBlock = topBlocks.get(fallingBlockColumn + 1);
                    if (topBlock != null && fallingBlockY + CRITICAL_DISTANCE >= squareMatrix.getTranslateY() + topBlock.getTranslateY()) {
                        break;
                    }
                    fallingBlock.setTranslateX(fallingBlock.getTranslateX() + fallingBlock.getBoundsInParent().getWidth());
                    fallingBlockColumn++;
                }
            }
        }
    }

    public void speedUpFallingBlock() {
        if (fallingBlockSpeed < MAX_SPEED) {
            fallingBlockSpeed *= SPEED_UP_FACTOR;
        }
    }

    public void moveFallingBlockVertically() {
        fallingBlock.setTranslateY(fallingBlock.getTranslateY() + fallingBlockSpeed / REFRESH_RATE);
    }

    public void checkCollision() {
        int fallingBlockRow;

        Square topBlock = topBlocks.get(fallingBlockColumn);
        // [Refatoracao 2 - Move Method] calculo da borda inferior agora vive em Block
        double fallingBlockY = fallingBlock.getBottomEdgeY();

        if (topBlock != null && fallingBlockY + CRITICAL_DISTANCE >= squareMatrix.getTranslateY() + topBlock.getTranslateY()) {
            fallingBlockRow = topBlockRows.get(fallingBlockColumn) - fallingBlock.getNumSquares();

            topBlocks.remove(fallingBlockColumn);
            topBlockRows.remove(fallingBlockColumn);

            squareMatrix.absorbColorsFrom(fallingBlockRow, fallingBlockColumn, fallingBlock);

            topBlocks.add(fallingBlockColumn, squareMatrix.getSquare(fallingBlockRow, fallingBlockColumn));
            topBlockRows.add(fallingBlockColumn, fallingBlockRow);

            returnFallingBlockToStartPosition();
        } else if (fallingBlockY + CRITICAL_DISTANCE >= squareMatrix.getTranslateY() + squareMatrix.getHeight()) {
            fallingBlockRow = squareMatrix.getNumRows() - fallingBlock.getNumSquares();

            topBlocks.remove(fallingBlockColumn);
            topBlockRows.remove(fallingBlockColumn);

            squareMatrix.absorbColorsFrom(fallingBlockRow, fallingBlockColumn, fallingBlock);

            topBlocks.add(fallingBlockColumn, squareMatrix.getSquare(fallingBlockRow, fallingBlockColumn));
            topBlockRows.add(fallingBlockColumn, fallingBlockRow);

            returnFallingBlockToStartPosition();
        }
    }

    private void returnFallingBlockToStartPosition() {
        fallingBlockSpeed = DEFAULT_FALLING_BLOCK_SPEED;
        fallingBlockColumn = squareMatrix.getNumColumns() / 2;
        fallingBlock.setTranslateX(squareMatrix.getTranslateX() + fallingBlock.getBoundsInParent().getWidth() * fallingBlockColumn);
        fallingBlock.setTranslateY(-fallingBlock.getBoundsInParent().getHeight());

        fallingBlock.absorbColorsFrom(nextBlock);
        nextBlock.setRandomSquareColors();
    }

    public void reorderFallingBlockSquares() {
        fallingBlock.reorderSquares();
    }

    public void destroyAdjacentSquares() {
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
                        Square adjacentSquare = squareMatrix.getSquare(i, k);
                        if (!adjacentSquares.contains(adjacentSquare)) {
                            adjacentSquares.add(adjacentSquare);
                            numDestroyedSquares++;
                            // [Refatoracao 1] Antes: score.setValue(score.getValue() + 1);
                            score.addPoints(1);
                        }
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
                        Square adjacentSquare = squareMatrix.getSquare(k, j);
                        if (!adjacentSquares.contains(adjacentSquare)) {
                            adjacentSquares.add(adjacentSquare);
                            numDestroyedSquares++;
                            // [Refatoracao 1] Antes: score.setValue(score.getValue() + 1);
                            score.addPoints(1);
                        }
                    }
                }
            }
        }

        if (!adjacentSquares.isEmpty()) {
            // [Refatoracao 3 - Parameterize Method] animacao comum, recebe as casas e a acao final
            state = GameState.DESTROYING_SQUARES;
            playDestructionAnimation(adjacentSquares, () -> {
                removeGaps();
                destroyAdjacentSquares();
            });
        } else {
            if (squareMatrix.isColumnOverflow()) {
                state = GameState.GAME_OVER;
            } else if (numDestroyedSquares >= 5) {
                removeBottomRow();
            } else {
                state = GameState.ACTIVE;
                numDestroyedSquares = 0;
            }
        }
    }

    private void removeGaps() {
        for (int j = 0; j < squareMatrix.getNumColumns(); j++) {
            int moveToRowIndex;
            for (moveToRowIndex = squareMatrix.getNumRows() - 1; moveToRowIndex >= 0; moveToRowIndex--) {
                Square square = squareMatrix.getSquare(moveToRowIndex, j);
                if (square.getSquareColor() == Square.DEFAULT_SQUARE_COLOR) {
                    break;
                }
            }

            int moveFromRowIndex;
            for (moveFromRowIndex = moveToRowIndex - 1; moveFromRowIndex >= 0; moveFromRowIndex--) {
                Square square = squareMatrix.getSquare(moveFromRowIndex, j);
                if (square.getSquareColor() != Square.DEFAULT_SQUARE_COLOR) {
                    break;
                }
            }

            while (moveFromRowIndex >= 0) {
                Square fromSquare = squareMatrix.getSquare(moveFromRowIndex, j);
                if (fromSquare.getSquareColor() == Square.DEFAULT_SQUARE_COLOR) {
                    break;
                }
                squareMatrix.getSquare(moveToRowIndex, j).absorbColorFrom(fromSquare);
                moveFromRowIndex--;
                moveToRowIndex--;
            }

            for (int i = moveToRowIndex; i >= 0; i--) {
                squareMatrix.getSquare(i, j).setSquareColor(Square.DEFAULT_SQUARE_COLOR);
            }

            if (moveToRowIndex < squareMatrix.getNumRows() - 1) {
                topBlocks.set(j, squareMatrix.getSquare(moveToRowIndex + 1, j));
                topBlockRows.set(j, moveToRowIndex + 1);
            } else {
                topBlocks.set(j, null);
                topBlockRows.set(j, 0);
            }
        }
    }

    private void removeBottomRow() {
        int i = squareMatrix.getNumRows() - 1;
        List<Square> bottomRowSquares = new ArrayList<>();

        for (int j = 0; j < squareMatrix.getNumColumns(); j++) {
            Square bottomRowSquare = squareMatrix.getSquare(i, j);
            if (bottomRowSquare.getSquareColor() != Square.DEFAULT_SQUARE_COLOR) {
                bottomRowSquares.add(bottomRowSquare);
            }
        }

        int numBottomRowSquares = bottomRowSquares.size();
        numDestroyedSquares += numBottomRowSquares;
        // [Refatoracao 1] Antes: score.setValue(score.getValue() + numBottomRowSquares);
        score.addPoints(numBottomRowSquares);
        state = GameState.DESTROYING_SQUARES;

        // [Refatoracao 3 - Parameterize Method] mesma rotina, outra acao ao terminar
        playDestructionAnimation(bottomRowSquares, () -> {
            removeGaps();
            state = GameState.ACTIVE;
            numDestroyedSquares = 0;
        });
    }

    /**
     * [Refatoracao 3 - Parameterize Method]
     * Rotina comum que antes estava duplicada em destroyAdjacentSquares()
     * e removeBottomRow(). Varia apenas as casas animadas e o que fazer ao final.
     */
    private void playDestructionAnimation(Collection<Square> squares, Runnable onFinished) {
        ParallelTransition parallelTransition = new ParallelTransition();
        parallelTransition.setCycleCount(1);
        parallelTransition.setAutoReverse(false);

        for (Square square : squares) {
            parallelTransition.getChildren().add(square.getDestructionAnimation());
        }

        parallelTransition.setOnFinished(event -> onFinished.run());
        parallelTransition.play();
    }

    // [Refatoracao 4] Consultas derivadas do estado; a API publica usada por Coloris nao muda
    public boolean isGameOver() {
        return state == GameState.GAME_OVER;
    }

    public boolean squareDestruction() {
        return state == GameState.DESTROYING_SQUARES;
    }

}