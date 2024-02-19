package kumaslash.sentry.jda;

import io.sentry.ITransaction;
import io.sentry.Sentry;
import io.sentry.SpanStatus;
import net.dv8tion.jda.api.events.GenericEvent;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Aspect
@Component
public class JdaAspect {

	private static final String EVENT_OPERATION = "discord_event";
	private static final String SLASH_COMMAND_OPERATION = "discord_slash_command";

	@Pointcut("execution(@kumaslash.jda.annotations.EventMapping * *(..))")
	public void jdaEventMapping() {}

	@Pointcut("execution(@kumaslash.jda.annotations.SlashCommandMapping * *(..))")
	public void jdaSlashCommandMapping() {}

	@Pointcut(value = "execution(void *(*)) && args(object)", argNames = "object")
	public void consumer(GenericEvent object) {}

	@Pointcut(value = "(jdaEventMapping() || jdaSlashCommandMapping()) && consumer(object)", argNames = "object")
	public void jdaMapping(GenericEvent object) {}

	@Around(value = "jdaEventMapping() && consumer(object)", argNames = "proceedingJoinPoint,object")
	public void jdaEventMappingTransaction(ProceedingJoinPoint proceedingJoinPoint, GenericEvent object) throws Throwable {
		jdaMappingTransaction(proceedingJoinPoint, object, EVENT_OPERATION);
	}

	@Around(value = "jdaSlashCommandMapping() && consumer(object)", argNames = "proceedingJoinPoint,object")
	public void jdaSlashCommandMappingTransaction(ProceedingJoinPoint proceedingJoinPoint, GenericEvent object) throws Throwable {
		jdaMappingTransaction(proceedingJoinPoint, object, SLASH_COMMAND_OPERATION);
	}

	void jdaMappingTransaction(ProceedingJoinPoint proceedingJoinPoint, GenericEvent object, String slashCommandOperation) throws Throwable {
		Signature signature = proceedingJoinPoint.getSignature();
		ITransaction transaction = Sentry.startTransaction(signature.getName(), slashCommandOperation);
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

}
