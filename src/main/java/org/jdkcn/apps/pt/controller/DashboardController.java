/*
 * Copyright 2004-2021 Homolo Co., Ltd. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */
package org.jdkcn.apps.pt.controller;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jdkcn.apps.pt.AttendanceProperties;
import org.jdkcn.apps.pt.HtmlParseUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * The dashboard controller.
 *
 * @author rory
 */
@Controller
@Slf4j
public class DashboardController {
	private final AttendanceProperties properties;

	private final String userAgent = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.114 Safari/537.36";
	private final String attendanceUrl = "/attendance.php";
	public DashboardController(AttendanceProperties properties) {
		this.properties = properties;
	}

	@GetMapping("/")
	public String index() {
		return "index";
	}

	@GetMapping("/attend")
	@ResponseBody
	public Map<String, Object> attend() {
		Map<String, Object> result = new HashMap<>();
		this.properties.getSites().forEach(site -> {
			log.info("attend site:{}", site.getUrl());
			String url = site.getUrl() + attendanceUrl;
			Request request = new Request.Builder()
					.url(url)
					.addHeader("referer", site.getUrl())
					.addHeader("user-agent", this.userAgent)
					.addHeader("Cookie", site.getCookies())
					.build();
			try (Response response = new OkHttpClient.Builder()
					.callTimeout(30, TimeUnit.SECONDS)
					.readTimeout(30, TimeUnit.SECONDS)
					.build().newCall(request).execute()) {
				okhttp3.ResponseBody body = response.body();
				if (body == null) {
					return;
				}
				log.info("{} attendance result:{}", site.getUrl(), HtmlParseUtils.parseAttendResult(body.string()));
			} catch (IOException ex) {
				log.error("request error: {}", url, ex);
			}
		});
		result.put("success", true);
		return result;
	}
}
