package com.wnhuang.chat.controller;

import com.wnhuang.chat.domain.request.ChatBaseRequest;
import com.wnhuang.chat.service.ChatModelService;
import com.wnhuang.common.domain.response.ApiResult;
import io.github.pigmesh.ai.deepseek.core.DeepSeekClient;
import io.github.pigmesh.ai.deepseek.core.Json;
import io.github.pigmesh.ai.deepseek.core.chat.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author wnhuang
 * @date 2025/2/27 15:17
 */
@RestController
@RequestMapping("/chat")
@Slf4j
public class ChatController {

    @Autowired
    private DeepSeekClient deepSeekClient;

    @Autowired
    private ChatModelService chatModelService;

    public final static HashMap<String, String> cache = new HashMap<>();

    private final String DEEPSEEK_MODEL = "deepseek/deepseek-r1/community";

    @GetMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ChatCompletionResponse> chat(String prompt) {
        return deepSeekClient.chatFluxCompletion(prompt);
    }

    @GetMapping(value = "/chat/advanced2", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ChatCompletionResponse> chatAdvanced(String prompt) {
        ChatCompletionRequest request = ChatCompletionRequest.builder()
                // 模型选择，支持 DEEPSEEK_CHAT、DEEPSEEK_REASONER 等
                .model(DEEPSEEK_MODEL)
                // 添加用户消息
                .addUserMessage(prompt)
                // 设置最大生成 token 数，默认 2048
                .maxTokens(1000)
                // 设置响应格式，支持 JSON 结构化输出
                .responseFormat(ResponseFormatType.JSON_OBJECT) // 可选
                // function calling
//        .tools(...) // 可选
                .build();

        return deepSeekClient.chatFluxCompletion(request);
    }

    @PostMapping(value = "/advanced", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ChatCompletionResponse> chatAdvanced(@RequestBody @Valid ChatBaseRequest chatBaseRequest) {
        log.info("cacheCode {}", chatBaseRequest);
        String cacheCode = chatBaseRequest.getGroupId();
        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(chatBaseRequest.getModel())
                .addUserMessage(chatBaseRequest.getPrompt())
                .addAssistantMessage(elt.apply(cache.getOrDefault(cacheCode, "")))
                .addSystemMessage("你是一个专业的助手")
                .maxCompletionTokens(2048)
                .build();
        log.info("request {}", Json.toJson(request));
        // 只保留上一次回答内容
        cache.remove(cacheCode);
        return deepSeekClient.chatFluxCompletion(request).doOnNext(i -> {
            String content = choicesProcess.apply(i.choices());
            // 其他ELT流程
            cache.merge(cacheCode, content, String::concat);
            if (Objects.nonNull(i.usage())) {
                // 在这里处理用量统计信息
                // 可以记录到日志或数据库中
                log.info("usage:{}", Json.toJson(i.usage()));
            }
        }).doOnComplete(() -> {
            log.info("chat complete!");
        }).doOnError(e -> log.error("/chat/advanced error:{}", e.getMessage()));
    }

    Function<List<ChatCompletionChoice>, String> choicesProcess = list -> list.stream().map(e -> e.delta().content())
            .collect(Collectors.joining());

    Function<String, String> elt = s -> s.replaceAll("<think>[\\s\\S]*?</think>", "").replaceAll("\n", "");


    @GetMapping("/model")
    public ApiResult getModel() {
        return ApiResult.success(chatModelService.list());
    }

}
