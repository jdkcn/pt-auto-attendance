/*
 * Copyright 2004-2021 Homolo Co., Ltd. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */
package org.jdkcn.apps.pt.tomcat;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.Constants;
import org.apache.catalina.valves.ErrorReportValve;
import org.apache.coyote.ActionCode;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.util.security.Escape;

import java.io.IOException;
import java.io.Writer;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The custom error report valve.
 *
 * @author rory
 */
public class CustomErrorReportValve extends ErrorReportValve {

	private static final String CSS = "*{ transition: all 0.6s; } html { height: 100%; } " +
			"body{ font-family: \"Tahoma\", \"Helvetica Neue\", \"Lantinghei SC\", Arial, " +
			"\"Microsoft Yahei\", \"Hiragino Sans GB\", STXihei, Helvetica, sans-serif; " +
			"color: #888; margin: 0; } #main{ display: table; width: 100%; height: 100vh; " +
			"text-align: center; } .error{ display: table-cell; vertical-align: middle; } " +
			".error h1{ font-size: 50px; display: inline-block; padding-right: 12px; " +
			"animation: type .5s alternate infinite; } .error p { font-size: 18px; }" +
			" @keyframes type{ from{box-shadow: inset -3px 0px 0px #888;} " +
			"to{box-shadow: inset -3px 0px 0px transparent;} }";

	@Override
	protected void report(Request request, Response response, Throwable throwable) {
		int statusCode = response.getStatus();

		// Do nothing on a 1xx, 2xx and 3xx status
		// Do nothing if anything has been written already
		// Do nothing if the response hasn't been explicitly marked as in error
		//    and that error has not been reported.
		if (statusCode < 400 || response.getContentWritten() > 0 || !response.setErrorReported()) {
			return;
		}

		// If an error has occurred that prevents further I/O, don't waste time
		// producing an error report that will never be read
		AtomicBoolean result = new AtomicBoolean(false);
		response.getCoyoteResponse().action(ActionCode.IS_IO_ALLOWED, result);
		if (!result.get()) {
			return;
		}
		String message = generateMessage(response, throwable);

		// Do nothing if there is no reason phrase for the specified status code and
		// no error message provided
		String reason = null;
		String description = null;
		StringManager smClient = StringManager.getManager(Constants.Package, request.getLocales());
		response.setLocale(smClient.getLocale());
		try {
			reason = smClient.getString("http." + statusCode + ".reason");
			description = smClient.getString("http." + statusCode + ".desc");
		} catch (Throwable t) {
			ExceptionUtils.handleThrowable(t);
		}
		if (reason == null || description == null) {
			if (message.isEmpty()) {
				return;
			} else {
				reason = smClient.getString("errorReportValve.unknownReason");
				description = smClient.getString("errorReportValve.noDescription");
			}
		}
		StringBuilder sb = generateHtmlPageContent(statusCode, reason, description, smClient);
		try {
			writeResponse(response, sb);
		} catch (IOException | IllegalStateException e) {
			// Ignore
		}
	}

	private String generateMessage(Response response, Throwable throwable) {
		String message = Escape.htmlElementContent(response.getMessage());
		if (message == null) {
			if (throwable != null) {
				String exceptionMessage = throwable.getMessage();
				if (exceptionMessage != null && exceptionMessage.length() > 0) {
					try (Scanner scanner = new Scanner(exceptionMessage)) {
						message = Escape.htmlElementContent(scanner.nextLine());
					}
				}
			}
			if (message == null) {
				message = "";
			}
		}
		return message;
	}

	private void writeResponse(Response response, StringBuilder sb) throws IOException {
		try {
			response.setContentType("text/html;charset=UTF-8");
		} catch (Throwable t) {
			ExceptionUtils.handleThrowable(t);
			if (container.getLogger().isDebugEnabled()) {
				container.getLogger().debug("status.setContentType", t);
			}
		}
		Writer writer = response.getReporter();
		if (writer != null) {
			// If writer is null, it's an indication that the response has
			// been hard committed already, which should never happen
			writer.write(sb.toString());
			response.finishResponse();
		}
	}

	private StringBuilder generateHtmlPageContent(int statusCode, String reason, String description, StringManager smClient) {
		StringBuilder sb = new StringBuilder();
		sb.append("<!doctype html><html lang=\"");
		sb.append(smClient.getLocale().getLanguage()).append("\">");
		sb.append("<head>");
		sb.append("<title>");
		sb.append(smClient.getString("errorReportValve.statusHeader", String.valueOf(statusCode), reason));
		sb.append("</title>");
		sb.append("<meta charset=\"UTF-8\">");
		sb.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">");
		sb.append("<meta name=\"renderer\" content=\"webkit\">");
		sb.append("<style type=\"text/css\">");
		sb.append(CSS);
		sb.append("</style>");
		sb.append("</head>");
		sb.append("<body>");
		sb.append("<div id=\"main\"><div class=\"error\"><h1> Error ").append(statusCode).append("</h1>");
		sb.append("<p>").append(reason).append("</p>");
		sb.append("<p>").append(description).append("</p>");
		sb.append("</div></div></body>");
		sb.append("</html>");
		return sb;
	}
}
