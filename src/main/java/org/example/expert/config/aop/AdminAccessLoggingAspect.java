package org.example.expert.config.aop;

import java.util.HashMap;
import java.util.Map;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class AdminAccessLoggingAspect {

	private final HttpServletRequest request;
	private final ObjectMapper objectMapper;

	@Around("@annotation(AdminLog)")
	public Object log(ProceedingJoinPoint pjp) throws Throwable {

		Map<String, Object> logMap = new HashMap<>();

		logMap.put("userId", request.getAttribute("userId"));
		logMap.put("requestTime", System.currentTimeMillis());
		logMap.put("requestURI", request.getRequestURI());
		logMap.put("args", pjp.getArgs());

		String json = objectMapper.writeValueAsString(logMap);

		log.info("[REQUEST] {}", json);
		try {
			Object result = pjp.proceed();
			log.info("[RESPONSE] {}", objectMapper.writeValueAsString(result));
			return result;
		}catch (Exception e) {
			log.error("[RESPONSE] exception message = {}", e.getMessage());
			throw e;
		}
	}
}
