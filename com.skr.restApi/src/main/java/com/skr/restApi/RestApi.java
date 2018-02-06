package com.skr.restApi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class RestApi {
	private static final String LOGINURL = "https://login.salesforce.com";
	private static final String GRANTTYPE = "/services/oauth2/token?grant_type=password";
	private static final String CLIENTID = "3MVG9d8..z.hDcPJd79beDUygquVlPpvr8ZG_zBWOuCViMDt6MwmOXkoRq4Q7Bmj_fhErzrqT0REm_m0VYnIY";
	private static final String CLIENTSECRET = "1463826778530438330";
	private static final String USERID = "sapanranjan@vy.com";
	private static final String PASSWORD = "aarush2014TALl1uGq51jYGuxMcRUtVx0rY";
	// = “instance_url”
	private static String API_VERSION = "/v32.0";
	private static String REST_ENDPOINT = "/services/data";
	private static final String ACCESSTOKEN = "access_token";
	private static final String INSTANCEURL = "instance_url";
	private static String instanceUrl;
	private static Header oauthHeader;
	private static Header prettyPrintHeader = new BasicHeader("X-PrettyPrint", "1");
	private static String baseUri;
	private static String leadId;
	private static String leadFirstName;
	private static String leadLastName;
	private static String leadCompany;

	public static void main(String[] args) {
		HttpClient httpclient = HttpClientBuilder.create().build();
		String loginURL = LOGINURL + GRANTTYPE + "&client_id=" + CLIENTID + "&client_secret=" + CLIENTSECRET
				+ "&username=" + USERID + "&password=" + PASSWORD;
		HttpPost httpPost = new HttpPost(loginURL);
		HttpResponse httpResponse = null;

		try {
			httpResponse = httpclient.execute(httpPost);
		} catch (ClientProtocolException clientProtocolException) {
			clientProtocolException.printStackTrace();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		final int statusCode = httpResponse.getStatusLine().getStatusCode();
		if (statusCode != HttpStatus.SC_OK) {
			System.out.println("Error" + statusCode);
			return;
		}
		String httpMessage = null;
		try {
			httpMessage = EntityUtils.toString(httpResponse.getEntity());
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
		JSONObject jsonObject = null;
		String accessToken = null;
		try {
			jsonObject = (JSONObject) new JSONTokener(httpMessage).nextValue();
			accessToken = jsonObject.getString(ACCESSTOKEN);
			instanceUrl = jsonObject.getString(INSTANCEURL);
		} catch (JSONException jsonException) {
			jsonException.printStackTrace();
		}
		System.out.println(accessToken);
		System.out.println(instanceUrl);
		baseUri = instanceUrl + REST_ENDPOINT + API_VERSION;
		oauthHeader = new BasicHeader("Authorization", "OAuth " + accessToken);
		System.out.println("oauthHeader1: " + oauthHeader);
		System.out.println("\n" + httpResponse.getStatusLine());
		System.out.println("Successful login");
		System.out.println("instance URL: " + instanceUrl);
		System.out.println("access token/session ID: " + accessToken);
		System.out.println("baseUri: " + baseUri);
		// queryLeads();
		queryAccount();
	}

	private static String getBody(InputStream inputStream) {
		String result = "";
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				result += inputLine;
				result += "\n";
			}
			in.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return result;
	}

	public static void queryAccount() {

		try {
			HttpClient httpClient = HttpClientBuilder.create().build();
			String uri = baseUri + "/query?q=Select+Id+,+Name+From+Account+Limit+100";
			//String uri = baseUri + "/query?q=desc+all+tables";
			System.out.println("Query URL: " + uri);
			HttpGet httpGet = new HttpGet(uri);
			System.out.println("oauthHeader2: " + oauthHeader);
			httpGet.addHeader(oauthHeader);
			httpGet.addHeader(prettyPrintHeader);
			HttpResponse response = httpClient.execute(httpGet);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200) {
				String response_string = EntityUtils.toString(response.getEntity());
				try {
					JSONObject json = new JSONObject(response_string);
					System.out.println("JSON result of Query:\n" + json.toString(1));
					JSONArray j = json.getJSONArray("records");
					System.out.println(json);
					// for (int i = 0; i < j.length(); i++) {

					// }
				} catch (JSONException je) {
					je.printStackTrace();
				}
			} else {
				System.out.println("Query was unsuccessful. Status code returned is " + statusCode);
				System.out.println("An error has occured. Http status: " + response.getStatusLine().getStatusCode());
				System.out.println(getBody(response.getEntity().getContent()));
				System.exit(-1);
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (NullPointerException npe) {
			npe.printStackTrace();
		}

	}

	private static void queryLeads() {
		try {
			HttpClient httpClient = HttpClientBuilder.create().build();
			String uri = baseUri + "/query?q=Select+Id+,+FirstName+,+LastName+,+Company+From+Lead+Limit+5";
			System.out.println("Query URL: " + uri);
			HttpGet httpGet = new HttpGet(uri);
			System.out.println("oauthHeader2: " + oauthHeader);
			httpGet.addHeader(oauthHeader);
			httpGet.addHeader(prettyPrintHeader);
			HttpResponse response = httpClient.execute(httpGet);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200) {
				String response_string = EntityUtils.toString(response.getEntity());
				try {
					JSONObject json = new JSONObject(response_string);
					System.out.println("JSON result of Query:\n" + json.toString(1));
					JSONArray j = json.getJSONArray("records");
					for (int i = 0; i < j.length(); i++) {
						leadId = json.getJSONArray("records").getJSONObject(i).getString("Id");
						leadFirstName = json.getJSONArray("records").getJSONObject(i).getString("FirstName");
						leadLastName = json.getJSONArray("records").getJSONObject(i).getString("LastName");
						leadCompany = json.getJSONArray("records").getJSONObject(i).getString("Company");
						System.out.println("Lead record is: " + i + ". " + leadId + " " + leadFirstName + " "
								+ leadLastName + "(" + leadCompany + ")");
					}
				} catch (JSONException je) {
					je.printStackTrace();
				}
			} else {
				System.out.println("Query was unsuccessful. Status code returned is " + statusCode);
				System.out.println("An error has occured. Http status: " + response.getStatusLine().getStatusCode());
				System.out.println(getBody(response.getEntity().getContent()));
				System.exit(-1);
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (NullPointerException npe) {
			npe.printStackTrace();
		}
	}

}
