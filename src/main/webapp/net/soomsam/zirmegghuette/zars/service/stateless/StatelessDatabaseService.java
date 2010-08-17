package net.soomsam.zirmegghuette.zars.service.stateless;

import net.soomsam.zirmegghuette.zars.service.DatabaseService;
import net.soomsam.zirmegghuette.zars.service.GroupReservationService;
import net.soomsam.zirmegghuette.zars.service.ServiceException;
import net.soomsam.zirmegghuette.zars.service.SettingService;
import net.soomsam.zirmegghuette.zars.service.UserService;
import net.soomsam.zirmegghuette.zars.service.bean.SettingBean;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class StatelessDatabaseService implements DatabaseService {
	private final static Logger logger = Logger.getLogger(StatelessDatabaseService.class);

	public static final String DATABASESCHEMAVERSION_SETTINGNAME = "DATABASE_SCHEMA_VERSION";
	public static final String DATABASESCHEMASTATE_SETTINGNAME = "DATABASE_SCHEMA_STATE";
	public static final String DATABASESCHEMASTATE_INITIALIZED_SETTINGVALUE = "INITIALIZED";
	public static final Integer DATABASESCHEMAVERSION_SUPPORTED_SETTINGVALUE = 1;

	@Autowired
	private SettingService settingService;

	@Autowired
	private UserService userService;
	
	@Autowired
	private GroupReservationService groupReservationService;

	@Override
	public void createInitialDataSet() {
		final SettingBean databaseSchemaVersionSetting = settingService.findSetting(DATABASESCHEMAVERSION_SETTINGNAME);
		final SettingBean databaseSchemaStateSetting = settingService.findSetting(DATABASESCHEMASTATE_SETTINGNAME);
		if (!isSupportedDatabaseSchemaVersionSetting(databaseSchemaVersionSetting)) {
			throw new ServiceException("detected unsupported schema version setting [" + databaseSchemaVersionSetting + "]");
		}

		if (isDatabaseInitializationRequired(databaseSchemaStateSetting)) {
			populateInitialDataSet();
		}
	}

	protected boolean isSupportedDatabaseSchemaVersionSetting(final SettingBean databaseSchemaVersionSetting) {
		if (null == databaseSchemaVersionSetting) {
			return true;
		}

		if (!Integer.class.equals(databaseSchemaVersionSetting.getType())) {
			return false;
		}

		final Integer databaseSchemaVersion = (Integer) databaseSchemaVersionSetting.getValue();
		if (null == databaseSchemaVersion) {
			return true;
		}

		return DATABASESCHEMAVERSION_SUPPORTED_SETTINGVALUE == databaseSchemaVersion;
	}

	protected boolean isDatabaseInitializationRequired(final SettingBean databaseSchemaStateSetting) {
		if (null == databaseSchemaStateSetting) {
			return true;
		}

		return false;
	}

	public void populateInitialDataSet() {
		logger.info("populating database with initial data set");

		settingService.createSetting(DATABASESCHEMAVERSION_SETTINGNAME, DATABASESCHEMAVERSION_SUPPORTED_SETTINGVALUE);
		settingService.createSetting(DATABASESCHEMASTATE_SETTINGNAME, DATABASESCHEMASTATE_INITIALIZED_SETTINGVALUE);
		userService.createAllRoles();
		userService.createDefaultUsers();
		groupReservationService.createAllRooms();
	}
}
