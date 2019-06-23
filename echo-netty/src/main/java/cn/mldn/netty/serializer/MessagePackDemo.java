package cn.mldn.netty.serializer;

import cn.mldn.vo.Member;
import com.alibaba.fastjson.JSON;
import org.json.simple.JSONArray;
import org.msgpack.MessagePack;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MessagePackDemo {
    public static void main(String[] args) throws IOException {
        List<Member> memberList = new ArrayList<Member>();
        for (int i = 0; i < 10; i++) {
            Member member = new Member();
            member.setMid("mid-" + i);
            member.setName("name-" + i);
            member.setAge(i);
            member.setSalary(1.2);
            memberList.add(member);
        }
        MessagePack messagePack = new MessagePack();
        messagePack.register(Member.class);
        byte[] write = messagePack.write(memberList);
        System.out.println(write.length);
        String jsonString = JSON.toJSONString(memberList);
        System.out.println(jsonString.getBytes().length);
//        new ObjectOutputStream(memberList);
    }
}
