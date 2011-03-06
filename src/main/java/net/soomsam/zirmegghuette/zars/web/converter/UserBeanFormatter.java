package net.soomsam.zirmegghuette.zars.web.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import net.soomsam.zirmegghuette.zars.service.bean.UserBean;
import net.soomsam.zirmegghuette.zars.utils.FormatterUtils;

public class UserBeanFormatter implements Converter {
	@Override
	public Object getAsObject(final FacesContext facesContext, final UIComponent uiComponent, final String value) {
		throw new ConversionNotSupportedException("'String' to 'UserBean' conversion not supported");
	}

	@Override
	public String getAsString(final FacesContext facesContext, final UIComponent uiComponent, final Object value) {
		if (value instanceof UserBean) {
			final UserBean userBean = (UserBean) value;
			return FormatterUtils.format(userBean);
		}

		return null;
	}
}
