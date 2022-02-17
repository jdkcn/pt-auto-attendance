/*
 * Copyright 2004-2021 Homolo Co., Ltd. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */
package org.jdkcn.apps.pt;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * the auto attendance properties.
 *
 * @author rory
 */
@Component
@ConfigurationProperties("pt.attendance")
public class AttendanceProperties {

	private Set<Site> sites = new HashSet<>();

	public Set<Site> getSites() {
		return sites;
	}

	public void setSites(Set<Site> sites) {
		this.sites = sites;
	}

	public static class Site {
		private String url;
		private String cookies;
		private String cron;

		public String getCron() {
			return cron;
		}

		public void setCron(String cron) {
			this.cron = cron;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getCookies() {
			return cookies;
		}

		public void setCookies(String cookies) {
			this.cookies = cookies;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (!(o instanceof Site)) {
				return false;
			}
			Site site = (Site) o;
			return url.equals(site.url);
		}

		@Override
		public int hashCode() {
			return Objects.hash(url);
		}
	}
}
