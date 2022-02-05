package com.song.websocket_im.service;


import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.song.websocket_im.pojo.Message;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MessageService{


    /* *
     *
     * @param fromId
     * @param toId
     * @param page
     * @param rows
     * @return
     */
    List<Message> queryMessageList(Long fromId, Long toId, Integer page, Integer rows);


    /* *
     * 查询点对点聊天记录
     * @param fromId
     * @param toId
     * @param page
     * @param rows
     * @return
     */
    List<Message> findListByFromAndTo(Long fromId, Long toId, Integer page, Integer rows);

    /* *
     * 根据id查询数据
     * @param id
     * @return
     */
    Message findMessageById(String id);

    /* *
     * 更新消息状态
     * @param id
     * @param status
     * @return
     */
    UpdateResult updateMessageState(ObjectId id, Integer status);

    /* *
     * 新增消息
     * @param message
     * @return
     */
    Message saveMessage(Message message);

    /* *
     * 根据消息id删除数据
     * @param id
     * @return
     */
    DeleteResult deleteMessage(String id);

}
