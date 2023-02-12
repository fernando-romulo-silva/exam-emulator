package org.examemulator.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.InjectionPoint;

@ApplicationScoped
public class LoggerProducer {

    @Produces
    public Logger producer(final InjectionPoint ip) {
	return LoggerFactory.getLogger(ip.getMember().getDeclaringClass().getName());
    }
}
