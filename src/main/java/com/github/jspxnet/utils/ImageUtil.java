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


import com.dd.plist.NSDictionary;
import com.dd.plist.NSObject;
import com.dd.plist.PropertyListParser;
import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.github.jspxnet.io.plist.ParseDictionary;
import com.github.jspxnet.io.plist.Rect;
import com.github.jspxnet.util.StringMap;
import lombok.extern.slf4j.Slf4j;
import com.github.jspxnet.graphics.gif.GIFEncoder;
import com.github.jspxnet.graphics.png.PngEncoder;
import com.github.jspxnet.security.utils.EncryptUtil;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.github.jspxnet.graphics.gif.AnimatedGifEncoder;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.*;
import java.io.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.*;
import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2004-5-24
 * Time: 8:44:16
 * 基本的图片数据出来
 * 0.5
 */

@Slf4j
public final  class ImageUtil {

    public static final String[] EXIF_TAGS = {"Compression Type", "Data Precision", "Image Height", "Image Width",
            "Make", "Model", "Orientation", "X Resolution", "Y Resolution", "Resolution Unit", "Software", "Date/Time", "YCbCr Positioning", "Exposure Time", "F-Number", "ISO Speed Ratings",
            "Exif Version", "Date/Time Original", "Date/Time Digitized", "Components Configuration", "Shutter Speed Value", "Aperture Value", "Brightness Value", "Exposure Bias Value",
            "Max Aperture Value", "Metering Mode", "Flash", "Focal Length", "Makernote", "FlashPix Version", "Color Space", "Exif Image Width", "Exif Image Height", "Exposure Mode",
            "White Balance Mode", "Scene Capture Type", "Unique Image ID", "Interoperability Index", "Interoperability Version", "Image Width", "Image Height", "Compression", "Orientation",
            "X Resolution", "Y Resolution", "Resolution Unit", "Thumbnail Offset", "Thumbnail Length", "File Name", "File Modified Date"};

    public static final String[] simpleExifTags = {"Data Precision", "Image Height", "Image Width", "Make", "Model", "Orientation", "Date/Time", "Exposure Time", "F-Number", "ISO Speed Ratings"};

    private ImageUtil() {

    }

    /**
     * <pre>{@code
     *         Map[String, BufferedImage] imageMap = ImageUtil.getPlistImages(new FileInputStream(filePlist), new FileInputStream(fileImg));
     *         for (String name : imageMap.keySet()) {
     *             ImageUtil.write("e:\\cutimg\\" + name,imageMap.get(name));
     *         }
     * }</pre>
     *
     * @param plistFile plist 文件
     * @param imgFile   图片文件
     * @return 返回图片列表和名称
     * @throws Exception 异常
     */
    public static Map<String, BufferedImage> getPlistImages(InputStream plistFile, InputStream imgFile) throws Exception {
        BufferedImage bufferedImage = ImageIO.read(imgFile);
        NSDictionary rootDict = (NSDictionary) PropertyListParser.parse(plistFile);
        ParseDictionary parseDictionary = new ParseDictionary(rootDict);
        Map<String, BufferedImage> result = new HashMap<>();
        for (String name : parseDictionary.getFileNameList()) {
            NSDictionary findDict = parseDictionary.findDictionary(name);
            HashMap<String, NSObject> hashMap = findDict.getHashMap();
            BufferedImage cutImg = null;
            Rect rect = ParseDictionary.getFrameRect(hashMap);
            boolean rotated = ParseDictionary.getRotated(hashMap);
            if (rect != null) {
                if (rotated) {
                    int w = rect.getWidth();
                    rect.setWidth(rect.getHeight());
                    rect.setHeight(w);
                }
                Rectangle rectangle = new Rectangle();
                rectangle.x = rect.getX();
                rectangle.y = rect.getY();
                rectangle.width = rect.getWidth();
                rectangle.height = rect.getHeight();
                cutImg = subImage(bufferedImage, rectangle);
            } else {
                // 图像区域（非透明区域）的位置和大小，就是有效区的图片,如果  Frame 没有就用这个区域
                Rect rectColor = ParseDictionary.getSourceColorRect(hashMap);
                if (rotated) {
                    int w = rectColor.getWidth();
                    rectColor.setWidth(rectColor.getHeight());
                    rectColor.setHeight(w);
                }
                Rectangle rectangle = new Rectangle();
                rectangle.x = rectColor.getX();
                rectangle.y = rectColor.getY();
                rectangle.width = rectColor.getWidth();
                rectangle.height = rectColor.getHeight();
                cutImg = subImage(bufferedImage, rectangle);
            }
            int[] sourceSize = ParseDictionary.getSourceSize(hashMap);
            BufferedImage sourceImg = new BufferedImage(sourceSize[0], sourceSize[1], BufferedImage.TYPE_INT_BGR);
            if (rotated) {
                sourceImg = rotate(sourceImg, 90);
            }

            /*
            SourceColorRect 为 图像区域（非透明区域）的位置和大小，就是有效区的图片
            Rect rectColor = ParseDictionary.getSourceColorRect(hashMap);
            //x1 = 12 + 182/2 = 103 ， y1 = 200 - 37 - 148/2 = 89
            //.图像区域中心坐标为
            int x1 = rectColor.getX() + rectColor.getWidth()/2;
            int y1 = sourceSize[1] - rectColor.getY() - rectColor.getHeight()/2;
            //整体区域中心坐标为 x2 = 200/2 =100, y2 = 200/2 =100
            int x2 = sourceSize[0]/2;
            int y2 = sourceSize[1]/2;
            //sourceColorRect
            //两个中心坐标的便宜量
            int x3= x1-x2;
            int y3 = y1-y2;
            ///x3 ,y3 应该就是 offset ,但实际中有一点偏差
            */
            int[] offset = ParseDictionary.getOffset(hashMap);
            Graphics2D g = sourceImg.createGraphics();
            if (rotated) {
                sourceImg = g.getDeviceConfiguration().createCompatibleImage(sourceSize[1], sourceSize[0], Transparency.TRANSLUCENT);
            } else {
                sourceImg = g.getDeviceConfiguration().createCompatibleImage(sourceSize[0], sourceSize[1], Transparency.TRANSLUCENT);
            }
            g = sourceImg.createGraphics();
            g.drawImage(cutImg, offset[0], offset[1], null);
            //1.图像区域中心坐标为 x1 = 12 + 182/2 = 103 ， y1 = 200 - 37 - 148/2 = 89
            result.put(name, sourceImg);
        }
        return result;
    }

