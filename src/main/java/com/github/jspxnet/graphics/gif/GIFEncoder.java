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

/*
 * @(#)GIFEncoder.java    0.90 4/21/96 Adam Doppelt
 */

import java.io.*;
import java.awt.*;
import java.awt.image.*;

/**
 * GIFEncoder is a class which takes an image and saves it transfer a stream
 * using the GIF file format (<A
 * HREF="http://www.dcs.ed.ac.uk/%7Emxr/gfx/">Graphics Interchange
 * Format</A>). A GIFEncoder
 * is constructed with either an AWT Image (which must be fully
 * loaded) or a set of RGB arrays. The image can be written out with a
 * roc transfer [code]Write } .
 * <p>
 * Three caveats:
 * <UL>
 * <LI>GIFEncoder will convert the image transfer indexed color upon
 * construction. This will take some time, depending on the size of
 * the image. Also, actually writing the image out (Write) will take
 * time.
 *
 * <LI>The image cannot have more than 256 colors, since GIF is an 8
 * bit format. For a 24 bit transfer 8 bit quantization algorithm, see
 * Graphics Gems II III.2 by Xialoin Wu. Or check out his <A
 * HREF="http://www.csd.uwo.ca/faculty/wu/cq.c">C source</A>.
 *
 * <LI>Since the image must be completely loaded into memory,
 * GIFEncoder may have problems with large images. Attempting transfer
 * encode an image which will not fit into memory will probably
 * result in the following exception:
 * [code]java.awt.AWTException: Grabber returned false: 192 [/code]
 * </UL>
 * <p>
 * GIFEncoder is based upon gifsave.c, which was written and released
 * by:
 * <CENTER>
 * Sverre H. Huseby<BR>
 * Bjoelsengt. 17<BR>
 * N-0468 Oslo<BR>
 * Norway
 * <p>
 * Phone: +47 2 230539<BR>
 * sverrehu@ifi.uio.no
 * </CENTER>
 *
 * @author <A HREF="http://www.cs.brown.edu/people/amd/">Adam Doppelt</A>
 * @version 0.90 21 Apr 1996
 */
public class GIFEncoder {
    short width_, height_;
    int numColors_;
    byte[] pixels_;
    byte[] colors_;

    ScreenDescriptor sd_;
    ImageDescriptor id_;

    /**
     * Construct a GIFEncoder. The constructor will convert the image transfer
     * an indexed color array. [b]This may take some time.[/B]
     *
     * @param image The image transfer encode. The image [b]must [/b]be
     *              completely loaded.
     * @throws AWTException Will be thrown if the pixel grab fails. This
     *                      can happen if Java runs out of memory. It may also indicate that the image
     *                      contains more than 256 colors.
     */
    public GIFEncoder(Image image) throws AWTException {
        width_ = (short) image.getWidth(null);
        height_ = (short) image.getHeight(null);

        int[] values = new int[width_ * height_];
        PixelGrabber grabber = new PixelGrabber(
                image, 0, 0, width_, height_, values, 0, width_);

        try {
            if (!grabber.grabPixels()) {
                throw new AWTException("Grabber returned false: " +
                        grabber.status());
            }
        } catch (InterruptedException e) {
            //
        }

        byte[][] r = new byte[width_][height_];
        byte[][] g = new byte[width_][height_];
        byte[][] b = new byte[width_][height_];
        int index = 0;
        for (int y = 0; y < height_; ++y) {
            for (int x = 0; x < width_; ++x) {
                r[x][y] = (byte) ((values[index] >> 16) & 0xFF);
                g[x][y] = (byte) ((values[index] >> 8) & 0xFF);
                b[x][y] = (byte) ((values[index]) & 0xFF);
                ++index;
            }
        }
        ToIndexedColor(r, g, b);
    }

    /**
     * Construct a GIFEncoder. The constructor will convert the image transfer
     * an indexed color array. [b]This may take some time.[/B]
     * <p>
     * Each array stores intensity values for the image. In other words,
     * r[x][y] refers transfer the red intensity of the pixel at column x, row
     * y.
     *
     * @param r An array containing the red intensity values.
     * @param g An array containing the green intensity values.
     * @param b An array containing the blue intensity values.
     * @throws AWTException Will be thrown if the image contains more than
     *                      256 colors.
     */
    public GIFEncoder(byte[][] r, byte[][] g, byte[][] b) throws AWTException {
        width_ = (short) (r.length);
        height_ = (short) (r[0].length);

        ToIndexedColor(r, g, b);
    }

    /**
     * Writes the image out transfer a stream in the GIF file format. This will
     * be a single GIF87a image, non-interlaced, with no background color.
     * [b]This may take some time.[/B]
     *
     * @param output The stream transfer output transfer. This should probably be a
     *               buffered stream.
     * @throws IOException Will be thrown if a write operation fails.
     */
    public void Write(OutputStream output) throws IOException {
        BitUtils.WriteString(output, "GIF87a");

        ScreenDescriptor sd = new ScreenDescriptor(width_, height_,
                numColors_);
        sd.Write(output);

        output.write(colors_, 0, colors_.length);

        ImageDescriptor id = new ImageDescriptor(width_, height_, ',');
        id.Write(output);

        byte codesize = BitUtils.BitsNeeded(numColors_);
        if (codesize == 1) {
            ++codesize;
        }
        output.write(codesize);

        LZWCompressor.LZWCompress(output, codesize, pixels_);
        output.write(0);

        id = new ImageDescriptor((byte) 0, (byte) 0, ';');
        id.Write(output);
        output.flush();
    }

