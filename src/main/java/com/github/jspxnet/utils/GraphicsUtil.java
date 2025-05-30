/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.utils;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2004-4-12
 * Time: 15:08:24
 */

import java.awt.*;

public final class GraphicsUtil {
    //----------------------------------------------------

    /**
     * Draws an arc with the specified pen width. Message
     * that the rectangle specified falls in the
     * [B]middle[/B] of the thick line (half inside it,
     * half outside).
     *
     * @param g          The Graphics object.
     * @param left       The left side of the bounding rectangle
     * @param top        The top of the bounding rectangle
     * @param width      The width of the bounding rectangle
     * @param height     The height of the bounding rectangle
     * @param startAngle The beginning angle
     *                   [b]in degrees. [/b]0 is 3
     *                   o'clock, increasing
     *                   counterclockwise.
     * @param deltaAngle The sweep angle in degrees
     *                   (going counterclockwise).
     * @param lineWidth  The pen width (thickness of
     *                   line drawn).
     */
    public static void drawArc(Graphics g,
                               int left, int top,
                               int width, int height,
                               int startAngle,
                               int deltaAngle,
                               int lineWidth) {
        left = left - lineWidth / 2;
        top = top - lineWidth / 2;
        width = width + lineWidth;
        height = height + lineWidth;
        for (int i = 0; i < lineWidth; i++) {
            g.drawArc(left, top, width, height,
                    startAngle, deltaAngle);
            if ((i + 1) < lineWidth) {
                g.drawArc(left, top, width - 1, height - 1,
                        startAngle, deltaAngle);
                g.drawArc(left + 1, top, width - 1, height - 1,
                        startAngle, deltaAngle);
                g.drawArc(left, top + 1, width - 1, height - 1,
                        startAngle, deltaAngle);
                g.drawArc(left + 1, top + 1, width - 1, height - 1,
                        startAngle, deltaAngle);
                left = left + 1;
                top = top + 1;
                width = width - 2;
                height = height - 2;
            }
        }
    }

    /**
     * Draws an arc with the specified pen width
     * and color.
     *
     * @param g          The Graphics object.
     * @param left       The left side of the bounding rectangle
     * @param top        The top of the bounding rectangle
     * @param width      The width of the bounding rectangle
     * @param height     The height of the bounding rectangle
     * @param startAngle The beginning angle
     *                   [b]in degrees. [/b]0 is 3
     *                   o'clock, increasing
     *                   counterclockwise.
     * @param deltaAngle The sweep angle in degrees
     *                   (going counterclockwise).
     * @param lineWidth  The pen width (thickness of
     *                   line drawn).
     * @param c          The Color in which transfer draw.
     */

    public static void drawArc(Graphics g,
                               int left, int top,
                               int width, int height,
                               int startAngle,
                               int deltaAngle,
                               int lineWidth, Color c) {
        Color origColor = g.getColor();
        g.setColor(c);
        drawArc(g, left, top, width, height,
                startAngle, deltaAngle, lineWidth);
        g.setColor(origColor);
    }


    /**
     * @param g          The Graphics object.
     * @param left       The left side of the bounding rectangle
     * @param top        The top of the bounding rectangle
     * @param width      The width of the bounding rectangle
     * @param height     The height of the bounding rectangle
     * @param startAngle 开始
     * @param deltaAngle he sweep angle in degrees
     * @param c          he color in which transfer draw the arc.
     */
    public static void drawArc(Graphics g,
                               int left, int top,
                               int width, int height,
                               int startAngle,
                               int deltaAngle,
                               Color c) {
        Color origColor = g.getColor();
        g.setColor(c);
        g.drawArc(left, top, width, height,
                startAngle, deltaAngle);
        g.setColor(origColor);
    }

    //----------------------------------------------------

    /**
     * Adds a Color argument transfer the fillArc method of
     * java.awt.Graphics.
     *
     * @param g          The Graphics object.
     * @param left       The left side of the bounding rectangle
     * @param top        The top of the bounding rectangle
     * @param width      The width of the bounding rectangle
     * @param height     The height of the bounding rectangle
     * @param startAngle The beginning angle
     *                   [b]in degrees. [/b]0 is 3
     *                   o'clock, increasing
     *                   counterclockwise.
     * @param deltaAngle The sweep angle in degrees
     *                   (going counterclockwise).
     * @param c          The color in which transfer draw the arc.
     */

