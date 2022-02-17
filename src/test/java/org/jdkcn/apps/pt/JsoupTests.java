/*
 * Copyright 2004-2021 Homolo Co., Ltd. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */
package org.jdkcn.apps.pt;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The jsoup tests.
 *
 * @author rory
 */
@Slf4j
public class JsoupTests {

	@Test
	void testParseAttendanceResult() throws Exception {
		Document htmlDocument = Jsoup.parse(this.getClass().getResourceAsStream("/html/attendance01.html"), "UTF-8", "");
		Elements textElements = htmlDocument.select("#outer .embedded .text");
		log.info("outer text:{}", textElements.isEmpty() ? "" : textElements.first().text());
		assertEquals("这是您的第 522 次签到，已连续签到 4 天，本次签到获得 300 个魔力值。", textElements.first().text());

		htmlDocument = Jsoup.parse(this.getClass().getResourceAsStream("/html/attendance02.html"), "UTF-8", "");
		textElements = htmlDocument.select("#outer .embedded .text");
		log.info("outer text:{}", textElements.isEmpty() ? "" : textElements.first().text());
		assertEquals("這是您的第 625 次簽到，已連續簽到 4 天，本次簽到獲得 300 個魔力值。", textElements.first().text());

		htmlDocument = Jsoup.parse(this.getClass().getResourceAsStream("/html/attendance03.html"), "UTF-8", "");
		textElements = htmlDocument.select("#outer .embedded .text");
		log.info("outer text:{}", textElements.isEmpty() ? "" : textElements.first().text());
		assertEquals("这是您的第 529 次签到，已连续签到 4 天，本次签到获得 300 个魔力值。", textElements.first().text());

		htmlDocument = Jsoup.parse(this.getClass().getResourceAsStream("/html/attendance04.html"), "UTF-8", "");
		textElements = htmlDocument.select("#outer .embedded .text");
		log.info("outer text:{}", textElements.isEmpty() ? "" : textElements.first().text());
		assertEquals("这是您的第 142 次签到，已连续签到 4 天，本次签到获得 715 个魔力值。", textElements.first().text());
	}
}
