package net.soomsam.zirmegghuette.zars.utils;

import net.soomsam.zirmegghuette.zars.service.bean.UserBean;

import org.apache.commons.lang.StringUtils;

public class FormatterUtils {
	public static String format(final UserBean userBean) {
		StringBuffer stringBuffer = new StringBuffer(userBean.getUsername());
		if (!StringUtils.isEmpty(userBean.getFirstName()) && !StringUtils.isEmpty(userBean.getLastName())) {
			stringBuffer.append(" (");
			stringBuffer.append(userBean.getFirstName());
			stringBuffer.append(" ");
			stringBuffer.append(userBean.getLastName());
			stringBuffer.append(")");
		} else {
			stringBuffer.append(" <");
			stringBuffer.append(userBean.getEmailAddress());
			stringBuffer.append(">");
		}

		return stringBuffer.toString();
	}
}
