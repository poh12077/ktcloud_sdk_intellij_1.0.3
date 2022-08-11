package com.solbox.delivery.ktcloudSDK;


public class App {

	public static void main(String[] args) {
		try {

			String confPath = "C:\\Users\\young hwa park\\Desktop\\yhp\\source\\ktcloud\\ktcloud_sdk_intelij_1.0.3\\main\\etc\\conf.json";

			ServerInformation serverInformation = KTCloudOpenAPI.createServer("nanana", "solbox",confPath);
			//ServerInformation serverInformation = KTCloudOpenAPI.createServer("nanana",confPath);

			String result = KTCloudOpenAPI.deleteServer(serverInformation);
			//KTCloudOpenAPI.init();

		} catch (Exception e) {
			System.out.println(e);
		}
	}
}