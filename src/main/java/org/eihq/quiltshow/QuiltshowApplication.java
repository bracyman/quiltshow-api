package org.eihq.quiltshow;

import org.eihq.quiltshow.configuration.RsaKeyProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(RsaKeyProperties.class)
@SpringBootApplication
public class QuiltshowApplication {

	public static void main(String[] args) {
		SpringApplication.run(QuiltshowApplication.class, args);
	}

}