    void ToIndexedColor(byte[][] r, byte[][] g,
                        byte[][] b) throws AWTException {
        pixels_ = new byte[width_ * height_];
        colors_ = new byte[256 * 3];
        int colornum = 0;
        for (int x = 0; x < width_; ++x) {
            for (int y = 0; y < height_; ++y) {
                int search;
                for (search = 0; search < colornum; ++search) {
                    if (colors_[search * 3] == r[x][y] &&
                            colors_[search * 3 + 1] == g[x][y] &&
                            colors_[search * 3 + 2] == b[x][y]) {
                        break;
                    }
                }

                if (search > 255) {
                    throw new AWTException("Too many colors.");
                }

                pixels_[y * width_ + x] = (byte) search;

                if (search == colornum) {
                    colors_[search * 3] = r[x][y];
                    colors_[search * 3 + 1] = g[x][y];
                    colors_[search * 3 + 2] = b[x][y];
                    ++colornum;
                }
            }
        }
        numColors_ = 1 << BitUtils.BitsNeeded(colornum);
        byte[] copy = new byte[numColors_ * 3];
        System.arraycopy(colors_, 0, copy, 0, numColors_ * 3);
        colors_ = copy;
    }

}

class BitFile {
    OutputStream output_;
    byte[] buffer_;
    int index_, bitsLeft_;

    public BitFile(OutputStream output) {
        output_ = output;
        buffer_ = new byte[256];
        index_ = 0;
        bitsLeft_ = 8;
    }

    public void Flush() throws IOException {
        int numBytes = index_ + (bitsLeft_ == 8 ? 0 : 1);
        if (numBytes > 0) {
            output_.write(numBytes);
            output_.write(buffer_, 0, numBytes);
            buffer_[0] = 0;
            index_ = 0;
            bitsLeft_ = 8;
        }
    }

    public void WriteBits(int bits, int numbits) throws IOException {
        int bitsWritten = 0;
        int numBytes = 255;
        do {
            if ((index_ == 254 && bitsLeft_ == 0) || index_ > 254) {
                output_.write(numBytes);
                output_.write(buffer_, 0, numBytes);

                buffer_[0] = 0;
                index_ = 0;
                bitsLeft_ = 8;
            }

            if (numbits <= bitsLeft_) {
                buffer_[index_] |= (bits & ((1 << numbits) - 1)) <<
                        (8 - bitsLeft_);
                bitsWritten += numbits;
                bitsLeft_ -= numbits;
                numbits = 0;
            } else {
                buffer_[index_] |= (bits & ((1 << bitsLeft_) - 1)) <<
                        (8 - bitsLeft_);
                bitsWritten += bitsLeft_;
                bits >>= bitsLeft_;
                numbits -= bitsLeft_;
                buffer_[++index_] = 0;
                bitsLeft_ = 8;
            }
        } while (numbits != 0);
    }
}

class LZWStringTable {
    private final static int RES_CODES = 2;
    private final static short HASH_FREE = (short) 0xFFFF;
    private final static short NEXT_FIRST = (short) 0xFFFF;
    private final static int MAXBITS = 12;
    private final static int MAXSTR = (1 << MAXBITS);
    private final static short HASHSIZE = 9973;
    private final static short HASHSTEP = 2039;

    byte[] strChr_;
    short[] strNxt_;
    short[] strHsh_;
    short numStrings_;

    public LZWStringTable() {
        strChr_ = new byte[MAXSTR];
        strNxt_ = new short[MAXSTR];
        strHsh_ = new short[HASHSIZE];
    }

    public int AddCharString(short index, byte b) {
        int hshidx;

        if (numStrings_ >= MAXSTR) {
            return 0xFFFF;
        }

        hshidx = Hash(index, b);
        while (strHsh_[hshidx] != HASH_FREE) {
            hshidx = (hshidx + HASHSTEP) % HASHSIZE;
        }

        strHsh_[hshidx] = numStrings_;
        strChr_[numStrings_] = b;
        strNxt_[numStrings_] = (index != HASH_FREE) ? index : NEXT_FIRST;

        return numStrings_++;
    }

    public short FindCharString(short index, byte b) {
        int hshidx, nxtidx;

        if (index == HASH_FREE) {
            return b;
        }

        hshidx = Hash(index, b);
        while ((nxtidx = strHsh_[hshidx]) != HASH_FREE) {
            if (strNxt_[nxtidx] == index && strChr_[nxtidx] == b) {
                return (short) nxtidx;
            }
            hshidx = (hshidx + HASHSTEP) % HASHSIZE;
        }

        return (short) 0xFFFF;
    }

