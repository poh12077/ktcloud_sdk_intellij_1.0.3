package com.solbox.delivery.ktcloudSDK;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.http.entity.StringEntity;

public class RestAPI {
	// token
	static String request(String URL, String method, String requestBody) throws Exception {
		URL url = new URL(URL);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setConnectTimeout(10000);
		connection.setReadTimeout(10000);
		connection.setRequestMethod(method);
		connection.setRequestProperty("User-Agent", "Mozilla/5.0");
		connection.setRequestProperty("Content-Type", "application/json");
		if (method == "POST" || method == "PUT") {
			connection.setDoOutput(true);
			DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
			outputStream.writeBytes(requestBody);
			outputStream.flush();
			outputStream.close();
		}
		int statusCode = connection.getResponseCode();
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		StringBuffer stringBuffer = new StringBuffer();
		String inputLine;
		while ((inputLine = bufferedReader.readLine()) != null) {
			stringBuffer.append(inputLine);
		}
		bufferedReader.close();
		String responseBody = stringBuffer.toString();
		System.out.println(statusCode + " " + responseBody);
		String projectID = Utils.projectIDParser(responseBody);
		
		String token = connection.getHeaderField("X-Subject-Token");
		JSONObject result = new JSONObject();
		result.put("statusCode", statusCode);
		result.put("response", token);
		result.put("projectID", projectID);
		return result.toString();
	}

	// general
	static String request(String URL, String method, String token, String requestBody) throws Exception {
		URL url = new URL(URL);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setConnectTimeout(10000);
		connection.setReadTimeout(10000);
		connection.setRequestMethod(method);
		connection.setRequestProperty("User-Agent", "Mozilla/5.0");
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setRequestProperty("X-Auth-Token", token);
		if (method == "POST" || method == "PUT") {
			connection.setDoOutput(true);
			DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
			outputStream.writeBytes(requestBody);
			outputStream.flush();
			outputStream.close();
		}
		int statusCode = connection.getResponseCode();
		if (statusCode == 409) {
			JSONObject result = new JSONObject();
			result.put("statusCode", statusCode);
			result.put("response", "");
			return result.toString();
		}
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		StringBuffer stringBuffer = new StringBuffer();
		String inputLine;
		while ((inputLine = bufferedReader.readLine()) != null) {
			stringBuffer.append(inputLine);
		}
		bufferedReader.close();
		String responseBody = stringBuffer.toString();
		System.out.println(statusCode + " " + responseBody);

		JSONObject result = new JSONObject();
		result.put("statusCode", statusCode);
		result.put("response", responseBody);
		return result.toString();
	}

	public static String get(String URL, String token, int timeout) throws ClientProtocolException, IOException {
		RequestConfig config = RequestConfig.custom()
				  .setConnectTimeout(timeout * 1000)
				  .setConnectionRequestTimeout(timeout * 1000)
				  .setSocketTimeout(timeout * 1000).build();
		CloseableHttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
		HttpGet httpGet = new HttpGet(URL);
		httpGet.addHeader("User-Agent","Mozilla/5.0");
		httpGet.setHeader("Accept", "application/json");
		httpGet.setHeader("X-Auth-Token", token);
		CloseableHttpResponse httpResponse = client.execute(httpGet);
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		if (statusCode == 409) {
			JSONObject result = new JSONObject();
			result.put("statusCode", statusCode);
			result.put("response", "");
			client.close();
			return result.toString();
		}
		String responseBody = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
		System.out.println(statusCode + " " + responseBody);
		JSONObject result = new JSONObject();
		result.put("statusCode", statusCode);
		result.put("response", responseBody);
		client.close();
		return result.toString();
	}

	//general
	public static String post(String URL, String token, String requestBody, int timeout) throws ClientProtocolException, IOException {
		RequestConfig config = RequestConfig.custom()
				  .setConnectTimeout(timeout * 1000)
				  .setConnectionRequestTimeout(timeout * 1000)
				  .setSocketTimeout(timeout * 1000).build();
		CloseableHttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
		HttpPost httpPost = new HttpPost(URL);
		StringEntity entity = new StringEntity(requestBody);
		httpPost.setEntity(entity);
		httpPost.addHeader("User-Agent","Mozilla/5.0");
		httpPost.setHeader("Accept", "application/json");
		httpPost.setHeader("Content-type", "application/json");
		httpPost.setHeader("X-Auth-Token", token);
		CloseableHttpResponse response = client.execute(httpPost);
		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == 409) {
			JSONObject result = new JSONObject();
			result.put("statusCode", statusCode);
			result.put("response", "");
			client.close();
			return result.toString();
		}
		String responseBody = EntityUtils.toString(response.getEntity(), "UTF-8");
		System.out.println(statusCode + " " + responseBody);
		JSONObject result = new JSONObject();
		result.put("statusCode", statusCode);
		result.put("response", responseBody);
		client.close();
		return result.toString();
	}
	
	//token
	public static String post(String URL, String requestBody, int timeout) throws ClientProtocolException, IOException {
		RequestConfig config = RequestConfig.custom()
				  .setConnectTimeout(timeout * 1000)
				  .setConnectionRequestTimeout(timeout * 1000)
				  .setSocketTimeout(timeout * 1000).build();
		CloseableHttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
		HttpPost httpPost = new HttpPost(URL);
		StringEntity entity = new StringEntity(requestBody);
		httpPost.setEntity(entity);
		httpPost.addHeader("User-Agent","Mozilla/5.0");
		httpPost.setHeader("Accept", "application/json");
		httpPost.setHeader("Content-type", "application/json");
		CloseableHttpResponse response = client.execute(httpPost);
		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == 409) {
			JSONObject result = new JSONObject();
			result.put("statusCode", statusCode);
			result.put("response", "");
			client.close();
			return result.toString();
		}
		String responseBody = EntityUtils.toString(response.getEntity(), "UTF-8");
		System.out.println(statusCode + " " + responseBody);
		String projectID = Utils.projectIDParser(responseBody);
		
		JSONObject result = new JSONObject();
		String token = response.getFirstHeader("X-Subject-Token").getValue();
		result.put("statusCode", statusCode);
		result.put("response", token);
		result.put("projectID", projectID);
		client.close();
		return result.toString();
	}

	public static String delete(String URL, String token, int timeout) throws ClientProtocolException, IOException {
		RequestConfig config = RequestConfig.custom()
				  .setConnectTimeout(timeout * 1000)
				  .setConnectionRequestTimeout(timeout * 1000)
				  .setSocketTimeout(timeout * 1000).build();
		CloseableHttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
		HttpDelete httpDelete = new HttpDelete(URL);
		httpDelete.addHeader("User-Agent","Mozilla/5.0");
		httpDelete.setHeader("Accept", "application/json");
		httpDelete.setHeader("Content-type", "application/json");
		httpDelete.setHeader("X-Auth-Token", token);
		CloseableHttpResponse response = client.execute(httpDelete);
		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == 409) {
			JSONObject result = new JSONObject();
			result.put("statusCode", statusCode);
			result.put("response", "");
			client.close();
			return result.toString();
		}
		String responseBody = EntityUtils.toString(response.getEntity(), "UTF-8");
		System.out.println(statusCode + " " + responseBody);
		JSONObject result = new JSONObject();
		result.put("statusCode", statusCode);
		result.put("response", responseBody);
		client.close();
		return result.toString();
	}

	
}