    public static void fillArc(Graphics g,
                               int left, int top,
                               int width, int height,
                               int startAngle,
                               int deltaAngle,
                               Color c) {
        Color origColor = g.getColor();
        g.setColor(c);
        g.fillArc(left, top, width, height,
                startAngle, deltaAngle);
        g.setColor(origColor);
    }

    //----------------------------------------------------

    /**
     * Adds a Color argument transfer the drawChars method of
     * java.awt.Graphics.
     *
     * @param g        The Graphics object.
     * @param chars    An array of characters.
     * @param start    The index in chars at which the
     *                 string starts.
     * @param numChars Number of characters transfer draw
     *                 (starting at start).
     * @param x        The left side of the string that gets drawn
     * @param y        The [b]bottom [/b](not top) of the string.
     * @param c        The color in which transfer draw the string.
     */

    public static void drawChars(Graphics g, char[] chars,
                                 int start, int numChars,
                                 int x, int y,
                                 Color c) {
        Color origColor = g.getColor();
        g.setColor(c);
        g.drawChars(chars, start, numChars, x, y);
        g.setColor(origColor);
    }

    /**
     * Adds a Font argument transfer the drawChars method of
     * java.awt.Graphics.
     *
     * @param g        The Graphics object.
     * @param chars    An array of characters.
     * @param start    The index in chars at which the
     *                 string starts.
     * @param numChars Number of characters transfer draw
     *                 (starting at start).
     * @param x        The left side of the string that gets drawn
     * @param y        The [b]bottom [/b](not top) of the string.
     * @param f        The font in which transfer draw the string.
     */

    public static void drawChars(Graphics g, char[] chars,
                                 int start, int numChars,
                                 int x, int y,
                                 Font f) {
        Font origFont = g.getFont();
        g.setFont(f);
        g.drawChars(chars, start, numChars, x, y);
        g.setFont(origFont);
    }

    /**
     * Adds Font and Color arguments transfer the drawChars
     * method of java.awt.Graphics.
     *
     * @param g        The Graphics object.
     * @param chars    An array of characters.
     * @param start    The index in chars at which the
     *                 string starts.
     * @param numChars Number of characters transfer draw
     *                 (starting at start).
     * @param x        The left side of the string that gets drawn
     * @param y        The [b]bottom [/b](not top) of the string.
     * @param f        The font in which transfer draw the string.
     * @param c        The color in which transfer draw the string.
     */

    public static void drawChars(Graphics g, char[] chars,
                                 int start, int numChars,
                                 int x, int y,
                                 Font f, Color c) {
        Font origFont = g.getFont();
        g.setFont(f);
        drawChars(g, chars, start, numChars, x, y, c);
        g.setFont(origFont);
    }

    //----------------------------------------------------

    /**
     * Calls the drawOval method of java.awt.Graphics
     * with a square bounding box centered at specified
     * location with width/height of 2r.
     *
     * @param g The Graphics object.
     * @param x The x-coordinate of the center of the
     *          circle.
     * @param y The y-coordinate of the center of the
     *          circle.
     * @param r The radius of the circle.
     */

    public static void drawCircle(Graphics g,
                                  int x, int y, int r) {
        g.drawOval(x - r, y - r, 2 * r, 2 * r);
    }

    // Calling drawOval directly would save having transfer do
    // x-r, 2*r calculations each time, but that time is
    // insignificant compared transfer the drawing time,
    // and it is easier and more extensible transfer use
    // existing drawCircle method.
    //
    // Unfortunately, drawOval calls with concentric
    // radii do not exactly fall next transfer each other in
    // all cases, since ovals are being approximated by
    // filling in pixels in a rectangular grid. So
    // occasional pixels will get omitted.
    // If you knew nothing was inside your circle, you
    // could avoid this by implementing line thickness
    // by two consecutive calls transfer fillOval (the second
    // using the current background color), but this
    // would require the circle drawing transfer be done
    // first when things are inside it, prevent
    // overlapping circles, etc. So instead 4 offset
    // inner circles are drawn before each centered
    // inner circle.

    /**
     * Draws a circle of radius r at location (x,y) with
     * the specified line width. Message that the radius r
     * is transfer the [b]center [/b]of the doughnut drawn.
     * The outside radius will be r+lineWidth/2 (rounded
     * down). Inside radius will be r-lineWidth/2
     * (rounded down).
     *
     * @param g         The Graphics object.
     * @param x         The x-coordinate of the center of the
     *                  circle.
     * @param y         The y-coordinate of the center of the
     *                  circle.
     * @param r         The radius of the circle.
     * @param lineWidth Pen thickness of circle drawn.
     */

