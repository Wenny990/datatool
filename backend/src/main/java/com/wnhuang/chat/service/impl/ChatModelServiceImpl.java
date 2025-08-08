package com.wnhuang.chat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wnhuang.chat.domain.entity.ChatModel;
import com.wnhuang.chat.service.ChatModelService;
import com.wnhuang.chat.mapper.ChatModelMapper;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【chat_model(ai模型)】的数据库操作Service实现
* @createDate 2025-03-01 01:40:07
*/
@Service
public class ChatModelServiceImpl extends ServiceImpl<ChatModelMapper, ChatModel>
    implements ChatModelService{

}




