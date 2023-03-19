package org.eihq.quiltshow;

import org.eihq.quiltshow.configuration.RsaKeyProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@EnableConfigurationProperties(RsaKeyProperties.class)
@SpringBootApplication
public class QuiltshowApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(QuiltshowApplication.class, args);
	}


	@Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(QuiltshowApplication.class);
    }

}
