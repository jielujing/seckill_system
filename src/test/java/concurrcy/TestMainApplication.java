package concurrcy;

import com.seckill.model.entity.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class TestMainApplication {
    public static void main(String[] args) {
        Map<String,List<Long>> map=new HashMap<>();
        map.put("login", new ArrayList<>());
        map.put("verifycode", new ArrayList<>());
        map.put("getverifycode", new ArrayList<>());
        map.put("path", new ArrayList<>());
        map.put("do_seckill", new ArrayList<>());
        map.put("total", new ArrayList<>());
        List<User> userList = new ArrayList<>();
        for (int i = 1; i <= 1000; i++) {
            User user = new User();
            user.setPassword("123456");
            user.setId(Long.valueOf("123456789" + i));
            userList.add(user);
        }
        List<Thread> threadList = new ArrayList<>();
        for (User user : userList) {
            Thread thread = new Thread(new SingleTime(user, user.getId()%101,map));
            threadList.add(thread);
        }
        for (Thread thread : threadList) {
            thread.start();
        }
        long timeall=System.currentTimeMillis();
        boolean isalive=true;
        while(isalive){
            for(Thread thread : threadList){
                if(thread.isAlive()){
                    continue;
                }
            }
            isalive=false;
        }
        while(map.get("total").size()<990){

        }
        timeall=System.currentTimeMillis()-timeall;
        for(Entry<String,List<Long>> entry:map.entrySet()){
            long totaltime=0;
            long min=10000000,max=0;
            for(Long time:entry.getValue()){
                totaltime +=time;
                if(time>max&&time>100){
                    max=time;
                }
                if(time<min&&time>100){
                    min=time;
                }
            }
            System.out.println(entry.getKey()+": "+totaltime/entry.getValue().size()+"  "+min+"   "+max);
        }
        System.out.println(timeall);

    }
}
