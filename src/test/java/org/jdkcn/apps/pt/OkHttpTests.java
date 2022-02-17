/*
 * Copyright 2004-2021 Homolo Co., Ltd. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */
package org.jdkcn.apps.pt;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * The ok http tests.
 *
 * @author rory
 */
@Slf4j
public class OkHttpTests {
	@Test
	@Disabled("only run once a day.")
	void testRequestWithCookie() {
		String url = "https://www.hddolby.com/attendance.php";
		OkHttpClient client = new OkHttpClient();
		Request request = new Request.Builder()
				.url(url)
				.addHeader("referer", "https://www.hddolby.com")
				.addHeader("user-agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.114 Safari/537.36")
				.addHeader("Cookie", "c_secure_uid=aaaa; c_secure_pass=bbbb;" +
						" c_secure_ssl=cccc; c_secure_tracker_ssl=dddd; c_secure_login=eeee;" +
						" cf_clearance=ffff")
				.build();
		try (Response response = client.newCall(request).execute()) {
			ResponseBody body = response.body();
			String responseText = body == null ? "" : body.string();
			log.info("responseText:{}",  responseText);
		} catch (IOException ex) {
			log.error("request error", ex);
		}
	}

}
