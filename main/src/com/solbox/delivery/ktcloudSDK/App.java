package com.solbox.delivery.ktcloudSDK;


public class App {

	public static void main(String[] args) {
		try {

			ServerInformation serverInformation = KTCloudOpenAPI.createServer("nanana", "solbox");
			String result = KTCloudOpenAPI.deleteServer(serverInformation);
			//KTCloudOpenAPI.init();
			System.out.println(result);

		} catch (Exception e) {
			System.out.println(e);
		}
	}
}