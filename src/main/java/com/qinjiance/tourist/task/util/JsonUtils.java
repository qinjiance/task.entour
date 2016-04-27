package com.qinjiance.tourist.task.util;

import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonUtils {

	private static final Logger logger = LoggerFactory.getLogger(JsonUtils.class);
	private final static ObjectMapper mapper = new ObjectMapper();
	
	/**
	 * 解析json string为map对象
	 * @param str
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> parse(String str){
		Map<String, Object> retMap = null;
		try {
			retMap = mapper.readValue(str, Map.class);
		} catch (Exception e) {
			System.out.println(e);
			logger.error("String to map failed!", e);
		}
		return retMap;
	}
	
	/**
	 * 解析json string为list对象
	 * @param str
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<Map<String, Object>> parseArray(String str){
		List<Map<String, Object>> array = null;
		try {
			array = mapper.readValue(str, List.class);
		} catch (Exception e) {
			logger.error("String to map failed!", e);
		}
		return array;
	}
	
	/**
	 * object对象转换给json string
	 * @param obj
	 * @return
	 */
	public static String objectToJson(Object obj){
		String retStr = "";
		if(obj == null){
			return retStr;
		}
		try {
			retStr = mapper.writeValueAsString(obj);
		} catch (Exception e) {
			logger.error("Object to json string failed!", e);
		}
		return retStr;
	}

}
