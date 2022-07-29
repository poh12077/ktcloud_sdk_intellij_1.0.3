package com.solbox.delivery.ktcloudSDK;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.HttpURLConnection;

public class ResponseParser {

	static String statusCodeParser(String result) throws JSONException {
		JSONObject jsonResult = new JSONObject(result);
		if (jsonResult.getInt("statusCode") == HttpURLConnection.HTTP_CREATED
				|| jsonResult.getInt("statusCode") == HttpURLConnection.HTTP_OK
				|| jsonResult.getInt("statusCode") == HttpURLConnection.HTTP_ACCEPTED) {
			return jsonResult.getString("response");
		} else {
			// fail logic should be here
			// System.out.println( jsonResult.getInt("statusCode") );
			return "";
		}
	}

	static String getProjectID(String result) throws JSONException {
		JSONObject jsonResult = new JSONObject(result);
		if (jsonResult.getInt("statusCode") == HttpURLConnection.HTTP_CREATED
				|| jsonResult.getInt("statusCode") == HttpURLConnection.HTTP_OK
				|| jsonResult.getInt("statusCode") == HttpURLConnection.HTTP_ACCEPTED) {
			return jsonResult.getString("projectID");
		} else {
			// fail logic should be here
			// System.out.println( jsonResult.getInt("statusCode") );
			return "";
		}
	}

	static String VmCreateResponseParser(String response) throws JSONException {
		JSONObject fianlJsonObject = new JSONObject(response);
		JSONObject server = fianlJsonObject.getJSONObject("server");
		String ID = server.getString("id");
		return ID;
	}

	static String VmDetailResponseParser(String response) throws JSONException {
		JSONObject fianlJsonObject = new JSONObject(response);
		JSONObject server = fianlJsonObject.getJSONObject("server");
		JSONObject addresses = server.getJSONObject("addresses");
		JSONArray Private = addresses.getJSONArray("Private");
		String privateIP = "";
		for (int i = 0; i < Private.length(); i++) {
			JSONObject jsonObject = Private.getJSONObject(i);
			privateIP = jsonObject.getString("addr");
		}
		return privateIP;
	}

	static String volumeCreateResponseParser(String response) throws JSONException {
		JSONObject fianlJsonObject = new JSONObject(response);
		JSONObject volume = fianlJsonObject.getJSONObject("volume");
		String ID = volume.getString("id");
		return ID;
	}

	static String IPCreateResponseParser(String response) throws JSONException {
		JSONObject fianlJsonObject = new JSONObject(response);
		JSONObject nc_associateentpublicipresponse = fianlJsonObject.getJSONObject("nc_associateentpublicipresponse");
		String job_id = nc_associateentpublicipresponse.getString("job_id");
		return job_id;
	}

	static String PublicIPJobIDlookupParser(String response) throws JSONException {
		JSONObject fianlJsonObject = new JSONObject(response);
		JSONObject nc_associateentpublicipresponse = fianlJsonObject.getJSONObject("nc_queryasyncjobresultresponse");
		JSONObject result = nc_associateentpublicipresponse.getJSONObject("result");
		String IP_id = result.getString("id");
		return IP_id;
	}

	static String staticNATSettingResponseParser(String response) throws JSONException {
		JSONObject fianlJsonObject = new JSONObject(response);
		JSONObject nc_enablestaticnatresponse = fianlJsonObject.getJSONObject("nc_enablestaticnatresponse");
		String staticNAT_ID = "";
		if (nc_enablestaticnatresponse.getBoolean("success") == true) {
			staticNAT_ID = nc_enablestaticnatresponse.getString("id");
			return staticNAT_ID;
		} else {
			return staticNAT_ID;
		}
	}

	static String projectIDParser(String response) throws JSONException {
		JSONObject fianlJsonObject = new JSONObject(response);
		JSONObject token = fianlJsonObject.getJSONObject("token");
		JSONObject project = token.getJSONObject("project");
		String ID = project.getString("id");
		return ID;
	}

	static String firewallJobIdParser(String response) throws JSONException {
		JSONObject fianlJsonObject = new JSONObject(response);
		JSONObject nc_createfirewallruleresponse = fianlJsonObject.getJSONObject("nc_createfirewallruleresponse");		
		String jobId = nc_createfirewallruleresponse.getString("job_id");
		return jobId;
	}
	
	static String firewallIdParser(String response) throws JSONException {
		JSONObject fianlJsonObject = new JSONObject(response);
		JSONObject nc_queryasyncjobresultresponse = fianlJsonObject.getJSONObject("nc_queryasyncjobresultresponse");
		JSONObject result = nc_queryasyncjobresultresponse.getJSONObject("result");
		String firewallId = "";
		if (result.getBoolean("success") == true) {
			firewallId = result.getString("id");
			return firewallId;
		} else {
			return firewallId;
		}
	}