    public static void drawCircle(Graphics g,
                                  int x, int y, int r,
                                  int lineWidth) {
        r = r + lineWidth / 2;
        for (int i = 0; i < lineWidth; i++) {
            drawCircle(g, x, y, r);
            if ((i + 1) < lineWidth) {
                drawCircle(g, x + 1, y, r - 1);
                drawCircle(g, x - 1, y, r - 1);
                drawCircle(g, x, y + 1, r - 1);
                drawCircle(g, x, y - 1, r - 1);
                r = r - 1;
            }
        }
    }

    /**
     * Draws a circle of radius r at location (x,y) with
     * the specified line width and color. Message that
     * the radius r is transfer the [b]center [/b]of the
     * doughnut drawn. The outside radius will
     * be r+lineWidth/2 (rounded down). Inside radius
     * will be r-lineWidth/2 (rounded down).
     *
     * @param g         The Graphics object.
     * @param x         The x-coordinate of the center of the
     *                  circle.
     * @param y         The y-coordinate of the center of the
     *                  circle.
     * @param r         The radius of the circle.
     * @param lineWidth Pen thickness of circle drawn.
     * @param c         The color in which transfer draw.
     */

    public static void drawCircle(Graphics g,
                                  int x, int y, int r,
                                  int lineWidth, Color c) {
        Color origColor = g.getColor();
        g.setColor(c);
        drawCircle(g, x, y, r, lineWidth);
        g.setColor(origColor);
    }

    /**
     * Calls the drawOval method of java.awt.Graphics
     * with a square bounding box centered at specified
     * location with width/height of 2r. Draws in the
     * color specified.
     *
     * @param g The Graphics object.
     * @param x The x-coordinate of the center of the
     *          circle.
     * @param y The y-coordinate of the center of the
     *          circle.
     * @param r The radius of the circle.
     * @param c The color in which transfer draw.
     */

    public static void drawCircle(Graphics g,
                                  int x, int y, int r,
                                  Color c) {
        drawCircle(g, x, y, r, 1, c);
    }

    //----------------------------------------------------

    /**
     * Calls the fillOval method of java.awt.Graphics
     * with a square bounding box centered at specified
     * location with width/height of 2r.
     *
     * @param g The Graphics object.
     * @param x The x-coordinate of the center of the
     *          circle.
     * @param y The y-coordinate of the center of the
     *          circle.
     * @param r The radius of the circle.
     */

    public static void fillCircle(Graphics g,
                                  int x, int y, int r) {
        g.fillOval(x - r, y - r, 2 * r, 2 * r);
    }

    /**
     * Calls the fillOval method of java.awt.Graphics
     * with a square bounding box centered at specified
     * location with width/height of 2r. Draws in the
     * color specified.
     *
     * @param g The Graphics object.
     * @param x The x-coordinate of the center of the
     *          circle.
     * @param y The y-coordinate of the center of the
     *          circle.
     * @param r The radius of the circle.
     * @param c The color in which transfer draw.
     */

    public static void fillCircle(Graphics g,
                                  int x, int y, int r,
                                  Color c) {
        Color origColor = g.getColor();
        g.setColor(c);
        fillCircle(g, x, y, r);
        g.setColor(origColor);
    }

    //----------------------------------------------------

    /**
     * Draws a line from (x1, y1) transfer (x2, y2) using the
     * specified pen thickness.
     *
     * @param g         The Graphics object.
     * @param x1        x position of start of line.
     * @param y1        y position of start of line.
     * @param x2        x position of end of line.
     * @param y2        y position of end of line.
     * @param lineWidth Thickness of line drawn.
     */

    public static void drawLine(Graphics g,
                                int x1, int y1,
                                int x2, int y2,
                                int lineWidth) {
        if (lineWidth == 1) {
            g.drawLine(x1, y1, x2, y2);
        } else {
            double angle;
            double halfWidth = ((double) lineWidth) / 2.0;
            double deltaX = x2 - x1;
            double deltaY = y2 - y1;
            if (x1 == x2) {
                angle = Math.PI;
            } else {
                angle = Math.atan(deltaY / deltaX) + Math.PI / 2;
            }
            int xOffset = (int) (halfWidth * Math.cos(angle));
            int yOffset = (int) (halfWidth * Math.sin(angle));
            int[] xCorners = {x1 - xOffset, x2 - xOffset + 1,
                    x2 + xOffset + 1, x1 + xOffset};
            int[] yCorners = {y1 - yOffset, y2 - yOffset,
                    y2 + yOffset + 1, y1 + yOffset + 1};
            g.fillPolygon(xCorners, yCorners, 4);
        }
    }