    public void ClearTable(int codesize) {
        numStrings_ = 0;

        for (int q = 0; q < HASHSIZE; q++) {
            strHsh_[q] = HASH_FREE;
        }

        int w = (1 << codesize) + RES_CODES;
        for (int q = 0; q < w; q++) {
            AddCharString((short) 0xFFFF, (byte) q);
        }
    }

    static public int Hash(short index, byte lastbyte) {
        return (((short) (lastbyte << 8) ^ index) & 0xFFFF) % HASHSIZE;
    }
}

class LZWCompressor {

    public static void LZWCompress(OutputStream output, int codesize,
                                   byte[] toCompress) throws IOException {
        byte c;
        short index;
        int clearcode, endofinfo, numbits, limit;
        short prefix = (short) 0xFFFF;

        BitFile bitFile = new BitFile(output);
        LZWStringTable strings = new LZWStringTable();

        clearcode = 1 << codesize;
        endofinfo = clearcode + 1;

        numbits = codesize + 1;
        limit = (1 << numbits) - 1;

        strings.ClearTable(codesize);
        bitFile.WriteBits(clearcode, numbits);

        for (byte toCompres : toCompress) {
            c = toCompres;
            if ((index = strings.FindCharString(prefix, c)) != -1) {
                prefix = index;
            } else {
                bitFile.WriteBits(prefix, numbits);
                if (strings.AddCharString(prefix, c) > limit) {
                    if (++numbits > 12) {
                        bitFile.WriteBits(clearcode, numbits - 1);
                        strings.ClearTable(codesize);
                        numbits = codesize + 1;
                    }
                    limit = (1 << numbits) - 1;
                }

                prefix = (short) ((short) c & 0xFF);
            }
        }

        if (prefix != -1) {
            bitFile.WriteBits(prefix, numbits);
        }

        bitFile.WriteBits(endofinfo, numbits);
        bitFile.Flush();
    }
}

class ScreenDescriptor {
    public short localScreenWidth_, localScreenHeight_;
    private byte byte_;
    public byte backgroundColorIndex_, pixelAspectRatio_;

    public ScreenDescriptor(short width, short height, int numColors) {
        localScreenWidth_ = width;
        localScreenHeight_ = height;
        SetGlobalColorTableSize((byte) (BitUtils.BitsNeeded(numColors) - 1));
        SetGlobalColorTableFlag((byte) 1);
        SetSortFlag((byte) 0);
        SetColorResolution((byte) 7);
        backgroundColorIndex_ = 0;
        pixelAspectRatio_ = 0;
    }

    public void Write(OutputStream output) throws IOException {
        BitUtils.WriteWord(output, localScreenWidth_);
        BitUtils.WriteWord(output, localScreenHeight_);
        output.write(byte_);
        output.write(backgroundColorIndex_);
        output.write(pixelAspectRatio_);
    }

    public void SetGlobalColorTableSize(byte num) {
        byte_ |= (num & 7);
    }

    public void SetSortFlag(byte num) {
        byte_ |= (num & 1) << 3;
    }

    public void SetColorResolution(byte num) {
        byte_ |= (num & 7) << 4;
    }

    public void SetGlobalColorTableFlag(byte num) {
        byte_ |= (num & 1) << 7;
    }
}

class ImageDescriptor {
    public byte separator_;
    public short leftPosition_, topPosition_, width_, height_;
    private byte byte_;

    public ImageDescriptor(short width, short height, char separator) {
        separator_ = (byte) separator;
        leftPosition_ = 0;
        topPosition_ = 0;
        width_ = width;
        height_ = height;
        SetLocalColorTableSize((byte) 0);
        SetReserved((byte) 0);
        SetSortFlag((byte) 0);
        SetInterlaceFlag((byte) 0);
        SetLocalColorTableFlag((byte) 0);
    }

    public void Write(OutputStream output) throws IOException {
        output.write(separator_);
        BitUtils.WriteWord(output, leftPosition_);
        BitUtils.WriteWord(output, topPosition_);
        BitUtils.WriteWord(output, width_);
        BitUtils.WriteWord(output, height_);
        output.write(byte_);
    }

    public void SetLocalColorTableSize(byte num) {
        byte_ |= (num & 7);
    }

    public void SetReserved(byte num) {
        byte_ |= (num & 3) << 3;
    }

    public void SetSortFlag(byte num) {
        byte_ |= (num & 1) << 5;
    }

    public void SetInterlaceFlag(byte num) {
        byte_ |= (num & 1) << 6;
    }

    public void SetLocalColorTableFlag(byte num) {
        byte_ |= (num & 1) << 7;
    }
}

class BitUtils {
    public static byte BitsNeeded(int n) {
        byte ret = 1;

        if (n-- == 0) {
            return 0;
        }

        while ((n >>= 1) != 0) {
            ++ret;
        }

        return ret;
    }

    public static void WriteWord(OutputStream output,
                                 short w) throws IOException {
        output.write(w & 0xFF);
        output.write((w >> 8) & 0xFF);
    }

    static void WriteString(OutputStream output,
                            String string) throws IOException {
        for (int loop = 0; loop < string.length(); ++loop) {
            output.write((byte) (string.charAt(loop)));
        }
    }
}