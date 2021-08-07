/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.graphics.screen;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: chenYuan
 * date: 12-9-4
 * Time: 下午5:59
 * 切屏代码
 */
public class CaptureScreen extends JPanel implements ActionListener, MouseListener, MouseMotionListener {
    //枚举
    enum States {
        NORTH_WEST(new Cursor(Cursor.NW_RESIZE_CURSOR)),//表示西北角
        NORTH(new Cursor(Cursor.N_RESIZE_CURSOR)),
        NORTH_EAST(new Cursor(Cursor.NE_RESIZE_CURSOR)),
        EAST(new Cursor(Cursor.E_RESIZE_CURSOR)),
        SOUTH_EAST(new Cursor(Cursor.SE_RESIZE_CURSOR)),
        SOUTH(new Cursor(Cursor.S_RESIZE_CURSOR)),
        SOUTH_WEST(new Cursor(Cursor.SW_RESIZE_CURSOR)),
        WEST(new Cursor(Cursor.W_RESIZE_CURSOR)),
        MOVE(new Cursor(Cursor.MOVE_CURSOR)),
        DEFAULT(new Cursor(Cursor.DEFAULT_CURSOR));
        private final Cursor cs;

        States(Cursor cs) {
            this.cs = cs;
        }

        public Cursor getCursor() {
            return cs;
        }
    }

    /**
     *
     */
    private JButton start, cancel, save;
    private BufferedImage bi;
    public BufferedImage get;
    private int width, height;
    private int startX, startY, endX, endY, tempX, tempY;
    private JFrame jf;
    private Rectangle select = new Rectangle(0, 0, 0, 0);//表示选中的区域
    private Cursor cs;//表示一般情况下的鼠标状态
    private States current = States.DEFAULT;// 表示当前的编辑状态
    private Rectangle[] rec;//表示八个编辑点的区域


    public static void main(String[] args) {
        new CaptureScreen();
    }

