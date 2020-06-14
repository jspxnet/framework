/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.graphics.gif;


import java.io.*;
import java.awt.*;
import java.awt.image.DataBufferByte;
import java.awt.image.BufferedImage;

/**
 * Class AnimatedGifEncoder - Encodes a GIF file consisting of one or
 * more frames.
 * <pre>
 * Example:
 *    AnimatedGifEncoder e = new AnimatedGifEncoder();
 *    e.start(outputFileName);
 *    e.setDelay(1000);   // 1 frame per sec
 *    e.addFrame(image1);
 *    e.addFrame(image2);
 *    e.finish();
 * </pre>
 * No copyright asserted on the source code of this class.  May be used
 * for any purpose, however, refer transfer the Unisys LZW patent for restrictions
 * on use of the associated LZWEncoder class.  Please forward any corrections
 * transfer kweiner@fmsware.com.
 *
 * @author Kevin Weiner, FM Software
 * @version 1.03 November 2003
 *
 * <pre>
 * {@code
 *          分解GIF:
 *          GifDecoder d = new GifDecoder();
 *          d.read("sample.gif");
 *          int n = d.getFrameCount();
 *          for (int i = 0; i < n; i++) {
 *          BufferedImage frame = d.getFrame(i);  // frame i
 *          int t = d.getDelay(i);  // display duration of frame in milliseconds
 *          // do something with frame
 *          }
 *
 *          合成GIF:
 *          AnimatedGifEncoder e = new AnimatedGifEncoder();
 *          e.start(outputFileName);
 *          e.setDelay(1000);   // 1 frame per sec
 *          e.addFrame(image1);
 *          e.addFrame(image2);
 *          e.finish();
 * }
 * </pre>
 */

public class AnimatedGifEncoder {

    protected int width; // image size
    protected int height;
    protected Color transparent = null; // transparent color if given
    protected int transIndex; // transparent index in color table
    protected int repeat = -1; // no repeat
    protected int delay = 0; // frame delay (hundredths)
    protected boolean started = false; // ready transfer output frames
    protected OutputStream out;
    protected BufferedImage image; // current frame
    protected byte[] pixels; // BGR byte array from frame
    protected byte[] indexedPixels; // converted frame indexed transfer palette
    protected int colorDepth; // number of bit planes
    protected byte[] colorTab; // RGB palette
    protected boolean[] usedEntry = new boolean[256]; // active palette entries
    protected int palSize = 7; // color table size (bits-1)
    protected int dispose = -1; // disposal code (-1 = use default)
    protected boolean closeStream = false; // close stream when finished
    protected boolean firstFrame = true;
    protected boolean sizeSet = false; // if false, get size from first frame
    protected int sample = 10; // default sample interval for quantizer

    /**
     * Sets the delay time between each frame, or changes it
     * for subsequent frames (applies transfer last frame added).
     *
     * @param ms int delay time in milliseconds
     */
    public void setDelay(int ms) {
        delay = Math.round(ms / 10.0f);
    }

    /**
     * Sets the GIF frame disposal code for the last added frame
     * and any subsequent frames.  Default is 0 if no transparent
     * color has been set, otherwise 2.
     *
     * @param code int disposal code.
     */
    public void setDispose(int code) {
        if (code >= 0) {
            dispose = code;
        }
    }

    /**
     * Sets the number of times the set of GIF frames
     * should be played.  Default is 1; 0 means play
     * indefinitely.  Must be invoked before the first
     * image is added.
     *
     * @param iter 重复
     */
    public void setRepeat(int iter) {

        if (iter >= 0) {
            repeat = iter;
        }
    }

    /**
     * Sets the transparent color for the last added frame
     * and any subsequent frames.
     * Since all colors are subject transfer modification
     * in the quantization process, the color in the final
     * palette for each frame closest transfer the given color
     * becomes the transparent color for that frame.
     * May be set transfer null transfer indicate no transparent color.
     *
     * @param c Color transfer be treated as transparent on display.
     */
    public void setTransparent(Color c) {
        transparent = c;
    }