    public static BufferedImage getBufferImage(Image image, int type) {
        BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
        Graphics2D g = bufferedImage.createGraphics();
        g.drawImage(image, null, null);
        return bufferedImage;
    }

    /**
     * @param fileName 写文件保存为图片,支持jpg,png,gif格式
     * @param image    图片
     * @return 是否成功
     * @throws Exception 异常
     */
    static public boolean write(String fileName, Image image) throws Exception {

        if (StringUtil.isNull(fileName)) {
            return false;
        }
        if (image == null) {
            return false;
        }
        if (fileName.toLowerCase().endsWith("png")) {
            return savePng(fileName, image);
        } else if (fileName.toLowerCase().endsWith("gif")) {
            return saveGif(fileName, image);
        } else if (fileName.toLowerCase().endsWith("jpg")) {
            return saveJpg(fileName, image);
        }
        return false;
    }

    /**
     * @param fileName 保存为jpg格式图片
     * @param image    图片
     * @return boolean
     */
    static private boolean saveJpg(String fileName, Image image) {
        int width = image.getWidth(null);
        int height = image.getHeight(null);
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = bi.getGraphics();
        g.drawImage(image, 0, 0, width, height, null);
        g.dispose();
        try (OutputStream out = new FileOutputStream(fileName)) {
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
            encoder.encode(bi);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * @param fileName 保存为png格式图片
     * @param image    图片
     * @return boolean
     */
    static private boolean savePng(String fileName, Image image) throws IOException {
        FileOutputStream outfile = new FileOutputStream(fileName);
        byte[] bytes;
        PngEncoder png = new PngEncoder(image);
        png.setCompressionLevel(1);
        png.setEncodeAlpha(true);
        bytes = png.pngEncode();
        if (bytes != null) {
            outfile.write(bytes);
        }
        outfile.flush();
        outfile.close();
        return true;
    }

    /**
     * @param fileName 保存为gif格式图片
     * @param image    图片
     * @return boolean
     */
    static private boolean saveGif(String fileName, Image image) throws AWTException, IOException {
        // Save the image in GIF with Acme GifEncoder
        OutputStream out = new FileOutputStream(fileName);
        GIFEncoder gif = new GIFEncoder(image);

        gif.Write(out);
        out.close();
        return true;
    }


    /**
     * @param repeat   创建GIF动画
     * @param time     时间
     * @param imgArray 图片
     * @param out      输出流
     */
    static public void createAnimatedGif(int repeat, int time, Collection<BufferedImage> imgArray, OutputStream out) {
        AnimatedGifEncoder ag = new AnimatedGifEncoder();
        ag.start(out);
        ag.setTransparent(Color.black);
        ag.setRepeat(repeat);
        ag.setDelay(time);   // 1000/1 frame per sec
        for (BufferedImage img : imgArray) {
            ag.addFrame(img);
        }
        ag.finish();
    }


    /**
     * @param repeat         循环
     * @param time           时间
     * @param imageFilePaths 图片路径
     * @param out            输出流
     */
    static public void createAnimatedGif(int repeat, int time, String[] imageFilePaths, OutputStream out) {
        AnimatedGifEncoder e = new AnimatedGifEncoder();
        e.start(out);
        e.setDelay(time);
        e.setRepeat(repeat);
        for (String imageFilePath : imageFilePaths) {
            try {
                e.addFrame(ImageIO.read(new File(imageFilePath)));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        e.finish();
    }

    /**
     * 添加图片水印
     *
     * @param image      目标图片路径，如：C:\\myPictrue\\1.jpg
     * @param waterImage 水印文件
     *                   水印图片路径，如：C:\\myPictrue\\logo.png
     * @param x          水印图片距离目标图片左侧的偏移量，如果{@code x<0}, 则在正中间
     * @param y          水印图片距离目标图片上侧的偏移量，如果{@code y<0}, 则在正中间
     * @param alpha      透明度(0.0 -- 1.0, 0.0为完全透明，1.0为完全不透明)
     * @return 图片buf
     */
    static public BufferedImage pressImage(BufferedImage image, BufferedImage waterImage, int x, int y, float alpha) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = bufferedImage.createGraphics();
        g.drawImage(image, 0, 0, width, height, null);


        int width_1 = waterImage.getWidth();
        int height_1 = waterImage.getHeight();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP,
                alpha));

        int widthDiff = width - width_1;
        int heightDiff = height - height_1;
        if (x < 0) {
            x = widthDiff / 2;
        } else if (x > widthDiff) {
            x = widthDiff;
        }
        if (y < 0) {
            y = heightDiff / 2;
        } else if (y > heightDiff) {
            y = heightDiff;
        }
        g.drawImage(waterImage, x, y, width_1, height_1, null); // 水印文件结束
        g.dispose();

        return bufferedImage;

    }

    /**
     * 添加文字水印
     *
     * @param image     目标图片
     * @param pressText 水印文字， 如：中国证券网
     * @param fontName  字体名称， 如：宋体
     * @param fontStyle 字体样式，如：粗体和斜体(Font.BOLD|Font.ITALIC)
     * @param fontSize  字体大小，单位为像素
     * @param color     字体颜色
     * @param x         水印文字距离目标图片左侧的偏移量，如果x 小于 0, 则在正中间
     * @param y         水印文字距离目标图片上侧的偏移量，如果y 小于 0, 则在正中间
     * @param alpha     透明度(0.0 -- 1.0, 0.0为完全透明，1.0为完全不透明)
     * @return 图片buf
     */
    static public BufferedImage pressText(BufferedImage image, String pressText,
                                          String fontName, int fontStyle, int fontSize, Color color, int x,
                                          int y, float alpha) {

        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = bufferedImage.createGraphics();
        g.drawImage(image, 0, 0, width, height, null);
        g.setFont(new Font(fontName, fontStyle, fontSize));
        g.setColor(color);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));

