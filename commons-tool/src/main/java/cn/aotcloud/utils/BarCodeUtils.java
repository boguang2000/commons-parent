package cn.aotcloud.utils;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

public class BarCodeUtils extends BaseCodeUtils {
	
    /**
     * 设置 条形码参数
     */
    private static Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>() {
        private static final long serialVersionUID = 1L;
        {
            // 设置编码方式
            put(EncodeHintType.CHARACTER_SET, CHARSET);
            // 容错级别 这里选用最高级H级别
            put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            put(EncodeHintType.MARGIN, 0);
        }
    };

    /**
     * 生成 图片缓冲
     *
     * @param vaNumber VA 码
     * @return 返回BufferedImage
     */
    public static BufferedImage getBarCode(String vaNumber, int height, float widthScale) {
        Code128Writer writer = new Code128Writer();
        //为了无边距，需设置宽度为条码自动生成规则的宽度
        int width = new Code128Writer().encode(vaNumber).length;
		BitMatrix bitMatrix = writer.encode(vaNumber, BarcodeFormat.CODE_128, width, height, hints);
		BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);
		
		image = scale(image, widthScale * 2);
		
		return image;
    }

    /**
     * JAVA 图像等比缩放
     * @param srcImageFile  缩放的图片
     * @param destImageFile  缩放后的图片
     * @param scale  缩放比例
     * @return
     */
    public static BufferedImage scale(BufferedImage image, float scale) {
        //获取缩放后的宽高
        int width = (int) (image.getWidth()*scale);
        //int height = (int) (image.getHeight()*scale);
        int height = image.getHeight();
        //调用缩放方法获取缩放后的图片
        Image img = image.getScaledInstance(width , height, Image.SCALE_DEFAULT);
        //创建一个新的缓存图片
        BufferedImage imageOut = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        //获取画笔
        Graphics2D graphics = imageOut.createGraphics();
        //将Image对象画在画布上,最后一个参数,ImageObserver:接收有关 Image 信息通知的异步更新接口,没用到直接传空
        graphics.drawImage(img, 0, 0, null);
        //一定要释放资源
        graphics.dispose();
        return imageOut;
    }
	
    /**
     * @param image   条形码图片
     * @param equipNo 设备编号
     * @return 返回 BufferedImage
     */
    public static BufferedImage insertTopWords(BufferedImage image,
    										   String equipNo, 
    										   String leftUpWords,
    										   String rightUpWords, 
    										   String leftDownWords,
    										   String rightDownWords) {
    	int widthPadding = 0;
    	int heightUpPadding = 0;
    	int enohPadding = 0;
    	int heightDownPadding = 0;
    	if(StringUtils.isNotBlank(leftUpWords) || StringUtils.isNotBlank(rightUpWords) || StringUtils.isNotBlank(leftDownWords) || StringUtils.isNotBlank(rightDownWords)) {
    		widthPadding += 20;
    	}
    	if(StringUtils.isNotBlank(leftUpWords) || StringUtils.isNotBlank(rightUpWords)) {
    		heightUpPadding += 30;
    	}
    	if(StringUtils.isNotBlank(equipNo)) {
    		enohPadding += 20;
    	}
    	if(StringUtils.isNotBlank(leftDownWords) || StringUtils.isNotBlank(rightDownWords)) {
    		heightDownPadding += 30;
    	}
    	int width = image.getWidth() + widthPadding;
    	int height = image.getHeight() + heightUpPadding + enohPadding + heightDownPadding;
    	if((StringUtils.isNotBlank(leftDownWords) || StringUtils.isNotBlank(rightDownWords)) && StringUtils.isBlank(leftUpWords) && StringUtils.isBlank(rightUpWords)) {
    		heightUpPadding += 10;
    		height += 10;
    	}
    	if(StringUtils.isBlank(equipNo)) {
	    	if((StringUtils.isNotBlank(leftUpWords) || StringUtils.isNotBlank(rightUpWords)) && StringUtils.isBlank(leftDownWords) && StringUtils.isBlank(rightDownWords)) {
	    		height += 10;
	    	}
    	}
    	
        BufferedImage outImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = outImage.createGraphics();
        // 抗锯齿
        setGraphics2D(g2d);
        // 设置白色
        setColorWhite(g2d, width, height);
        // 画条形码到新的面板
        //g2d.drawImage(image, 20, 40, image.getWidth() - 20, image.getHeight(), null);
        g2d.drawImage(image.getScaledInstance(image.getWidth(), image.getHeight(), Image.SCALE_SMOOTH), widthPadding/2, heightUpPadding, null);
        // 画文字到新的面板
        Color color = new Color(0, 0, 0);
        g2d.setColor(color);
        // 字体、字型、字号
        g2d.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        
        // 右上角文字
        if(StringUtils.isNotBlank(rightUpWords)) {
            int rightUpWordsWidth = width - g2d.getFontMetrics().stringWidth(leftUpWords);
        	g2d.drawString(rightUpWords, rightUpWordsWidth - widthPadding/2, heightUpPadding/2 + 5);
        }
        // 左上角文字
        if(StringUtils.isNotBlank(leftUpWords)) {
        	g2d.drawString(leftUpWords, widthPadding/2, heightUpPadding/2 + 5);
        }
        // 中下部文字
        if(StringUtils.isNotBlank(equipNo)) {
        	// 文字长度
            String equipNoString = equipNo.replace("", " ").trim();
            int strWidth = g2d.getFontMetrics().stringWidth(equipNoString);
            // 总长度减去文字长度的一半  （居中显示）
            int wordStartX = (width - strWidth) / 2;
            g2d.drawString(equipNoString, wordStartX, image.getHeight() + heightUpPadding + enohPadding/2 + 8);
        }
        // 右下角文字
        if(StringUtils.isNotBlank(rightDownWords)) {
        	int rightDownWordsWidth = width - widthPadding/2 - g2d.getFontMetrics().stringWidth(rightDownWords);
            g2d.drawString(rightDownWords, rightDownWordsWidth, image.getHeight() + heightUpPadding + enohPadding + heightDownPadding/2 + 5);
        }
        // 左下角文字
        if(StringUtils.isNotBlank(leftDownWords)) {
        	g2d.drawString(leftDownWords, widthPadding/2, image.getHeight() + heightUpPadding + enohPadding + heightDownPadding/2 + 5);
        }
        // 设置边框
        //if(setDrawRect) {
        //	setDrawRect(g2d, width, height);
        //}
        // 设置虚线边框
        //if(setDrawRectDottedLine) {
        //	setDrawRectDottedLine(g2d, width, height);
        //}
        g2d.dispose();
        outImage.flush();
        return outImage;
    }
    
    /**
     * 设置 Graphics2D 属性  （抗锯齿）
     *
     * @param g2d Graphics2D提供对几何形状、坐标转换、颜色管理和文本布局更为复杂的控制
     */
    private static void setGraphics2D(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_DEFAULT);
        Stroke s = new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
        g2d.setStroke(s);
    }

    /**
     * 设置背景为白色
     *
     * @param g2d Graphics2D提供对几何形状、坐标转换、颜色管理和文本布局更为复杂的控制
     */
    private static void setColorWhite(Graphics2D g2d, int width, int height) {
        g2d.setColor(Color.WHITE);
        //填充整个屏幕
        g2d.fillRect(0, 0, width, height);
        //设置笔刷
        g2d.setColor(Color.BLACK);
    }

    /**
     * 设置边框
     *
     * @param g2d Graphics2D提供对几何形状、坐标转换、颜色管理和文本布局更为复杂的控制
     */
    @SuppressWarnings("unused")
	private static void setDrawRect(Graphics2D g2d, int width, int height) {
    	int padding = 5;
        //设置笔刷
        g2d.setColor(Color.DARK_GRAY);
        g2d.drawRect(padding, padding, width - padding * 2, height - padding * 2);
    }

    /**
     * 设置边框虚线点
     *
     * @param g2d Graphics2D提供对几何形状、坐标转换、颜色管理和文本布局更为复杂的控制
     */
    @SuppressWarnings("unused")
	private static void setDrawRectDottedLine(Graphics2D g2d, int width, int height) {
    	int padding = 10;
        //设置笔刷
        g2d.setColor(Color.BLUE);
        BasicStroke stroke = new BasicStroke(
        		0.5f, 
        		BasicStroke.CAP_ROUND,
        		BasicStroke.JOIN_ROUND, 
        		0.5f, 
        		new float[]{1, 4}, 
        		0.5f);
        g2d.setStroke(stroke);
        g2d.drawRect(padding, padding, width - padding * 2, height - padding * 2);
    }
    
}