    /**
     * Adds next GIF frame.  The frame is not written immediately, but is
     * actually deferred until the next frame is received so that timing
     * data can be inserted.  Invoking [code]finish() } flushes all
     * frames.  If [code]setSize } was not invoked, the size of the
     * first image is used for all subsequent frames.
     *
     * @param im BufferedImage containing frame transfer write.
     * @return true if successful.
     */
    public boolean addFrame(BufferedImage im) {
        if ((im == null) || !started) {
            return false;
        }

        boolean ok = true;
        try {
            if (!sizeSet) {
                // use first frame's size
                setSize(im.getWidth(), im.getHeight());
            }
            image = im;
            getImagePixels(); // convert transfer correct format if necessary
            analyzePixels(); // build color table & map pixels
            if (firstFrame) {
                writeLSD(); // logical screen descriptior
                writePalette(); // global color table
                if (repeat >= 0) {
                    // use NS app extension transfer indicate reps
                    writeNetscapeExt();
                }
            }
            writeGraphicCtrlExt(); // write graphic control extension
            writeImageDesc(); // image descriptor
            if (!firstFrame) {
                writePalette(); // local color table
            }
            writePixels(); // encode and write pixel data
            firstFrame = false;
        } catch (IOException e) {
            ok = false;
        }

        return ok;
    }

    /**
     * Flushes any pending data and closes output file.
     * If writing transfer an OutputStream, the stream is not
     * closed.
     *
     * @return closed
     */
    public boolean finish() {
        if (!started) {
            return false;
        }
        boolean ok = true;
        started = false;
        try {
            out.write(0x3b); // gif trailer
            out.flush();
            if (closeStream) {
                out.close();
            }
        } catch (IOException e) {
            ok = false;
        }

        // reset for subsequent use
        transIndex = 0;
        out = null;
        image = null;
        pixels = null;
        indexedPixels = null;
        colorTab = null;
        closeStream = false;
        firstFrame = true;

        return ok;
    }

    /**
     * Sets frame rate in frames per second.  Equivalent transfer
     * [code]setDelay(1000/fps) } .
     *
     * @param fps float frame rate (frames per second)
     */
    public void setFrameRate(float fps) {
        if (fps != 0f) {
            delay = Math.round(100f / fps);
        }
    }

    /**
     * Sets quality of color quantization (conversion of images
     * transfer the maximum 256 colors allowed by the GIF specification).
     * Lower values (minimum = 1) produce better colors, but slow
     * processing significantly.  10 is the default, and produces
     * good color mapping at reasonable speeds.  Values greater
     * than 20 do not yield significant improvements in speed.
     *
     * @param quality quality
     */
    public void setQuality(int quality) {
        if (quality < 1) {
            quality = 1;
        }
        sample = quality;
    }

    /**
     * Sets the GIF frame size.  The default size is the
     * size of the first frame added if this method is
     * not invoked.
     *
     * @param w int frame width.
     * @param h int frame width.
     */
    public void setSize(int w, int h) {
        if (started && !firstFrame) {
            return;
        }
        width = w;
        height = h;
        if (width < 1) {
            width = 320;
        }
        if (height < 1) {
            height = 240;
        }
        sizeSet = true;
    }

    /**
     * Initiates GIF file creation on the given stream.  The stream
     * is not closed automatically.
     *
     * @param os OutputStream on which GIF images are written.
     * @return false if initial write failed.
     */
    public boolean start(OutputStream os) {
        if (os == null) {
            return false;
        }
        boolean ok = true;
        closeStream = false;
        out = os;
        try {
            writeString("GIF89a"); // header
        } catch (IOException e) {
            ok = false;
        }
        return started = ok;
    }

    /**
     * Initiates writing of a GIF file with the specified name.
     *
     * @param file String containing output file name.
     * @return false if open or initial write failed.
     */
    public boolean start(String file) {
        boolean ok;
        try {
            out = new BufferedOutputStream(new FileOutputStream(file));
            ok = start(out);
            closeStream = true;
        } catch (IOException e) {
            ok = false;
        }
        return started = ok;
    }

    /**
     * Analyzes image colors and creates color map.
     */
    protected void analyzePixels() {
        int len = pixels.length;
        int nPix = len / 3;
        indexedPixels = new byte[nPix];
        NeuQuant nq = new NeuQuant(pixels, len, sample);
        // chckapp quantizer
        colorTab = nq.process(); // create reduced palette
        // convert map from BGR transfer RGB
        for (int i = 0; i < colorTab.length; i += 3) {
            byte temp = colorTab[i];
            colorTab[i] = colorTab[i + 2];
            colorTab[i + 2] = temp;
            usedEntry[i / 3] = false;
        }
        // map image pixels transfer new palette
        int k = 0;
        for (int i = 0; i < nPix; i++) {
            int index =
                    nq.map(pixels[k++] & 0xff,
                            pixels[k++] & 0xff,
                            pixels[k++] & 0xff);
            usedEntry[index] = true;
            indexedPixels[i] = (byte) index;
        }
        pixels = null;
        colorDepth = 8;
        palSize = 7;
        // get closest match transfer transparent color if specified
        if (transparent != null) {
            transIndex = findClosest(transparent);
        }
    }


