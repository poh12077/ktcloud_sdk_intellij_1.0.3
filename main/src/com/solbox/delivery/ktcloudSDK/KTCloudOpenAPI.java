package com.solbox.delivery.ktcloudSDK;
public class KTCloudOpenAPI {

	static final String getVm_URL = "https://api.ucloudbiz.olleh.com/d1/server/servers";
	static final String forceDeleteVm_URL = "https://api.ucloudbiz.olleh.com/d1/server/servers/";
	static final String VmList_URL = "https://api.ucloudbiz.olleh.com/d1/server/servers/detail";
	static final String VmDetail_URL = "https://api.ucloudbiz.olleh.com/d1/server/servers/";

	static final String getVolume_URL = "https://api.ucloudbiz.olleh.com/d1/volume/";
	static final String connectVmAndVolume_URL = "https://api.ucloudbiz.olleh.com/d1/server/servers/";
	static final String deleteAllVolume_URL = "https://api.ucloudbiz.olleh.com/d1/volume/";
	static final String deleteVolume_URL = "https://api.ucloudbiz.olleh.com/d1/volume/";
	static final String getIP_URL = "https://api.ucloudbiz.olleh.com/d1/nc/IpAddress";
	static final String deleteIP_URL = "https://api.ucloudbiz.olleh.com/d1/nc/IpAddress/";
	static final String IPList_URL = "https://api.ucloudbiz.olleh.com/d1/nc/IpAddress";

	static final String setStaticNAT_URL = "https://api.ucloudbiz.olleh.com/d1/nc/StaticNat";
	static final String staticNATList_URL = "https://api.ucloudbiz.olleh.com/d1/nc/StaticNat";
	static final String DeleteStaticNAT_URL = "https://api.ucloudbiz.olleh.com/d1/nc/StaticNat/";

	static final String openFirewall_URL = "https://api.ucloudbiz.olleh.com/d1/nc/Firewall";
	static final String firewall_List_URL = "https://api.ucloudbiz.olleh.com/d1/nc/Firewall";
	static final String closeFirewall_URL = "https://api.ucloudbiz.olleh.com/d1/nc/Firewall/";

	static final String getToken_URL = "https://api.ucloudbiz.olleh.com/d1/identity/auth/tokens";
	static final String jobID_URL = "https://api.ucloudbiz.olleh.com/d1/nc/Etc?command=queryAsyncJob&jobid=";

	static final String GET = "GET";
	static final String DELETE = "DELETE";
	static final String POST = "POST";
	static final int timeout = 10; //sec

