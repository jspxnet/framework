package com.github.jspxnet.util;

import com.github.jspxnet.utils.FileUtil;
import com.github.jspxnet.utils.NumberUtil;
import com.github.jspxnet.utils.StringUtil;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Hashtable;
import java.util.Map;

/**
 * user: Rex  chenyuan
 * date: 2016年12月29日  上午12:31:29
 */
public final class QRCodeUtil {

    private QRCodeUtil() {

    }

    private static final String CHARSET = StandardCharsets.UTF_8.name();
    private static final String FORMAT_NAME = "jpg";
    // 二维码尺寸
    private static final int QRCODE_SIZE = 200;
    // LOGO宽度
    private static final int WIDTH = 60;
    // LOGO高度
    private static final int HEIGHT = 60;

    private static final int secretKey = 269;


    /**
     * 创建二维码图片
     *
     * @param content      二维码内容
     * @param logoImgPath  Logo
     * @param needCompress 是否压缩Logo
     * @return 图片数据
     * @throws WriterException 异常
     * @throws IOException     异常
     */

    public static BufferedImage createImage(String content, String logoImgPath, boolean needCompress) throws Exception {
        Map<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, CHARSET);
        hints.put(EncodeHintType.MARGIN, 1);
        BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, QRCODE_SIZE, QRCODE_SIZE, hints);
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
            }
        }
        if (!FileUtil.isFileExist(logoImgPath)) {
            return image;
        }
        // 插入图片
        insertImage(image, logoImgPath, needCompress);
        return image;
    }

    /**
     * user: Rex
     * date: 2016年12月29日  上午12:30:09
     *
     * @param source       二维码图片
     * @param logoImgPath  Logo
     * @param needCompress 是否压缩Logo
     * @throws IOException 异常
     *                     void
     *                     添加Logo
     */
    private static void insertImage(BufferedImage source, String logoImgPath, boolean needCompress) throws IOException {
        File file = new File(logoImgPath);
        if (!file.exists()) {
            return;
        }

        Image src = ImageIO.read(new File(logoImgPath));
        int width = src.getWidth(null);
        int height = src.getHeight(null);
        if (needCompress) { // 压缩LOGO
            if (width > WIDTH) {
                width = WIDTH;
            }

            if (height > HEIGHT) {
                height = HEIGHT;
            }

            Image image = src.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics g = tag.getGraphics();
            g.drawImage(image, 0, 0, null); // 绘制缩小后的图
            g.dispose();
            src = image;
        }

        // 插入LOGO
        Graphics2D graph = source.createGraphics();
        int x = (QRCODE_SIZE - width) / 2;
        int y = (QRCODE_SIZE - height) / 2;
        graph.drawImage(src, x, y, width, height, null);
        Shape shape = new RoundRectangle2D.Float(x, y, width, width, 6, 6);
        graph.setStroke(new BasicStroke(3f));
        graph.draw(shape);
        graph.dispose();
    }


    /**
     * 生成带Logo的二维码
     *
     * @param content      二维码内容
     * @param logoImgPath  Logo
     * @param destPath     二维码输出路径
     * @param needCompress 是否压缩Logo
     * @param imgFormat    图片格式
     * @return 生成带Logo的二维码
     * @throws Exception 异常
     */
    public static boolean encode(String content, String logoImgPath, String destPath, boolean needCompress, String imgFormat) throws Exception {
        BufferedImage image = createImage(content, logoImgPath, needCompress);
        FileUtil.makeDirectory(destPath);
        if (StringUtil.isNull(imgFormat)) {
            imgFormat = FORMAT_NAME;
        }
        return ImageIO.write(image, imgFormat, new File(destPath));
    }


    /**
     * @param content  二维码内容
     * @param destPath 二维码输出路径
     * @return 转换是否成功
     * @throws Exception 异常
     */
    public static boolean encode(String content, String destPath) throws Exception {
        return encode(content, null, destPath, false, FORMAT_NAME);
    }

    /**
     * 生成带Logo的二维码，并输出到指定的输出流
     *
     * @param content      二维码内容
     * @param logoImgPath  Logo路径
     * @param output       输出流
     * @param needCompress 是否压缩Logo
     * @param imgFormat    图片格式
     * @return 是否成功
     * @throws Exception 异常
     */
    public static boolean encode(String content, String logoImgPath, OutputStream output, boolean needCompress, String imgFormat) throws Exception {
        BufferedImage image = QRCodeUtil.createImage(content, logoImgPath, needCompress);
        if (StringUtil.isNull(imgFormat)) {
            imgFormat = FORMAT_NAME;
        }
        return ImageIO.write(image, imgFormat, output);
    }

    /**
     * 简单方式直接生成，输出二维码
     *
     * @param content   正文
     * @param output    输出
     * @param imgFormat 图片格式
     * @return 简单方式直接生成
     * @throws Exception 异常
     */
    public static boolean encode(String content, OutputStream output, String imgFormat) throws Exception {
        BufferedImage image = QRCodeUtil.createImage(content, null, false);
        if (StringUtil.isNull(imgFormat)) {
            imgFormat = FORMAT_NAME;
        }
        return ImageIO.write(image, imgFormat, output);
    }


    /**
     * @param content 二维码内容
     * @param output  输出流
     * @return 生成不带Logo的二维码，并输出到指定的输出流
     * @throws Exception 异常
     */
    public static boolean encode(String content, OutputStream output) throws Exception {
        return encode(content, null, output, false, FORMAT_NAME);
    }

    /**
     * user: Rex
     * date: 2016年12月29日  上午12:39:10
     *
     * @param file 二维码
     * @return 返回解析得到的二维码内容
     * @throws Exception 异常
     *                   String
     *                   二维码解析
     */
    public static String decode(File file) throws Exception {
        BufferedImage image = ImageIO.read(file);
        if (image == null) {
            return null;
        }
        BufferedImageLuminanceSource source = new BufferedImageLuminanceSource(image);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        Result result;
        Hashtable<DecodeHintType, Object> hints = new Hashtable<DecodeHintType, Object>();
        hints.put(DecodeHintType.CHARACTER_SET, CHARSET);
        result = new MultiFormatReader().decode(bitmap, hints);
        return result.getText();
    }

    /**
     * user: Rex
     * date: 2016年12月29日  上午12:39:48
     *
     * @param path 二维码存储位置
     * @return 返回解析得到的二维码内容
     * @throws Exception 异常
     *                   String
     *                   二维码解析
     */
    public static String decode(String path) throws Exception {
        return decode(new File(path));
    }

    /**
     * @param pid 父ID
     * @param id  自己ID
     * @return 创建一个16位的编码, 编码中整合了时间
     */
    public static String createCode(long pid, long id) {
        String sTemp = ((pid + id + secretKey) + NumberUtil.getKeepLength(System.currentTimeMillis(), 2));
        int je = StringUtil.getNumberEvenCount(sTemp);
        return Long.toHexString(StringUtil.toLong(sTemp + NumberUtil.getKeepLength(je, 2))).toUpperCase();
    }

    /**
     * 上边编码方式验证
     *
     * @param code 16位编码
     * @param pid  父ID
     * @param id   自己ID
     * @return 返回子ID说明正确，验证成功，如果返回0或其他数字表示错误，验证不通过
     */
    public static boolean verifyCode(String code, long pid, long id) {
        if (code == null || code.length() < 4) {
            return false;
        }
        long temp = Long.parseLong(code.toLowerCase(), 16);
        if (temp <= 0) {
            return false;
        }
        String stmp = NumberUtil.toString(temp);
        int je = StringUtil.toInt(stmp.substring(stmp.length() - 2));
        String vTmp = stmp.substring(0, stmp.length() - 2);
        int je2 = StringUtil.getNumberEvenCount(vTmp);
        if (je != je2) {
            return false;
        }
        stmp = stmp.substring(0, stmp.length() - 4);
        return StringUtil.toLong(stmp) == (pid + id + secretKey);
    }

    /**
     * @param id id
     * @return 单个ID生成二维码
     */
    public static String createCode(long id) {
        return createCode(0, id);
    }

    /**
     * @param code 验证码
     * @param id   验证id
     * @return 验证单个
     */
    public static boolean verifyCode(String code, long id) {
        return verifyCode(code, 0, id);
    }
}