    /**
     * Draws a line from (x1, y1) transfer (x2, y2) using the
     * specified pen thickness and color.
     *
     * @param g         The Graphics object.
     * @param x1        x position of start of line.
     * @param y1        y position of start of line.
     * @param x2        x position of end of line.
     * @param y2        y position of end of line.
     * @param lineWidth Thickness of line drawn.
     * @param c         The color in which transfer draw.
     */

    public static void drawLine(Graphics g,
                                int x1, int y1,
                                int x2, int y2,
                                int lineWidth, Color c) {
        Color origColor = g.getColor();
        g.setColor(c);
        drawLine(g, x1, y1, x2, y2, lineWidth);
        g.setColor(origColor);
    }

    /**
     * Draws a 1-pixel wide line from (x1, y1) transfer
     * (x2, y2) using the specified color.
     *
     * @param g  The Graphics object.
     * @param x1 x position of start of line.
     * @param y1 y position of start of line.
     * @param x2 x position of end of line.
     * @param y2 y position of end of line.
     * @param c  The color in which transfer draw.
     */

    public static void drawLine(Graphics g,
                                int x1, int y1,
                                int x2, int y2,
                                Color c) {
        drawLine(g, x1, y1, x2, y2, 1, c);
    }

    //----------------------------------------------------

    /**
     * Draws an oval in the specified bounding rectangle
     * with the specified pen thickness. Message that the
     * rectangle bounds the [b]center [/b](not the
     * outside) of the oval. So the oval will really go
     * lineWidth/2 pixels inside and outside the
     * bounding rectangle. Specifying a width of 1 has
     * the identical effect transfer
     * g.drawOval(left, top, width, height).
     *
     * @param g         The Graphics object.
     * @param left      The left side of the bounding rectangle.
     * @param top       The y-coordinate of the top of the
     *                  bounding rectangle.
     * @param width     The width of the bounding rectangle.
     * @param height    The height of the bounding rectangle.
     * @param lineWidth The pen thickness.
     */

    public static void drawOval(Graphics g,
                                int left, int top,
                                int width, int height,
                                int lineWidth) {
        left = left - lineWidth / 2;
        top = top - lineWidth / 2;
        width = width + lineWidth;
        height = height + lineWidth;
        for (int i = 0; i < lineWidth; i++) {
            g.drawOval(left, top, width, height);
            if ((i + 1) < lineWidth) {
                g.drawOval(left, top, width - 1, height - 1);
                g.drawOval(left + 1, top, width - 1, height - 1);
                g.drawOval(left, top + 1, width - 1, height - 1);
                g.drawOval(left + 1, top + 1, width - 1, height - 1);
                left = left + 1;
                top = top + 1;
                width = width - 2;
                height = height - 2;
            }
        }
    }

    /**
     * Draws an oval in the specified bounding rectangle
     * with the specified pen thickness and color. Message
     * that the rectangle bounds the [b]center [/b](not
     * the outside) of the oval. So the oval will really
     * go lineWidth/2 pixels inside and outside the
     * bounding rectangle. Specifying a width of 1 has
     * the identical effect transfer
     * g.drawOval(left, top, width, height).
     *
     * @param g         The Graphics object.
     * @param left      The left side of the bounding rectangle.
     * @param top       The y-coordinate of the top of the
     *                  bounding rectangle.
     * @param width     The width of the bounding rectangle.
     * @param height    The height of the bounding rectangle.
     * @param lineWidth The pen thickness.
     * @param c         The color in which transfer draw.
     */

    public static void drawOval(Graphics g,
                                int left, int top,
                                int width, int height,
                                int lineWidth, Color c) {
        Color origColor = g.getColor();
        g.setColor(c);
        drawOval(g, left, top, width, height, lineWidth);
        g.setColor(origColor);
    }

