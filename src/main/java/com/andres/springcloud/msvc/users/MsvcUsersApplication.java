package com.andres.springcloud.msvc.users;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableDiscoveryClient
@ImportAutoConfiguration({FeignAutoConfiguration.class})
@EnableFeignClients("*")
@ComponentScan({
		"com.andres.springcloud.msvc.users", // <--- AGREGA ESTA LÍNEA
		"com.braidsbeautybyangie.sagapatternspringboot"
})
public class MsvcUsersApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsvcUsersApplication.class, args);
	}

}
