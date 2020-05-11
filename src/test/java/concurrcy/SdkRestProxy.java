package concurrcy;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class SdkRestProxy {
    private static final Logger logger = LoggerFactory.getLogger(SdkRestProxy.class);

    RestTemplate restTemplate=new RestTemplate();

    /**
     * POST
     */
    public ResponseEntity<String> post(String url, Object params, HttpHeaders headers) {
        ResponseEntity<String> response = this.execute(url, HttpMethod.POST, params, headers);
        return response;
    }

    /**
     * GET
     */
    public ResponseEntity<String> get(String url, Object params, HttpHeaders headers) {
        url = this.formatUrlParam(url, params);
        ResponseEntity<String> response = this.execute(url, HttpMethod.GET, null, headers);
        return response;
    }

    /**
     * put
     */
    public ResponseEntity<String> put(String url, Object params, HttpHeaders headers) {
        ResponseEntity<String> response = this.execute(url, HttpMethod.PUT, params, headers);
        return response;
    }

    /**
     * delete
     */
    public ResponseEntity<String> delete(String url, Object params, HttpHeaders headers) {
        url = this.formatUrlParam(url, params);
        ResponseEntity<String> response = this.execute(url, HttpMethod.DELETE, null, headers);
        return response;
    }

    /**
     * 执行请求方法
     *
     * @param url
     * @param method
     * @return
     */
    public ResponseEntity execute(String url, HttpMethod method, Object params, HttpHeaders headers) {
        try {
            if (headers == null) {
                headers = new HttpHeaders();
            }
            HttpEntity request = new HttpEntity(params, headers);
            System.out.println(request);
            ResponseEntity<String> response = restTemplate.exchange(url, method, request, String.class);
            System.out.println(response);
            return response;
        } catch (Exception e) {
            throw new RuntimeException(HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase(), e);
        }
    }

    /**
     * 处理接口返回数据
     *
     * @param result
     * @param type
     * @param <T>
     * @return
     */
    private <T> T processResult(String result, TypeReference<T> type) {
        if (StringUtils.isEmpty(result)) {
            throw new RuntimeException(HttpStatus.NO_CONTENT.getReasonPhrase());
        }
        return JSON.parseObject(result, type);
    }

    /**
     * 组装URL参数
     *
     * @param url
     * @param params
     * @return
     */
    private String formatUrlParam(String url, Object params) {
        if (params != null) {
            try {
                Map<String, Object> paramMap;
                if (params instanceof Map) {
                    paramMap = (Map<String, Object>) params;
                } else if (params instanceof Integer || params instanceof String) {
                    return url;
                } else {
                    paramMap = objectToMap(params);
                }
                String paramStr = formatUrlParam(paramMap);
                url += url.contains("?") ? "&" : "?";
                url += paramStr;
            } catch (Exception e) {
                logger.error("参数格式化异常：" + JSON.toJSONString(params), e);
            }
        }
        return url;
    }

    /**
     * 对象转换为Map
     *
     * @param obj
     * @return
     * @throws Exception
     */
    public static Map<String, Object> objectToMap(Object obj) throws Exception {
        Map<String, Object> map = new HashMap<>();
        BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor property : propertyDescriptors) {
            String key = property.getName();
            if (key.compareToIgnoreCase("class") == 0) {
                continue;
            }
            Method getter = property.getReadMethod();
            Object value = getter != null ? getter.invoke(obj) : null;
            if (value != null) {
                if (value instanceof List && !CollectionUtils.isEmpty((List) value)) {
                    value = ((List) value).stream().map(String::valueOf).collect(Collectors.joining(","));
                }
                map.put(key, value);
            }
        }
        return map;
    }

    /**
     * 格式化处理参数
     *
     * @param paramMap
     * @return
     */
    public static String formatUrlParam(Map<String, Object> paramMap) {

        StringBuilder paramStr = new StringBuilder("");
        if (paramMap == null) {
            return paramStr.toString();
        }
        for (Map.Entry<String, Object> map : paramMap.entrySet()) {
            paramStr.append(StringUtils.isEmpty(paramStr) ? "" : "&");
            paramStr.append(map.getKey()).append("=").append(map.getValue());
        }

        return paramStr.toString();
    }
}
