package cn.aotcloud.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;

import org.springframework.core.io.ClassPathResource;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

/**
 * 二维码生成工具类
 */
public class QrCodeUtils extends BaseCodeUtils {

	/**
	 * 生成二维码
	 *
	 * @param content      二维码内容
	 * @param logoPath     logo地址
	 * @param needCompress 是否压缩logo
	 * @return 图片
	 * @throws WriterException 
	 * @throws IOException 
	 */
	public static BufferedImage createQRCodeImage(
			String content, 
			QrConts qrConts, 
			boolean defaultLogo, 
			boolean defaultFrame,
			boolean needCompress,
			int targetRgb,
			boolean changeRgb) throws WriterException {
		Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
		hints.put(EncodeHintType.CHARACTER_SET, CHARSET);
		hints.put(EncodeHintType.MARGIN, 0);
		BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, qrConts.getQrSize(), qrConts.getQrSize(), hints);
		int width = bitMatrix.getWidth();
		int height = bitMatrix.getHeight();
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		if(width < Integer.MAX_VALUE && height < Integer.MAX_VALUE) {
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					image.setRGB(x, y, bitMatrix.get(x, y) ? (changeRgb ? targetRgb : 0xFF000000) : 0xFFFFFFFF);
				}
			}
		}
		InputStream logoInput = null;
		try {
			if(!defaultLogo) {
				return image;
			} else {
				ClassPathResource logoResource = new ClassPathResource("qrlogo/" + qrConts.getLogoSize() + ".png");
				logoInput = logoResource.getInputStream();
				// 插入默认logo图片
				QrCodeUtils.insertQRCodeImageLogo(image, logoInput, qrConts, needCompress);
			}
		} catch(IOException e) {
			IOUtils.closeQuietly(logoInput);
		}
		// 放置边框图片
		image = QrCodeUtils.drawQRCodeImageFrame(image, qrConts, defaultFrame, changeRgb, targetRgb);
		
		return image;
	}
	
	/**
	 * 生成二维码
	 *
	 * @param content      二维码内容
	 * @param logoPath     logo地址
	 * @param needCompress 是否压缩logo
	 * @return 图片
	 * @throws WriterException 
	 * @throws IOException 
	 */
	public static BufferedImage createQRCodeImage(
			String content, 
			InputStream logoInput, 
			QrConts qrConts, 
			boolean defaultLogo, 
			boolean defaultFrame,
			boolean needCompress,
			int targetRgb,
			boolean changeRgb) throws WriterException, IOException {
		Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
		hints.put(EncodeHintType.CHARACTER_SET, CHARSET);
		hints.put(EncodeHintType.MARGIN, 0);
		BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, qrConts.getQrSize(), qrConts.getQrSize(), hints);
		int width = bitMatrix.getWidth();
		int height = bitMatrix.getHeight();
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		if(width < Integer.MAX_VALUE && height < Integer.MAX_VALUE) {
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					image.setRGB(x, y, bitMatrix.get(x, y) ? (changeRgb ? targetRgb : 0xFF000000) : 0xFFFFFFFF);
				}
			}
		}
		// 插入上传logo图片
		QrCodeUtils.insertQRCodeImageLogo(image, logoInput, qrConts, needCompress);
		// 放置边框图片
		image = QrCodeUtils.drawQRCodeImageFrame(image, qrConts, defaultFrame, changeRgb, targetRgb);
		
		return image;
	}

	/**
	 * 插入LOGO
	 *
	 * @param source       二维码图片
	 * @param logoPath     LOGO图片地址
	 * @param needCompress 是否压缩
	 * @throws IOException
	 */
	private static void insertQRCodeImageLogo(BufferedImage source, InputStream logoInput, QrConts qrConts, boolean needCompress) throws IOException {
		if (logoInput == null) {
			return;
		}
		Image src = ImageIO.read(logoInput);
		int width = src.getWidth(null);
		int height = src.getHeight(null);
		if (needCompress) { // 压缩LOGO
			if (width > qrConts.getLogoSize()) {
				width = qrConts.getLogoSize();
			}
			if (height > qrConts.getLogoSize()) {
				height = qrConts.getLogoSize();
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
		int x = (qrConts.getQrSize() - width) / 2;
		int y = (qrConts.getQrSize() - height) / 2;
		graph.drawImage(src, x, y, width, height, null);
		Shape shape = new RoundRectangle2D.Float(x, y, width, width, 6, 6);
		graph.setStroke(new BasicStroke(3f));
		graph.draw(shape);
		graph.dispose();
		
		IOUtils.closeQuietly(logoInput);
	}

	public static BufferedImage drawQRCodeImageFrame(BufferedImage image, QrConts qrConts, boolean defaultFrame, boolean changeRgb, int targetRgb) {
		if(defaultFrame) {
			ClassPathResource frameResource = new ClassPathResource("qrframe/" + qrConts.getFrameSize() + ".png");
			try (InputStream frameInputStream = frameResource.getInputStream()) {
				BufferedImage frame = ImageIO.read(frameInputStream);
				if(changeRgb) {
					// 改变边框图片颜色
					frame = QrCodeUtils.changeQRCodeImageRGB(frame, changeRgb, targetRgb);
				}
				// 在背景图片中添加入需要写入的信息，
				Graphics2D g = frame.createGraphics();
				// 设置为透明覆盖
				// g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 1.0f));
				// 在背景图片上相框
				g.drawImage(image, (frame.getWidth() - image.getWidth()) / 2, (frame.getHeight() - image.getHeight()) / 2, image.getWidth(), image.getHeight(), null);
				g.dispose();
				IOUtils.closeQuietly(frameInputStream);
				
				return frame;
			} catch (IOException e) {
				logger.error(e);
			}
		}
		
		return image;
	}
	
	/***
	 * @param path 原图地址
	 * @param targetRgb 目标颜色RGB值 16进制颜色码
	 * @param isNetWork 原图是否为网络图片地址
	 * @return
	 */
	public static BufferedImage changeQRCodeImageRGB(BufferedImage image, boolean changeRgb, int targetRgb) {
		if(!changeRgb) {
			return image;
		}
	    /**
	     * 得到图片的长宽
	     */
	    int width = image.getWidth();
	    int height = image.getHeight();
	    /**
	     * 取背景色的坐标
	     */
	    int critical = 5;
	    BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	    /**
	     * 获取左上角颜色，默认左上角像素块颜色为背景色
	     */
	    int pixel = image.getRGB(critical, critical);
	    /**
	     * 这里是遍历图片的像素，因为要处理图片的背色，所以要把指定像素上的颜色换成目标颜色
	     * 这里 是一个二层循环，遍历长和宽上的每个像素
	     */
	    //Graphics g = image.getGraphics();
	    Graphics2D g = bufferedImage.createGraphics();
	    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	    //Graphics g = image.getGraphics();
	    if(width < Integer.MAX_VALUE && height < Integer.MAX_VALUE) {
		    for (int x = 0; x < width; x++) {
		        for (int y = 0; y < height; y++) {
		            /**
		             * 得到指定像素（i,j)上的RGB值，
		             */
		            int nowPixel = image.getRGB(x, y);
		            /**
		             * 进行换色操作，我这里是要把蓝底换成白底，那么就判断图片中rgb值是否在蓝色范围的像素
		             */
		            // 核心代码：但是这样会有误差，还需要优化边缘、人像边框
		            int p = pixel != nowPixel ? targetRgb : nowPixel;
		            g.setColor(new Color(p));
		            g.fillRect(x, y, 1, 1);
		        }
		    }
	    }
	    return bufferedImage;
	}
	
	public static int getRGBFromHex(String hexStr) {
		int[] rgbArray = getRGBArrayFromHex(hexStr);
		return getIntFromRGBArray(rgbArray[0], rgbArray[1], rgbArray[2]);
	}
	
	public static int[] getRGBArrayFromHex(String hexStr){
		if(hexStr != null && !"".equals(hexStr) && hexStr.length() == 7){
			int[] rgb = new int[3];
			rgb[0] = Integer.valueOf(hexStr.substring( 1, 3 ), 16);
			rgb[1] = Integer.valueOf(hexStr.substring( 3, 5 ), 16);
			rgb[2] = Integer.valueOf(hexStr.substring( 5, 7 ), 16);
			return rgb;
		}
		return null;
	}
	
	public static int getIntFromRGBArray(int Red, int Green, int Blue){
	    Red = (Red << 16) & 0x00FF0000; //Shift red 16-bits and mask out other stuff
	    Green = (Green << 8) & 0x0000FF00; //Shift Green 8-bits and mask out other stuff
	    Blue = Blue & 0x000000FF; //Mask out anything not blue.

	    return 0xFF000000 | Red | Green | Blue; //0xFF000000 for 100% Alpha. Bitwise OR everything together.
	}
	
}
