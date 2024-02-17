package kumaslash.sentry.jda;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.ComponentScan;

@AutoConfiguration
@ComponentScan(basePackages = "kumaslash.sentry.jda")
@ConditionalOnClass(name = "kumaslash.jda.autoconfig.JDAAutoConfiguration")
public class JdaAspectAutoConfiguration {

}
