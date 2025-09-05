package co.com.pragma.api.requests;

import co.com.pragma.api.requests.dto.*;
import co.com.pragma.jjwtsecurity.jwt.provider.JwtProvider;
import co.com.pragma.model.requests.Requests;
import co.com.pragma.usecase.requests.RequestsUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
@RestController
@Tag(name = "Request", description = "Request endpoints")
@Slf4j
public class Handler {
    private final RequestsUseCase requestsUseCase;
    private final JwtProvider jwtProvider;

    public Handler(RequestsUseCase requestsUseCase, JwtProvider jwtProvider) {
        this.requestsUseCase = requestsUseCase;
        this.jwtProvider = jwtProvider;
    }

    @PostMapping(path = "/api/v1/request", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Create a new request",
            description = "Returns the information about the new request created.",
            requestBody = @RequestBody(
                    required = true,
                    description = "Request information required.",
                    content = @Content(schema = @Schema(implementation = CreateRequestsRequestDTO.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Created",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = CreateRequestsReponseDTO.class)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "Bad request",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = CreateRequestBRResponseDTO.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UnauthorizedDTO.class))),
                    @ApiResponse(responseCode = "403", description = "Forbidden",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UnauthorizedDTO.class))),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = CreateRequestsFailResponseDTO.class)))
            }
    )
    @SecurityRequirement(name = "Authorization")
    @PreAuthorize("hasAnyAuthority('CLIENTE')")
    public Mono<ServerResponse> listenSaveRequest(ServerRequest serverRequest) {
        String authHeader = serverRequest.headers().firstHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return errorResponse(401, "Unauthorized","Missing or invalid Authorization header", HttpStatus.UNAUTHORIZED);
        }
        String token = authHeader.substring(7);
        return serverRequest.bodyToMono(Requests.class)
                .doOnNext(request -> log.info("📥 Se va a resistrar la solicitud: {}", request))
                .flatMap(request -> {
                            String subject = jwtProvider.getSubject(token);

                            if (!subject.equalsIgnoreCase(request.getEmail())) {
                                return errorResponse(401, "Unauthorized","Token subject does not match email", HttpStatus.UNAUTHORIZED);
                            }

                            return requestsUseCase.saveRequests(request)
                                    .flatMap(savedRequest -> {
                                        URI url = UriComponentsBuilder
                                                .fromUriString("/api/v1/request/{id}")
                                                .buildAndExpand(savedRequest.getId())
                                                .toUri();
                                        return ServerResponse.created(url)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .bodyValue(savedRequest);
                                    });
                        })
                .doOnError(ex -> {
                    log.error("❌ Ocurrió un error en el proceso: {}", ex.getMessage());
                })
                .doOnNext(savedRequest -> log.info("✅ solicitud almacenada en la base de datos: {}", savedRequest))
                .onErrorResume(IllegalArgumentException.class, e -> {
                    log.warn("Error al validar los datos de la solicitud: {}", e.getMessage());
                    return errorResponse(400, "Bad request",e.getMessage(), HttpStatus.BAD_REQUEST);
                })
                .onErrorResume(e -> {
                    log.error("Internal server error", e);
                    return ServerResponse.status(500).bodyValue("Error interno: " + e.getMessage());
                });
    }

    public Mono<ServerResponse> listenGetAllRequests(ServerRequest serverRequest) {
        return ServerResponse.ok()
                //.contentType(MediaType.APPLICATION_JSON)
                //.contentType(MediaType.APPLICATION_NDJSON)
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(requestsUseCase.findAllRequests(), Requests.class);
    }

    public Mono<ServerResponse> listenGetRequestByIdentityNumber(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");

        return requestsUseCase.getRequestsByIdentityNumber(id)
                .flatMap(task -> ServerResponse.ok()
                        .contentType(MediaType.TEXT_EVENT_STREAM)
                        .bodyValue(task))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    private Mono<ServerResponse> errorResponse(Integer code,String error, String message, HttpStatus httpStatus) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", code);
        errorResponse.put("timestamp", LocalDateTime.now().toString());
        errorResponse.put("error", error);
        errorResponse.put("message", message);
        return ServerResponse.status(httpStatus).bodyValue(errorResponse);
    }

}
