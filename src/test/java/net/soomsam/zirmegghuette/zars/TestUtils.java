package net.soomsam.zirmegghuette.zars;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.soomsam.zirmegghuette.zars.enums.RoleType;
import net.soomsam.zirmegghuette.zars.persistence.entity.BaseEntity;
import net.soomsam.zirmegghuette.zars.service.bean.RoleBean;
import net.soomsam.zirmegghuette.zars.service.bean.UserBean;

import org.apache.log4j.Logger;

public class TestUtils {
	private final static Logger logger = Logger.getLogger(TestUtils.class);

	private final static int INPUTSTREAM_READBUFFER_SIZE = 512;

	public static byte[] readFile(final String classpathResourceName) {
		final InputStream classpathResourceInputStream = ClassLoader.getSystemResourceAsStream(classpathResourceName);
		if (null == classpathResourceInputStream) {
			throw new TestUtilsException("unable to obtain 'InputStream' of classpath resource [" + classpathResourceName + "]");
		}

		final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		final byte[] inputStreamBuffer = new byte[INPUTSTREAM_READBUFFER_SIZE];
		int length;
		try {
			while ((length = classpathResourceInputStream.read(inputStreamBuffer)) != -1) {
				byteArrayOutputStream.write(inputStreamBuffer, 0, length);
			}
		} catch (final IOException ioException) {
			throw new RuntimeException();
		} finally {
			try {
				classpathResourceInputStream.close();
			} catch (final IOException ioException) {
				logger.warn("unable to close 'InputStream' of classpath resource [" + classpathResourceName + "]");
			}

			try {
				byteArrayOutputStream.flush();
			} catch (final IOException ioException) {
				throw new TestUtilsException("unable to flush 'OutputStream' with content read from classpath resource [" + classpathResourceName + "]");
			}
		}

		final byte[] outputStreamBuffer = byteArrayOutputStream.toByteArray();
		logger.info("read [" + outputStreamBuffer.length + "] bytes from file [" + classpathResourceName + "]");
		return outputStreamBuffer;
	}

	public static boolean containsRoleType(final List<RoleBean> roleBeanList, final RoleType roleType) {
		for (final RoleBean roleBean : roleBeanList) {
			if (roleType.getRoleName().equals(roleBean.getName())) {
				return true;
			}
		}

		return false;
	}

	public static Set<Long> determineRoleIds(final List<RoleBean> roleBeanList, final RoleType... roleTypeArray) {
		final Set<Long> roleIdSet = new HashSet<Long>();
		for (final RoleBean roleBean : roleBeanList) {
			for (final RoleType roleType : roleTypeArray) {
				if (roleType.getRoleName().equals(roleBean.getName())) {
					roleIdSet.add(roleBean.getRoleId());
				}
			}
		}

		return roleIdSet;
	}

	public static boolean containsUser(final List<UserBean> userBeanList, final long userId) {
		for (final UserBean userBean : userBeanList) {
			if (userId == userBean.getUserId()) {
				return true;
			}
		}

		return false;
	}

	public static <Entity extends BaseEntity> boolean containsEntity(final Set<Entity> entitySet, final Entity otherEntity) {
		for (final Entity entity : entitySet) {
			if (entity.sameValues(otherEntity)) {
				return true;
			}
		}

		return false;
	}
}
