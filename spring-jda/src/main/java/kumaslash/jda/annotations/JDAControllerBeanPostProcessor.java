/*
 * Copyright (C) 2023 ghostbear
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package kumaslash.jda.annotations;

import kumaslash.jda.SpringEventManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

@Component
public class JDAControllerBeanPostProcessor implements BeanPostProcessor {

	private static final Logger LOG = LoggerFactory.getLogger(JDAControllerBeanPostProcessor.class);

	private final SpringEventManager eventManager;

	public JDAControllerBeanPostProcessor(SpringEventManager eventManager) {
		this.eventManager = eventManager;
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName)
			throws BeansException {
		process(bean, beanName);
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName)
			throws BeansException {
		return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
	}

	public void process(Object bean, String beanName) {
		if (AnnotationUtils.isAnnotationDeclaredLocally(JDAController.class, bean.getClass())) {
			LOG.info(
					"Bean named {} is annotated with JDAController registering as event listener",
					beanName);
			eventManager.register(bean);
		}
	}
}
