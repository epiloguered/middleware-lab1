package cn.edu.xmu.chat.demos.web.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.MessageDigest;
import java.util.UUID;
import java.net.URLEncoder;

@Service
public class TranslateService {
    @Value("${baidu.translate.appid}")
    private String appId;

    @Value("${baidu.translate.secret}")
    private String secret;

    public String translate(String text, String sourceLang, String targetLang) throws Exception {
        // 生成随机salt
        String salt = UUID.randomUUID().toString();
        // 计算签名：appid + text + salt + secret 的 MD5 值
        String sign = md5(appId + text + salt + secret);

        // 构建API请求URL和参数
        String url = "https://fanyi-api.baidu.com/api/trans/vip/translate";
        String body = String.format("q=%s&from=%s&to=%s&appid=%s&salt=%s&sign=%s",
                URLEncoder.encode(text, "UTF-8"), sourceLang, targetLang, appId, salt, sign);

        // 发送HTTP POST请求
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            // 返回原始响应，实际应用中需解析JSON提取翻译结果
            return response.body();
        } else {
            throw new RuntimeException("翻译失败: " + response.body());
        }
    }

    // MD5加密方法
    private String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(input.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("MD5计算失败", e);
        }
    }
}