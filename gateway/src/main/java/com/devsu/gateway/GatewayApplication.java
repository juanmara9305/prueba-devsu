package com.devsu.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}

	@Bean
	public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
		return builder.routes()
			// Route for person-service: Client operations
			.route("person_service_route", r -> r.path("/api/clientes/**")
				.uri("http://localhost:8082"))
			
			// Route for account-service: Account operations
			.route("account_service_accounts_route", r -> r.path("/api/cuentas/**")
				.uri("http://localhost:8081"))
			
			// Route for account-service: Transaction operations
			.route("account_service_transactions_route", r -> r.path("/api/movimientos/**")
				.uri("http://localhost:8081"))
			
			// Route for account-service: Report operations
			.route("account_service_reports_route", r -> r.path("/api/reportes/**")
				.uri("http://localhost:8081"))
			
			.build();
	}

}