	public static ServerInformation createServer(String serverName, String volumeName) throws Exception {

		String requestBody;
		String result;
		String response;
		String VmImage_complete1 = "03a6328b-76c8-4d15-8e3f-d5cae5cf1156";
		String VmImage_nginx = "fab16e16-5d53-4e00-892f-bec4b10079bb";
		
		System.out.println("Server creation has started");
		// token
		result = RestAPI.request(getToken_URL, POST, RequestBody.getToken());
		// result = RestAPI.post(getToken_URL, RequestBody.getToken(), 10);
		String token = ResponseParser.statusCodeParser(result);
		String projectID = ResponseParser.getProjectIdFromToken(result);

		// get vm
		String VmimageID = VmImage_complete1;
		String specs = "61c68bc1-3a56-4827-9fd1-6a7929362bf6";
		requestBody = RequestBody.getVm(serverName, VmimageID, specs);
		// result = RestAPI.request(getVm_URL, POST, token, requestBody);
		result = RestAPI.post(getVm_URL, token, requestBody, 10);
		response = ResponseParser.statusCodeParser(result);
		String VmId = ResponseParser.VmCreateResponseParser(response);

		// get volume
		String volumeImageID = "556aacd2-de16-47fc-b230-3db3a55be50d";
		requestBody = RequestBody.getVolume(volumeName, volumeImageID);
		result = RestAPI.request(getVolume_URL + projectID + "/volumes", POST, token, requestBody);
		response = ResponseParser.statusCodeParser(result);
		String volumeID = ResponseParser.volumeCreateResponseParser(response);

		// get public ip
		result = RestAPI.request(getIP_URL, POST, token, "");
		response = ResponseParser.statusCodeParser(result);
		String publicIpjobID = ResponseParser.IPCreateResponseParser(response);

		//get public ip_id
		response = ResponseParser.lookupJobId(publicIpjobID, token, 10);
		String publicIP_ID = ResponseParser.PublicIPJobIDlookupParser(response);

		// connect vm and volume
		System.out.print("Server creation is in progress ");
		int count = 0;
		while (true) {
			requestBody = RequestBody.connectVmAndVolume(volumeID);
			result = RestAPI.request(connectVmAndVolume_URL + VmId + "/os-volume_attachments", POST, token, requestBody);
			response = ResponseParser.statusCodeParser(result);
			if (response.length() > 0) {
				break;
			}
			Thread.sleep(1000);
			count++;
			System.out.print(count+" ");
		}
		 System.out.println("Server creation is done");
		 System.out.println("Server is connected with disk");
		 
		// look up vm ip
		result = RestAPI.request(VmDetail_URL + VmId, GET, token, "");
		response = ResponseParser.statusCodeParser(result);
		String privateIP = ResponseParser.VmDetailResponseParser(response);

		// set static NAT
		String networkID = "71655962-3e67-42d6-a17d-6ab61a435dfe";
		requestBody = RequestBody.setStaticNat(privateIP, networkID, publicIP_ID);
		result = RestAPI.request(setStaticNAT_URL, POST, token, requestBody);
		response = ResponseParser.statusCodeParser(result);
		String staticNAT_ID = ResponseParser.staticNATSettingResponseParser(response);

		// open firewall
		requestBody = RequestBody.openFirewall("0", "65535", staticNAT_ID, "6b812762-c6bc-4a6d-affb-c469af1b4342",
				"172.25.1.1/24", "ALL", "71655962-3e67-42d6-a17d-6ab61a435dfe");
		result = RestAPI.request(openFirewall_URL, POST, token, requestBody);
		response = ResponseParser.statusCodeParser(result);
		String firewallJobId = ResponseParser.firewallJobIdParser(response);
		
		System.out.println("server creation is done");
		
		ServerInformation serverInformation = new ServerInformation(VmId, volumeID, publicIP_ID, staticNAT_ID, projectID, firewallJobId);
		return serverInformation;
		
	}

	public static void deleteServer(ServerInformation serverInformation) throws Exception {
		String result="";
		System.out.println("Server deletion has started");
		// token
		result = RestAPI.post(getToken_URL, RequestBody.getToken(), 10);
		String token = ResponseParser.statusCodeParser(result);

		ResourceHandler.deleteVmOnly(serverInformation.getVmId(), token, timeout);
		ResourceHandler.deleteVolume(serverInformation.getVolumeID(), serverInformation.getProjectID(), token, timeout);
		ResourceHandler.closeFirewall(serverInformation.getFirewallJobId(), token, timeout);
		ResourceHandler.deleteStaticNat(serverInformation.getStaticNAT_ID(), token, timeout);
		ResourceHandler.deletePublicIp(serverInformation.getPublicIP_ID(), token, timeout);

		System.out.println("server deletion is done");
	}

	public static void init() throws Exception {
		String result;
		String response;
		result = RestAPI.request(getToken_URL, POST, RequestBody.getToken());
		String token = ResponseParser.statusCodeParser(result);
		String projectID = ResponseParser.getProjectIdFromToken(result);

		// close firewall
		result = RestAPI.request(firewall_List_URL, GET, token, "");
		response = ResponseParser.statusCodeParser(result);
		Initialization.closeAllFirewall(response, token);

		// unlock static NAT
		result = RestAPI.request(staticNATList_URL, GET, token, "");
		response = ResponseParser.statusCodeParser(result);
		Initialization.deleteAllStaticNAT(response, token);

		// delete ip
		result = RestAPI.request(IPList_URL, GET, token, "");
		response = ResponseParser.statusCodeParser(result);
		Initialization.deleteAllIP(response, token);

		// delete vm
		result = RestAPI.request(VmList_URL, GET, token, "");
		response = ResponseParser.statusCodeParser(result);
		Initialization.deleteAllVm(response, token);

		//delete volume
		result = RestAPI.get(deleteAllVolume_URL+projectID+"/volumes/detail", token, timeout);
		response = ResponseParser.statusCodeParser(result);
		Initialization.deleteAllVolume(response, token, projectID);

		System.out.println("initialization is done");
	}

 	public static void lookup() throws Exception {
	}

}
