package fi.bizhop.kiekkohamsteri.search.discs;

import fi.bizhop.kiekkohamsteri.model.Disc;
import fi.bizhop.kiekkohamsteri.search.SearchCriteria;
import fi.bizhop.kiekkohamsteri.search.SearchOperation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.persistence.criteria.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.EnumSource.Mode.EXCLUDE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class DiscSpecificationTest {
    @Mock
    Root<Disc> discRoot;

    @Mock
    Path<Object> path;

    @Mock
    CriteriaBuilder cb;

    @Mock
    CriteriaBuilder.In<Object> cbIn;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void givenCriteriaValueIsNull_whenToPredicate_thenReturnNull() {
        var criteria = new SearchCriteria("weight", null, SearchOperation.EQUAL);

        var spec = DiscSpecificationBuilder.builder().with(criteria).build();

        var predicate = spec.toPredicate(discRoot, mock(CriteriaQuery.class), cb);

        assertNull(predicate);
    }

    @Test
    void givenCriteriaKeyIsNull_whenToPredicate_thenReturnNull() {
        var criteria = new SearchCriteria(null, 170, SearchOperation.EQUAL);

        var spec = DiscSpecificationBuilder.builder().with(criteria).build();

        var predicate = spec.toPredicate(discRoot, mock(CriteriaQuery.class), cb);

        assertNull(predicate);
    }

    @Test
    void givenNumberComparisonQueryWithInvalidValue_whenToPredicate_thenReturnNull() {
        var criteria = new SearchCriteria("weight", "this is not integer", SearchOperation.LESS_THAN);

        var spec = DiscSpecificationBuilder.builder().with(criteria).build();

        var predicate = spec.toPredicate(discRoot, mock(CriteriaQuery.class), cb);

        assertNull(predicate);
    }

    @Test
    void givenNumberComparisonQuery_whenToPredicate_thenReturnPredicate() {
        var key = "weight";
        var criteria = new SearchCriteria(key, 170, SearchOperation.LESS_THAN);

        var spec = DiscSpecificationBuilder.builder().with(criteria).build();

        var mockPredicate = mock(Predicate.class);
        when(discRoot.get(key)).thenReturn(path);
        when(cb.lessThan(any(), eq(170))).thenReturn(mockPredicate);
        var predicate = spec.toPredicate(discRoot, mock(CriteriaQuery.class), cb);

        assertEquals(mockPredicate, predicate);
    }

    @ParameterizedTest
    @EnumSource(value = SearchOperation.class, names = {"IN", "NOT_IN"}, mode = EXCLUDE)
    void givenValidCriteria_whenToPredicate_thenCallRightMethodOnCriteriaBuilder(SearchOperation operation) {
        var key = "weight";
        var criteria = new SearchCriteria(key, 170, operation);

        var spec = DiscSpecificationBuilder.builder().with(criteria).build();
        when(discRoot.get(key)).thenReturn(path);
        spec.toPredicate(discRoot, mock(CriteriaQuery.class), cb);

        switch (operation) {
            case GREATER_THAN:
                verify(cb, times(1)).greaterThan(any(), any(Integer.class));
                break;
            case GREATER_THAN_EQUAL:
                verify(cb, times(1)).greaterThanOrEqualTo(any(), any(Integer.class));
                break;
            case LESS_THAN:
                verify(cb, times(1)).lessThan(any(), any(Integer.class));
                break;
            case LESS_THAN_EQUAL:
                verify(cb, times(1)).lessThanOrEqualTo(any(), any(Integer.class));
                break;
            case EQUAL:
                verify(cb, times(1)).equal(any(), any(Integer.class));
                break;
            case NOT_EQUAL:
                verify(cb, times(1)).notEqual(any(), any(Integer.class));
                break;
            default:
                fail();
        }
    }

    @Test
    void givenValidInCriteria_whenToPredicate_thenCallInMethodOnCriteriaBuilder() {
        var key = "weight";
        var criteria = new SearchCriteria(key, 170, SearchOperation.IN);

        var spec = DiscSpecificationBuilder.builder().with(criteria).build();
        when(discRoot.get(key)).thenReturn(path);
        when(cb.in(any())).thenReturn(cbIn);
        spec.toPredicate(discRoot, mock(CriteriaQuery.class), cb);

        verify(cb, times(1)).in(any());
        verify(cbIn, times(1)).value(any(Integer.class));
    }

    @Test
    void givenValidNotInCriteria_whenToPredicate_thenCallNotInMethodOnCriteriaBuilder() {
        var key = "weight";
        var criteria = new SearchCriteria(key, 170, SearchOperation.NOT_IN);

        var spec = DiscSpecificationBuilder.builder().with(criteria).build();
        when(discRoot.get(key)).thenReturn(path);
        when(cb.in(any())).thenReturn(cbIn);
        when(cbIn.value(any(Integer.class))).thenReturn(cbIn);
        spec.toPredicate(discRoot, mock(CriteriaQuery.class), cb);

        verify(cb, times(1)).in(any());
        verify(cbIn, times(1)).value(any(Integer.class));
        verify(cbIn, times(1)).not();
    }
}
