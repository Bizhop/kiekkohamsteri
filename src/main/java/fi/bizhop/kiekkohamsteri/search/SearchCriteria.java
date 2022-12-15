package fi.bizhop.kiekkohamsteri.search;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchCriteria {
    String key;
    Object value;
    SearchOperation operation;
}
