/*
 * Copyright 2004-2021 Homolo Co., Ltd. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */
package org.jdkcn.apps.pt.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * The dashboard controller.
 *
 * @author rory
 */
@Controller
public class DashboardController {

	@GetMapping("/")
	public String index() {
		return "index";
	}
}
