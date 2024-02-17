package kumaslash.sentry.jda;

import io.sentry.ITransaction;
import io.sentry.Sentry;
import io.sentry.SpanStatus;
import kumaslash.jda.annotations.EventMapping;
import kumaslash.jda.annotations.SlashCommandMapping;
import net.dv8tion.jda.api.events.GenericEvent;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Objects;

@Aspect
@Component
public class JdaAspect {

	private static final String EVENT_OPERATION = "discord_event";
	private static final String SLASH_COMMAND_OPERATION = "discord_slash_command";
	private static final String UNKNOWN_OPERATION = "discord_unknown";

	@Pointcut("execution(@kumaslash.jda.annotations.EventMapping * *(..))")
	public void jdaEventMapping() {}

	@Pointcut("execution(@kumaslash.jda.annotations.SlashCommandMapping * *(..))")
	public void jdaSlashCommandMapping() {}

	@Pointcut(value = "execution(void *(*)) && args(object)", argNames = "object")
	public void consumer(GenericEvent object) {}

	@Pointcut(value = "(jdaEventMapping() || jdaSlashCommandMapping()) && consumer(object)", argNames = "object")
	public void jdaMapping(GenericEvent object) {}

	@Around(value = "jdaMapping(object)", argNames = "proceedingJoinPoint,object")
	public void jdaMappingPerformanceMetrics(ProceedingJoinPoint proceedingJoinPoint, GenericEvent object) throws Throwable {
		Signature signature = proceedingJoinPoint.getSignature();
		ITransaction transaction = Sentry.startTransaction(signature.getName(), getOperationName(signature, object.getClass()));
		try {
			proceedingJoinPoint.proceed();
		} catch (Throwable e) {
			transaction.setThrowable(e);
			transaction.setStatus(SpanStatus.UNKNOWN_ERROR);
			transaction.setData("event", Objects.requireNonNullElse(object.getRawData(), "missing raw data"));
			throw e;
		} finally {
			transaction.finish();
		}
	}

	String getOperationName(Signature signature, Class<?> argumentClass) {
		try {
			Method declaredMethod = signature.getDeclaringType().getDeclaredMethod(signature.getName(), argumentClass);
			return getOperationName(declaredMethod);
		} catch (NoSuchMethodException e) {
			return UNKNOWN_OPERATION;
		}
	}

	String getOperationName(Method method) {
		System.out.println(method.getName());
		if (Objects.nonNull(method.getAnnotation(EventMapping.class))) {
			return EVENT_OPERATION;
		} else if (Objects.nonNull(method.getAnnotation(SlashCommandMapping.class))) {
			return SLASH_COMMAND_OPERATION;
		} else {
			return UNKNOWN_OPERATION;
		}
	}


}