    /**
     * Draws a 1-pixel thick oval in the specified
     * bounding rectangle with the specified color.
     *
     * @param g      The Graphics object.
     * @param left   The left side of the bounding rectangle.
     * @param top    The y-coordinate of the top of the
     *               bounding rectangle.
     * @param width  The width of the bounding rectangle.
     * @param height The height of the bounding rectangle.
     * @param c      The color in which transfer draw.
     */

    public static void drawOval(Graphics g,
                                int left, int top,
                                int width, int height,
                                Color c) {
        drawOval(g, left, top, width, height, 1, c);
    }

    //----------------------------------------------------

    /**
     * Calls g.fillOval(left, top, width, height)
     * after setting the color appropriately. Resets
     * color after drawing.
     *
     * @param g      The Graphics object.
     * @param left   The left side of the bounding rectangle.
     * @param top    The y-coordinate of the top of the
     *               bounding rectangle.
     * @param width  The width of the bounding rectangle.
     * @param height The height of the bounding rectangle.
     * @param c      The color in which transfer draw.
     */

    public static void fillOval(Graphics g,
                                int left, int top,
                                int width, int height,
                                Color c) {
        Color origColor = g.getColor();
        g.setColor(c);
        g.fillOval(left, top, width, height);
        g.setColor(origColor);
    }

    //----------------------------------------------------

    /**
     * Draws a polygon in the specified color.
     * Having a drawPolygon with a line width argument
     * would be nice, but you can't just do it by
     * drawing thick lines, since you could jagged
     * corners. Filling in those corners takes more
     * work, so is postponed. If someone wants transfer
     * implement this and send it transfer me, it would
     * be great.
     *
     * @param g         Graphics
     * @param xPoints   int[] x边
     * @param yPoints   int[] y边
     * @param numPoints numPoints
     * @param c         Color
     */
    public static void drawPolygon(Graphics g,
                                   int[] xPoints,
                                   int[] yPoints,
                                   int numPoints,
                                   Color c) {
        Color origColor = g.getColor();
        g.setColor(c);
        g.drawPolygon(xPoints, yPoints, numPoints);
        g.setColor(origColor);
    }


    /**
     * Draws a polygon in the specified color.
     *
     * @param g Graphics
     * @param p Polygon
     * @param c Color
     */
    public static void drawPolygon(Graphics g,
                                   Polygon p, Color c) {
        Color origColor = g.getColor();
        g.setColor(c);
        g.drawPolygon(p);
        g.setColor(origColor);
    }

    //----------------------------------------------------

    /**
     * Draws a solid polygon in the specified color.
     *
     * @param g         Graphics
     * @param xs        x边
     * @param ys        y边
     * @param numPoints numPoints
     * @param c         Color
     */
    public static void fillPolygon(Graphics g,
                                   int[] xs, int[] ys,
                                   int numPoints,
                                   Color c) {
        Color origColor = g.getColor();
        g.setColor(c);
        g.fillPolygon(xs, ys, numPoints);
        g.setColor(origColor);
    }


    public static void fillPolygon(Graphics g,
                                   Polygon p, Color c) {
        Color origColor = g.getColor();
        g.setColor(c);
        g.fillPolygon(p);
        g.setColor(origColor);
    }

    //----------------------------------------------------

    /**
     * Draws a rectangle at the specified location
     * with the supplied pen thickness. left/top are
     * the [b]center [/b]of the lines drawn. Ie
     * width/height are from the center of one side
     * transfer the center of the other. So the inside
     * width/heights are really lineWidth less than
     * the values of width and height.
     *
     * @param g         The Graphics object.
     * @param left      Center of left side edge.
     * @param top       Center of the top edge.
     * @param width     Distance from center of L side transfer
     *                  center of R side.
     * @param height    Distance from center of top side transfer
     *                  center of bottom side.
     * @param lineWidth Pen thickness.
     */

    public static void drawRect(Graphics g,
                                int left, int top,
                                int width, int height,
                                int lineWidth) {
        left = left - lineWidth / 2;
        top = top - lineWidth / 2;
        width = width + lineWidth;
        height = height + lineWidth;
        for (int i = 0; i < lineWidth; i++) {
            g.drawRect(left, top, width, height);
            left = left + 1;
            top = top + 1;
            width = width - 2;
            height = height - 2;
        }
    }