        int width_1 = fontSize * getLength(pressText);
        int widthDiff = width - width_1;
        int heightDiff = height - fontSize;
        if (x < 0) {
            x = widthDiff / 2;
        } else if (x > widthDiff) {
            x = widthDiff;
        }
        if (y < 0) {
            y = heightDiff / 2;
        } else if (y > heightDiff) {
            y = heightDiff;
        }

        g.drawString(pressText, x, y + fontSize);
        g.dispose();
        return bufferedImage;

    }

    /**
     * 获取字符长度，一个汉字作为 1 个字符, 一个英文字母作为 0.5 个字符
     * x 字体大小就得到真正的长度
     *
     * @param text 图片中文字的长度
     * @return 字符长度，如：text="中国",返回 2；text="testaio",返回 2；text="中国ABC",返回 4.
     */
    static public int getLength(String text) {
        int textLength = text.length();
        int length = textLength;
        for (int i = 0; i < textLength; i++) {
            if (String.valueOf(text.charAt(i)).getBytes().length > 1) {
                length++;
            }
        }
        return (length % 2 == 0) ? length / 2 : length / 2 + 1;
    }


    /**
     * 缩放图像,更具比率方式
     *
     * @param inputStream  源图像文件地址
     * @param outputStream 缩放后的图像地址
     * @param fileType     文件类型
     * @param scale        缩放比例   1 表示原比例，必须大一0， 大于1 表示扩大
     * @return 是否成功
     */
    static public boolean scale(InputStream inputStream, OutputStream outputStream, String fileType, float scale) {
        try {
            BufferedImage src = ImageIO.read(inputStream); // 读入文件
            int width = src.getWidth(); // 得到源图宽
            int height = src.getHeight(); // 得到源图长
            // 放大
            width = (int) (width * scale);
            height = (int) (height * scale);

            Image image = src.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics g = tag.getGraphics();
            g.drawImage(image, 0, 0, null); // 绘制缩小后的图
            g.dispose();
            return ImageIO.write(tag, fileType, outputStream);// 输出到文件流
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param inputStream  源图像文件地址
     * @param outputStream 缩放后的图像地址
     * @param fileType     文件类型
     * @param targetW      宽
     * @param targetH      高
     * @return 缩放图像, 能够放大图片
     */
    static public boolean thumbnail(InputStream inputStream, OutputStream outputStream, String fileType, int targetW, int targetH) {
        try {
            BufferedImage src = ImageIO.read(inputStream); // 读入文件
            return thumbnail(src, outputStream, fileType, targetW, targetH);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param src          图片对象
     * @param outputStream 输出
     * @param fileType     文件类型
     * @param targetW      宽
     * @param targetH      高
     * @return 缩图
     */
    static public boolean thumbnail(final BufferedImage src, OutputStream outputStream, String fileType, int targetW, int targetH) {
        if (src == null) {
            return false;
        }
        try {
            if (src.getWidth() <= targetW || src.getHeight() <= targetH) {
                return ImageIO.write(src, fileType, outputStream);// 输出到文件流
            } else {
                BufferedImage image = scale(src, targetW, targetH);
                return ImageIO.write(image, fileType, outputStream);// 输出到文件流
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param source  源图片
     * @param targetW 宽度
     * @param targetH 高度
     * @return 缩小比例
     */
    static public BufferedImage scale(BufferedImage source, int targetW, int targetH) {
        int type = source.getType();
        BufferedImage target = null;
        double sx = 0;
        double sy = 0;
        int targetWidth = source.getWidth();
        int targetHeight = source.getHeight();
        if (targetW > 0) {
            sx = (double) targetW / targetWidth;
            targetHeight = (int) (sx * targetHeight);
            sy = sx;
            targetWidth = targetW;
        } else if (targetH > 0) {
            sy = (double) targetH / targetHeight;
            targetWidth = (int) (sy * targetWidth);
            sx = sy;
            targetHeight = targetH;
        } else if (targetWidth <= targetW) {
            return source;
        }

        if (type == BufferedImage.TYPE_CUSTOM) {
            ColorModel cm = source.getColorModel();
            WritableRaster raster = cm.createCompatibleWritableRaster(targetWidth, targetHeight);
            target = new BufferedImage(cm, raster, cm.isAlphaPremultiplied(), null);
        } else {
            target = new BufferedImage(targetWidth, targetHeight, type);
        }
        Graphics2D g = target.createGraphics();
        // smoother than exlax:
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.drawRenderedImage(source, AffineTransform.getScaleInstance(sx, sy));
        g.dispose();
        return target;
    }

    /**
     * 图像切割 上传头像部分会使用
     *
     * @param inputStream  源图像地址
     * @param outputStream 切片目标文件
     * @param destWidth    目标切片宽度
     * @param destHeight   目标切片高度
     * @param copyright    版权信息
     */
    static public void cuts(InputStream inputStream, OutputStream outputStream, int destWidth, int destHeight, String copyright) {
        try {
            Image img;
            ImageFilter cropFilter;
            // 读取源图像
            BufferedImage bi = ImageIO.read(inputStream);
            int srcWidth = bi.getHeight(); // 源图宽度
            int srcHeight = bi.getWidth(); // 源图高度
            if (srcWidth > destWidth && srcHeight > destHeight) {
                Image image = bi.getScaledInstance(srcWidth, srcHeight, Image.SCALE_DEFAULT);
                destWidth = 300; // 切片宽度
                destHeight = 300; // 切片高度
                int cols = 0; // 切片横向数量
                int rows = 0; // 切片纵向数量
                // 计算切片的横向和纵向数量
                if (srcWidth % destWidth == 0) {
                    cols = srcWidth / destWidth;
                } else {
                    cols = (int) Math.floor(srcWidth / destWidth) + 1;
                }
                if (srcHeight % destHeight == 0) {
                    rows = srcHeight / destHeight;
                } else {
                    rows = (int) Math.floor(srcHeight / destHeight) + 1;
                }
                // 循环建立切片
                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < cols; j++) {
                        // 四个参数分别为图像起点坐标和宽高
                        // 即: CropImageFilter(int x,int y,int width,int height)
                        cropFilter = new CropImageFilter(j * 300, i * 300, destWidth, destHeight);
                        img = Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(image.getSource(), cropFilter));
                        BufferedImage tag = new BufferedImage(destWidth, destHeight, BufferedImage.TYPE_INT_RGB);
                        Graphics g = tag.getGraphics();
                        g.drawImage(img, 0, 0, null); // 绘制缩小后的图
                        if (!StringUtil.isNull(copyright))    //写版权信息
                        {
                            g.drawString(copyright, 1, 1);
                        }
                        g.dispose();
                        // 输出为文件
                        ImageIO.write(tag, "jpg", outputStream);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 注意，输入不能等于输出
     * 对图片裁剪，并把裁剪完蛋新图片保存 。
     *
     * @param inputStream  输入
     * @param outputStream 输出
     * @param fileType     文件类型
     * @param x            x 坐标
     * @param y            y 坐标
     * @param width        宽
     * @param height       高
     * @return 是否成功
     */
    static public boolean cut(InputStream inputStream, OutputStream outputStream, String fileType, int x, int y, int width, int height) {
        try {
            BufferedImage image = ImageIO.read(inputStream);
            if (image == null) {
                return false;
            }
            Rectangle subImageBounds = new Rectangle();
            subImageBounds.x = x;
            subImageBounds.y = y;
            subImageBounds.width = width;
            subImageBounds.height = height;
            BufferedImage subImage = subImage(image, subImageBounds);
            return subImage != null && ImageIO.write(subImage, fileType, outputStream);
        } catch (IOException e) {
            log.error("cut image error", e);
            return false;
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 截图
     *
     * @param image          图像
     * @param subImageBounds 要截取的子图的范围
     * @return 截图
     */
    static public BufferedImage subImage(BufferedImage image, Rectangle subImageBounds) {
        if (subImageBounds.x < 0) {
            subImageBounds.x = 0;
        }
        if (subImageBounds.y < 0) {
            subImageBounds.y = 0;
        }

        if (subImageBounds.width - subImageBounds.x > image.getWidth()) {
            subImageBounds.width = image.getWidth() - subImageBounds.x;
        }
        if (subImageBounds.height - subImageBounds.y > image.getHeight()) {
            subImageBounds.height = image.getHeight() - subImageBounds.y;
        }

        if (subImageBounds.x + subImageBounds.width > image.getWidth()) {
            return null;
        }
        if (subImageBounds.y + subImageBounds.height > image.getHeight()) {
            return null;
        }

        return image.getSubimage(subImageBounds.x, subImageBounds.y, subImageBounds.width, subImageBounds.height);
    }


    static public boolean gray(InputStream inputStream, OutputStream outputStream, String fileType) {
        try {
            BufferedImage image = ImageIO.read(inputStream);
            return image != null && ImageIO.write(gray(image), fileType, outputStream);
        } catch (IOException e) {
            log.error("cut image error", e);
            return false;
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param src 灰度转换,成黑白
     * @return 得到Image
     */
    static public BufferedImage gray(BufferedImage src) {
        try {
            ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
            ColorConvertOp op = new ColorConvertOp(cs, null);
            return op.filter(src, null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param inputStream  输入
     * @param outputStream 输出
     * @param fileType     文件类型
     * @param contrast     对比
     * @param brightness   亮度
     * @return 亮度
     */
    static public boolean filter(InputStream inputStream, OutputStream outputStream, String fileType, float contrast, float brightness) {
        if (contrast == 0) {
            contrast = 1.1f;  //对比
        }
        if (brightness == 0) {
            brightness = 1.3f; //亮度
        }
        try {
            BufferedImage image = ImageIO.read(inputStream);
            if (image == null) {
                return false;
            }
            BufferedImage subImage = filter(image, contrast, brightness);
            return ImageIO.write(subImage, fileType, outputStream);
        } catch (IOException e) {
            log.error("cut image error", e);
            return false;
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * @param src        图片
     * @param contrast   对比
     * @param brightness 亮度
     * @return 返回过滤图片
     */
    static public BufferedImage filter(BufferedImage src, float contrast, float brightness) {
        int width = src.getWidth();
        int height = src.getHeight();

        int[] inPixels = new int[width * height];
        int[] outPixels = new int[width * height];
        src.getRGB(0, 0, width, height, inPixels, 0, width);

        // calculate RED, GREEN, BLUE means of pixel
        int index = 0;
        int[] rgbmeans = new int[3];
        double redSum = 0, greenSum = 0, blueSum = 0;
        double total = height * width;
        for (int row = 0; row < height; row++) {
            int ta = 0, tr = 0, tg = 0, tb = 0;
            for (int col = 0; col < width; col++) {
                index = row * width + col;
                ta = (inPixels[index] >> 24) & 0xff;
                tr = (inPixels[index] >> 16) & 0xff;
                tg = (inPixels[index] >> 8) & 0xff;
                tb = inPixels[index] & 0xff;
                redSum += tr;
                greenSum += tg;
                blueSum += tb;
            }
        }

        rgbmeans[0] = (int) (redSum / total);
        rgbmeans[1] = (int) (greenSum / total);
        rgbmeans[2] = (int) (blueSum / total);

        // adjust contrast and brightness algorithm, here
        for (int row = 0; row < height; row++) {
            int ta = 0, tr = 0, tg = 0, tb = 0;
            for (int col = 0; col < width; col++) {
                index = row * width + col;
                ta = (inPixels[index] >> 24) & 0xff;
                tr = (inPixels[index] >> 16) & 0xff;
                tg = (inPixels[index] >> 8) & 0xff;
                tb = inPixels[index] & 0xff;

                // remove means
                tr -= rgbmeans[0];
                tg -= rgbmeans[1];
                tb -= rgbmeans[2];

                // adjust contrast now !!!
                tr = (int) (tr * contrast);
                tg = (int) (tg * contrast);
                tb = (int) (tb * contrast);

                // adjust brightness
                tr += (int) (rgbmeans[0] * brightness);
                tg += (int) (rgbmeans[1] * brightness);
                tb += (int) (rgbmeans[2] * brightness);
                outPixels[index] = (ta << 24) | (clamp(tr) << 16) | (clamp(tg) << 8) | clamp(tb);
            }
        }
        src.setRGB(0, 0, width, height, outPixels, 0, width);
        return src;
    }

    static public int clamp(int value) {
        return value > 255 ? 255 : (value < 0 ? 0 : value);
    }


    /**
     * @param inputStream  输入
     * @param outputStream 输出
     * @param fileType     文件类型
     * @param rgb          RGB
     * @param jump         jump
     * @return 添加颜色
     */
    static public boolean addColorToImage(InputStream inputStream, OutputStream outputStream, String fileType, int rgb, int jump) {
        try {
            BufferedImage image = ImageIO.read(inputStream);
            if (image == null) {
                return false;
            }
            BufferedImage subImage = addColorToImage(image, rgb, jump);
            return ImageIO.write(subImage, fileType, outputStream);
        } catch (IOException e) {
            log.error("cut image error", e);
            return false;
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 添加颜色
     *
     * @param bufferedImage 图片
     * @param rgb           RGB
     * @param jump          jump
     * @return 添加颜色
     */
    static public BufferedImage addColorToImage(BufferedImage bufferedImage, int rgb, int jump) {
        if (jump <= 0) {
            return bufferedImage;
        }
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        for (int y = 0; y < height; y++) {
            for (int x = y % jump; x < width; x += jump) {
                bufferedImage.setRGB(x, y, rgb);
            }
        }
        return bufferedImage;
    }

    /**
     * @param src 绘制矩形边框
     * @return 得到Image
     */
    static public BufferedImage rectangle(BufferedImage src) {
        try {

            BufferedImage icon = scale(src, src.getWidth() - 4, src.getHeight() - 4);
            BufferedImage bufImg = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = bufImg.createGraphics();
            g.setColor(Color.GRAY);
            Shape shape = new RoundRectangle2D.Double(0, 0, src.getWidth() - 2, src.getHeight() - 2, 4, 4);
            g.draw(shape);
            g.setColor(Color.WHITE);
            shape = new RoundRectangle2D.Double(1, 1, src.getWidth() - 4, src.getHeight() - 4, 4, 4);
            g.draw(shape);
            g.drawImage(icon, 2, 2, icon.getWidth(), icon.getHeight(), null);
            g.dispose();
            return bufImg;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param panel 使用jpanel 生产对话转换为图片
     * @return 得到Image
     */
    static public BufferedImage createImage(JComponent panel) {
        int w = panel.getWidth(), h = panel.getHeight();
        if (panel.getWidth() <= 0 && panel.getHeight() <= 0) {
            w = panel.getPreferredSize().width;
            h = panel.getPreferredSize().height;
        }
        BufferedImage bufImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bufImg.createGraphics();
        panel.paint(g);
        g.translate(0, 0);
        g.dispose();
        return bufImg;
    }


    static public BufferedImage toImage(Icon icon) {
        BufferedImage bufImg = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bufImg.createGraphics();
        icon.paintIcon(null, g, 0, 0);
        g.dispose();
        return bufImg;
    }

    /**
     * @param label 得到容器上 Icon图标的图片数据
     * @return 图片缓存
     */
    static public BufferedImage getIcon(JComponent label) {
        Icon icon = (Icon) BeanUtil.getProperty(label, "icon");
        if (icon == null) {
            return null;
        }
        BufferedImage bufImg = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bufImg.createGraphics();
        icon.paintIcon(null, g, 0, 0);
        g.dispose();
        return bufImg;
    }

    //半透明效果
    static public BufferedImage alpha(BufferedImage bg, Image img, float alpha) {
        BufferedImage out = new BufferedImage(bg.getWidth(), bg.getHeight(), BufferedImage.TYPE_INT_RGB);
        // 获取Graphics2D
        Graphics2D g2d = out.createGraphics();
        // 设置透明度
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));
        g2d.drawImage(bg, 0, 0, null);
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();
        return out;

    }

    static public int[] handlePixels(Image img, int x, int y, int w, int h) {
        int[] pixel = new int[w * h];
        PixelGrabber pg = new PixelGrabber(img, x, y, w, h, pixel, 0, w);
        try {
            pg.grabPixels();
        } catch (InterruptedException e) {
            log.error("interrupted waiting for pixels!", e);

        }
        if ((pg.getStatus() & ImageObserver.ABORT) != 0) {
            log.error("image fetch aborted or errored");
        }
        return pixel;
    }

    /**
     * @param imageA 图片A
     * @param imageB 图片B
     * @return 图片左右拼接
     */
    static public BufferedImage joinRight(BufferedImage imageA, BufferedImage imageB) {
        int w = imageA.getWidth() + imageB.getWidth();
        int h = imageA.getHeight();
        if (h < imageB.getHeight()) {
            h = imageB.getHeight();
        }

        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = out.createGraphics();
        g2d.drawImage(imageA, 0, 0, imageA.getWidth(), imageA.getHeight(), null);
        g2d.drawImage(imageB, imageA.getWidth(), 0, imageB.getWidth(), imageB.getHeight(), null);
        g2d.dispose();
        return out;
    }

    /**
     * @param imageA 背景
     * @param imageB 底色
     * @param alpha  透明度
     * @return 合成背景
     */
    static public BufferedImage getBackground(BufferedImage imageA, BufferedImage imageB, float alpha) {
        BufferedImage out = new BufferedImage(imageA.getWidth(), imageA.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = out.createGraphics();
        g2d.setBackground(Color.WHITE);
        g2d.drawImage(imageA, 0, 0, imageA.getWidth(), imageA.getHeight(), null);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));
        g2d.drawImage(imageB, 0, (int) (imageA.getHeight() * 0.15), imageA.getWidth(), imageA.getHeight(), null);
        g2d.dispose();
        return out;
    }

    /**
     * @param imageA 背景
     * @param imageB 前景
     * @param below  0:左右,1:上下
     * @return 渐变合并
     */
    static public BufferedImage splice(BufferedImage imageA, BufferedImage imageB, int below) {
        int[] arrayImageA = handlePixels(imageA, 0, 0, imageA.getWidth(), imageA.getHeight());
        int[] arrayImageB = handlePixels(imageB, 0, 0, imageB.getWidth(), imageB.getHeight());

        int[] pixels = null;
        if (below == 1) {
            pixels = spliceBelow(arrayImageA, imageA.getWidth(), imageA.getHeight(), arrayImageB, imageB.getWidth(), imageB.getHeight());
        } else {
            pixels = spliceRight(arrayImageA, imageA.getWidth(), imageA.getHeight(), arrayImageB, imageB.getWidth(), imageB.getHeight());
        }
        JPanel jf = new JPanel();
        Image img = jf.createImage(new MemoryImageSource(imageB.getWidth(), imageB.getHeight(), pixels, 0, imageB.getWidth()));
        BufferedImage out = new BufferedImage(imageA.getWidth(), imageA.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = out.createGraphics();
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();
        return out;

    }

    /**
     * 渐变合并 算法
     *
     * @param imageA 背景图片
     * @param Aw     背景宽
     * @param Ah     背景高
     * @param imageB 前景图片
     * @param Bw     前景宽
     * @param Bh     前景高
     * @return 合并后的拴缚数组
     */
    static private int[] spliceRight(int[] imageA, int Aw, int Ah, int[] imageB, int Bw, int Bh) {
        int rangeFrom = Bw / 4;
        int rangeTo = Bw * 3 / 4;
        int range = Bw >> 1;
        int alpha = 0xff;
        for (int i = rangeFrom; i < Bw; i++) {
            float div = (float) (rangeTo - i) / (float) range;
            if (i > rangeTo) {   //渐变范围以外，不衰减
                div = 0;
            }
            float div1 = 1 - div;  //渐变左边范围以外，不衰减，用imgeA填充

            for (int j = 0; j < Bh; j++) {
                int pixel = imageB[j * Bw + i];
                alpha = (pixel >> 24) & 0xff; //分离imageB象素好相加
                int red = (pixel >> 16) & 0xff;
                int green = (pixel >> 8) & 0xff;
                int blue = (pixel) & 0xff;

                int red1, green1, blue1;
                pixel = imageA[j * Aw + i];  //准备分离imageA象素
                if (i > Aw || j > Ah) {    //imageA 的大小不够，用白色填充
                    alpha = 0xff;
                    red1 = 0xff;
                    green1 = 0xff;
                    blue1 = 0xff;
                } else {
                    alpha = (pixel >> 24) & 0xff;
                    red1 = (pixel >> 16) & 0xff;
                    green1 = (pixel >> 8) & 0xff;
                    blue1 = (pixel) & 0xff;
                }

                alpha = 0xFF;
                red = Math.round(red * div + red1 * div1); //象素按每种颜色的衰减相加
                green = Math.round(green * div + green1 * div1);
                blue = Math.round(blue * div + blue1 * div1);

                imageB[j * Bw + i] = (alpha << 24) | (red << 16) | (green << 8) | (blue); //合成颜色

            }
        }
        return imageB;
    }


    static private int[] spliceBelow(int[] imageA, int aw, int Ah, int[] imageB, int Bw, int Bh) {
        int rangeFrom = Bh / 4;
        int rangeTo = Bh * 3 / 4;
        int range = Bh >> 1;
        int alpha = 0xff;
        for (int i = rangeFrom; i < Bh; i++) {
            float div = (float) (rangeTo - i) / (float) range;
            if (i > rangeTo) {   //渐变范围以外，不衰减
                div = 0;
            }
            float div1 = 1 - div;  //渐变左边范围以外，不衰减，用imgeA填充

            for (int j = 0; j < Bw; j++) {
                int pixel = imageB[i * Bw + j];
                alpha = (pixel >> 24) & 0xff; //分离imageB象素好相加
                int red = (pixel >> 16) & 0xff;
                int green = (pixel >> 8) & 0xff;
                int blue = (pixel) & 0xff;

                int red1, green1, blue1;
                pixel = imageA[i * aw + j];  //准备分离imageA象素
                if (i > aw || j > Ah) {    //imageA 的大小不够，用白色填充
                    alpha = 0xff;
                    red1 = 0xff;
                    green1 = 0xff;
                    blue1 = 0xff;
                } else {
                    alpha = (pixel >> 24) & 0xff;
                    red1 = (pixel >> 16) & 0xff;
                    green1 = (pixel >> 8) & 0xff;
                    blue1 = (pixel) & 0xff;
                }

                alpha = 0xFF;
                red = Math.round(red * div + red1 * div1); //象素按每种颜色的衰减相加
                green = Math.round(green * div + green1 * div1);
                blue = Math.round(blue * div + blue1 * div1);

                imageB[i * Bw + j] = (alpha << 24) | (red << 16) | (green << 8) | (blue); //合成颜色

            }
        }
        return imageB;
    }

    /**
     * @param inputStream  输入
     * @param outputStream 输出
     * @param fileType     文件类型
     * @param degree       角度
     * @return 旋转图片为指定角度
     */
    static public boolean rotate(InputStream inputStream, OutputStream outputStream, String fileType, int degree) {
        if (degree < 0) {
            degree = 360 - degree;
        }
        try {
            BufferedImage image = ImageIO.read(inputStream);
            if (image == null) {
                return false;
            }
            BufferedImage img = rotate(image, degree);
            return ImageIO.write(img, fileType, outputStream);
        } catch (IOException e) {
            log.error("cut image error", e);
            return false;
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 旋转图片为指定角度
     *
     * @param image 目标图像
     * @param angel 旋转角度
     * @return 图片
     */
    static public BufferedImage rotate(BufferedImage image, int angel) {
        int src_width = image.getWidth();
        int src_height = image.getHeight();
        // calculate the new image size
        Rectangle rect_des = calcRotatedSize(new Rectangle(new Dimension(src_width, src_height)), angel);

        BufferedImage res = new BufferedImage(rect_des.width, rect_des.height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = res.createGraphics();
        // transform
        g2.translate((rect_des.width - src_width) / 2, (rect_des.height - src_height) / 2);
        g2.rotate(Math.toRadians(angel), src_width / 2, src_height / 2);

        g2.drawImage(image, null, null);
        return res;
    }


    static public Rectangle calcRotatedSize(Rectangle src, int angel) {
        // if angel is greater than 90 degree, we need transfer do some conversion
        if (angel >= 90) {
            if (angel / 90 % 2 == 1) {
                int temp = src.height;
                src.height = src.width;
                src.width = temp;
            }
            angel = angel % 90;
        }

        double r = Math.sqrt(src.height * src.height + src.width * src.width) / 2;
        double len = 2 * Math.sin(Math.toRadians(angel) / 2) * r;
        double angel_alpha = (Math.PI - Math.toRadians(angel)) / 2;
        double angel_dalta_width = Math.atan((double) src.height / src.width);
        double angel_dalta_height = Math.atan((double) src.width / src.height);

        int len_dalta_width = (int) (len * Math.cos(Math.PI - angel_alpha
                - angel_dalta_width));
        int len_dalta_height = (int) (len * Math.cos(Math.PI - angel_alpha
                - angel_dalta_height));
        int des_width = src.width + len_dalta_width * 2;
        int des_height = src.height + len_dalta_height * 2;
        return new Rectangle(new Dimension(des_width, des_height));
    }

    /**
     * @param image 图片
     * @return 转换为网页支持的字符数据
     * @throws IOException 异常
     */
    static public String toString(BufferedImage image) throws IOException {
        if (image == null) {
            return StringUtil.empty;
        }
        IIOImage iioImage = new IIOImage(image, null, null);
        ImageTypeSpecifier type = ImageTypeSpecifier.createFromRenderedImage(image);
        ImageWriter writer = ImageIO.getImageWriters(type, "png").next();

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            writer.setOutput(ImageIO.createImageOutputStream(out));
            writer.write(null, iioImage, null);
            return ("data:image/png;base64," + EncryptUtil.getBase64Encode(out.toByteArray(), EncryptUtil.NO_WRAP));
        }
    }


    /**
     * @param imgStr data:image/png;base64,
     * @return 解析协议，得到图片格式是jpg,png 等，data:image/png;base64,
     */
    static public String getImageType(String imgStr) {
        if (imgStr.contains(",")) {
            imgStr = StringUtil.substringBefore(imgStr, ",");
        }
        return StringUtil.substringBetween(imgStr, "/", StringUtil.SEMICOLON);
    }

    /**
     * 输出到文件
     * OutputStream out = new FileOutputStream(imgFilePath);
     * out.write(bytes);
     * out.flush();
     * out.close();
     *
     * @param imgStr 图片字符串
     * @return 输出到文件
     */
    static public byte[] base64ToImage(String imgStr) {
        // 对字节数组字符串进行Base64解码并生成图片
        // 图像数据为空
        if (imgStr == null) {
            return null;
        }
        if (imgStr.contains("base64,")) {
            imgStr = StringUtil.substringAfter(imgStr, "base64,");
        }
        byte[] bytes = EncryptUtil.getBase64Decode(imgStr);
        for (int i = 0; i < bytes.length; ++i) {
            if (bytes[i] < 0) {// 调整异常数据
                bytes[i] += 256;
            }
        }
        // 生成jpeg图片
        return bytes;
    }

    /**
     * 依赖文件  xercesImpl.jar xml-apis.jar  metadata-extractor.jar
     *
     * @param file     需要解析的文件
     * @param exifTags 图片信息
     * @return 图片信息获取metadata元数据信息
     */
    static public StringMap<String,String> parsePhotoExif(File file, final String[] exifTags) {
        StringMap<String,String> valueMap = new StringMap<>();
        valueMap.setKeySplit(StringUtil.EQUAL);
        valueMap.setLineSplit(StringUtil.CRLF);
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(file);
            if (metadata==null)
            {
                return valueMap;
            }
            for (Directory directory : metadata.getDirectories()) {
                for (Tag tag : directory.getTags()) {
                    String tagName = tag.getTagName();
                    if (!ArrayUtil.inArray(exifTags, tagName, true)) {
                        continue;
                    }
                    String desc = tag.getDescription();
                    valueMap.put(tagName, desc);
                }
            }
            valueMap.put("fileSize", NumberUtil.toString(file.length()));
        } catch (Exception e) {
            log.error("parsePhotoExif",e);
        }
        return valueMap;
    }


    static public BufferedImage getPngAlpha(BufferedImage ima) {
        BufferedImage bufIma = new BufferedImage(ima.getWidth(), ima.getHeight(), BufferedImage.TYPE_INT_BGR);
        Graphics2D g = bufIma.createGraphics();
        bufIma = g.getDeviceConfiguration().createCompatibleImage(ima.getWidth(null), ima.getHeight(null), Transparency.TRANSLUCENT);
        g = bufIma.createGraphics();
        g.drawImage(ima, 0, 0, null);
        return ima;
    }

}