	static String lookupJobId(String jobId, String token, int timeout) throws Exception {
		String result = RestAPI.get(KTCloudOpenAPI.jobID_URL+jobId, token, timeout);
		String response= ResponseParser.statusCodeParser(result);
		return response;
	}

	
//
//	static void deleteVmOnly(String serverID, String token, int timeout) throws Exception {
//		String requestBody = RequestBody.forceDeleteVm();
//		String result = RestAPI.post(KTCloudOpenAPI.forceDeleteVm_URL + serverID + "/action", token, requestBody,
//				timeout);
//		JSONObject jsonResult = new JSONObject(result);
//		if (jsonResult.getInt("statusCode") == HttpURLConnection.HTTP_CREATED
//				|| jsonResult.getInt("statusCode") == HttpURLConnection.HTTP_OK
//				|| jsonResult.getInt("statusCode") == HttpURLConnection.HTTP_ACCEPTED) {
//			System.out.println("Server deletion is in progress");
//		} else {
//			System.out.println("Server deletion failed");
//		}
//	}
//
//	static void deleteVolume(String volumeID, String projectID, String token, int timeout) throws Exception {
//		int count = 0;
//		while (true) {
//			String result = RestAPI.delete(KTCloudOpenAPI.deleteVolume_URL + projectID + "/volumes/" + volumeID, token,
//					timeout);
//			JSONObject jsonResult = new JSONObject(result);
//			if (jsonResult.getInt("statusCode") == HttpURLConnection.HTTP_CREATED
//					|| jsonResult.getInt("statusCode") == HttpURLConnection.HTTP_OK
//					|| jsonResult.getInt("statusCode") == HttpURLConnection.HTTP_ACCEPTED) {
//				System.out.println("Volume deletion is in progress");
//				break;
//			} else {
//				System.out.print(count + " ");
//			}
//			count++;
//			Thread.sleep(1000);
//		}
//	}
//
//	static void deleteStaticNat(String staticNatId, String token, int timeout) throws Exception {
//			String result = RestAPI.delete(KTCloudOpenAPI.DeleteStaticNAT_URL+staticNatId, token,	timeout);
//			JSONObject jsonResult = new JSONObject(result);
//			if (jsonResult.getInt("statusCode") == HttpURLConnection.HTTP_CREATED
//					|| jsonResult.getInt("statusCode") == HttpURLConnection.HTTP_OK
//					|| jsonResult.getInt("statusCode") == HttpURLConnection.HTTP_ACCEPTED) {
//				System.out.println("static NAT has been disabled");
//			} else {
//				System.out.println("static NAT deletion has failed");
//			}
//	}
//
//	static void deletePublicIp(String publicIpId, String token, int timeout) throws Exception {
//		String result = RestAPI.delete(KTCloudOpenAPI.deleteIP_URL+publicIpId, token, timeout);
//		JSONObject jsonResult = new JSONObject(result);
//		if (jsonResult.getInt("statusCode") == HttpURLConnection.HTTP_CREATED
//				|| jsonResult.getInt("statusCode") == HttpURLConnection.HTTP_OK
//				|| jsonResult.getInt("statusCode") == HttpURLConnection.HTTP_ACCEPTED) {
//			System.out.println("public IP has been deleted");
//		} else {
//			System.out.println("public IP deletion has failed");
//		}
//}
//
//
//
//	static String lookupJobId(String jobId, String token, int timeout) throws Exception {
//		String result = RestAPI.get(KTCloudOpenAPI.jobID_URL+jobId, token, timeout);
//		String response= ResponseParser.statusCodeParser(result);
//		return response;
//	}
//
//	static void closeFirewall(String firewallJobId, String token, int timeout) throws Exception {
//		String response = lookupJobId(firewallJobId,token,timeout);
//		String firewallId = firewallIdParser(response);
//		String result = RestAPI.delete(KTCloudOpenAPI.closeFirewall_URL+firewallId, token, timeout);
//		JSONObject jsonResult = new JSONObject(result);
//		if (jsonResult.getInt("statusCode") == HttpURLConnection.HTTP_CREATED
//				|| jsonResult.getInt("statusCode") == HttpURLConnection.HTTP_OK
//				|| jsonResult.getInt("statusCode") == HttpURLConnection.HTTP_ACCEPTED) {
//			System.out.println("firewall has closed");
//		} else {
//			System.out.println("firewall still opened");
//		}
//	}
//
	
	
}
