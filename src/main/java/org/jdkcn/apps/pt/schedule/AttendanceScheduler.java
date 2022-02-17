/*
 * Copyright 2004-2021 Homolo Co., Ltd. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */
package org.jdkcn.apps.pt.schedule;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jdkcn.apps.pt.AttendanceProperties;
import org.springframework.context.SmartLifecycle;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

/**
 * The attendance scheduler.
 *
 * @author rory
 */
@Component
@Slf4j
public class AttendanceScheduler implements SmartLifecycle {
	private final TaskScheduler taskScheduler;

	private final AttendanceProperties properties;

	private boolean running = false;

	private final Map<AttendanceProperties.Site, ScheduledFuture<?>> scheduledFutureMap = new HashMap<>();

	private final Map<AttendanceProperties.Site, ScheduledFuture<?>> failedScheduledFutureMap = new HashMap<>();

	public AttendanceScheduler(TaskScheduler taskScheduler, AttendanceProperties properties) {
		this.taskScheduler = taskScheduler;
		this.properties = properties;
	}

	@Override
	public void start() {
		log.info(StringUtils.center(" init the attendance scheduler. ", 60, "#"));
		this.properties.getSites().forEach(site -> {
			log.info("init the site:{} scheduler at:{}", site.getUrl(), site.getCron());
			ScheduledFuture<?> scheduledFuture = this.taskScheduler.schedule(new AttendanceExecutor(site, this.taskScheduler, this),
					new CronTrigger(site.getCron()));
			putScheduleFuture(site, scheduledFuture);
		});
		log.info(StringUtils.center(" init done. " + this.properties.getSites().size(), 60, "#"));
		this.running = true;
	}

	@Override
	public void stop() {
		log.info(StringUtils.center(" shutdown the attendance scheduler. ", 60, "#"));
		this.scheduledFutureMap.forEach((key, value) -> {
			log.info("shutdown the site:{}", key.getUrl());
			value.cancel(false);
		});
		this.failedScheduledFutureMap.forEach((key, value) -> {
			log.info("shutdown the site failed:{}", key.getUrl());
			value.cancel(false);
		});
		log.info(StringUtils.center(" shutdown all. ", 60, "#"));
	}

	@Override
	public boolean isRunning() {
		return this.running;
	}

	public void putScheduleFuture(AttendanceProperties.Site site, ScheduledFuture<?> scheduledFuture) {
		if (scheduledFuture != null) {
			this.scheduledFutureMap.put(site, scheduledFuture);
		}
	}

	public void putFailedScheduleFuture(AttendanceProperties.Site site, ScheduledFuture<?> scheduledFuture) {
		if (scheduledFuture != null) {
			this.failedScheduledFutureMap.put(site, scheduledFuture);
		}
	}

	public boolean hasFailedScheduleFuture(AttendanceProperties.Site site) {
		return this.failedScheduledFutureMap.containsKey(site);
	}

	public void clearFailedScheduleFuture(AttendanceProperties.Site site) {
		ScheduledFuture<?> scheduledFuture = this.failedScheduledFutureMap.get(site);
		if (scheduledFuture != null) {
			scheduledFuture.cancel(false);
			this.failedScheduledFutureMap.remove(site);
		}
	}

}
