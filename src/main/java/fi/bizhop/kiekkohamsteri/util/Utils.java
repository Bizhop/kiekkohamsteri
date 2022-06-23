package fi.bizhop.kiekkohamsteri.util;

import fi.bizhop.kiekkohamsteri.controller.BaseController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class Utils {
	private static final Logger LOG = LogManager.getLogger(Utils.class);

	public static String[] getNullPropertyNames (Object source) {
		final BeanWrapper src = new BeanWrapperImpl(source);
		PropertyDescriptor[] pds = src.getPropertyDescriptors();

		Set<String> emptyNames = new HashSet<String>();
		for(PropertyDescriptor pd : pds) {
			Object srcValue = src.getPropertyValue(pd.getName());
			if (srcValue == null) emptyNames.add(pd.getName());
		}
		String[] result = new String[emptyNames.size()];
		return emptyNames.toArray(result);
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
