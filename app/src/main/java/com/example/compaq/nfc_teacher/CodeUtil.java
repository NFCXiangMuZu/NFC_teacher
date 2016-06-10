package com.example.compaq.nfc_teacher;


import java.io.ByteArrayOutputStream;

public class CodeUtil {
	private static String hexString = "0123456789ABCDEF";
	/*
	 * 将字符串换为16进制
	 */
	public static String StringTohexString(String str) {
		// 根据默认编码获取字节数组
		byte[] bytes = str.getBytes();
		StringBuilder sb = new StringBuilder(bytes.length * 2);
		// 将字节数组中每个字节拆解成2位16进制整数
		for (int i = 0; i < bytes.length; i++) {
			sb.append(hexString.charAt((bytes[i] & 0xf0) >> 4));
			sb.append(hexString.charAt((bytes[i] & 0x0f) >> 0));
		}
		return sb.toString();
	}

	/*
	 * 将16进制数字解码成字符串,适用于所有字符（包括中文）
	 */
	public static String hexStringToString(String bytes) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(
				bytes.length() / 2);
		// 将每2位16进制整数组装成一个字节
		for (int i = 0; i < bytes.length(); i += 2)
			baos.write((hexString.indexOf(bytes.charAt(i)) << 4 | hexString
					.indexOf(bytes.charAt(i + 1))));
		return new String(baos.toByteArray());
	}

	/*
	 * 把16进制字符串转换成字节数组
	 */
	public static byte[] hexStringToByte(String hex) {
		int len = (hex.length() / 2);
		byte[] result = new byte[len];
		char[] achar = hex.toCharArray();
		for (int i = 0; i < len; i++) {
			int pos = i * 2;
			result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
		}
		return result;
	}

	private static int toByte(char c) {
		byte b = (byte) hexString.indexOf(c);
		return b;
	}
	/*
	 * 数组转换成十六进制字符串
	 */
	public static String bytesToHexString(byte[] bArray) {
		StringBuffer sb = new StringBuffer(bArray.length);
		String sTemp;
		for (int i = 0; i < bArray.length; i++) {
			sTemp = Integer.toHexString(0xFF & bArray[i]);
			if (sTemp.length() < 2)
				sb.append(0);
			sb.append(sTemp.toUpperCase());
		}
		return sb.toString();
	}

	public final static String qpDecoding(String str)
	{
		if (str == null)
		{
			return "";
		}
		try
		{
			StringBuffer sb = new StringBuffer(str);
			for (int i = 0; i < sb.length(); i++)
			{
				if (sb.charAt(i) == '\n' && sb.charAt(i - 1) == '=')
				{
					// 解码这个地方也要修改一下
					// sb.deleteCharAt(i);
					sb.deleteCharAt(i - 1);
				}
			}
			str = sb.toString();
			byte[] bytes = str.getBytes("US-ASCII");
			for (int i = 0; i < bytes.length; i++)
			{
				byte b = bytes[i];
				if (b != 95)
				{
					bytes[i] = b;
				}
				else
				{
					bytes[i] = 32;
				}
			}
			if (bytes == null)
			{
				return "";
			}
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			for (int i = 0; i < bytes.length; i++)
			{
				int b = bytes[i];
				if (b == '=')
				{
					try
					{
						int u = Character.digit((char) bytes[++i], 16);
						int l = Character.digit((char) bytes[++i], 16);
						if (u == -1 || l == -1)
						{
							continue;
						}
						buffer.write((char) ((u << 4) + l));
					}
					catch (ArrayIndexOutOfBoundsException e)
					{
						e.printStackTrace();
					}
				}
				else
				{
					buffer.write(b);
				}
			}
			return new String(buffer.toByteArray(), "UTF-8");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "";
		}
	}
}
