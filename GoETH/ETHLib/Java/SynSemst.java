package EthSign;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.bouncycastle.jcajce.provider.digest.Keccak;
import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;



public class SynSemst {

	public static void main__(String[] args) {
		// TODO Auto-generated method stub
		syncSecmstData();
	}

	
	protected static void syncSecmstData ()
	{
//		String srcAttr = "org.gjt.mm.mysql.Driver";
//		String srcLoc = "jdbc:mysql://127.0.0.1/tiger";
//		String srcUser = "home";		
//		String srcPass = "home";
//		System.out.println(srcUser + " :: " +srcPass);
//		
//		String bc = "http://127.0.0.1:8545";
//		String wallet = "0xc171299505a18fb066a104f88faa98be26777cf1";
//		String pwd = "FI2018^^";
//		String asc = "0x10837f7795fa3c64e1ffcd64469c947e3ec486b3";
//		String gas = "0x82F79CD900";
		
//		//UAT
//		String srcAttr = "com.mysql.cj.jdbc.Driver";
//		String srcLoc = "jdbc:mysql://127.0.0.1/tiger";
//		String srcUser = "vce";		
//		String srcPass = "vcevce1$F";
//		System.out.println(srcUser + " :: " +srcPass);
//		
//		String bc = "http://127.0.0.1:8545";
//		String wallet = "0xbcf70538688c0c01879d0b8ee5aa6c0b907af086";
//		String pwd = "FI2018^^";
//		String asc = "0x4b879f5436eaf95fad9ed9fe3ade220d069fb9ea";
//		String gas = "0x82F79CD900";
		
		//XREX
		String srcAttr = "com.mysql.cj.jdbc.Driver";
		String srcLoc = "jdbc:mysql://aurora-microx-dbcluster-2d777znpjk6y.cluster-csvax1vtczqa.ap-northeast-1.rds.amazonaws.com/microx";
		String srcUser = "dev01";		
		String srcPass = "pass1234";
		System.out.println(srcUser + " :: " +srcPass);
		
						  //"http://ec2-3-112-1-16.ap-northeast-1.compute.amazonaws.com:8545";
		String bc = "http://ec2-3-112-1-16.ap-northeast-1.compute.amazonaws.com:8545";
		bc = "http://3.112.1.16:8545";
		String wallet = "0x5f2427e69c23ddbc742f39e1639e2a9bdb253dfe";
		String pwd = "1234567";
		String asc = "0x4e69367249aa7b69fa267e52f58e2da73b665cca";
		asc = "0x0ecd27293e06d197eae5a06a0e491821df2bd3de";
		asc = "0x53927dd10a3eaf0c9a7074ce603fd98cc0be0758";
		String gas = "0x82F79CD900";
		
		
		
		HashMap<String, String> secmstList = new HashMap<String, String>(32);
		
		try
		{
			if (srcAttr != null && srcLoc != null && srcUser != null && srcPass != null )
			{
				if (srcAttr.startsWith("$"))
					srcAttr = System.getenv(srcAttr.substring(1));
				
				if (srcLoc.startsWith("$"))
					srcLoc = System.getenv(srcLoc.substring(1));
				
				if (srcUser.startsWith("$"))
					srcUser = System.getenv(srcUser.substring(1));
				
				if (srcPass.startsWith("$"))
					srcPass = System.getenv(srcPass.substring(1));
				
			
				
				
				try {
					DriverManager.registerDriver((Driver)Class.forName(srcAttr).newInstance());
		            Connection conn = DriverManager.getConnection(srcLoc,srcUser,srcPass);
		            Statement stmt = conn.createStatement();
		            ResultSet rs;
		 
		            rs = stmt.executeQuery("SELECT security, fractionbase FROM secmstdata");
		            while ( rs.next() ) {
		                String security = rs.getString("security");
		                String fractionbase = rs.getString("fractionbase");
		                System.out.println(security + " :: " + fractionbase);
		                
		                try{
			                Long.parseLong(fractionbase);
			                secmstList.put(security, fractionbase);
		                }catch (Exception e){
		                	System.err.println(security + " :: " + fractionbase + " was not added.");
		                }
		            }
		            conn.close();
		        } catch (Exception e) {
		            e.printStackTrace();
		        }

				
System.out.println("connecting bc...");
				MySocketClient ethSocket = new MySocketClient(bc);
System.out.println("connecting bc connected.");
				
				Set<String> keys = secmstList.keySet();
				Iterator<String> it = keys.iterator();
				int tn = keys.size();
				int i = 0;
				while(it.hasNext()){
					String key = (String)(it.next());
					System.out.println(++i +"/"+tn+")" +  key + " : " + secmstList.get(key));
					createTokens(ethSocket, key, Long.parseLong(secmstList.get(key)), bc, wallet, pwd, asc, gas);
				}
				
			}
		}
		catch (Exception e)
		{
			System.err.print("Error: "+e.toString());
			e.printStackTrace();
			
			try
			{
				Thread.sleep(5000);
			}
			catch (Exception e1){}
			
			System.exit(1);
		}
	}
	
