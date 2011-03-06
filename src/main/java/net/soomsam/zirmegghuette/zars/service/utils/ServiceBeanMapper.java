package net.soomsam.zirmegghuette.zars.service.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("serviceBeanMapper")
public class ServiceBeanMapper {
	@Autowired
	private Mapper dozerBeanMapper;

	public <T extends Object> T map(final Class<T> destinationType, final Object sourceObject) {
		if (null == destinationType) {
			throw new IllegalArgumentException("'destinationType' must not be null");
		}

		if (null == sourceObject) {
			return null;
		}

		return dozerBeanMapper.map(sourceObject, destinationType);
	}

	public <S, T extends Object> List<T> map(final Class<T> destinationType, final List<S> sourceObjectList) {
		if (null == destinationType) {
			throw new IllegalArgumentException("'destinationType' must not be null");
		}

		if (null == sourceObjectList) {
			return null;
		}

		final List<T> targetObjectList = new ArrayList<T>();
		for (final S sourceObject : sourceObjectList) {
			targetObjectList.add(map(destinationType, sourceObject));
		}
		return targetObjectList;
	}

	public <S, T extends Object> Set<T> map(final Class<T> destinationType, final Set<S> sourceObjectSet) {
		if (null == destinationType) {
			throw new IllegalArgumentException("'destinationType' must not be null");
		}

		if (null == sourceObjectSet) {
			return null;
		}

		final Set<T> targetObjectList = new HashSet<T>();
		for (final S sourceObject : sourceObjectSet) {
			targetObjectList.add(map(destinationType, sourceObject));
		}
		return targetObjectList;
	}
}
