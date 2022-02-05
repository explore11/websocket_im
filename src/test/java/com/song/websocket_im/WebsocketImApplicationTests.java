package com.song.websocket_im;

import com.song.websocket_im.pojo.Message;
import com.song.websocket_im.pojo.User;
import com.song.websocket_im.service.MessageService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = WebsocketImApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WebsocketImApplicationTests {

    @Autowired
    private MessageService messageService;

    @Test
    public void testSave1() {
        System.out.println(1);
    }

    @Test
    public void testSave() {
        Message message = Message.builder()
                .id(ObjectId.get())
                .msg("你好")
                .sendDate(new Date())
                .status(1)
                .from(new User(1001L, "zhangsan"))
                .to(new User(1002L, "lisi"))
                .build();
        this.messageService.saveMessage(message);

        message = Message.builder()
                .id(ObjectId.get())
                .msg("你也好")
                .sendDate(new Date())
                .status(1)
                .to(new User(1001L, "zhangsan"))
                .from(new User(1002L, "lisi"))
                .build();
        this.messageService.saveMessage(message);

        message = Message.builder()
                .id(ObjectId.get())
                .msg("我在学习开发IM")
                .sendDate(new Date())
                .status(1)
                .from(new User(1001L, "zhangsan"))
                .to(new User(1002L, "lisi"))
                .build();
        this.messageService.saveMessage(message);

        message = Message.builder()
                .id(ObjectId.get())
                .msg("那很好啊！")
                .sendDate(new Date())
                .status(1)
                .to(new User(1001L, "zhangsan"))
                .from(new User(1002L, "lisi")).build();
        this.messageService.saveMessage(message);
        System.out.println("ok");
    }

    @Test
    public void testQueryList() {
        List<Message> list = this.messageService.findListByFromAndTo(1001L, 1002L, 1,
                10);
        for (Message message : list) {
            System.out.println(message);
        }
    }
}