	private static void createTokens(MySocketClient ethSocket, String symbol, Long fractionBase, String bc, String wallet, String pwd, String asc, String gas )
	{
		Converter converter = new Converter();

		
		String token_name = converter.addZero(converter.stringToHex(symbol.toString()), 'l');
        String token_symbol = converter.addZero(converter.stringToHex(""), 'l');
        
        long fbLog = BigDecimal.valueOf(Math.log10(fractionBase * 1.0)).toBigInteger().intValue();
        
        String fraction_base = converter.addZero(Long.toHexString(fbLog), 'r');
        String keccak_createToken = getKeccakHashByName("createToken(bytes32,bytes32,uint32)");
        String hash = "0x" + keccak_createToken + token_name + token_symbol + fraction_base;
        
        
        TransactionBodyModel transactionBodyModel = new TransactionBodyModel();
        transactionBodyModel.setFrom(wallet);
        transactionBodyModel.setTo(asc);
        transactionBodyModel.setGas(gas);
        transactionBodyModel.setData(hash);

        ArrayList<Object> params = new ArrayList<Object>();
        params.add(transactionBodyModel);
        
        
         	try {
                ArrayList<Object> params1 = new ArrayList<Object>();
                params1.add(wallet);
                params1.add(pwd);
                params1.add(600);
                boolean status = (Boolean) ethSocket.send("personal_unlockAccount", params1, "");
		       if (status) {
		    	   try {
						String chainHash = (String) ethSocket.send("eth_sendTransaction", params,"");
						
						System.out.println("\t status = " + getStatus(ethSocket, chainHash));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}       	           
		        } else {
					//controller.getEventDispatcher().closeTransaction((int)tranId, GlobalResult.FAILURE, "Unlock user failed.");					
		            System.err.println("Unlock user failed.");
		        }           
		       } catch (Exception e) {
                System.err.println("Can\'t unlock Account!" + e.getMessage());
            }
  	
	}
	
	 private static String getKeccakHashByName(String name) {    	
	    	byte[] bytes = Numeric.hexStringToByteArray(Numeric.toHexString(name.replaceAll("\\s","").getBytes()));
	    	Keccak.DigestKeccak kecc = new Keccak.Digest256();
	        kecc.update(bytes, 0, bytes.length);
	        return Numeric.toHexString(kecc.digest()).substring(2, 10); 
	    }
	
	 private static String getStatus(MySocketClient ethSocket, String hash_) throws Exception {

	        ArrayList<Object> params = new ArrayList<Object>();
	        params.add(hash_);

	            Object ethReturn = ethSocket.send("eth_getTransactionReceipt", params, "");
	            
	            String status = "NA";
	            String ts = ((JSONObject)ethReturn).getString("status"); 
	      		if (ts.equals("0x0"))
	                status = "Declined";
	      		else
	                status = "Confirmed";        

	        return status;
	    }
	
	
}




class MySocketClient {
    private Client client;
    private WebResource webResource;
    private ClientResponse response;

    public MySocketClient(String resource) {
        connect( resource);
    }

    private void connect(String resource) {
        try {
            client = Client.create();
            //webResource = client.resource("http://127.0.0.1:8545");
            //webResource = client.resource("http://10.123.15.70:8545");
System.out.println("connecting ::::" + resource);
            webResource = client.resource(resource);
            
            
            ClientResponse response = webResource.accept("application/json")
                    .get(ClientResponse.class);
	 		if (response.getStatus() != 200) {
	 		   throw new RuntimeException("Failed : HTTP error code : "
	 			+ response.getStatus());
	 		}	
	 		String output = response.getEntity(String.class);	
	 		System.out.println("Output from Server .... \n");
	 		System.out.println(output);
	 		
	 		
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public Object send(String method, Object params, String message) throws Exception {
        Object result;
        Object parameters = params;
        
        if (method.contains("lockAccount")){
        	//return true;
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
        if(method.contains("_sendTransaction--")){
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
//        System.out.println("\t" + method + " : " + message);
        if (method.equals(""))
        	return null;
//        System.out.println("\tParam: " + requestPara);
        try {
            response = webResource.type("application/json").post(ClientResponse.class, requestPara.toString());
            //TimeUnit.SECONDS.sleep(8);
            output = response.getEntity(String.class);
//            System.out.println("\tOutput: " + output);


	    	JSONObject jsonObject1 = new JSONObject(output);         	
	    	ArrayList<Object> params1 = new ArrayList<Object>();            	
            if((method.contains("_sendTransaction") || method.contains("_sendRawTransaction")) && jsonObject1.has("result")){            	
                params1.add(jsonObject1.getString("result"));
            	JSONObject requestPara1 = new JSONObject();
                requestPara1.put("jsonrpc", "2.0");
                requestPara1.put("method", "eth_getTransactionReceipt");
                requestPara1.put("params", params1);
                requestPara1.put("id", 1);
                
				System.out.println("\tChainHash = " +params1);
                System.out.print("\tGetting TransactionReceipt...");
                int i = 0;
                for (; i < 60; i++){
	            	ClientResponse response1 = webResource.type("application/json").post(ClientResponse.class, requestPara1.toString());
	                String output1 = response1.getEntity(String.class);
	                
//	                ObjectMapper mapper = new ObjectMapper();
//	                Object json = mapper.readValue(output1, Object.class);
//	                String indented = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
	                
	                
//	                String formattedData=new GsonBuilder().setPrettyPrinting().create().toJson(output1);
	                
//	                System.out.println("\t\tReceipt Output: " + formattedData);
	               System.out.print(i);
	                if (output1.contains("\"result\":null")){
		            	TimeUnit.SECONDS.sleep(10);
	                }else{
	                	break;
	                }
                }
                System.out.println("OK.");
                 
            }
            
            
            JSONObject jsonObject = new JSONObject(output);
            if (jsonObject.has("result")) 
            	result = jsonObject.get("result");
            else if (jsonObject.has("error")) 
            	result =  jsonObject.toString() ;
            else 
            		result = jsonObject;

        } catch (Exception e) {
            System.out.println(e.getMessage());

            throw new Exception("GETH is down!");
        }

        return result;
    }
}
