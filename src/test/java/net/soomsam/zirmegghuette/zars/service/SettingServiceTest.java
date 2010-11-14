package net.soomsam.zirmegghuette.zars.service;

import junit.framework.Assert;
import net.soomsam.zirmegghuette.zars.enums.SettingType;
import net.soomsam.zirmegghuette.zars.service.bean.SettingBean;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/WEB-INF/spring/core-context.xml" })
@Transactional
@TransactionConfiguration(transactionManager = "jpaTransactionManager")
public class SettingServiceTest {
	private final static Logger logger = Logger.getLogger(SettingServiceTest.class);

	@Autowired
	private SettingService settingService;

	@Test
	public void createSetting() {
		String settingValue = "value01";
		settingService.createSetting(SettingType.TEST, settingValue);
	}

	@Test
	public void findSetting() {
		String settingValue = "value02";
		settingService.createSetting(SettingType.TEST, settingValue);
		SettingBean settingBean = settingService.findSetting(SettingType.TEST);
		Assert.assertEquals(SettingType.TEST, settingBean.getSettingType());
		Assert.assertEquals(settingValue, settingBean.getValue());
		Assert.assertEquals(String.class, settingBean.getType());
	}

	@Test
	public void updateSetting() {
		Integer settingValue01 = 1;
		settingService.createSetting(SettingType.TEST, settingValue01);

		Integer settingValue02 = 2;
		settingService.updateSetting(SettingType.TEST, settingValue02);

		SettingBean settingBean = settingService.findSetting(SettingType.TEST);
		Assert.assertEquals(SettingType.TEST, settingBean.getSettingType());
		Assert.assertEquals(settingValue02, settingBean.getValue());
		Assert.assertEquals(Integer.class, settingBean.getType());
	}

	@Test
	public void updateSettingWithDifferentType() {
		String settingValue01 = "value01";
		settingService.createSetting(SettingType.TEST, settingValue01);
		SettingBean settingBean01 = settingService.findSetting(SettingType.TEST);
		Assert.assertEquals(SettingType.TEST, settingBean01.getSettingType());
		Assert.assertEquals(settingValue01, settingBean01.getValue());
		Assert.assertEquals(String.class, settingBean01.getType());

		Integer settingValue02 = 1234;
		settingService.updateSetting(SettingType.TEST, settingValue02);
		SettingBean settingBean02 = settingService.findSetting(SettingType.TEST);
		Assert.assertEquals(SettingType.TEST, settingBean02.getSettingType());
		Assert.assertEquals(settingValue02, settingBean02.getValue());
		Assert.assertEquals(Integer.class, settingBean02.getType());
	}

	@Test
	public void findNonExistingSetting() {
		SettingBean settingBean = settingService.findSetting(SettingType.TEST);
		Assert.assertNull(settingBean);
	}
}