package com.example.spider.business;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Crack {
    // 定义公共变量
    private static Pattern urlPattern = Pattern.compile("url\\(\"(.*)\"\\)");
    private static Pattern sizePattern = Pattern.compile("background-position: (.*)px (.*)px;");
    private static Pattern resultPattern = Pattern.compile("网络位置:(.*)\\s+运营商:(.*)");
    private static Pattern result2Pattern = Pattern.compile("网络位置:(.*)\\s+(.*)");
    private static Pattern result3Pattern = Pattern.compile("行为位置:(.*)\\s+运营商:(.*)");
    private static String[] geoKeys = new String[]{"country", "province", "city", "district"};
    public WebDriver chrome;
    public WebDriverWait wait;

    public Crack(String CHROME_DRIVER_PATH, int timeout, boolean headless) {
        // 初始化 Chrome
        System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_PATH);
        ChromeOptions options = new ChromeOptions();
        options.setPageLoadStrategy(PageLoadStrategy.NONE);
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("profile.default_content_settings.images", 2);
        options.setExperimentalOption("prefs", prefs);
        options.setHeadless(headless);
        options.addArguments("no-sandbox");
        options.addArguments("disable-dev-shm-usage");
        options.addArguments("incognito");
        options.addArguments("disable-gpu");
        options.addArguments("disable-infobars");
        options.addArguments("disable-extensions");
        options.addArguments("ignore-certificate-errors");
        chrome = new ChromeDriver(options);
        wait = new WebDriverWait(chrome, timeout);
    }

    public Map<String, String> search(String ip) throws IOException, InterruptedException {
        String url = "https://ip.rtbasia.com/?ipstr=" + ip;
        // open
        chrome.get(url);
        WebElement slider = wait.until(ExpectedConditions.presenceOfElementLocated(By.className("gt_slider_knob")));
        WebElement fullbg = wait.until(ExpectedConditions.presenceOfElementLocated(By.className("gt_cut_fullbg_slice")));
        WebElement bg = wait.until(ExpectedConditions.presenceOfElementLocated(By.className("gt_cut_bg_slice")));
        // 获取图片url
        String fullbgUrl = getImageUrl(fullbg);
        String bgUrl = getImageUrl(bg);
        // 获取碎片元素列表
        List<WebElement> fullbgList = chrome.findElements(By.className("gt_cut_fullbg_slice"));
        List<WebElement> bgList = chrome.findElements(By.className("gt_cut_bg_slice"));
        // 获取碎片元素位置信息
        List<Map<String, Integer>> fullbgLocation = getLocation(fullbgList);
        List<Map<String, Integer>> bgLocation = getLocation(bgList);
        // 读取图片
        BufferedImage fullbgImage = ImageIO.read(new URL(fullbgUrl));
        BufferedImage bgImage = ImageIO.read(new URL(bgUrl));
        // 按碎片位置进行切割和重组
        fullbgImage = getImage(fullbgImage, fullbgLocation);
        bgImage = getImage(bgImage, bgLocation);
        // 移动距离
        int distance = getGap(fullbgImage, bgImage);
        List<Integer> trace = getTrace(distance);
        // 拖动滑块
        move2Gap(slider, trace);

        return parse();
    }

    private int randint(int min, int max) {
        // 随机整数
        return min + (int) ((max + 1 - min) * Math.random());
    }

    private List<Integer> getTrace(int distance) {
        // 模拟移动算法
        List<Integer> trace = new ArrayList<>();
        int firstMove = (randint(6, 8)) * distance / 10;
        trace.add(firstMove);
        trace.add(distance - firstMove);
        trace.add(0);
        return trace;
    }

    private BufferedImage getImage(BufferedImage image, List<Map<String, Integer>> locations) {
        // 获取完整的图片
        int per_image_with = 10;
        int per_image_height = 58;
        List<BufferedImage> upperList = new ArrayList<>();
        List<BufferedImage> downList = new ArrayList<>();
        for (Map<String, Integer> location : locations) {
            int x = location.get("x");
            int y = location.get("y");
            if (y == -58) {
                upperList.add(image.getSubimage(Math.abs(x), 58, per_image_with, per_image_height));
            } else if (y == 0) {
                downList.add(image.getSubimage(Math.abs(x), 0, per_image_with, per_image_height));
            }
        }
        BufferedImage newImage = new BufferedImage(
                upperList.size() * per_image_with, image.getHeight(), image.getType());
        mergeImage(newImage, upperList, 0);
        mergeImage(newImage, downList, 58);
        return newImage;
    }

    private void mergeImage(BufferedImage image, List<BufferedImage> fragmentList, int n) {
        // 组合图片碎片
        int x_offset = 0;
        for (BufferedImage bufferedImage : fragmentList) {
            Graphics graphics = image.getGraphics();
            graphics.drawImage(bufferedImage, x_offset, n, null);
            x_offset += bufferedImage.getWidth();
        }
    }

    private List<Map<String, Integer>> getLocation(List<WebElement> elements) {
        // 获取碎片位置信息
        List<Map<String, Integer>> location = new ArrayList<>();
        for (WebElement item : elements) {
            Matcher matcher = sizePattern.matcher(item.getAttribute("Style"));
            if (matcher.find()) {
                Integer x = Integer.parseInt(matcher.group(1));
                Integer y = Integer.parseInt(matcher.group(2));
                Map<String, Integer> map = new HashMap<>();
                map.put("x", x);
                map.put("y", y);
                location.add(map);
            }
        }
        return location;
    }

    private String getImageUrl(WebElement image) {
        // 使用正则获取图片url
        String url = "";
        Matcher matcher = urlPattern.matcher(image.getAttribute("style"));
        if (matcher.find()) {
            url = matcher.group(1);
            url = url.replace(".webp", ".jpg");
        }
        return url;
    }

    private int getGap(BufferedImage image1, BufferedImage image2) {
        // 根据对比像素，计算缺口距离
        for (int x = 43; x < image1.getWidth(); x++) {
            for (int y = 0; y < image1.getHeight(); y++) {
                if (!contrastPixels(image1, image2, x, y)) {
                    return x - 7;
                }
            }
        }
        return 0;
    }

    private boolean contrastPixels(BufferedImage image1, BufferedImage image2, int xOffset, int yOffset) {
        // 对比像素
        Color pixel1 = new Color(image1.getRGB(xOffset, yOffset));
        Color pixel2 = new Color(image2.getRGB(xOffset, yOffset));
        return Math.abs(pixel1.getBlue() - pixel2.getBlue()) < 50 && Math.abs(pixel1.getGreen() -
                pixel2.getGreen()) < 50 && Math.abs(pixel1.getRed() - pixel2.getRed()) < 50;
    }

    private void move2Gap(WebElement slider, List<Integer> tracks) throws InterruptedException {
        // 模拟拖动滑块
        Actions actions = new Actions(chrome);
        actions.clickAndHold(slider).perform();
        for (Integer x : tracks) {
            actions.moveToElement(slider, x, 0).perform();
            TimeUnit.MILLISECONDS.sleep(randint(580, 610));
        }
        TimeUnit.MILLISECONDS.sleep(randint(200, 300));
        actions.release(slider).perform();
    }

    private Map<String, String> parse() {
        // 解析查询结果
        Map<String, String> result = new HashMap<>();
        result.put("country", "");
        result.put("province", "");
        result.put("city", "");
        result.put("district", "");
        result.put("org", "");
        result.put("ip_type", "未知网络");
        String ipText;
        try {
            ipText = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//h5/span"))).getText();
        } catch (TimeoutException e) {
            return result;
        }

        Matcher matcher;
        if (ipText.contains("局域网")) {
            result.put("ip_type", "局域网");
            return result;
        } else if (ipText.contains("行为位置")) {
            matcher = result3Pattern.matcher(ipText);
        } else if (ipText.contains("网络位置")) {
            if (ipText.contains("运营商")) {
                matcher = resultPattern.matcher(ipText);
            } else {
                matcher = result2Pattern.matcher(ipText);
            }
        } else {
            return result;
        }
        if (matcher.find()) {
            String[] geo = matcher.group(1).split("-");
            for (int i = 0; i < geo.length; i++) {
                result.put(geoKeys[i], geo[i]);
            }
            result.put("org", matcher.group(2));
        }
        try {
            String ipType = chrome.findElement(By.className("itype")).getText();
            result.put("ip_type", ipType);
        } catch (NoSuchElementException ignored) {
        }
        return result;
    }

    public void close() {
        // 关闭session
        chrome.close();
        chrome.quit();
    }
}