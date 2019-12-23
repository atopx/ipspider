package org.socmap.slide.controller;

import org.socmap.slide.business.SlideBusiness;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.server.PathParam;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class SlideController {

    @Value("${chrome.driver}")
    private String CHROME_DRIVER_PATH;

    @Value("${chrome.timeout}")
    private int timeout;

    @Value("${chrome.headless}")
    private boolean headless;

    private static Pattern ipv4Pattern = Pattern.compile("^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?).){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");

    @GetMapping("/ping")
    public Map<String, Object> ping() {
        Map<String, Object> response = new HashMap<>();
        response.put("driver", CHROME_DRIVER_PATH);
        response.put("timeout", timeout);
        response.put("headless", headless);
        return response;
    }

    @GetMapping("/query")
    public Map<String, String> search(@PathParam("ip") String ip) throws IOException, InterruptedException {
        Matcher mather = ipv4Pattern.matcher(ip);
        if (!mather.find()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Not a valid ipv4");
            return response;
        }
        long startTime = System.currentTimeMillis();
        SlideBusiness slide = new SlideBusiness(CHROME_DRIVER_PATH, timeout, headless);
        Map<String, String> result = slide.search(ip);
        slide.close();
        long endTime = System.currentTimeMillis();
        System.out.println(result);
        System.out.println("程序运行时间: " + (endTime - startTime) + "ms");
        return result;
    }

    @PostMapping("/search")
    public Map<String, Map<String, String>> v2(@RequestBody Map<String, List<String>> data) throws IOException, InterruptedException {

        long startTime = System.currentTimeMillis();
        Map<String, Map<String, String>> response = new HashMap<>();
        SlideBusiness slide = new SlideBusiness(CHROME_DRIVER_PATH, timeout, headless);
        for (String ip : data.get("ips")) {
            Matcher mather = ipv4Pattern.matcher(ip);
            if (mather.find()) {
                Map<String, String> result = slide.search(ip);
                response.put(ip, result);
            }
        }
        slide.close();
        long endTime = System.currentTimeMillis();
        System.out.println("程序运行时间: " + (endTime - startTime) + "ms");
        return response;
    }
}
