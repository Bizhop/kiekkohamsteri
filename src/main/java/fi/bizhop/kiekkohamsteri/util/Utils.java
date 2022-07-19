package fi.bizhop.kiekkohamsteri.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class Utils {
	private static final Logger LOG = LogManager.getLogger(Utils.class);

	public static List<String> getNullPropertyNames (Object source) {
		final BeanWrapper src = new BeanWrapperImpl(source);
		PropertyDescriptor[] pds = src.getPropertyDescriptors();

		Set<String> emptyNames = new HashSet<>();
		for(PropertyDescriptor pd : pds) {
			Object srcValue = src.getPropertyValue(pd.getName());
			if (srcValue == null) emptyNames.add(pd.getName());
		}
		return new ArrayList<>(emptyNames);
	}

	public static Date asDate(LocalDate localDate) {
		return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
	}

	public static Long getLongFromEnv(String key, Long defaultValue) {
		var valueString = System.getProperty(key);
		if(valueString == null) return defaultValue;

		try {
			return Long.valueOf(valueString);
		} catch (NumberFormatException e) {
			LOG.warn("Invalid long: {}", valueString);
			return defaultValue;
		}
	}
}
