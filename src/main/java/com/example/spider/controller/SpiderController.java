package com.example.spider.controller;

import com.example.spider.business.Crack;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class SpiderController {

    @Value("${chrome.driver}")
    private String CHROME_DRIVER_PATH;

    @Value("${chrome.timeout}")
    private int timeout;

    @Value("${chrome.headless}")
    private boolean headless;

    @GetMapping("/ping")
    public String ping() {
        return "{\"driverpath\":\"" + CHROME_DRIVER_PATH + "\",\"timeout\":"
                + timeout + ",\"headless\":" + headless + "}";
    }

    @GetMapping("/query")
    public Map<String, String> search(@PathParam("ip") String ip) throws IOException, InterruptedException {
        long startTime = System.currentTimeMillis();
        Crack crack = new Crack(CHROME_DRIVER_PATH, timeout, headless);
        Map<String, String> result = crack.search(ip);
        crack.close();
        long endTime = System.currentTimeMillis();
        System.out.println(result);
        System.out.println("程序运行时间: " + (endTime - startTime) + "ms");
        return result;
    }

    @PostMapping("/search")
    public Map<String, Map<String, String>> v2(@RequestBody Map<String, List<String>> data) throws IOException, InterruptedException {
        Map<String, Map<String, String>> response = new HashMap<>();
        Crack crack = new Crack(CHROME_DRIVER_PATH, timeout, headless);
        for (String ip : data.get("ips")) {
            Map<String, String> result = crack.search(ip);
            response.put(ip, result);
        }
        crack.close();
        return response;
    }
}
