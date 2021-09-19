/*
 * Copyright (c) 2013. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.github.jspxnet.ui.style;

import com.github.jspxnet.ui.icon.IconPath;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: chenyuan
 * date: 13-2-1
 * Time: 上午11:17
 */
public class SimpleScrollBarUI extends BasicScrollBarUI {
    private Color frameColor = Color.WHITE;
    private Image vscrollbar;

    public SimpleScrollBarUI() {
        try {
            vscrollbar = ImageIO.read(IconPath.class.getResourceAsStream("vscrollbar.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // 重绘滚动条的滑块
    @Override
    public void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) {
            return;
        }
        setThumbBounds(thumbBounds.x, thumbBounds.y, 8, thumbBounds.height < 20 ? 20 : thumbBounds.height);
        g.setColor(Color.GRAY);
        g.translate(thumbBounds.x, thumbBounds.y);

        g.drawImage(vscrollbar, 4, 0, thumbBounds.width, thumbBounds.height, null);
        g.translate(-thumbBounds.x, -thumbBounds.y);
    }


    // 重绘滑块的滑动区域背景
    @Override
    public void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.WHITE);
        g2.fillRect(trackBounds.x, trackBounds.y - 10, trackBounds.width, trackBounds.height + 10);
        trackHighlightColor = new Color(220, 220, 220);
        g2.setColor(trackHighlightColor);
        g2.fillRect(4, trackBounds.y - 10, 8, trackBounds.height);
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return new BasicArrowButton(orientation) {
            // 重绘按钮的三角标记
            @Override
            public void paintTriangle(Graphics g, int x, int y, int size, int direction, boolean isEnabled) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(frameColor);
                g2.fillRect(0, 0, getWidth(), getHeight());

                Image arrowImg = null;
                switch (this.getDirection()) {
                    case BasicArrowButton.SOUTH:
                        try {
                            arrowImg = ImageIO.read(IconPath.class.getResourceAsStream("downArrow.gif"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        g2.drawImage(arrowImg, 4, 0, arrowImg.getWidth(null), arrowImg.getHeight(null), null);

                        break;
                    case BasicArrowButton.EAST:
                        try {
                            arrowImg = ImageIO.read(IconPath.class.getResourceAsStream("leftArrow.gif"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        g2.drawImage(arrowImg, 0, 0, arrowImg.getWidth(null), arrowImg.getHeight(null), null);

                        break;
                    default:{
                        break;
                    }

                }


            }
        };
    }

    @Override
    protected JButton createDecreaseButton(int orientation) {
        return new BasicArrowButton(orientation) {
            @Override
            public void paintTriangle(Graphics g, int x, int y, int size, int direction, boolean isEnabled) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setPaint(frameColor);
                g2.fillRect(0, 0, getWidth(), getHeight());

                Image arrowImg = null;
                switch (this.getDirection()) {
                    case BasicArrowButton.NORTH:
                        try {
                            arrowImg = ImageIO.read(IconPath.class.getResourceAsStream("upArrow.gif"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        g2.drawImage(arrowImg, 4, 0, arrowImg.getWidth(null), arrowImg.getHeight(null), null);

                        break;
                    case BasicArrowButton.WEST:
                        try {
                            arrowImg = ImageIO.read(IconPath.class.getResourceAsStream("rightArrow.gif"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        g2.drawImage(arrowImg, 0, 0, arrowImg.getWidth(null), arrowImg.getHeight(null), null);

                        break;
                }

            }
        };
    }


}