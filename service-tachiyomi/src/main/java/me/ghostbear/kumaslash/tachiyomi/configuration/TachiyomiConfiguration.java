package me.ghostbear.kumaslash.tachiyomi.configuration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(TachiyomiProperties.class)
public class TachiyomiConfiguration {

}
