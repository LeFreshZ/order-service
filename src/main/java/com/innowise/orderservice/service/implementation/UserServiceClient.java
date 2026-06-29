package com.innowise.orderservice.service.implementation;

import com.innowise.orderservice.dto.UserResponse;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class UserServiceClient {

  private final WebClient webClient;
  private final ReactiveCircuitBreakerFactory<?, ?> circuitBreakerFactory;

  public UserServiceClient(
      WebClient.Builder builder,
      ReactiveCircuitBreakerFactory<?, ?> circuitBreakerFactory,
      @Value("${user-service.url}") String userServiceUrl) {

    this.webClient = builder.baseUrl(userServiceUrl).build();
    this.circuitBreakerFactory = circuitBreakerFactory;
  }

  public Optional<UserResponse> getUserById(Long userId) {
    UserResponse response = circuitBreakerFactory.create("user-service")
        .run(
            webClient.get()
                .uri("/users/{id}", userId)
                .retrieve()
                .bodyToMono(UserResponse.class),
            throwable -> Mono.empty()
        ).block();

    return Optional.ofNullable(response);
  }
}
