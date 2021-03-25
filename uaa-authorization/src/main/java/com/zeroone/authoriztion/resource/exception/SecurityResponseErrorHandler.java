package com.zeroone.authoriztion.resource.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.UnknownHttpStatusCodeException;

import java.io.IOException;
import java.nio.charset.Charset;

import static org.springframework.http.HttpStatus.EXPECTATION_FAILED;

/**
 *  自定义异常处理
 */
public class SecurityResponseErrorHandler implements ResponseErrorHandler {

	@Override
	public boolean hasError(ClientHttpResponse response) throws IOException {
		HttpStatus statusCode = HttpStatus.valueOf(response.getRawStatusCode());
		return statusCode != null && this.hasError(statusCode);
	}

	protected boolean hasError(HttpStatus statusCode) {
		return statusCode.series() == HttpStatus.Series.CLIENT_ERROR || statusCode.series() == HttpStatus.Series.SERVER_ERROR;
	}

	@Override
	public void handleError(ClientHttpResponse response) throws IOException {
		HttpStatus statusCode = HttpStatus.valueOf(response.getRawStatusCode());
		if (statusCode == null) {
			throw new UnknownHttpStatusCodeException(response.getRawStatusCode(), response.getStatusText(), response.getHeaders(), this.getResponseBody(response), this.getCharset(response));
		} else {
			this.handleError(response, statusCode);
		}
	}

	protected void handleError(ClientHttpResponse response, HttpStatus statusCode) throws IOException {
		String statusText = response.getStatusText();
		HttpHeaders headers = response.getHeaders();
		byte[] body = getResponseBody(response);
		Charset charset = getCharset(response);
		switch (statusCode.series()) {
			case CLIENT_ERROR:
				if (statusCode == EXPECTATION_FAILED) {
//					Map exceptionData = JsonUtils.byteToObject(body, Map.class);
//					String detailMessage = exceptionData.get("message") != null ? String.valueOf(exceptionData.get("message")) : String.valueOf(exceptionData.get
//							("error_description"));
//					throw SecurityBizException.build(SecurityErrorCode.error, detailMessage);
				}
				throw new HttpClientErrorException(statusCode, statusText, headers, body, charset);

			case SERVER_ERROR:
				throw new HttpServerErrorException(statusCode, statusText, headers, body, charset);

			default:
				throw new UnknownHttpStatusCodeException(statusCode.value(), statusText, headers, body, charset);
		}
	}

	protected byte[] getResponseBody(ClientHttpResponse response) {
		try {
			return FileCopyUtils.copyToByteArray(response.getBody());
		} catch (IOException var3) {
			return new byte[0];
		}
	}

	protected Charset getCharset(ClientHttpResponse response) {
		HttpHeaders headers = response.getHeaders();
		MediaType contentType = headers.getContentType();
		return (contentType != null ? contentType.getCharset() : null);
	}
}