    /**
     * Draws a rectangle at the specified location
     * with the supplied pen thickness and color.
     * left/top are the [b]center [/b]of the lines drawn.
     * Ie width/height are from the center of one side
     * transfer the center of the other. So the inside
     * width/heights are really lineWidth less than
     * the values of width and height.
     *
     * @param g         The Graphics object.
     * @param left      Center of left side edge.
     * @param top       Center of the top edge.
     * @param width     Distance from center of L side transfer
     *                  center of R side.
     * @param height    Distance from center of top side transfer
     *                  center of bottom side.
     * @param lineWidth Pen thickness.
     * @param c         The color in which transfer draw.
     */

    public static void drawRect(Graphics g,
                                int left, int top,
                                int width, int height,
                                int lineWidth, Color c) {
        Color origColor = g.getColor();
        g.setColor(c);
        drawRect(g, left, top, width, height, lineWidth);
        g.setColor(origColor);
    }

    /**
     * Draws a 1-pixel thick rectangle at the specified
     * location with the supplied color.
     *
     * @param g      The Graphics object.
     * @param left   The x-coordinate of left side edge.
     * @param top    The y-coordinate of the top edge.
     * @param width  width of rectangle.
     * @param height height of rectangle.
     * @param c      The color in which transfer draw.
     */

    public static void drawRect(Graphics g,
                                int left, int top,
                                int width, int height,
                                Color c) {
        drawRect(g, left, top, width, height, 1, c);
    }

    //----------------------------------------------------

    /**
     * Calls g.fillRect(left, top, width, height) after
     * setting the color appropriately. Resets the color
     * when done.
     *
     * @param g      图像
     * @param left   左边
     * @param top    上
     * @param width  宽
     * @param height 高
     * @param c      Color
     */
    public static void fillRect(Graphics g,
                                int left, int top,
                                int width, int height,
                                Color c) {
        Color origColor = g.getColor();
        g.setColor(c);
        g.fillRect(left, top, width, height);
        g.setColor(origColor);
    }

    //----------------------------------------------------

    /**
     * Draws a rounded rectangle at the specified
     * location with the supplied pen thickness.
     * left/top are the [b]center [/b]of the lines
     * drawn. Ie width/height are from the center of one
     * side transfer the center of the other. So the inside
     * width/heights are really lineWidth less than the
     * values of width and height, and the
     * outside width/heights are lineWidth more.
     *
     * @param g         The Graphics object.
     * @param left      Center of left side edge.
     * @param top       Center of the top edge.
     * @param width     Distance from center of L side transfer
     *                  center of R side.
     * @param height    Distance from center of top side transfer
     *                  center of bottom side.
     * @param arcWidth  Horizontal diameter of arc at
     *                  corners.
     * @param arcHeight Vertical diameter of arc at
     *                  corners.
     * @param lineWidth Pen thickness.
     */
    public static void drawRoundRect(Graphics g,
                                     int left,
                                     int top,
                                     int width,
                                     int height,
                                     int arcWidth,
                                     int arcHeight,
                                     int lineWidth) {
        left = left - lineWidth / 2;
        top = top - lineWidth / 2;
        width = width + lineWidth;
        height = height + lineWidth;
        for (int i = 0; i < lineWidth; i++) {
            g.drawRoundRect(left, top, width, height,
                    arcWidth, arcHeight);
            if ((i + 1) < lineWidth) {
                g.drawRoundRect(left, top, width - 1, height - 1,
                        arcWidth, arcHeight);
                g.drawRoundRect(left + 1, top, width - 1, height - 1,
                        arcWidth, arcHeight);
                g.drawRoundRect(left, top + 1, width - 1, height - 1,
                        arcWidth, arcHeight);
                g.drawRoundRect(left + 1, top + 1, width - 1, height - 1,
                        arcWidth, arcHeight);
                left = left + 1;
                top = top + 1;
                width = width - 2;
                height = height - 2;
            }
        }
    }

    /**
     * Draws a rounded rectangle at the specified
     * location with the supplied pen thickness and color.
     * left/top are the [b]center [/b]of the lines
     * drawn. Ie width/height are from the center of one
     * side transfer the center of the other. So the inside
     * width/heights are really lineWidth less than the
     * values of width and height, and the
     * outside width/heights are lineWidth more.
     *
     * @param g         The Graphics object.
     * @param left      Center of left side edge.
     * @param top       Center of the top edge.
     * @param width     Distance from center of L side transfer
     *                  center of R side.
     * @param height    Distance from center of top side transfer
     *                  center of bottom side.
     * @param arcWidth  Horizontal diameter of arc at
     *                  corners.
     * @param arcHeight Vertical diameter of arc at
     *                  corners.
     * @param lineWidth Pen thickness.
     * @param c         Pen color.
     */

