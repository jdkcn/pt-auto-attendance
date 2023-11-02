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

		htmlDocument = Jsoup.parse(this.getClass().getResourceAsStream("/html/attendance05.html"), "UTF-8", "");
		textElements = htmlDocument.select("#outer .embedded li");
		log.info("outer text:{}", textElements.isEmpty() ? "" : textElements.first().text());
		assertEquals("这是你的第 930 次签到， 连续签到 195 天，本次签到获得 600 个魔力值。", textElements.first().text());

		htmlDocument = Jsoup.parse(this.getClass().getResourceAsStream("/html/attendance06.html"), "UTF-8", "");
		textElements = htmlDocument.select("#outer .embedded");
		log.info("outer text:{}", textElements.isEmpty() ? "" : textElements.first().text());
		assertEquals("系统提醒 今天已签到，请勿重复刷新(高频高量刷可能导致封号)，已刷次数：0次。 签到记录 7天签到记录（补签卡剩余:37）时间：2023-11-02 07:44:11获得魔力值：100连续天数：225天 时间：2023-11-01 07:44:14获得魔力值：100连续天数：224天 时间：2023-10-31 20:18:25获得魔力值：600连续天数：223天 时间：2023-10-30 12:09:29获得魔力值：600连续天数：222天 时间：2023-10-29 10:25:20获得魔力值：600连续天数：221天 时间：2023-10-28 15:25:55获得魔力值：600连续天数：220天 时间：2023-10-27 00:22:32获得魔力值：600连续天数：219天 补签卡使用说明 1、补签卡一般会因为系统维护发放，或捐赠5RMB获得5张补签卡。捐赠后参考捐赠页说明发送站内信。 2、补签卡只能补签7天内的未签到。 3、补签卡可以获得基于补签前一天的魔力值。 4、补签卡可以弥补连续天数，但不能弥补当天后续魔力。", textElements.first().text());

	}
}
