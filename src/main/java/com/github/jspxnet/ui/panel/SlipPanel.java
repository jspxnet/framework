/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.ui.panel;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.github.jspxnet.ui.icon.IconPath;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 11-5-25
 * Time: 上午10:40
 * Iphone 桌面一样的滚动面板
 */
public class SlipPanel extends JPanel {
    //按钮面板
    private JLayeredPane buttonPanel;
    private JLayeredPane panel = new JLayeredPane();
    private JLabel[] buttonArray = new JLabel[0];
    private JPanel[] panelArray = new JPanel[0];
    private BufferedImage buttonBackground = null;
    private BufferedImage buttonPanelBackground = null;
    private Color inBorderColor = new Color(158, 162, 167);
    private boolean first = true;
    private int index = 0;
    private int buttonX = 0;
    private int side = 3;

    public SlipPanel() {
        try {
            buttonBackground = ImageIO.read(IconPath.class.getResource("slip_button_bg.png"));
            buttonPanelBackground = ImageIO.read(IconPath.class.getResource("bg.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        buttonPanel = new JLayeredPane() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.drawImage(buttonPanelBackground, 0, 0, getWidth(), getHeight(), null);

                int w = buttonPanel.getWidth() / buttonArray.length;
                if (buttonBackground != null) {
                    g.drawImage(buttonBackground, buttonX, 0, w, buttonBackground.getHeight(), null);
                    g2d.setColor(inBorderColor);
                    Shape shape = new RoundRectangle2D.Double(buttonX, 0, w - 1, buttonBackground.getHeight(), side, side);
                    g2d.draw(shape);
                    g2d.setColor(Color.WHITE);
                    shape = new RoundRectangle2D.Double(buttonX + 1, 1, w - 4, buttonBackground.getHeight() - 4, side, side);
                    g2d.draw(shape);
                }
                //RoundRectangle2D.Float rect = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), side + 3, side + 3);
                //g2d.setClip(rect);
            }

        };


        //顶上按钮面板弹簧
        Spacer conSpacer = new Spacer();


        buttonPanel.setPreferredSize(new Dimension(-1, 32));
        //buttonPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
//        setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(167, 196, 214)));

        setLayout(new GridLayoutManager(2, 2));
        //======== buttonPanel ========
        //3表示按钮数
        buttonPanel.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        add(buttonPanel, new GridConstraints(0, 0, 1, 2,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                null, null, null));
        add(conSpacer, new GridConstraints(1, 0, 1, 1,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL,
                GridConstraints.SIZEPOLICY_CAN_SHRINK,
                GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW,
                null, null, null));

        add(panel, new GridConstraints(1, 0, 1, 2,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                null, null, null));
        panel.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.GRAY));
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateUI();
            }

        });

    }

    @Override
    public void updateUI() {
        if (panelArray != null) {
            for (int i = 0; i < panelArray.length; i++) {
                panelArray[i].setBounds((i - index) * panel.getWidth(), 0, panel.getWidth(), panel.getHeight());
            }
        }
        super.updateUI();
    }


    public void setButtonAndPanel(JLabel[] buttonArray, JPanel[] panelArray) {
        if (buttonArray == null || panelArray == null) {
            return;
        }
        this.buttonArray = buttonArray;
        this.panelArray = panelArray;
        //------------------------------
        buttonPanel.setLayout(new GridLayoutManager(1, this.buttonArray.length + 1));
        for (int i = 0; i < this.buttonArray.length; i++) {
            JLabel button = this.buttonArray[i];
            final int to = i;
            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    setActive(to);
                }
            });
            button.setBorder(new EmptyBorder(0, 0, 0, 0));
            buttonPanel.add(button, new GridConstraints(0, i + 1, 1, 1,
                    GridConstraints.ALIGN_RIGHT, GridConstraints.ALIGN_FILL,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    null, null, null));


        }

    }


    public int getActive() {
        return index;
    }

    public JLayeredPane getPanel() {
        return panel;
    }

    public void setPanel(JLayeredPane panel) {
        this.panel = panel;
    }

    public BufferedImage getButtonBackground() {
        return buttonBackground;
    }

    public void setButtonBackground(BufferedImage buttonBackground) {
        this.buttonBackground = buttonBackground;
    }

    public void setActive(final int show) {
        this.index = show;
        if (buttonArray == null || panelArray == null || buttonArray.length <= 0) {
            return;
        }
        if (first) {


            for (int i = 0; i < panelArray.length; i++) {
                panel.add(this.panelArray[i], JLayeredPane.DEFAULT_LAYER);
                panelArray[i].setBorder(new EmptyBorder(0, 0, 0, 0));
                panelArray[i].setBounds(i * panel.getWidth(), 0, panel.getWidth(), panel.getHeight());
            }
        } else {
            int buttonW = buttonPanel.getWidth() / buttonArray.length;
            for (int i = 0; i < buttonArray.length; i++) {
                JLabel button = buttonArray[i];
                button.setMinimumSize(new Dimension(buttonW, -1));
                if (index == i) {
                    button.setEnabled(true);
                } else {
                    button.setEnabled(false);
                }
            }
            //动画移动
            Thread thread = new Thread() {
                @Override
                synchronized public void run() {
                    int willTo = (0 - index) * panel.getWidth();
                    final int step = (Math.abs(show - index) + 1) * 10 + panelArray.length;
                    int endButtonX = index * (buttonPanel.getWidth() / buttonArray.length) - (index * 5);
                    int times = willTo / step;
                    int stepButtonX = 0;
                    if (times != 0) {
                        stepButtonX = endButtonX / times;
                    }
                    if (stepButtonX == 0) {
                        stepButtonX = buttonPanel.getWidth() / buttonArray.length / step;
                    }
                    while (Math.abs(Math.abs(panelArray[0].getX()) - Math.abs(willTo)) > step) {
                        int x = 0;
                        for (int i = 0; i < panelArray.length; i++) {
                            int toX = (i - index) * panel.getWidth();
                            int formX = panelArray[i].getX();
                            if (formX >= toX) {
                                x = formX - step;
                            } else {
                                x = formX + step;
                            }
                            panelArray[i].setBounds(x, 0, panel.getWidth(), panel.getHeight());
                        }
                        if (endButtonX != buttonX) {
                            if (endButtonX == 0) {
                                buttonX = buttonX - stepButtonX;
                            } else if (endButtonX >= buttonX) {
                                buttonX = buttonX - stepButtonX;
                            } else {
                                buttonX = buttonX + stepButtonX;
                            }
                            buttonPanel.repaint();
                        }
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    for (int i = 0; i < panelArray.length; i++) {
                        panelArray[i].setBounds((i - index) * panel.getWidth(), 0, panel.getWidth(), panel.getHeight());
                    }

                    buttonX = endButtonX;
                    repaint();
                    interrupt();
                }
            };
            thread.start();
        }
        first = false;

    }
}