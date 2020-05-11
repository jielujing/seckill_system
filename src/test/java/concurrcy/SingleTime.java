package concurrcy;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.seckill.model.entity.CodeMsg;
import com.seckill.model.entity.Result;
import com.seckill.model.entity.User;
import com.seckill.model.rediskey.SeckillKey;
import com.seckill.model.rediskey.UserKey;
import com.seckill.redis.RedisService;
import com.seckill.util.MD5Util;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.List;

public class SingleTime implements Runnable {
    User user;
    long goodsId;
    String token;
    SdkRestProxy restProxy=new SdkRestProxy();

    SingleTime(){}

    SingleTime(User user,long goodsId){
        this.user=user;
        this.goodsId=goodsId;
    }

    @Override
    public void run() {
        try{
            login();
            createVC();
            doSeckill();
            //getResult();
        }catch (Exception e){
            //e.printStackTrace();
        }

    }

    public boolean login(){
        HttpHeaders headers=new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String,String> params=new LinkedMultiValueMap<>();
        params.add("mobile",""+user.getId());
        params.add("password",MD5Util.formPassToDBPass(user.getPassword(),"1a2b3c4d"));
        //String param="{\"mobile\":\"18233334444\",\"password\":\""+ MD5Util.formPassToDBPass("123456","1a2b3c4d")+"\"}";
        ResponseEntity<String> response=restProxy.post("http://127.0.0.1:8080/login/do_login",params,headers);
        verifyResult(response);
        token=response.toString().split("token=")[1].split(";")[0];
        return response.getStatusCodeValue()==200;
    }

    public String getToken(){

        return token;
    }

    public boolean createVC(){
        HttpHeaders headers=new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        List<String> cookies=new ArrayList<>();
        cookies.add("token="+ getToken());
        headers.put(HttpHeaders.COOKIE,cookies);
        MultiValueMap<String,String> params=new LinkedMultiValueMap<>();
//        params.add("goodsId",""+goodsId);
        //String param="{\"mobile\":\"18233334444\",\"password\":\""+ MD5Util.formPassToDBPass("123456","1a2b3c4d")+"\"}";
        ResponseEntity<String> response=restProxy.get("http://127.0.0.1:8080/seckill/verifyCode?goodsId="+goodsId,params,headers);
        return response.getStatusCodeValue()==200;
    }

    public Integer getVerifyCode(){
        HttpHeaders headers=new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        List<String> cookies=new ArrayList<>();
        cookies.add("token="+ getToken());
        headers.put(HttpHeaders.COOKIE,cookies);
        MultiValueMap<String,String> params=new LinkedMultiValueMap<>();
        params.add("goodsId",""+goodsId);
        //String param="{\"mobile\":\"18233334444\",\"password\":\""+ MD5Util.formPassToDBPass("123456","1a2b3c4d")+"\"}";
        ResponseEntity<String> response=restProxy.post("http://127.0.0.1:8080/seckill/getVerifyCode",params,headers);
        verifyResult(response);
        return Integer.valueOf(response.getBody().split("data\":\"")[1].split("\"")[0]);
    }

    public String getPath(){
        HttpHeaders headers=new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        List<String> cookies=new ArrayList<>();
        cookies.add("token="+ getToken());
        headers.put(HttpHeaders.COOKIE,cookies);
        MultiValueMap<String,Object> params=new LinkedMultiValueMap<>();
//        params.add("goodsId",goodsId);
//        params.add("verifyCode",getVerifyCode());
        //String param="{\"mobile\":\"18233334444\",\"password\":\""+ MD5Util.formPassToDBPass("123456","1a2b3c4d")+"\"}";
        ResponseEntity<String> response=restProxy.get("http://127.0.0.1:8080/seckill/path?goodsId="+goodsId+"&verifyCode="+getVerifyCode(),params,headers);
        verifyResult(response);
        return response.getBody().split("data\":\"")[1].split("\"")[0];
    }

    public boolean doSeckill(){
        HttpHeaders headers=new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        List<String> cookies=new ArrayList<>();
        cookies.add("token="+ getToken());
        headers.put(HttpHeaders.COOKIE,cookies);
        MultiValueMap<String,String> params=new LinkedMultiValueMap<>();
        params.add("goodsId",""+goodsId);
        //String param="{\"mobile\":\"18233334444\",\"password\":\""+ MD5Util.formPassToDBPass("123456","1a2b3c4d")+"\"}";
        ResponseEntity<String> response=restProxy.post("http://127.0.0.1:8080/seckill/"+getPath()+"/do_seckill",params,headers);
        verifyResult(response);
        return response.getStatusCodeValue()==200;
    }

    public boolean getResult(){
        HttpHeaders headers=new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        List<String> cookies=new ArrayList<>();
        cookies.add("token="+ getToken());
        headers.put(HttpHeaders.COOKIE,cookies);
        MultiValueMap<String,String> params=new LinkedMultiValueMap<>();
        //params.add("goodsId",""+goodsId);
        ResponseEntity<String> response=restProxy.get("http://127.0.0.1:8080/seckill/result?goodsId="+goodsId,params,headers);
        while(Integer.valueOf(response.getBody().split("data\":")[1].split("}")[0])==0){
            response=restProxy.get("http://127.0.0.1:8080/seckill/result?goodsId="+goodsId,params,headers);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return response.getStatusCodeValue()==200;
    }

    public void verifyResult(ResponseEntity<String> responseEntity){
        if (responseEntity.getStatusCodeValue()!=200) {
            throw new RuntimeException();
        }
        CodeMsg codeMsg=JSON.parseObject( responseEntity.getBody(), CodeMsg.class);
        System.out.println(responseEntity.getBody());
        if(codeMsg.getCode()!=0){
            throw new RuntimeException();
        }
    }
}
