package net.soomsam.zirmegghuette.zars;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;

public class TestUtils {
	private final static Logger logger = Logger.getLogger(TestUtils.class);

	private final static int INPUTSTREAM_READBUFFER_SIZE = 512;

	public static byte[] readFile(String classpathResourceName) {
		InputStream classpathResourceInputStream = ClassLoader.getSystemResourceAsStream(classpathResourceName);
		if (null == classpathResourceInputStream) {
			throw new TestUtilsException("unable to obtain 'InputStream' of classpath resource [" + classpathResourceName + "]");
		}

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		byte[] inputStreamBuffer = new byte[INPUTSTREAM_READBUFFER_SIZE];
		int length;
		try {
			while ((length = classpathResourceInputStream.read(inputStreamBuffer)) != -1) {
				byteArrayOutputStream.write(inputStreamBuffer, 0, length);
			}
		} catch (IOException ioException) {
			throw new RuntimeException();
		} finally {
			try {
				classpathResourceInputStream.close();
			} catch (IOException ioException) {
				logger.warn("unable to close 'InputStream' of classpath resource [" + classpathResourceName + "]");
			}

			try {
				byteArrayOutputStream.flush();
			} catch (IOException ioException) {
				throw new TestUtilsException("unable to flush 'OutputStream' with content read from classpath resource [" + classpathResourceName + "]");
			}
		}

		byte[] outputStreamBuffer = byteArrayOutputStream.toByteArray();
		logger.info("read [" + outputStreamBuffer.length + "] bytes from file [" + classpathResourceName + "]");
		return outputStreamBuffer;
	}
}
