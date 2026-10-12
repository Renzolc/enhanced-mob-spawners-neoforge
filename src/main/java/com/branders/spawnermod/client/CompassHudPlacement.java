package com.branders.spawnermod.client;

import java.util.List;

/**
 * Places the compass tracker from an anchor, a pixel offset, and rectangles
 * that other HUDs already occupy. The result stays on screen.
 */
public final class CompassHudPlacement {

    public static final int MARGIN = 2;
    /** Hotbar plus the experience bar, so bottom center stays readable. */
    private static final int BOTTOM_CENTER_CLEARANCE = 32;
    private static final int GAP = 4;

    public record Box(int x, int y, int width, int height) {
    }

    private CompassHudPlacement() {
    }

    public static int[] place(CompassHudAnchor anchor, int screenWidth, int screenHeight, int widgetWidth,
            int widgetHeight, int offsetX, int offsetY, List<Box> obstacles) {
        int x = anchorX(anchor, screenWidth, widgetWidth) + offsetX;
        int y = anchorY(anchor, screenHeight, widgetHeight) + offsetY;
        if (obstacles != null) {
            for (Box obstacle : obstacles) {
                if (obstacle == null || obstacle.width <= 0 || obstacle.height <= 0) {
                    continue;
                }
                int[] shifted = avoid(x, y, widgetWidth, widgetHeight, obstacle, screenWidth, screenHeight, anchor);
                x = shifted[0];
                y = shifted[1];
            }
        }
        return new int[] { clamp(x, 0, Math.max(0, screenWidth - widgetWidth)),
                clamp(y, 0, Math.max(0, screenHeight - widgetHeight)) };
    }

    static int anchorX(CompassHudAnchor anchor, int screenWidth, int widgetWidth) {
        return switch (anchor) {
            case TOP_LEFT, BOTTOM_LEFT -> MARGIN;
            case TOP_RIGHT, BOTTOM_RIGHT -> screenWidth - widgetWidth - MARGIN;
            default -> (screenWidth - widgetWidth) / 2;
        };
    }

    static int anchorY(CompassHudAnchor anchor, int screenHeight, int widgetHeight) {
        if (anchor.top()) {
            return MARGIN;
        }
        if (anchor == CompassHudAnchor.BOTTOM_CENTER) {
            return screenHeight - widgetHeight - BOTTOM_CENTER_CLEARANCE;
        }
        return screenHeight - widgetHeight - MARGIN;
    }

    /**
     * Moves off a covering rectangle. Prefers below a top anchor and above a bottom
     * anchor. If that spot is off screen, slides to the side with more room.
     */
    private static int[] avoid(int x, int y, int width, int height, Box obstacle, int screenWidth, int screenHeight,
            CompassHudAnchor anchor) {
        Box padded = new Box(obstacle.x - GAP, obstacle.y - GAP, obstacle.width + GAP * 2, obstacle.height + GAP * 2);
        if (!overlaps(x, y, width, height, padded)) {
            return new int[] { x, y };
        }
        int shiftedY = anchor.top() ? padded.y + padded.height : padded.y - height;
        if (fits(x, shiftedY, width, height, screenWidth, screenHeight)
                && !overlaps(x, shiftedY, width, height, padded)) {
            return new int[] { x, shiftedY };
        }
        int spaceLeft = padded.x;
        int spaceRight = screenWidth - (padded.x + padded.width);
        int shiftedX = spaceRight >= spaceLeft ? padded.x + padded.width : padded.x - width;
        if (fits(shiftedX, y, width, height, screenWidth, screenHeight)
                && !overlaps(shiftedX, y, width, height, padded)) {
            return new int[] { shiftedX, y };
        }
        return new int[] { x, shiftedY };
    }

    private static boolean fits(int x, int y, int width, int height, int screenWidth, int screenHeight) {
        return x >= 0 && y >= 0 && x + width <= screenWidth && y + height <= screenHeight;
    }

    private static boolean overlaps(int x, int y, int width, int height, Box box) {
        return x < box.x + box.width && x + width > box.x && y < box.y + box.height && y + height > box.y;
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
