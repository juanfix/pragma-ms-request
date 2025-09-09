package co.com.pragma.api.requests;

import co.com.pragma.api.config.GlobalPath;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@RequiredArgsConstructor
public class RouterTest {
    private final GlobalPath globalPath;
    private final Handler userHandler;

    @Bean
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(POST(globalPath.getGlobal() + "/request"), userHandler::listenSaveRequest)
                .andRoute(GET(globalPath.getGlobal()+ "/request"), userHandler::listenGetAllRequests)
                .andRoute(GET(globalPath.getGlobal()+ "/request/by-filter"), userHandler::listenGetAllRequestsByFilter)
                .andRoute(GET(globalPath.getGlobal() + "/request/{id}"), userHandler::listenGetRequestByIdentityNumber);
    }
}
