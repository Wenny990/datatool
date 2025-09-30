package com.wnhuang.api.utils;


import org.apache.commons.net.ftp.FTPClient;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.Base64;

/**
 * @author wnhuang
 * @Package com.wnhuang.api.utils
 * @date 2025/9/2 16:51
 */
public class FtpPdfUtil {

    public static String downloadPdf(String ftpUrl) throws Exception {
        URI uri = new URI(ftpUrl);

        String userInfo = uri.getUserInfo(); // username:password
        String username = "anonymous";
        String password = "";
        if (userInfo != null) {
            String[] parts = userInfo.split(":");
            username = parts[0];
            if (parts.length > 1) {
                password = parts[1];
            }
        }

        String host = uri.getHost();
        int port = (uri.getPort() == -1) ? 21 : uri.getPort();
        String filePath = uri.getPath();

        FTPClient ftpClient = new FTPClient();
        ftpClient.connect(host, port);
        ftpClient.login(username, password);
        ftpClient.enterLocalPassiveMode();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (InputStream inputStream = ftpClient.retrieveFileStream(filePath)) {
            if (inputStream == null) {
                throw new RuntimeException("无法获取文件: " + filePath);
            }
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
        ftpClient.completePendingCommand();
        ftpClient.logout();
        ftpClient.disconnect();

        // 转换为 Base64
        return Base64.getEncoder().encodeToString(outputStream.toByteArray());
    }
}

