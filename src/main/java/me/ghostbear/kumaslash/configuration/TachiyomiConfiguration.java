package me.ghostbear.kumaslash.configuration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(TachiyomiProperties.class)
public class TachiyomiConfiguration {

}
