package fi.bizhop.kiekkohamsteri.util;

import fi.bizhop.kiekkohamsteri.model.User;
import fi.bizhop.kiekkohamsteri.search.SearchOperation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import static fi.bizhop.kiekkohamsteri.search.SearchOperation.*;

public class Utils {
	private static final Logger LOG = LogManager.getLogger(Utils.class);

	public static final String USER_ROLE_ADMIN = "ADMIN";
	public static final String USER_ROLE_GROUP_ADMIN = "GROUP_ADMIN";

	public static final Set<String> COMPARABLE_NUMBER_FIELDS = Set.of("weight", "price", "condition");
	public static final Set<String> BOOLEAN_FIELDS = Set.of("dyed", "special", "swirly", "forSale", "lostAndFound", "itb", "publicDisc", "lost");

	private static final List<SupportedOperation> SUPPORTED_OPERATIONS = new ArrayList<>();

	static {
		//add comparable number fields
		final Set<SearchOperation> operationsForComparableNumberFields = new LinkedHashSet<>(List.of(GREATER_THAN, GREATER_THAN_EQUAL, LESS_THAN, LESS_THAN_EQUAL, EQUAL, NOT_EQUAL, IN, NOT_IN));
		COMPARABLE_NUMBER_FIELDS.forEach(field -> SUPPORTED_OPERATIONS.add(new SupportedOperation(field, "number", operationsForComparableNumberFields)));

		//add boolean fields
		final Set<SearchOperation> operationsForBooleans = new LinkedHashSet<>(List.of(EQUAL));
		BOOLEAN_FIELDS.forEach(field -> SUPPORTED_OPERATIONS.add(new SupportedOperation(field, "boolean", operationsForBooleans)));
	}

	public static List<SupportedOperation> getSupportedOperations() {
		return List.copyOf(SUPPORTED_OPERATIONS);
	}

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

	public static boolean userIsAdmin(User user) {
		if(user == null || user.getRoles() == null) return false;
		return user.getRoles().stream()
				.anyMatch(role -> USER_ROLE_ADMIN.equals(role.getName()));
	}

	public static boolean userIsGroupAdmin(User user, Long groupId) {
		if(user == null || user.getRoles() == null || groupId == null) return false;
		return user.getRoles().stream()
				.anyMatch(role -> USER_ROLE_GROUP_ADMIN.equals(role.getName()) && groupId.equals(role.getGroupId()));
	}

	public static boolean userBelongsToGroup(User user, Long groupId) {
		if(user == null || user.getGroups() == null || groupId == null) return false;
		return user.getGroups().stream()
				.anyMatch(group -> groupId.equals(group.getId()));
	}

	@AllArgsConstructor
	@Getter
	public static class SupportedOperation {
		final String field;
		final String type;
		final Set<SearchOperation> operations;
	}
}