    public CaptureScreen() {
        doStart();
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == start) {
            doStart();
        } else if (ae.getSource() == cancel) {
            //System.exit(0);
        } else if (ae.getSource() == save) {
            doSave();
        }
    }

    private void doStart() {
        try {
            Robot ro = new Robot();
            Toolkit tk = Toolkit.getDefaultToolkit();
            Dimension di = tk.getScreenSize();
            Rectangle rec = new Rectangle(0, 0, di.width, di.height);
            BufferedImage bi = ro.createScreenCapture(rec);
            JFrame jf = new JFrame();
            initRecs();
            jf.getContentPane().add(new CaptureScreen(jf, bi, di.width, di.height));
            jf.setUndecorated(true);
            jf.setSize(di);
            jf.setVisible(true);
            jf.setAlwaysOnTop(true);
        } catch (Exception exe) {
            exe.printStackTrace();
        }
    }

    public CaptureScreen(JFrame jf, BufferedImage bi, int width, int height) {
        this.jf = jf;
        this.bi = bi;
        this.width = width;
        this.height = height;
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        //Image icon = Toolkit.getDefaultToolkit().createImage(this.getClass().getResource("jspx/jspeak/client/ui/res/status/status6.png"));
        //cs = Toolkit.getDefaultToolkit().createCustomCursor(icon, new Point(0, 0), "icon");
        //this.setCursor(cs);
        initRecs();
    }

    private void initRecs() {
        rec = new Rectangle[8];
        for (int i = 0; i < rec.length; i++) {
            rec[i] = new Rectangle();
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        g.drawImage(bi, 0, 0, width, height, this);
        g.setColor(Color.RED);
        g.drawLine(startX, startY, endX, startY);
        g.drawLine(startX, endY, endX, endY);
        g.drawLine(startX, startY, startX, endY);
        g.drawLine(endX, startY, endX, endY);
        int x = startX < endX ? startX : endX;
        int y = startY < endY ? startY : endY;
        select = new Rectangle(x, y, Math.abs(endX - startX), Math.abs(endY - startY));
        int x1 = (startX + endX) / 2;
        int y1 = (startY + endY) / 2;
        g.fillRect(x1 - 2, startY - 2, 5, 5);
        g.fillRect(x1 - 2, endY - 2, 5, 5);
        g.fillRect(startX - 2, y1 - 2, 5, 5);
        g.fillRect(endX - 2, y1 - 2, 5, 5);
        g.fillRect(startX - 2, startY - 2, 5, 5);
        g.fillRect(startX - 2, endY - 2, 5, 5);
        g.fillRect(endX - 2, startY - 2, 5, 5);
        g.fillRect(endX - 2, endY - 2, 5, 5);
        rec[0] = new Rectangle(x - 5, y - 5, 10, 10);
        rec[1] = new Rectangle(x1 - 5, y - 5, 10, 10);
        rec[2] = new Rectangle((startX > endX ? startX : endX) - 5, y - 5, 10, 10);
        rec[3] = new Rectangle((startX > endX ? startX : endX) - 5, y1 - 5, 10, 10);
        rec[4] = new Rectangle((startX > endX ? startX : endX) - 5, (startY > endY ? startY : endY) - 5, 10, 10);
        rec[5] = new Rectangle(x1 - 5, (startY > endY ? startY : endY) - 5, 10, 10);
        rec[6] = new Rectangle(x - 5, (startY > endY ? startY : endY) - 5, 10, 10);
        rec[7] = new Rectangle(x - 5, y1 - 5, 10, 10);
    }

    @Override
    public void mouseMoved(MouseEvent me) {
        if (select.contains(me.getPoint())) {
            this.setCursor(new Cursor(Cursor.MOVE_CURSOR));
            current = States.MOVE;
        } else {
            States[] st = States.values();
            for (int i = 0; i < rec.length; i++) {
                if (rec[i].contains(me.getPoint())) {
                    current = st[i];
                    this.setCursor(st[i].getCursor());
                    return;
                }
            }
            this.setCursor(cs);
            current = States.DEFAULT;
        }
    }

    @Override
    public void mouseExited(MouseEvent me) {

    }

    @Override
    public void mouseEntered(MouseEvent me) {

    }

    @Override
    public void mouseDragged(MouseEvent me) {
        int x = me.getX();
        int y = me.getY();
        if (current == States.MOVE) {
            startX += (x - tempX);
            startY += (y - tempY);
            endX += (x - tempX);
            endY += (y - tempY);
            tempX = x;
            tempY = y;
        } else if (current == States.EAST) {
            if (startX > endX) {
                startX += (x - tempX);
                tempX = x;
            } else {
                endX += (x - tempX);
                tempX = x;
            }
        } else if (current == States.NORTH) {
            if (startY < endY) {
                startY += (y - tempY);
                tempY = y;
            } else {
                endY += (y - tempY);
                tempY = y;
            }
        } else if (current == States.WEST) {
            if (startX < endX) {
                startX += (x - tempX);
                tempX = x;
            } else {
                endX += (x - tempX);
                tempX = x;
            }
        } else if (current == States.SOUTH) {
            if (startY > endY) {
                startY += (y - tempY);
                tempY = y;
            } else {
                endY += (y - tempY);
                tempY = y;
            }
        } else if (current == States.NORTH_EAST) {
            if (startX > endX) {
                startX += (x - tempX);
                tempX = x;
            } else {
                endX += (x - tempX);
                tempX = x;
            }
            if (startY < endY) {
                startY += (y - tempY);
                tempY = y;
            } else {
                endY += (y - tempY);
                tempY = y;
            }
        } else if (current == States.NORTH_WEST) {
            if (startX < endX) {
                startX += (x - tempX);
                tempX = x;
            } else {
                endX += (x - tempX);
                tempX = x;
            }
            if (startY < endY) {
                startY += (y - tempY);
                tempY = y;
            } else {
                endY += (y - tempY);
                tempY = y;
            }
        } else if (current == States.SOUTH_EAST) {
            if (startY > endY) {
                startY += (y - tempY);
                tempY = y;
            } else {
                endY += (y - tempY);
                tempY = y;
            }
            if (startX > endX) {
                startX += (x - tempX);
                tempX = x;
            } else {
                endX += (x - tempX);
                tempX = x;
            }
        } else if (current == States.SOUTH_WEST) {
            if (startY > endY) {
                startY += (y - tempY);
                tempY = y;
            } else {
                endY += (y - tempY);
                tempY = y;
            }
            if (startX < endX) {
                startX += (x - tempX);
                tempX = x;
            } else {
                endX += (x - tempX);
                tempX = x;
            }
        } else {
            startX = tempX;
            startY = tempY;
            endX = me.getX();
            endY = me.getY();
        }
        this.repaint();
    }

    @Override
    public void mousePressed(MouseEvent me) {
        tempX = me.getX();
        tempY = me.getY();
    }

    @Override
    public void mouseReleased(MouseEvent me) {

        if (me.isPopupTrigger()) {
            if (current == States.MOVE) {
                startX = 0;
                startY = 0;
                endX = 0;
                endY = 0;
                repaint();
            } else {
                jf.dispose();
//doSave();

            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent me) {
        if (me.getClickCount() == 2) {
            //Rectangle rec=new Rectangle(startX,startY,Math.abs(endX-startX),Math.abs(endY-startY));
            Point p = me.getPoint();
            if (select.contains(p)) {
                if (select.x + select.width < this.getWidth() && select.y + select.height < this.getHeight()) {
                    get = bi.getSubimage(select.x, select.y, select.width, select.height);
                    jf.dispose();
//save.setEnabled(true);
                    doSave();
                } else {
                    int wid = select.width, het = select.height;
                    if (select.x + select.width >= this.getWidth()) {
                        wid = this.getWidth() - select.x;
                    }
                    if (select.y + select.height >= this.getHeight()) {
                        het = this.getHeight() - select.y;
                    }
                    get = bi.getSubimage(select.x, select.y, wid, het);
                    jf.dispose();
//save.setEnabled(true);
                    doSave();
                }
            }
        }
    }

    private void doSave() throws NullPointerException {
        try {

            File file = new File("d:/temp/123.jpg");//这里我写死了路径和类型jpg
            String about = "jpg";
            String ext = file.toString().toLowerCase();
            System.out.println(ext);

            ImageIO.write(get, about, file);
        } catch (Exception exe) {
            exe.printStackTrace();
        }
    }


}