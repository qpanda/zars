package net.soomsam.zirmegghuette.zars;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.soomsam.zirmegghuette.zars.enums.RoleType;
import net.soomsam.zirmegghuette.zars.service.bean.RoleBean;
import net.soomsam.zirmegghuette.zars.service.bean.UserBean;

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
	
	public static boolean containsRoleType(List<RoleBean> roleBeanList, RoleType roleType) {
		for (RoleBean roleBean : roleBeanList) {
			if (roleType.getRoleName().equals(roleBean.getName())) {
				return true;
			}
		}
		
		return false;
	}
	
	public static Set<Long> determineRoleIds(List<RoleBean> roleBeanList, RoleType... roleTypeArray) {
		Set<Long> roleIdSet = new HashSet<Long>();
		for (RoleBean roleBean : roleBeanList) {
			for (RoleType roleType : roleTypeArray) {
				if (roleType.getRoleName().equals(roleBean.getName())) {
					roleIdSet.add(roleBean.getRoleId());
				}
			}
		}
		
		return roleIdSet;
	}
	
	public static boolean containsUser(List<UserBean> userBeanList, long userId) {
		for (UserBean userBean : userBeanList) {
			if (userId == userBean.getUserId()) {
				return true;
			}
		}
		
		return false;
	}
}
