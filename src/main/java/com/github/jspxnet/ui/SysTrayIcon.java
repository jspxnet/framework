/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.ui;

import com.github.jspxnet.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.jspxnet.ui.menu.PhotoPopupMenu;
import com.github.jspxnet.utils.SwingUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.UnsupportedEncodingException;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 11-5-18
 * Time: 下午4:27
 * 系统托盘图标实现 必须jdk1.5以上版本才支持,简单封装显示，演示了用法
 */
public class SysTrayIcon extends TrayIcon {
    final static private Logger log = LoggerFactory.getLogger(SysTrayIcon.class);
    //PopupMenu 只支持GBK编码才能显示中文 JPopupMenu可以显示UTF-8
    private JPopupMenu popupMenu = null;
    private boolean show = false;
    private JDialog frame = new JDialog();

    public SysTrayIcon(Image image) {
        super(image, StringUtil.empty, null);
    }

    public SysTrayIcon(Image image, String tooltip, JPopupMenu popupMenu) {
        super(image, tooltip, null);
        this.popupMenu = popupMenu;
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(d.width, d.height);
        frame.setBounds(0, 0, 0, 0);
        frame.setUndecorated(true);
        SwingUtil.setVisibleRegion(frame, 0, 0, 0);
        frame.setVisible(true);
    }

    public SysTrayIcon(Image image, String tooltip, PopupMenu popupMenu) {
        super(image, tooltip, popupMenu);
    }

    public void show() {
        if (show) {
            return;
        }
        if (!SystemTray.isSupported()) {

            log.error("Tray unavailable,系统不支持,请使用JDK1.6以上版本");
            return;
        }
        SystemTray tray = SystemTray.getSystemTray();
        try {
            if (popupMenu != null) {
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (e.getButton() == 3) {
                            //鼠标右键

                            if (!popupMenu.isShowing()) {
                                popupMenu.show(frame, e.getX(), e.getY() - popupMenu.getHeight() - 10);

                            }

                        }
                    }
                });
            }

            tray.add(this);
        } catch (AWTException e) {
            log.error("System Can't add transfer tray,托盘图标添加失败.");
        }
        show = true;
    }


    public static void main(String[] args) throws UnsupportedEncodingException {
        //System.setProperty("file.encoding","GBK");
        Font f = new Font("宋体", Font.PLAIN, 12);
        SwingUtil.updateUIFont(f);
        //  System.out.println("file.encoding=" + System.getProperty("file.encoding"));
        Image image = Toolkit.getDefaultToolkit().getImage(SysTrayIcon.class.getResource("icon/jspx16.png"));
        JPopupMenu popup = new PhotoPopupMenu();
        popup.setForeground(Color.WHITE);
        popup.setBackground(Color.WHITE);


        //DefaultSingleSelectionModel dssm = new DefaultSingleSelectionModel();
        //dssm.setSelectedIndex(1);
        //popup.setSelectionModel(dssm);
        //   popup.setBorderPainted(false);
        //   popup.setLightWeightPopupEnabled(false);


        final SysTrayIcon tray = new SysTrayIcon(image, "我的应用", popup);
        tray.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == 1) {
                    //鼠标左建

                }
            }
        });

        JMenuItem item = new JMenuItem();


        item = new JMenuItem();
        item.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(SysTrayIcon.class.getResource("icon/close.png"))));
        item.setDisabledIcon(new ImageIcon(image));
        item.setRolloverIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(SysTrayIcon.class.getResource("icon/no.png"))));
        item.setPressedIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(SysTrayIcon.class.getResource("icon/yes.png"))));
        item.setText("测试弹出菜单");
        item.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == 1) {
                    //鼠标左建

                    JOptionPane.showMessageDialog(null, "点击 None");
                }
            }
        });
        item.setOpaque(false);
        popup.add(item);

        /*
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tray.displayMessage("None Title", "None 按钮显示信息", TrayIcon.MessageType.NONE);  //显示提示信息
                tray.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {

                        JOptionPane.showMessageDialog(null, "点击 None");
                        //item.setLabel("没有");
                        //tray.ShowMessage("None Title", "None", TrayIcon.MessageType.NONE);
                    }
                });
            }
        });
        */

        JMenuItem item2 = new JMenuItem();
        item2 = new JMenuItem("图标2");
        item2.setIcon(new ImageIcon(image));
        item2.setOpaque(false);
        item2.setBackground(Color.BLUE);
        popup.add(item2);


        JMenuItem item3 = new JMenuItem();
        item3 = new JMenuItem("图标3");
        item3.setIcon(new ImageIcon(image));
        item3.setOpaque(false);
        popup.add(item3);

        item = new JMenuItem();

        item.setText("退出"); //中文有乱码 必须使用 -Dfile.encoding=GBK
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(1);
            }
        });
        popup.add(item);

        //双击后显示的窗口 begin
        //   final JFrame menuForm = new JFrame();  //主窗口
        //SwingUtil.setFont(menuForm);
        /*
        tray.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { // 鼠标双击显示主窗口
                    //   tray.remove(trayIcon); // 从系统的托盘实例中移除托盘图标
                    if (menuForm != null && !menuForm.isVisible()) {
                        menuForm.pack();
                        menuForm.setVisible(true); // 显示窗口
                    }
                }
            }
        });
        */


        //双击后显示的窗口 end

        tray.show();
    }
}