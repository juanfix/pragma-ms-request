package co.com.pragma.api.requests.dto.get;

import co.com.pragma.model.requests.Requests;

import java.util.List;

public record GetRequestsByFiltersResponseDTO(
        List<Requests> request,
        Integer page,
        Integer size,
        Integer total

) {
}
