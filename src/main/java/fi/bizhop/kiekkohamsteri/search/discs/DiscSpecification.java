package fi.bizhop.kiekkohamsteri.search.discs;

import fi.bizhop.kiekkohamsteri.model.Disc;
import fi.bizhop.kiekkohamsteri.search.SearchCriteria;
import fi.bizhop.kiekkohamsteri.search.SearchOperation;
import fi.bizhop.kiekkohamsteri.util.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Set;

import static fi.bizhop.kiekkohamsteri.search.SearchOperation.EQUAL;
import static fi.bizhop.kiekkohamsteri.search.SearchOperation.NOT_EQUAL;

@RequiredArgsConstructor
public class DiscSpecification implements Specification<Disc> {
    private final SearchCriteria criteria;

    @Override
    public Predicate toPredicate(@NonNull Root<Disc> root, @NonNull CriteriaQuery<?> cq, @NonNull CriteriaBuilder cb) {
        if(this.criteria.getValue() == null || this.criteria.getKey() == null) return null;
        if(!isOperationSupported(this.criteria.getOperation(), this.criteria.getKey())) return null;

        switch (this.criteria.getOperation()) {
            case GREATER_THAN:
            case GREATER_THAN_EQUAL:
            case LESS_THAN:
            case LESS_THAN_EQUAL:
                return handleNumberComparison(root, cb);
            case EQUAL:
                return cb.equal(root.get(this.criteria.getKey()), this.criteria.getValue());
            case NOT_EQUAL:
                return cb.notEqual(root.get(this.criteria.getKey()), this.criteria.getValue());
            case IN:
                return cb.in(root.get(this.criteria.getKey())).value(this.criteria.getValue());
            case NOT_IN:
                return cb.in(root.get(this.criteria.getKey())).value(this.criteria.getValue()).not();
            default:
                return null;
        }
    }

    private boolean isOperationSupported(SearchOperation operation, String key) {
        //equals operations are always supported
        if(operation.equals(EQUAL) || operation.equals(NOT_EQUAL)) return true;

        return Utils.getSupportedOperations().stream()
                .anyMatch(supportedOperation ->
                        supportedOperation.getField().equals(key) && supportedOperation.getOperations().contains(operation));
    }

    private Predicate handleNumberComparison(Root<Disc> root, CriteriaBuilder cb) {
        var key = this.criteria.getKey();
        if(Utils.COMPARABLE_NUMBER_FIELDS.contains(key)) {
            switch (this.criteria.getOperation()) {
                case GREATER_THAN:
                    return cb.greaterThan(root.get(key), getValueNumber());
                case GREATER_THAN_EQUAL:
                    return cb.greaterThanOrEqualTo(root.get(key), getValueNumber());
                case LESS_THAN:
                    return cb.lessThan(root.get(key), getValueNumber());
                case LESS_THAN_EQUAL:
                    return cb.lessThanOrEqualTo(root.get(key), getValueNumber());
                default:
                    return null;
            }
        }
        return null;
    }

    private Integer getValueNumber() {
        try {
            return Integer.valueOf(this.criteria.getValue().toString());
        } catch (Exception e) {
            return null;
        }
    }
}