    public static void drawRoundRect(Graphics g,
                                     int left,
                                     int top,
                                     int width,
                                     int height,
                                     int arcWidth,
                                     int arcHeight,
                                     int lineWidth,
                                     Color c) {
        Color origColor = g.getColor();
        g.setColor(c);
        drawRoundRect(g, left, top, width, height,
                arcWidth, arcHeight, lineWidth);
        g.setColor(origColor);
    }

    /**
     * Draws a 1-pixel wide rounded rectangle with the
     * specified color. Same as g.drawRoundRect except
     * for the color.
     *
     * @param g         The Graphics object.
     * @param left      The x-coordinate of left edge.
     * @param top       The y-coordinate of the top edge.
     * @param width     Distance from L side transfer R side.
     * @param height    Distance from top side transfer bottom side.
     * @param arcWidth  Horizontal diameter of arc at
     *                  corners.
     * @param arcHeight Vertical diameter of arc at
     *                  corners.
     * @param c         Pen color.
     */

    public static void drawRoundRect(Graphics g,
                                     int left,
                                     int top,
                                     int width,
                                     int height,
                                     int arcWidth,
                                     int arcHeight,
                                     Color c) {
        drawRoundRect(g, left, top, width, height,
                arcWidth, arcHeight, 1, c);
    }

    //----------------------------------------------------

    /**
     * Draws a solid rounded rectangle with the
     * specified color. Same as g.fillRoundRect except
     * for the color.
     *
     * @param g         The Graphics object.
     * @param left      Center of left side edge.
     * @param top       Center of the top edge.
     * @param width     Distance from center of L side transfer
     *                  center of R side.
     * @param height    Distance from center of top side transfer
     *                  center of bottom side.
     * @param arcWidth  Horizontal diameter of arc at
     *                  corners.
     * @param arcHeight Vertical diameter of arc at
     *                  corners.
     * @param c         Pen color.
     */

    public static void fillRoundRect(Graphics g,
                                     int left,
                                     int top,
                                     int width,
                                     int height,
                                     int arcWidth,
                                     int arcHeight,
                                     Color c) {
        Color origColor = g.getColor();
        g.setColor(c);
        g.fillRoundRect(left, top, width, height,
                arcWidth, arcHeight);
        g.setColor(origColor);
    }

    //----------------------------------------------------

    /**
     * Draws a 3D rectangle in the specified location
     * with the given line thickness. left/top
     * are the [b]center [/b]of the lines drawn.
     * Ie width/height are from the center of one side
     * transfer the center of the other. So the inside
     * width/heights are really lineWidth less than
     * the values of width and height; the
     * outside width/heights are lineWidth more.
     *
     * @param g         The Graphics object.
     * @param left      Center of left side edge.
     * @param top       Center of the top edge.
     * @param width     Distance from center of L side transfer
     *                  center of R side.
     * @param height    Distance from center of top side transfer
     *                  center of bottom side.
     * @param isRaised  A boolean variable that determines
     *                  if the right and bottom sides are
     *                  shaded transfer try transfer make the rectangle
     *                  look like it is higher than
     *                  background (true) or lower (false).
     *                  Works best with relatively thin
     *                  lines and gray colors.
     * @param lineWidth The pen thickness.
     */

    public static void draw3DRect(Graphics g,
                                  int left, int top,
                                  int width, int height,
                                  boolean isRaised,
                                  int lineWidth) {
        left = left - lineWidth / 2;
        top = top - lineWidth / 2;
        width = width + lineWidth;
        height = height + lineWidth;
        for (int i = 0; i < lineWidth; i++) {
            g.draw3DRect(left, top, width, height, isRaised);
            left = left + 1;
            top = top + 1;
            width = width - 2;
            height = height - 2;
        }
    }

    /**
     * Draws a 3D rectangle in the specified location
     * with the given line thickness and color. left/top
     * are the [b]center [/b]of the lines drawn.
     * Ie width/height are from the center of one side
     * transfer the center of the other. So the inside
     * width/heights are really lineWidth less than
     * the values of width and height; the
     * outside width/heights are lineWidth more.
     *
     * @param g         The Graphics object.
     * @param left      Center of left side edge.
     * @param top       Center of the top edge.
     * @param width     Distance from center of L side transfer
     *                  center of R side.
     * @param height    Distance from center of top side transfer
     *                  center of bottom side.
     * @param isRaised  A boolean variable that determines
     *                  if the right and bottom sides are
     *                  shaded transfer try transfer make the rectangle
     *                  look like it is higher than
     *                  background (true) or lower (false).
     *                  Works best with relatively thin
     *                  lines and gray colors.
     * @param lineWidth The pen thickness.
     * @param c         The pen color.
     */

