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


import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;


/**
 *
 * @author Danijel Askov
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class Frame extends Sprite {

    private static final Stop[] OUTER_BLUE_GRADIENT_STOPS = {
            new Stop(0, Color.web("0x000044")),
            new Stop(0.05, Color.web("0x4857A3")),
            new Stop(0.2, Color.web("0xBBCCEE")),
            new Stop(0.8, Color.web("0x000000")),
            new Stop(0.95, Color.web("0x000077")),
            new Stop(1, Color.web("0x0000DD"))
    };
    private static final Stop[] INNER_BLUE_GRADIENT_STOPS = {
            new Stop(0, Color.web("0x000044")),
            new Stop(0.05, Color.web("0x7788AA")),
            new Stop(0.2, Color.web("0xEEEEEE")),
            new Stop(0.8, Color.web("0x000000")),
            new Stop(0.95, Color.web("0x0000DD")),
            new Stop(1, Color.web("0x0000AA")),
    };
    private static final Stop[] INNER_YELLOW_GRADIENT_STOPS = {
            new Stop(0, Color.web("0x000044")),
            new Stop(0.05, Color.web("0xCCCC00")),
            new Stop(0.2, Color.web("0xEEEE00")),
            new Stop(0.8, Color.web("0x444400")),
            new Stop(0.95, Color.web("0x666600")),
            new Stop(1, Color.web("0x444400")),
    };

    /**
     * Cada tipo de preenchimento conhece os gradientes (linear e radial) de que
     * precisa. Assim, Segment (retangular) e EdgeSegment (curvo) apenas pedem o
     * gradiente ao tipo, sem precisar de um switch cada um.
     */
    protected enum FillType {
        BLUE(OUTER_BLUE_GRADIENT_STOPS, INNER_BLUE_GRADIENT_STOPS, false),
        BLUE_INVERTED(OUTER_BLUE_GRADIENT_STOPS, INNER_BLUE_GRADIENT_STOPS, true),
        YELLOW(OUTER_BLUE_GRADIENT_STOPS, INNER_YELLOW_GRADIENT_STOPS, false),
        YELLOW_INVERTED(OUTER_BLUE_GRADIENT_STOPS, INNER_YELLOW_GRADIENT_STOPS, true);

        private final LinearGradient outerLinearGradient;
        private final LinearGradient innerLinearGradient;
        private final RadialGradient outerRadialGradient;
        private final RadialGradient innerRadialGradient;

        FillType(Stop[] outerStops, Stop[] innerStops, boolean inverted) {
            double startX = inverted ? 1 : 0;
            double endX = inverted ? 0 : 1;
            outerLinearGradient = new LinearGradient(startX, 0, endX, 0, true, CycleMethod.NO_CYCLE, outerStops);
            innerLinearGradient = new LinearGradient(startX, 0, endX, 0, true, CycleMethod.NO_CYCLE, innerStops);
            outerRadialGradient = new RadialGradient(0, 0, 0, 0, 1, true, CycleMethod.NO_CYCLE, inverted ? invertStops(outerStops) : outerStops);
            innerRadialGradient = new RadialGradient(0, 0, 0, 0, 1, true, CycleMethod.NO_CYCLE, inverted ? invertStops(innerStops) : innerStops);
        }

        LinearGradient getOuterLinearGradient() {
            return outerLinearGradient;
        }

        LinearGradient getInnerLinearGradient() {
            return innerLinearGradient;
        }

        RadialGradient getOuterRadialGradient() {
            return outerRadialGradient;
        }

        RadialGradient getInnerRadialGradient() {
            return innerRadialGradient;
        }

        private static Stop[] invertStops(Stop[] stops) {
            Stop[] invertedStops = new Stop[stops.length];
            for (int i = 0; i < stops.length; i++) {
                invertedStops[i] = new Stop(stops[stops.length - 1 - i].getOffset(), stops[i].getColor());
            }
            return invertedStops;
        }
    }


    @SuppressWarnings("WeakerAccess")
    protected static final class Segment extends Group {

        private final Rectangle outerRectangle;
        private final Rectangle innerRectangle;

        public Segment(double width, double height, FillType fillType) {
            outerRectangle = new Rectangle(width, height);
            outerRectangle.setStroke(null);

            innerRectangle = new Rectangle(0.80 * width, 0.80 * height);
            innerRectangle.setTranslateX(0.10 * width);
            innerRectangle.setTranslateY(0.10 * height);
            innerRectangle.setStroke(null);

            setFillType(fillType);

            super.getChildren().addAll(outerRectangle, innerRectangle);
        }

        public void setFillType(FillType fillType) {
            outerRectangle.setFill(fillType.getOuterLinearGradient());
            innerRectangle.setFill(fillType.getInnerLinearGradient());
        }

    }


    @SuppressWarnings("WeakerAccess")
    protected static final class EdgeSegment extends Group {

        private final Arc outerArc;
        private final Arc innerArc;

        @SuppressWarnings("SuspiciousNameCombination")
        public EdgeSegment(double width, FillType fillType) {
            outerArc = new Arc(0, 0, width, width, 0, -90);
            outerArc.setType(ArcType.ROUND);
            outerArc.setStroke(null);

            innerArc = new Arc(0, 0, 0.80 * width, 0.80 * width, 0, -90);
            innerArc.setType(ArcType.ROUND);
            innerArc.setTranslateX(0.10 * width);
            innerArc.setTranslateY(0.10 * width);
            innerArc.setStroke(null);

            setFillType(fillType);

            super.getChildren().addAll(outerArc, innerArc);
        }

        public void setFillType(FillType fillType) {
            outerArc.setFill(fillType.getOuterRadialGradient());
            innerArc.setFill(fillType.getInnerRadialGradient());
        }

    }


    protected final double totalWidth;
    protected final double totalHeight;
    protected final double borderSize;

    private final Segment[] segmentsLeft;
    private final Segment[] segmentsRight;
    private final Segment[] segmentsTop;
    private final Segment[] segmentsBottom;


    public Frame(double innerWidth, double innerHeight, int numVerticalSegments, Color backgoundColor) {
        int numSegments = numVerticalSegments;
        double segmentWidth = borderSize = 0.22 * innerWidth;
        double segmentHeight = innerHeight / numSegments;

        Rectangle background = new Rectangle(innerWidth, innerHeight);
        background.setTranslateX(segmentWidth);
        background.setTranslateY(segmentWidth);
        background.setFill(backgoundColor);
        super.getChildren().addAll(background);

        this.totalWidth = innerWidth + 2 * segmentWidth;
        this.totalHeight = innerHeight + 2 * segmentWidth;

        segmentsLeft = new Segment[numSegments];
        segmentsRight = new Segment[numSegments];

        for (int i = 0; i < numSegments; i++) {
            Segment segmentLeft = segmentsLeft[i] = new Segment(segmentWidth, segmentHeight, FillType.BLUE);
            segmentLeft.setTranslateY(segmentWidth + i * segmentHeight);

            Segment segmentRight = segmentsRight[i] = new Segment(segmentWidth, segmentHeight, FillType.BLUE_INVERTED);
            segmentRight.setTranslateX(segmentWidth + innerWidth);
            segmentRight.setTranslateY(segmentWidth + i * segmentHeight);

            super.getChildren().addAll(segmentLeft, segmentRight);
        }

        EdgeSegment leftBottomSegmentConnector = new EdgeSegment(segmentWidth, FillType.BLUE_INVERTED);
        leftBottomSegmentConnector.setScaleX(-1);
        leftBottomSegmentConnector.setTranslateY(segmentWidth + innerHeight);

        EdgeSegment rightBottomSegmentConnector = new EdgeSegment(segmentWidth, FillType.BLUE_INVERTED);
        rightBottomSegmentConnector.setTranslateX(segmentWidth + innerWidth);
        rightBottomSegmentConnector.setTranslateY(segmentWidth + innerHeight);

        super.getChildren().addAll(leftBottomSegmentConnector, rightBottomSegmentConnector);

        EdgeSegment leftTopSegmentConnector = new EdgeSegment(segmentWidth, FillType.BLUE_INVERTED);
        leftTopSegmentConnector.setScaleX(-1);
        leftTopSegmentConnector.setScaleY(-1);

        EdgeSegment rightTopSegmentConnector = new EdgeSegment(segmentWidth, FillType.BLUE_INVERTED);
        rightTopSegmentConnector.setScaleY(-1);
        rightTopSegmentConnector.setTranslateX(segmentWidth + innerWidth);

        super.getChildren().addAll(leftTopSegmentConnector, rightTopSegmentConnector);

        numSegments = (int) ((innerWidth / innerHeight) * numVerticalSegments);
        segmentHeight = innerWidth / numSegments;

        Rotate rotate = new Rotate();
        rotate.setPivotX(0);
        rotate.setPivotY(0);
        rotate.setAngle(-90);

        segmentsTop = new Segment[numSegments];
        segmentsBottom = new Segment[numSegments];

        for (int i = 0; i < numSegments; i++) {
            Segment segmentTop = segmentsTop[i] = new Segment(segmentWidth, segmentHeight, FillType.BLUE_INVERTED);
            segmentTop.getTransforms().add(rotate);
            segmentTop.setTranslateY(segmentWidth);
            segmentTop.setTranslateX(segmentWidth + i * segmentHeight);

            Segment segmentBottom = segmentsBottom[i] = new Segment(segmentWidth, segmentHeight, FillType.BLUE);
            segmentBottom.getTransforms().add(rotate);
            segmentBottom.setTranslateY(2 * segmentWidth + innerHeight);
            segmentBottom.setTranslateX(segmentWidth + i * segmentHeight);

            super.getChildren().addAll(segmentTop, segmentBottom);
        }
    }

    /**
     * Destaca (em amarelo) o par de segmentos laterais da moldura que fica na
     * posição indicada. Encapsula o acesso aos arrays de segmentos.
     *
     * @param segmentIndex índice vertical do segmento a ser destacado
     */
    protected void markSegment(int segmentIndex) {
        segmentsLeft[segmentIndex].setFillType(FillType.YELLOW);
        segmentsRight[segmentIndex].setFillType(FillType.YELLOW_INVERTED);
    }

    public double getBorderSize() {
        return borderSize;
    }

    @Override
    public void update() {
    }

    @Override
    public double getWidth() {
        return totalWidth;
    }

    @Override
    public double getHeight() {
        return totalHeight;
    }

}