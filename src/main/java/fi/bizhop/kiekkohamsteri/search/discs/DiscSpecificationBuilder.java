package fi.bizhop.kiekkohamsteri.search.discs;

import fi.bizhop.kiekkohamsteri.model.Disc;
import fi.bizhop.kiekkohamsteri.search.SearchCriteria;
import fi.bizhop.kiekkohamsteri.search.SearchOperation;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class DiscSpecificationBuilder {
    private final List<SearchCriteria> criteria = new ArrayList<>();

    private DiscSpecificationBuilder() {}

    public static DiscSpecificationBuilder builder() {
        return new DiscSpecificationBuilder();
    }

    public DiscSpecificationBuilder with(String key, SearchOperation operation, Object value) {
        this.criteria.add(new SearchCriteria(key, value, operation));
        return this;
    }

    public DiscSpecificationBuilder with(SearchCriteria criteria) {
        this.criteria.add(criteria);
        return this;
    }

    public Specification<Disc> build() {
        if(criteria.isEmpty()) return null;

        Specification<Disc> result = new DiscSpecification(criteria.get(0));
        for(var criteria : this.criteria.subList(1, this.criteria.size())) {
            result = Specification.where(result).and(new DiscSpecification(criteria));
        }
        return result;
    }
}
