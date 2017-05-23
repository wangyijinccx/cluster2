package com.ipeaksoft.moneyday.core.service;

import java.io.IOException;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

@Service
public class HttpServiceForRobot extends BaseService {

	public String get(String url) {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		RequestConfig requestConfig = RequestConfig.custom()
				.setConnectTimeout(10000).setConnectionRequestTimeout(10000)
				.setSocketTimeout(10000).build();
		long time = System.currentTimeMillis();
		CloseableHttpResponse response = null;
		String result = null;
		try {
			HttpGet httpGet = new HttpGet(url);
			httpGet.setConfig(requestConfig);
			response = httpClient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			String responseStr = EntityUtils.toString(entity, "UTF-8");
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				result = responseStr;
			}
		} catch (Exception e) {
			// logger.error("ERROR:{}", url, e);
		} finally {
			try {
				if (response != null)
					response.close();
			} catch (IOException e) {
			}
		}
		logger.info("GET to:{}, result:{}, time:{}", url, result,
				(System.currentTimeMillis() - time));
		return result;
	}
}
