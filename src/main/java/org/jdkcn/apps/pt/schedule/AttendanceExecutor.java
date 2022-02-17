/*
 * Copyright 2004-2021 Homolo Co., Ltd. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */
package org.jdkcn.apps.pt.schedule;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.jdkcn.apps.pt.AttendanceProperties;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.PeriodicTrigger;

import java.io.IOException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * The attendance executor.
 *
 * @author rory
 */
@Slf4j
public class AttendanceExecutor implements Runnable{
	private AttendanceProperties.Site site;
	private final TaskScheduler taskScheduler;
	private final AttendanceScheduler attendanceScheduler;

	private final String userAgent = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.114 Safari/537.36";
	private final String attendanceUrl = "/attendance.php";

	public AttendanceExecutor(AttendanceProperties.Site site, TaskScheduler taskScheduler,
			AttendanceScheduler attendanceScheduler) {
		this.site = site;
		this.taskScheduler = taskScheduler;
		this.attendanceScheduler = attendanceScheduler;
	}

	@Override
	public void run() {
		String url = this.site.getUrl() + attendanceUrl;
		Request request = new Request.Builder()
				.url(url)
				.addHeader("referer", this.site.getUrl())
				.addHeader("user-agent", this.userAgent)
				.addHeader("Cookie", this.site.getCookies())
				.build();
		try (Response response = new OkHttpClient.Builder()
				.callTimeout(30, TimeUnit.SECONDS)
				.readTimeout(30, TimeUnit.SECONDS)
				.build().newCall(request).execute()) {
			ResponseBody body = response.body();
			if (body == null) {
				this.attendanceScheduler.clearFailedScheduleFuture(site);
				return;
			}
			String responseText = body.string();
			Document htmlDocument = Jsoup.parse(responseText);
			Elements textElements = htmlDocument.select("#outer .embedded .text");
			if (textElements.isEmpty()) {
				log.info("{} responseText:{}", this.site.getUrl(), responseText);
			} else {
				log.info("{} attendance result:{}", this.site.getUrl(), textElements.first().text());
			}
			this.attendanceScheduler.clearFailedScheduleFuture(site);
		} catch (IOException ex) {
			log.error("request error: {}", url, ex);
			if (!this.attendanceScheduler.hasFailedScheduleFuture(site)) {
				ScheduledFuture<?> scheduledFuture = this.taskScheduler.schedule(this, new PeriodicTrigger(22, TimeUnit.MINUTES));
				this.attendanceScheduler.putFailedScheduleFuture(site, scheduledFuture);
			}
		}
	}
}