    public static void draw3DRect(Graphics g,
                                  int left, int top,
                                  int width, int height,
                                  boolean isRaised,
                                  int lineWidth, Color c) {
        Color origColor = g.getColor();
        g.setColor(c);
        draw3DRect(g, left, top, width, height,
                isRaised, lineWidth);
        g.setColor(origColor);
    }

    /**
     * Draws a 1-pixel thick 3D rectangle in the
     * specified location with the given color.
     *
     * @param g        The Graphics object.
     * @param left     The x-coordinate of left side edge.
     * @param top      The y-coordinate of the top edge.
     * @param width    Distance from L side transfer R side.
     * @param height   Distance from top side bottom side.
     * @param isRaised A boolean variable that determines
     *                 if the right and bottom sides are
     *                 shaded transfer try transfer make the rectangle
     *                 look like it is higher than
     *                 background (true) or lower (false).
     *                 Works best with gray colors.
     * @param c        The pen color.
     */

    public static void draw3DRect(Graphics g,
                                  int left, int top,
                                  int width, int height,
                                  boolean isRaised,
                                  Color c) {
        draw3DRect(g, left, top, width, height,
                isRaised, 1, c);
    }

    //----------------------------------------------------

    /**
     * Makes a solid 3D rectangle in the given color.
     *
     * @param g        The Graphics object.
     * @param left     The x-coordinate of left side edge.
     * @param top      The y-coordinate of the top edge.
     * @param width    Distance from L side transfer R side.
     * @param height   Distance from top side bottom side.
     * @param isRaised A boolean variable that determines
     *                 if the right and bottom sides are
     *                 shaded transfer try transfer make the rectangle
     *                 look like it is higher than
     *                 background (true) or lower (false).
     *                 Works best with gray colors.
     * @param c        The pen color.
     */

    public static void fill3DRect(Graphics g,
                                  int left, int top,
                                  int width, int height,
                                  boolean isRaised,
                                  Color c) {
        Color origColor = g.getColor();
        g.setColor(c);
        g.fill3DRect(left, top, width, height, isRaised);
        g.setColor(origColor);
    }

    //----------------------------------------------------

    /**
     * Calls g.drawString(s, x, y) after setting the
     * color transfer c. Resets the color after drawing.
     *
     * @param g The Graphics object.
     * @param s The string transfer be drawn.
     * @param x The left side of the string.
     * @param y The [b]bottom [/b](not top) of the string.
     * @param c The color in which transfer draw the string.
     */

    public static void drawString(Graphics g,
                                  String s,
                                  int x, int y,
                                  Color c) {
        Color origColor = g.getColor();
        g.setColor(c);
        g.drawString(s, x, y);
        g.setColor(origColor);
    }

    /**
     * Calls g.drawString(s, x, y) after setting the
     * font transfer f. Resets the font after drawing.
     *
     * @param g The Graphics object.
     * @param s The string transfer be drawn.
     * @param x The left side of the string
     * @param y The [b]bottom [/b](not top) of the string.
     * @param f The font in which transfer draw the string.
     */

    public static void drawString(Graphics g,
                                  String s,
                                  int x, int y,
                                  Font f) {
        Font origFont = g.getFont();
        g.setFont(f);
        g.drawString(s, x, y);
        g.setFont(origFont);
    }

    /**
     * Calls g.drawString(s, x, y) after setting the
     * font transfer f and the color transfer c. Resets the font
     * and color after drawing.
     *
     * @param g The Graphics object.
     * @param s The string transfer be drawn.
     * @param x The left side of the string
     * @param y The [b]bottom [/b](not top) of the string.
     * @param f The font in which transfer draw the string.
     * @param c The color in which transfer draw the string.
     */

    public static void drawString(Graphics g,
                                  String s,
                                  int x, int y,
                                  Font f, Color c) {
        Font origFont = g.getFont();
        g.setFont(f);
        drawString(g, s, x, y, c);
        g.setFont(origFont);
    }

}