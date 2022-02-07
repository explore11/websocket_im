package com.song.websocket_im.pojo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "message") // 指定表的名称
@Builder
public class Message {

    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;

    //消息
    private String msg;
    //消息状态，1-未读，2-已读
    @Indexed
    private Integer status;

    //发送时间
    @Field("send_date")
    @Indexed
    private Date sendDate;

    //已读时间
    @Field("read_date")
    private Date readDate;

    //发送人
    @Indexed
    private User from;

    //接收人
    @Indexed
    private User to;

}
