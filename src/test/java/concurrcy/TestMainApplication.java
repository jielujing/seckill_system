package concurrcy;

import com.seckill.model.entity.User;

import java.util.ArrayList;
import java.util.List;

public class TestMainApplication {
    public static void main(String[] args) {
        List<User> userList=new ArrayList<>();
        for(int i=1;i<=100;i++){
            User user=new User();
            user.setPassword("123456");
            user.setId(Long.valueOf("123456789"+i));
            userList.add(user);
        }
        List<Thread> threadList=new ArrayList<>();
        for(User user:userList){
            for(int i=1;i<=100;i++){
                Thread thread=new Thread(new SingleTime(user,Long.valueOf(i)));
                threadList.add(thread);
            }
        }
        for(Thread thread:threadList){
            thread.start();
        }

    }
}
