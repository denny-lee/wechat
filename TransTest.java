package com.lee;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import net.sf.json.JSONObject;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;


public class TransTest {

	public static void main(String[] args) {
		long time = Calendar.getInstance().getTimeInMillis() / 1000;
		String tiemStr = String.valueOf(time);
		Random r = new Random(time);
		int nonce = r.nextInt();
		if(nonce < 0) {
			nonce = - nonce;
		}
		String nStr = String.valueOf(nonce);
		if(nStr.length() > 4) {
			nStr = nStr.substring(0, 4);
		}
		System.out.println(nStr);
		CloseableHttpClient httpclient = HttpClients.createDefault();
//		String text = "你好";
		String text = "我爱你";
		String text2 = "";
		try {
//			text = URLEncoder.encode(text, "UTF-8");
			text2 = URLEncoder.encode(text, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String str = "tmt.api.qcloud.com/v2/index.php?Action=TextTranslate&Nonce="+nStr+"&Region=sh&SecretId=AKIDtfFiFihfR5Zt2wkKn5B9wYcaP2Wo4sdF&Timestamp="+tiemStr;
		String strEnd = "&source=zh&sourceText="+text+"&target=en";
		String strEnd2 = "&source=zh&sourceText="+text2+"&target=en";
        String str2 = "GET"+ str+ strEnd;
        System.out.println(str2);
        String key = "ZzDF4a1INYbT9dFgweEcopu42oU0LUSF";
//        String aaa = "cvm.api.qcloud.com/v2/index.php?Action=DescribeInstances&Nonce="+nStr+"&Region=gz&SecretId=AKIDtfFiFihfR5Zt2wkKn5B9wYcaP2Wo4sdF&Timestamp="+tiemStr;
//        String aaaEnd = "&instanceIds.0=ins-09dx96dg&limit=20&offset=0";
//        String aaa2 = "GET" + aaa + aaaEnd;
        /*https://tmt.api.qcloud.com/v2/index.php?
            Action=TextTranslate
            &Nonce=3253
            &Region=gz
            &SecretId=AKIDz8krbsJ5yKBZQpn74WFkmLPx3gnPhESA
            &Timestamp=1480060650
            &Signature=HgIYOPcx5lN6gz8JsCFBNAWp2oQ
            &sourceText=%E4%BD%A0%E5%A5%BD
            &source=zh
            &target=en*/
        try {
        	String sign = enc(str2, key);
        	System.out.println(sign);
        	String url = "https://"+str+strEnd2+"&Signature="+URLEncoder.encode(sign, "UTF-8");
        	String urlexample = "https://tmt.api.qcloud.com/v2/index.php?Nonce=9547&Region=gz&Timestamp=1484487800&Action=TextTranslate&SecretId=AKIDtfFiFihfR5Zt2wkKn5B9wYcaP2Wo4sdF&source=zh&sourceText=%25E4%25BD%25A0%25E5%25A5%25BD&target=en&Signature=9ESznad4zKiy81l4c6f8FNwKV5k%3D";
        	System.out.println(url);
        	System.out.println(urlexample);
        	System.out.println("{");
        	for(String sss : url.split("&")) {
        		System.out.println("\t" + sss);
        	}
        	System.out.println("}");
            HttpGet httpget = new HttpGet(url);
            
            System.out.println("Executing request " + httpget.getRequestLine());

            // Create a custom response handler
            ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

                @Override
                public String handleResponse(
                        final HttpResponse response) throws ClientProtocolException, IOException {
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {
                        HttpEntity entity = response.getEntity();
                        return entity != null ? EntityUtils.toString(entity) : null;
                    } else {
                        throw new ClientProtocolException("Unexpected response status: " + status);
                    }
                }

            };
            String responseBody = httpclient.execute(httpget, responseHandler);
            System.out.println("----------------------------------------");
            System.out.println(responseBody);
            JSONObject json = JSONObject.fromObject(responseBody);
            String result = URLDecoder.decode(json.getString("targetText"), "UTF-8");
            System.out.println(result);
        } catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
            try {
				httpclient.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
	}

	public static String enc(String s, String key) throws Exception {
//		String key = "Gu5t9xGARNpq86cd98joQYCN3Cozk1qA";
//		String s = "GETcvm.api.qcloud.com/v2/index.php?Action=DescribeInstances&Nonce=11886&Region=gz&SecretId=AKIDz8krbsJ5yKBZQpn74WFkmLPx3gnPhESA&Timestamp=1465185768&instanceIds.0=ins-09dx96dg&limit=20&offset=0";
		byte[] bt = HmacSHA1Encrypt(s, key);
//		System.out.println(encode(bt));
		return encode(bt);
	}
	public static byte[] HmacSHA1Encrypt(String encryptText, String encryptKey) throws Exception {
		String ENCODING = "UTF-8";
		String MAC_NAME = "HmacSHA1";
        byte[] data=encryptKey.getBytes(ENCODING);  
        //根据给定的字节数组构造一个密钥,第二参数指定一个密钥算法的名称  
        SecretKey secretKey = new SecretKeySpec(data, MAC_NAME);   
        //生成一个指定 Mac 算法 的 Mac 对象  
        Mac mac = Mac.getInstance(MAC_NAME);   
        //用给定密钥初始化 Mac 对象  
        mac.init(secretKey);    
          
        byte[] text = encryptText.getBytes(ENCODING);    
        //完成 Mac 操作   
        return mac.doFinal(text);    
    } 
	public static String encode(byte[] bstr){    
		   return new sun.misc.BASE64Encoder().encode(bstr);    
		   }    
		   
		   /**  
		    * 解码  
		    * @param str  
		    * @return string  
		    */    
		   public static byte[] decode(String str){    
		   byte[] bt = null;    
		   try {    
		       sun.misc.BASE64Decoder decoder = new sun.misc.BASE64Decoder();    
		       bt = decoder.decodeBuffer( str );    
		   } catch (IOException e) {    
		       e.printStackTrace();    
		   }    
		   
		       return bt;    
		   }   
}
