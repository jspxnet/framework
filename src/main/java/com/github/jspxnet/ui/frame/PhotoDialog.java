/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.ui.frame;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.github.jspxnet.ui.button.AlphaButton;
import com.github.jspxnet.ui.icon.IconPath;
import com.github.jspxnet.ui.label.SideLabel;
import com.github.jspxnet.utils.ImageUtil;
import com.github.jspxnet.utils.SwingUtil;
import com.sun.awt.AWTUtilities;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 11-6-9
 * Time: 下午9:12
 */
public class PhotoDialog extends JDialog {

    //按钮begin
    private AlphaButton helpButton = new AlphaButton();
    private AlphaButton minButton = new AlphaButton();
    private AlphaButton maxButton = new AlphaButton();
    private AlphaButton closeButton = new AlphaButton();
    private Rectangle oldRectangle = null;

    //按钮end

    private JLabel titleLabel = new SideLabel();
    private BufferedImage backgroundImage;
    private double side = 10;
    private JDialog frame = this;
    private Point origin; //用于移动窗体
    private JPanel clientPanel = new JPanel();


    public PhotoDialog() {


        Icon closeNormalIcon = new ImageIcon(IconPath.class.getResource("close_normal.png"));
        Icon maxNormalIcon = new ImageIcon(IconPath.class.getResource("max_normal.png"));
        Icon minNormalIcon = new ImageIcon(IconPath.class.getResource("min_normal.png"));
        Icon helpNormalIcon = new ImageIcon(IconPath.class.getResource("help_normal.png"));

        Icon closeButtonIcon = new ImageIcon(IconPath.class.getResource("closeButton.png"));
        Icon maxButtonIcon = new ImageIcon(IconPath.class.getResource("maxButton.png"));
        Icon minButtonIcon = new ImageIcon(IconPath.class.getResource("minButton.png"));
        Icon helpButtonIcon = new ImageIcon(IconPath.class.getResource("helpButton.png"));

        origin = new Point(); //初始化偏移坐标

        JPanel headPanel = new JPanel();
        JPanel contentPane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                if (backgroundImage != null) {

                    g2d.drawImage(ImageUtil.scale(backgroundImage, getWidth(), getHeight()), 0, 0, getWidth(), getHeight(), null);
                } else {
                    g2d.setPaint(new GradientPaint(0, 0, new Color(255, 255, 247), getWidth() / 5, getHeight(), new Color(197, 206, 211)));
                    g2d.fillRect(0, getHeight() / 5, getWidth(), getHeight());
                }

                SwingUtil.setVisibleRegion(frame, getWidth(), getHeight(), side);

                g2d.setColor(Color.BLACK);
                //绘制窗体边框
                Shape shape = new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, side + 1, side + 1);
                g2d.draw(shape);
            }
        };


        contentPane.setBorder(BorderFactory.createEmptyBorder());
        setContentPane(contentPane);


        Spacer titleSpacer = new Spacer();
        Spacer conSpacer = new Spacer();

        contentPane.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), 1, 1));
        headPanel.setLayout(new GridLayoutManager(1, 6, new Insets(1, 4, 0, 1), 1, 1));
        //---- titleLabel ----

        titleLabel.setBorder(new EmptyBorder(6, 8, 0, 0));
        Font font = titleLabel.getFont();

        titleLabel.setFont(font.deriveFont(Font.BOLD));
        titleLabel.setText("speak.net");

        titleLabel.setForeground(Color.WHITE);
        headPanel.add(titleLabel, new GridConstraints(0, 0, 1, 1,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                null, null, null));
        headPanel.add(titleSpacer, new GridConstraints(0, 1, 1, 1,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK,
                null, null, null));


        //---- helpLabel ----

        helpButton.setIcon(helpNormalIcon);
        helpButton.setRolloverIcon(helpButtonIcon);
        helpButton.setPreferredSize(new Dimension(27, 20));
        headPanel.add(helpButton, new GridConstraints(0, 2, 1, 1,
                GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                null, null, null, 0, true));

        //---- minLabel ----


        minButton.setIcon(minNormalIcon);
        minButton.setRolloverIcon(minButtonIcon);
        minButton.setPreferredSize(new Dimension(28, 20));
        minButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                frame.setVisible(false);
            }
        });

        headPanel.add(minButton, new GridConstraints(0, 3, 1, 1,
                GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                null, null, null, 0, true));

        //---- maxLabel ----

        maxButton.setIcon(maxNormalIcon);
        maxButton.setRolloverIcon(maxButtonIcon);
        maxButton.setPreferredSize(new Dimension(28, 20));


        maxButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                maxButtonClick();
            }
        });
        headPanel.add(maxButton, new GridConstraints(0, 4, 1, 1,
                GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                null, null, null, 0, true));

        //---- closeLabel ----
        closeButton.setIcon(closeNormalIcon);
        closeButton.setRolloverIcon(closeButtonIcon);
        closeButton.setPreferredSize(new Dimension(37, 20));
        closeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
            }
        });
        setCloseButton(closeButton);
        headPanel.add(closeButton, new GridConstraints(0, 5, 1, 1,
                GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                null, null, null, 0, true));


        add(headPanel, new GridConstraints(0, 0, 1, 1,
                GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_BOTH,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                null, null, null, 0, true));

        add(conSpacer, new GridConstraints(1, 0, 2, 1,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL,
                GridConstraints.SIZEPOLICY_CAN_SHRINK,
                GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW,
                null, null, null));

        //======== clientPanel ========
        clientPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        add(clientPanel, new GridConstraints(1, 0, 2, 1,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                null, null, null));

        //BorderLayout.NORTH 上  BorderLayout.SOUTH 下   BorderLayout.WEST 左  BorderLayout.EAST 右

        setUndecorated(true);//去掉所有装饰：标题栏、大小、关闭
        AWTUtilities.setWindowOpaque(this, true);//设置窗体是否透明
        SwingUtil.setVisibleRegion(this, getWidth(), getHeight(), side);
        MouseInputListener listener = new MouseInputHandler();//添加窗体事件
        addMouseListener(listener);
        addMouseMotionListener(listener);

    }

    public void maxButtonClick() {
        if (oldRectangle != null) {
            setBounds(oldRectangle);
            oldRectangle = null;

        } else {
            oldRectangle = getBounds();
            Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
            setBounds(0, 0, d.width, d.height);
        }
        repaint();
    }

    public BufferedImage getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(BufferedImage backgroundImage) {

        this.backgroundImage = backgroundImage;

    }

    public void setLabelText(String text) {
        titleLabel.setText(text);
    }

    public JLabel getTitleLabel() {
        return titleLabel;
    }

    public void setTitleLabel(JLabel titleLabel) {
        this.titleLabel = titleLabel;
    }

    public void setLabelIcon(BufferedImage icon) {
        if (icon != null) {
            titleLabel.setIcon(new ImageIcon(ImageUtil.scale(icon, 26, 26)));
        }
    }


    public double getSide() {
        return side;
    }

    public void setSide(double side) {
        this.side = side;
    }

    public JPanel getClientPanel() {
        return clientPanel;
    }

    public AlphaButton getHelpButton() {
        return helpButton;
    }

    public void setHelpButton(AlphaButton helpButton) {
        this.helpButton = helpButton;
    }

    public AlphaButton getMinButton() {
        return minButton;
    }

    public void setMinButton(AlphaButton minButton) {
        this.minButton = minButton;
    }

    public AlphaButton getMaxButton() {
        return maxButton;
    }

    public void setMaxButton(AlphaButton maxButton) {
        this.maxButton = maxButton;
    }

    public AlphaButton getCloseButton() {
        return closeButton;
    }

    public void setCloseButton(AlphaButton closeButton) {
        this.closeButton = closeButton;
    }

    public void setTitleColor(Color color) {
        titleLabel.setForeground(color);

    }

    //界面鼠标监听事件
    private class MouseInputHandler implements MouseInputListener {
        private Rectangle r;
        int chx = 0;
        int chy = 0;

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2) {
                maxButtonClick();
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {


        }

        @Override
        public void mouseExited(MouseEvent e) {
        }

        //鼠标按下
        @Override
        public void mousePressed(MouseEvent e) {
            //记录按下时鼠标的坐标位置
            origin.x = e.getX();
            origin.y = e.getY();
        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        //鼠标拖拽
        @Override
        public void mouseDragged(MouseEvent e) {
            final Cursor cursor = frame.getCursor();//获取光标显示方式
            final Point point = e.getPoint();//获取鼠标当前坐标
            //获取界面显示区域
            r = frame.getBounds();
            //初始化偏移量
            chx = 0;
            chy = 0;
            //判断光标样式
            switch (cursor.getType()) {
                case Cursor.SE_RESIZE_CURSOR://左上角
                    chx = point.x - origin.x;
                    chy = point.y - origin.y;
                    r.x += chx;
                    r.y += chy;
                    r.width -= chx;
                    r.height -= chy;
                    break;
                case Cursor.SW_RESIZE_CURSOR://右上角
                    chx = point.x - origin.x;
                    chy = point.y - origin.y;
                    origin.x = point.x;
                    origin.y = point.y;
                    r.y += chy;
                    r.width += chx;
                    r.height -= chy;
                    break;
                case Cursor.NE_RESIZE_CURSOR://左下角
                    chx = point.x - origin.x;
                    chy = point.y - origin.y;
                    origin.x = point.x;
                    origin.y = point.y;
                    r.x += chx;
                    r.width -= chx;
                    r.height += chy;
                    break;
                case Cursor.NW_RESIZE_CURSOR://右下角

                    chx = point.x - origin.x;
                    chy = point.y - origin.y;
                    origin.x = point.x;
                    origin.y = point.y;
                    if ((r.width + chx) > frame.getMinimumSize().getWidth()//下边界拉伸高度只能大于最小高度
                            && (r.height + chy) > frame.getMinimumSize()//下边界拉伸高度只能大于最小高度
                            .getHeight()) {
                        r.width += chx;
                        r.height += chy;
                    }
                    break;
                case Cursor.E_RESIZE_CURSOR://左边界

                    chx = point.x - origin.x;
                    r.x += chx;
                    r.width -= chx;
                    break;
                case Cursor.W_RESIZE_CURSOR://右边界

                    chx = point.x - origin.x;
                    origin.x = point.x;
                    if ((r.width + chx) > frame.getMinimumSize().getWidth())//下边界拉伸高度只能大于最小宽度
                    {
                        r.width += chx;
                    }
                    break;
                case Cursor.N_RESIZE_CURSOR://上边界

                    chy = origin.y - point.y;
                    r.y -= chy;
                    r.width += chx;
                    r.height += chy;
                    break;
                case Cursor.S_RESIZE_CURSOR://下边界
                    chy = point.y - origin.y;
                    origin.y = point.y;
                    if ((r.height + chy) > frame.getMinimumSize().getHeight())//下边界拉伸高度只能大于最小高度
                    {
                        r.height += chy;
                    }
                    break;
                case Cursor.DEFAULT_CURSOR://默认光标样式
                    r.x += point.x - origin.x;
                    r.y += point.y - origin.y;
                    break;
            }
            //重新设定可见区域
            SwingUtil.setVisibleRegion(frame, r.width, r.height, side);
            //重新设定界面显示区域
            setBounds(r.x, r.y, r.width, r.height);
            clientPanel.repaint();
            repaint();


        }

        @Override
        public void mouseMoved(MouseEvent e) {
            Point p = e.getPoint();//获取当前坐标
            Point framePoint = new Point(0, 0);//设定界面起点坐标
            Dimension dim = frame.getSize();//获取界面大小
            Rectangle seRect = new Rectangle(0, 0, 3, 3);//设定左上角拖动区域
            Rectangle swRect = new Rectangle(dim.width - 5, 0, dim.width, 3);//设定右上角拖动区域
            Rectangle neRect = new Rectangle(0, dim.height - 5, 5, dim.height);//设定左下角拖动区域
            Rectangle nwRect = new Rectangle(dim.width - 5, dim.height - 5, dim.width, dim.height);//设定右下角拖动区域

            if (seRect.contains(p)) {//判断光标位置是否在左上角拖动区域
                setCursor(new Cursor(Cursor.SE_RESIZE_CURSOR));
            } else if (swRect.contains(p)) {//判断光标位置是否在右上角拖动区域
                setCursor(new Cursor(Cursor.SW_RESIZE_CURSOR));
            } else if (neRect.contains(p)) {//判断光标位置是否在左下角拖动区域
                setCursor(new Cursor(Cursor.NE_RESIZE_CURSOR));
            } else if (nwRect.contains(p)) {//判断光标位置是否在右下角拖动区域
                setCursor(new Cursor(Cursor.NW_RESIZE_CURSOR));
            } else if (p.x == framePoint.x) {//判断光标位置是否在左边界
                setCursor(new Cursor(Cursor.E_RESIZE_CURSOR));
            } else if (p.x == (dim.width - 1)) {//判断光标位置是否在右边界
                setCursor(new Cursor(Cursor.W_RESIZE_CURSOR));
            } else if (p.y == framePoint.y) {//判断光标位置是否在上边界
                setCursor(new Cursor(Cursor.N_RESIZE_CURSOR));
            } else if (p.y == (dim.height - 1)) {//判断光标位置是否在下边界
                setCursor(new Cursor(Cursor.S_RESIZE_CURSOR));
            } else {//判断光标位置是否在窗口中
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }

        }


    }


}