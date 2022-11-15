package org.eihq.quiltshow.configuration;

import java.security.interfaces.RSAPublicKey;
import java.security.interfaces.RSAPrivateKey;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@ConfigurationProperties(prefix="rsa")
@Data
public class RsaKeyProperties {

	RSAPublicKey publicKey;
	RSAPrivateKey privateKey;
}
