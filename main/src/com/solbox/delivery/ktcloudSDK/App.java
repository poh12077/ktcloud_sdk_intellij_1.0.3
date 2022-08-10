package com.solbox.delivery.ktcloudSDK;


public class App {

	public static void main(String[] args) {
		try {

			ServerInformation serverInformation = KTCloudOpenAPI.createServer("nanana", "solbox");
			//KTCloudOpenAPI.deleteServer(serverInformation);
			//KTCloudOpenAPI.init();
			

		} catch (Exception e) {
			System.out.println(e);
		}
	}
}