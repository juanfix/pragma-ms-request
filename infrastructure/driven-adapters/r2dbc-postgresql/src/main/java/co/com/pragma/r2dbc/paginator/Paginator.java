package co.com.pragma.r2dbc.paginator;

import co.com.pragma.model.requests.dto.PageCriteria;
import co.com.pragma.model.requests.dto.PagedSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Paginator {
    private final R2dbcEntityTemplate template;

    public <T> Mono<PagedSummary<T>> paginate(
            Criteria criteria,
            Class<T> type,
            PageCriteria request
    ) {
        Query query = Query.query(criteria);

        Query pagedQuery = query
                .limit(request.size())
                .offset(request.offset())
                .sort(Sort.by(Sort.Direction.DESC, "id"));

        Mono<Long> total = template.count(query, type);

        return template.select(pagedQuery, type)
                .collectList()
                .zipWith(total)
                .map(tuple -> new PagedSummary<>(
                        tuple.getT1(),
                        request.page() + 1,
                        request.size(),
                        tuple.getT2()
                ));
    }

}
