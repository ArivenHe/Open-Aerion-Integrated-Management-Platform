package cn.ariven.openaimpbackend.service.impl;

import cn.ariven.openaimpbackend.dto.ResponseMessage;
import cn.ariven.openaimpbackend.entity.BotQQ;
import cn.ariven.openaimpbackend.repository.BotQQRepository;
import cn.ariven.openaimpbackend.service.INodeServerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NodeServerServiceImpl implements INodeServerService {

    @Value("${node.path}")
    private String nodePath;

    @Value("${node.qqBotscript}")
    private String scriptPath;

    private Process nodeProcess;
    private final StringBuilder logBuffer = new StringBuilder();
    private final RestTemplate restTemplate;
    private final BotQQRepository botQQRepository;

    @Override
    public synchronized ResponseMessage<String> startNodeServer() {
        if (nodeProcess != null && nodeProcess.isAlive()) {
            return ResponseMessage.error(HttpStatus.CONFLICT.value(), "⚠️ AerionBot 服务已经在运行");
        }
        try {
            File scriptFile = new File(scriptPath);
            if (!scriptFile.exists()) {
                return ResponseMessage.error(HttpStatus.NOT_FOUND.value(), "❌ 找不到脚本文件: " + scriptPath);
            }

            ProcessBuilder pb = new ProcessBuilder(nodePath, scriptPath);
            pb.redirectErrorStream(true);
            pb.directory(scriptFile.getParentFile());

            nodeProcess = pb.start();

            new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(nodeProcess.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String logLine = "[AerionBot] " + line + "\n";
                        synchronized (logBuffer) {
                            logBuffer.append(logLine);
                            if (logBuffer.length() > 1000000) {
                                logBuffer.delete(0, logBuffer.length() - 500000);
                            }
                        }
                        System.out.print(logLine);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            return ResponseMessage.success("✅ qqBot 服务已启动");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseMessage.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "❌ 启动失败: " + e.getMessage());
        }
    }

    @Override
    public synchronized ResponseMessage<String> stopNodeServer() {
        if (nodeProcess != null && nodeProcess.isAlive()) {
            nodeProcess.destroy();
            nodeProcess = null;
            return ResponseMessage.success("🛑 AerionBot 服务已停止");
        }
        return ResponseMessage.error(HttpStatus.NOT_FOUND.value(), "⚠️ AerionBot 服务未运行");
    }

    @Override
    public synchronized ResponseMessage<String> status() {
        if (nodeProcess != null && nodeProcess.isAlive()) {
            return ResponseMessage.success("✅ AerionBot 服务正在运行 (PID=" + nodeProcess.pid() + ")");
        }
        return ResponseMessage.error(HttpStatus.NOT_FOUND.value(), "❌ AerionBot 服务未运行");
    }

    @Override
    public ResponseMessage<String> getLogs(int lines) {
        synchronized (logBuffer) {
            String[] allLogs = logBuffer.toString().split("\n");
            int start = Math.max(0, allLogs.length - lines);
            StringBuilder result = new StringBuilder();
            for (int i = start; i < allLogs.length; i++) {
                result.append(allLogs[i]).append("\n");
            }
            return ResponseMessage.success(result.toString());
        }
    }

    @Override
    public ResponseMessage<Object> getList() {
        String url = "http://localhost:6070/Bot/List";
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            Map body = response.getBody();
            return ResponseMessage.success(body != null ? body.get("tbData") : null);
        } catch (Exception e) {
            log.error("Error getting bot list: {}", e.getMessage());
            return ResponseMessage.error("获取列表失败: " + e.getMessage());
        }
    }

    @Override
    public ResponseMessage<String> updateBotConfig(BotQQ qq) {
        botQQRepository.save(qq);
        return ResponseMessage.success("保存成功！");
    }
}
