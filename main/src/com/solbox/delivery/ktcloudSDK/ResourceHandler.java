package com.solbox.delivery.ktcloudSDK;

import org.json.JSONObject;
import java.net.HttpURLConnection;
import org.json.JSONArray;
import org.json.JSONException;


public class ResourceHandler {

    static void getToken() throws Exception {

    }

    static String getVm(String getVmUrl, String token, String serverName, String vmImageId, String specs, int timeout) throws Exception {
       String requestBody = RequestBody.getVm(serverName, vmImageId, specs);
       String result = RestAPI.post(getVmUrl, token, requestBody, timeout);
       String  response = ResponseParser.statusCodeParser(result);
        String vmId = ResponseParser.VmCreateResponseParser(response);
        return vmId;
    }


    static String getVolume(String getVolumeUrl, String token, String volumeName, String volumeImageId, String projectId, int timeout) throws Exception {
       String requestBody = RequestBody.getVolume(volumeName, volumeImageId);
      String  result = RestAPI.post(getVolumeUrl + projectId + "/volumes", token, requestBody, timeout);
       String  response = ResponseParser.statusCodeParser(result);
        String volumeID = ResponseParser.volumeCreateResponseParser(response);
        return volumeID;
    }

    static String getPublicIp(String getPublicIpUrl, String token, int timeout) throws Exception {
        String  result = RestAPI.post(getPublicIpUrl, token, "", timeout);
       String response = ResponseParser.statusCodeParser(result);
        String publicIpJobId = ResponseParser.IPCreateResponseParser(response);
        response = ResponseParser.lookupJobId(publicIpJobId, token, timeout);
        String publicIpId = ResponseParser.PublicIPJobIDlookupParser(response);
        return publicIpId;
    }

    static String setStaticNat(String setStaticNatUrl, String token, String networkId, String vmPrivateIp, String publicIpId, int timeout) throws Exception {
       String  requestBody = RequestBody.setStaticNat(vmPrivateIp, networkId, publicIpId);
       String  result = RestAPI.post(setStaticNatUrl, token, requestBody, timeout);
       String  response = ResponseParser.statusCodeParser(result);
        String staticNatId = ResponseParser.staticNATSettingResponseParser(response);
        return staticNatId;
    }

    static String openFirewall(String openFirewallUrl, String token, String startPort, String endPort, String staticNatId, String sourceNetworkId,
                               String destinationNetworkAddress, String protocol, String destinationNetworkId, int timeout) throws Exception {
       String requestBody = RequestBody.openFirewall(startPort, endPort, staticNatId, sourceNetworkId, destinationNetworkAddress, protocol, destinationNetworkId);
       String result = RestAPI.post(openFirewallUrl, token, requestBody, timeout);
       String response = ResponseParser.statusCodeParser(result);
        String firewallJobId = ResponseParser.firewallJobIdParser(response);
        return firewallJobId;
    }

    static void deleteVmOnly(String serverID, String token, int timeout) throws Exception {
        String requestBody = RequestBody.forceDeleteVm();
        String result = RestAPI.post(KTCloudOpenAPI.forceDeleteVm_URL + serverID + "/action", token, requestBody,
                timeout);
        JSONObject jsonResult = new JSONObject(result);
        if (jsonResult.getInt("statusCode") == HttpURLConnection.HTTP_CREATED
                || jsonResult.getInt("statusCode") == HttpURLConnection.HTTP_OK
                || jsonResult.getInt("statusCode") == HttpURLConnection.HTTP_ACCEPTED) {
            System.out.println("Server deletion is in progress");
        } else {
            System.out.println("Server deletion failed");
        }
    }

    static void deleteVolume(String volumeID, String projectID, String token, int timeout) throws Exception {
        int count = 0;
        while (true) {
            String result = RestAPI.delete(KTCloudOpenAPI.deleteVolume_URL + projectID + "/volumes/" + volumeID, token,
                    timeout);
            JSONObject jsonResult = new JSONObject(result);
            if (jsonResult.getInt("statusCode") == HttpURLConnection.HTTP_CREATED
                    || jsonResult.getInt("statusCode") == HttpURLConnection.HTTP_OK
                    || jsonResult.getInt("statusCode") == HttpURLConnection.HTTP_ACCEPTED) {
                System.out.println("Volume deletion is in progress");
                break;
            } else {
                System.out.print(count + " ");
            }
            count++;
            Thread.sleep(1000);
        }
    }

    static void deleteStaticNat(String staticNatId, String token, int timeout) throws Exception {
        String result = RestAPI.delete(KTCloudOpenAPI.DeleteStaticNAT_URL+staticNatId, token,	timeout);
        JSONObject jsonResult = new JSONObject(result);
        if (jsonResult.getInt("statusCode") == HttpURLConnection.HTTP_CREATED
                || jsonResult.getInt("statusCode") == HttpURLConnection.HTTP_OK
                || jsonResult.getInt("statusCode") == HttpURLConnection.HTTP_ACCEPTED) {
            System.out.println("static NAT has been disabled");
        } else {
            System.out.println("static NAT deletion has failed");
        }
    }

    static void deletePublicIp(String publicIpId, String token, int timeout) throws Exception {
        String result = RestAPI.delete(KTCloudOpenAPI.deleteIP_URL+publicIpId, token, timeout);
        JSONObject jsonResult = new JSONObject(result);
        if (jsonResult.getInt("statusCode") == HttpURLConnection.HTTP_CREATED
                || jsonResult.getInt("statusCode") == HttpURLConnection.HTTP_OK
                || jsonResult.getInt("statusCode") == HttpURLConnection.HTTP_ACCEPTED) {
            System.out.println("public IP has been deleted");
        } else {
            System.out.println("public IP deletion has failed");
        }
    }



    static void closeFirewall(String firewallJobId, String token, int timeout) throws Exception {
        String response = ResponseParser.lookupJobId(firewallJobId,token,timeout);
        String firewallId = ResponseParser.firewallIdParser(response);
        String result = RestAPI.delete(KTCloudOpenAPI.closeFirewall_URL+firewallId, token, timeout);
        JSONObject jsonResult = new JSONObject(result);
        if (jsonResult.getInt("statusCode") == HttpURLConnection.HTTP_CREATED
                || jsonResult.getInt("statusCode") == HttpURLConnection.HTTP_OK
                || jsonResult.getInt("statusCode") == HttpURLConnection.HTTP_ACCEPTED) {
            System.out.println("firewall has closed");
        } else {
            System.out.println("firewall still opened");
        }
    }


}
