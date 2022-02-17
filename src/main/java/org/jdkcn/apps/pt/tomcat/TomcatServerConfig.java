/*
 * Copyright 2004-2021 Homolo Co., Ltd. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */
package org.jdkcn.apps.pt.tomcat;

import org.apache.catalina.Container;
import org.apache.catalina.core.StandardHost;
import org.jdkcn.apps.pt.tomcat.CustomErrorReportValve;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The tomcat server config.
 *
 * @author rory
 */
@Configuration
public class TomcatServerConfig {
	@Bean("pt-auto-attendance.contextCustomizer")
	public WebServerFactoryCustomizer<TomcatServletWebServerFactory> contextCustomizer() {
		return tomcatFactory -> tomcatFactory.addContextCustomizers(
				context -> {
					Container parent = context.getParent();
					if (parent instanceof StandardHost) {
						((StandardHost) parent).setErrorReportValveClass(CustomErrorReportValve.class.getName());
					}
				}
		);
	}
}
