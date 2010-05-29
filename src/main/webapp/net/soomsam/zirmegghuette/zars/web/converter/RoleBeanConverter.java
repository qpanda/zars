package net.soomsam.zirmegghuette.zars.web.converter;

import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import net.soomsam.zirmegghuette.zars.service.bean.RoleBean;

import org.primefaces.component.picklist.PickList;
import org.primefaces.model.DualListModel;

public class RoleBeanConverter implements Converter {
	@Override
	public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String value) {
		Long roleId;
		try {
			roleId = Long.valueOf(value);
		} catch (NumberFormatException numberFormatException) {
			return null;
		}

		if (uiComponent instanceof PickList) {
			PickList pickList = (PickList) uiComponent;
			DualListModel<RoleBean> roleDualListModel = (DualListModel<RoleBean>) pickList.getValue();
			List<RoleBean> availableRoles = roleDualListModel.getSource();
			for (RoleBean roleBean : availableRoles) {
				if (roleId.equals(roleBean.getRoleId())) {
					return roleBean;
				}
			}
		}

		return null;
	}

	@Override
	public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object value) {
		if (value instanceof Long) {
			return String.valueOf(value);
		}

		return null;
	}
}