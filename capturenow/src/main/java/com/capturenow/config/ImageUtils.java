package com.capturenow.config;

import java.io.ByteArrayOutputStream;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import org.springframework.stereotype.Service;

@Service
public class ImageUtils {

	public static byte[] compressImage(byte[] data)
	{
		Deflater deflater = new Deflater();
		deflater.setLevel(Deflater.BEST_COMPRESSION);
		deflater.setInput(data);
		deflater.finish();
		
		ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream(data.length);
		byte[] tmp = new byte[4*1024];
		while(!deflater.finished())
		{
			int size = deflater.deflate(tmp);
			arrayOutputStream.write(tmp,0,size);
		}
		try {
			arrayOutputStream.close();
		}catch (Exception e) {
			// TODO: handle exception
		}
		return arrayOutputStream.toByteArray();
	}
	
	public static byte[] decompressImage(byte[] data)
	{
		Inflater inflater = new Inflater();
		inflater.setInput(data);
		ByteArrayOutputStream  arrayOutputStream = new ByteArrayOutputStream(data.length);
		byte[] temp = new byte[4*1024];
		try {
			while(!inflater.finished())
			{
				int count = inflater.inflate(temp);
				arrayOutputStream.write(temp,0,count);
			}
			arrayOutputStream.close();
		}catch (Exception e) {
			// TODO: handle exception
		}
		return arrayOutputStream.toByteArray();
	}
}
