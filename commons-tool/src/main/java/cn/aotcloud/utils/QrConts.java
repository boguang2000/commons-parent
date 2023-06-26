package cn.aotcloud.utils;

import org.apache.commons.lang3.StringUtils;

public enum QrConts {

	SMALL(258, 64, 400, "small"), MEDIUM(344, 86, 562, "medium"), LARGE(430, 105, 648, "large");

	private final int qrSize;

	private final int logoSize;

	private final int frameSize;

	private final String desc;

	private QrConts(int qrSize, int logoSize, int frameSize, String desc) {
		this.qrSize = qrSize;
		this.logoSize = logoSize;
		this.frameSize = frameSize;
		this.desc = desc;
	}

	public int getQrSize() {
		return qrSize;
	}

	public int getLogoSize() {
		return logoSize;
	}

	public int getFrameSize() {
		return frameSize;
	}

	public String getDesc() {
		return desc;
	}

	public static QrConts valueOfDesc(String desc) {
		for (QrConts qrConts : QrConts.values()) {
			if (StringUtils.equals(qrConts.getDesc(), desc)) {
				return qrConts;
			}
		}
		return SMALL;
	}
}
