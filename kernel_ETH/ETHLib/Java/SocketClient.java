package EthSign;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import org.json.JSONArray;
import org.json.JSONObject;


public class SocketClient {
    private Client client;
    private WebResource webResource;
    private ClientResponse response;

    public SocketClient(String resource) {
        connect( resource);
    }

    private void connect(String resource) {
//        try {
            client = Client.create();
            //webResource = client.resource("http://127.0.0.1:8545");
            //webResource = client.resource("http://10.123.15.70:8545");
            webResource = client.resource(resource);
            
            
            ClientResponse response = webResource.accept("application/json")
                    .get(ClientResponse.class);
	 		if (response.getStatus() != 200) {
	 		   throw new RuntimeException("Failed : HTTP error code : "
	 			+ response.getStatus());
	 		}	
	 		//\\String output = response.getEntity(String.class);	
	 		//\\System.out.println("Output from Server .... \n");
	 		//\\System.out.println(output);
	 		
	 		
            
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }


    public Object send(String method, Object params, String message) throws Exception {
        Object result;
        Object parameters = params;
        
        if (method.contains("lockAccount")){
        	return true;
        }
        
        if (method.equals("personal_sendTransaction")){
        	
        		JSONObject jo = new JSONObject();

        		jo.put("params", parameters);

        		JSONObject joo = new JSONObject(jo.toString());
        		JSONArray ja = new JSONArray();
        		ja.put(joo.getJSONArray("params").get(0));
        		ja.put("FI2018^^");
        		parameters = ja;
        }
        
        JSONArray jaP = new JSONArray();
        if(method.contains("_sendTransaction")){
        	JSONObject jo = new JSONObject();

    		jo.put("params", parameters);

    		JSONObject joo = new JSONObject(jo.toString());
    		JSONArray ja = new JSONArray();
    		ja.put(joo.getJSONArray("params").get(0));
        	
    		//System.out.println(((JSONObject)ja.get(0)).getString("data"));
        	
	        //jaP.put(Test.transRawMessage(ja.get(0).toString()));
    		jaP.put(((JSONObject)ja.get(0)).getString("data"));
	        parameters = jaP;
	        method = "eth_sendRawTransaction";
        }
	    
        JSONObject requestPara = new JSONObject();
        requestPara.put("jsonrpc", "2.0");
        requestPara.put("method", method);
        requestPara.put("params", parameters);
        requestPara.put("id", 1);


        
        
        String output;
        //\\System.out.println("\t" + method + " : " + message);
        if (method.equals(""))
        	return null;
        //\\System.out.println("\tParam: " + requestPara);
        try {
            response = webResource.type("application/json").post(ClientResponse.class, requestPara.toString());
            //TimeUnit.SECONDS.sleep(8);
            output = response.getEntity(String.class);
            //\\System.out.println("\tOutput: " + output);


	    	JSONObject jsonObject1 = new JSONObject(output);         	
	    	ArrayList<Object> params1 = new ArrayList<Object>();            	
            if((method.contains("_sendTransaction") || method.contains("_sendRawTransaction")) && jsonObject1.has("result")){            	
                params1.add(jsonObject1.getString("result"));
            	JSONObject requestPara1 = new JSONObject();
                requestPara1.put("jsonrpc", "2.0");
                requestPara1.put("method", "eth_getTransactionReceipt");
                requestPara1.put("params", params1);
                requestPara1.put("id", 1);

                for (int i = 0; i < 0; i++){
	            	ClientResponse response1 = webResource.type("application/json").post(ClientResponse.class, requestPara1.toString());
	                String output1 = response1.getEntity(String.class);
	                //\\System.out.println("\t\tReceipt Output: " + output1);
	                if (output1.contains("\"result\":null")){
		            	TimeUnit.SECONDS.sleep(10);
	                }else{
	                	break;
	                }
                }
                
            }
            
            
            JSONObject jsonObject = new JSONObject(output);
            if (jsonObject.has("result")) 
            	result = jsonObject.get("result");
            else if (jsonObject.has("error")) 
            	result =  jsonObject.toString() ;
            else 
            		result = jsonObject;

        } catch (Exception e) {
            //\\System.out.println(e.getMessage());

            throw new Exception("GETH is down!");
        }

        return result;
    }
}



