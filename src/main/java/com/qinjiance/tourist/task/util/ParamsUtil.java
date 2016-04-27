package com.qinjiance.tourist.task.util;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.net.URLEncoder;
import java.net.URLDecoder;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParamsUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(ParamsUtil.class);

	public static String encode(String s, String charset) {
        try {
            return URLEncoder.encode(s, charset);
        } catch (UnsupportedEncodingException e) {
        	logger.error("ParamsUtil encode error", e);
        }
        return null;
    }

	public static String decode(String s, String charset) {
        try {
            return URLDecoder.decode(s, charset);
        } catch (Exception e) {
        	logger.error("ParamsUtil decode error", e);
        }
        return null;
    }

    public static String joinParamsValue(Map<String, String> params){
		StringBuilder sb = new StringBuilder();
        TreeMap<String, String> paramsMap = new TreeMap<String, String>();
		for (Map.Entry<String, String> entry : params.entrySet()) {
            paramsMap.put(entry.getKey(), entry.getValue());
        }
		for (Map.Entry<String, String> entry : paramsMap.entrySet()){
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
		}
		return sb.toString();
	}
    
    public static String joinValueOnly(Map<String, String> params){
    	StringBuilder sb = new StringBuilder();
        TreeMap<String, String> paramsMap = new TreeMap<String, String>();
		for (Map.Entry<String, String> entry : params.entrySet()) {
            paramsMap.put(entry.getKey().toLowerCase(), entry.getValue());
        }
		for (Map.Entry<String, String> entry : paramsMap.entrySet()){
            sb.append(entry.getValue());
		}
		return sb.toString();
    }
    
    public static Integer parseChannelId(String userIdentity){
		Pattern p = Pattern.compile("(^[0-9]*)_");
		Matcher m = p.matcher(userIdentity);
		
		if(m.find()){
			String channelId = m.group(1).trim();
			return Integer.parseInt(channelId);
		}
		return null;
	}
    
    public static String parseUserId(String userIdentity){
    	if(StringUtils.isEmpty(userIdentity))
    		return null;
    	Pattern p = Pattern.compile("^[0-9]*_(.+)");
    	Matcher m = p.matcher(userIdentity);
		
		if(m.find()){
			String userid = m.group(1).trim();
			return userid;
		}
		return null;
    }
    
    public static void main(String[] args){
    	Map<String, String> params = new HashMap<String, String>();
		params.put("userIdentity", "userIdentity->");
		params.put("appId", "appId->"); //通用sdk的appid
		params.put("generalOrder", "generalOrder->");
		params.put("t", "t->");
		params.put("appOrder", "appOrder->");
		params.put("amount", "amount->");
		params.put("payStatus", "payStatus->");
		params.put("serverId", "serverId->");
		params.put("ext", "ext->");
		System.out.println(joinValueOnly(params));
    }
}