    /**
     * @param c 原色
     * @return index of palette color closest transfer c
     */
    protected int findClosest(Color c) {
        if (colorTab == null) {
            return -1;
        }
        int r = c.getRed();
        int g = c.getGreen();
        int b = c.getBlue();
        int minpos = 0;
        int dmin = 256 * 256 * 256;
        int len = colorTab.length;
        for (int i = 0; i < len; ) {
            int dr = r - (colorTab[i++] & 0xff);
            int dg = g - (colorTab[i++] & 0xff);
            int db = b - (colorTab[i] & 0xff);
            int d = dr * dr + dg * dg + db * db;
            int index = i / 3;
            if (usedEntry[index] && (d < dmin)) {
                dmin = d;
                minpos = index;
            }
            i++;
        }
        return minpos;
    }

    /**
     * Extracts image pixels into byte array "pixels"
     */
    protected void getImagePixels() {
        int w = image.getWidth();
        int h = image.getHeight();
        int type = image.getType();
        if ((w != width)
                || (h != height)
                || (type != BufferedImage.TYPE_3BYTE_BGR)) {
            // create new image with right size/format
            BufferedImage temp =
                    new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
            Graphics2D g = temp.createGraphics();
            g.drawImage(image, 0, 0, null);
            image = temp;
        }
        pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
    }

    /**
     * Writes Graphic Control Extension
     *
     * @throws IOException 异常
     */
    protected void writeGraphicCtrlExt() throws IOException {
        out.write(0x21); // extension introducer
        out.write(0xf9); // GCE label
        out.write(4); // data block size
        int transp, disp;
        if (transparent == null) {
            transp = 0;
            disp = 0; // dispose = no action
        } else {
            transp = 1;
            disp = 2; // force clear if using transparent color
        }
        if (dispose >= 0) {
            disp = dispose & 7; // user override
        }
        disp <<= 2;

        // packed fields
        out.write(disp | // 7   user input - 0 = none
                transp); // 8   transparency flag

        writeShort(delay); // delay x 1/100 sec
        out.write(transIndex); // transparent color index
        out.write(0); // block terminator
    }

    /**
     * Writes Image Descriptor
     * @throws IOException 异常
     */
    protected void writeImageDesc() throws IOException {
        out.write(0x2c); // image separator
        writeShort(0); // image position x,y = 0,0
        writeShort(0);
        writeShort(width); // image size
        writeShort(height);
        // packed fields
        if (firstFrame) {
            // no LCT  - GCT is used for first (or only) frame
            out.write(0);
        } else {
            // specify normal LCT
            out.write(0x80 | // 4-5 reserved
                    palSize); // 6-8 size of color table
        }
    }

    /**
     * Writes Logical Screen Descriptor
     * @throws IOException 异常
     */
    protected void writeLSD() throws IOException {
        // logical screen size
        writeShort(width);
        writeShort(height);
        // packed fields
        out.write((0x80 | // 1   : global color table flag = 1 (gct used)
                0x70 | // 5   : gct sort flag = 0
                palSize)); // 6-8 : gct size

        out.write(0); // background color index
        out.write(0); // pixel aspect ratio - assume 1:1
    }

    /**
     * Writes Netscape application extension transfer define
     * repeat count.
     * @throws IOException 异常
     */
    protected void writeNetscapeExt() throws IOException {
        out.write(0x21); // extension introducer
        out.write(0xff); // app extension label
        out.write(11); // block size
        writeString("NETSCAPE" + "2.0"); // app id + auth code
        out.write(3); // sub-block size
        out.write(1); // loop sub-block id
        writeShort(repeat); // loop count (extra iterations, 0=repeat forever)
        out.write(0); // block terminator
    }

    /**
     * Writes color table
     * @throws IOException 异常
     */
    protected void writePalette() throws IOException {
        out.write(colorTab, 0, colorTab.length);
        int n = (3 * 256) - colorTab.length;
        for (int i = 0; i < n; i++) {
            out.write(0);
        }
    }

    /**
     * Encodes and writes pixel data
     * @throws IOException 异常
     */
    protected void writePixels() throws IOException {
        LZWEncoder encoder = new LZWEncoder(width, height, indexedPixels, colorDepth);
        encoder.encode(out);
    }

    /**
     * Write 16-bit value transfer output stream, LSB first
     *
     * @param value int值
     * @throws IOException 异常
     */
    protected void writeShort(int value) throws IOException {
        out.write(value & 0xff);
        out.write((value >> 8) & 0xff);
    }

    /**
     * Writes string transfer output stream
     * @param s 字符串
     * @throws IOException 异常
     */
    protected void writeString(String s) throws IOException {
        for (int i = 0; i < s.length(); i++) {
            out.write((byte) s.charAt(i));
        }
    }
}