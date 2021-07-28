package com.example.detector.request;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RequestUrl {
    private static final String TAG = "CLASS >>> RequestUrl";

    private List<String> shortList = new ArrayList<String>(Arrays.asList(
            "bit.ly", "t.co", "tinyurl.com", "url.sg", "zas.kr", "qops.xyz",
            "url.kr", "lrl.kr", "c11.kr", "kisu.me", "nazr.in", "na.to",
            "vot.kr", "vo.la", "han.gl", "muz.so", "gg.gg", "twr.kr",
            "di.do", "gtz.kr", "hoy.kr", "me2.do", "xzx.kr", "naver.me",
            "durl.kr", "durl.me", "goo.gl", "shorturl.at"));

    public int request(String content) {
        List<String> urlList = extractUrl(content);

        String[] domainPath = extractDomain(urlList.get(0));
        String domain = domainPath[0];
        String path = domainPath[1];

        String inputUrl = "";
        if (shortList.contains(domain)) {
            String expandUrl = expandUrl(domain, path);
            if (expandUrl.equals("ERROR")) {
                inputUrl = domain;
            } else {
                inputUrl = expandUrl;
            }
        } else {
            inputUrl = domain;
        }

        if(shortList.contains(inputUrl)) {
            return -1;
        }

        StringBuilder page = getPage(inputUrl);

        StringBuilder exPage = removeTags(page);

        int count = getCount(exPage, inputUrl);

        return count;
    }

    private List<String> extractUrl(String sentence) {
        List<String> containedUrls = new ArrayList<String>();
        String urlRegex = "((https:\\/\\/)|(http:\\/\\/))?(www\\.)?[a-zA-Z0-9./]+$";
        Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
        Matcher urlMatcher = pattern.matcher(sentence);
        while (urlMatcher.find()) {
            containedUrls.add(sentence.substring(urlMatcher.start(0), urlMatcher.end(0)));
        }
        return containedUrls;
    }

    private String[] extractDomain(String url) {
        String[] res = new String[2];

        String tmp1 = url.replace("http://", "").replace("https://", "").replace("www.", "");
        String[] tmp2 = tmp1.split("/");
        res[0] = tmp2[0];

        res[1] = "";

        for (String tmp3 : tmp2) {
            if (tmp3.equals(res[0])) {
                continue;
            }
            res[1] += "/" + tmp3;
        }
        return res;
    }

    private String expandUrl(String domain, String path) {
        String url = "";

        if (domain.startsWith("http")) {
            url += domain;
        } else {
            url += "https://" + domain;
        }

        url += path;

        String expandUrl = "";

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            int inputStream = connection.getInputStream().read();
            URL conUrl = connection.getURL();
            String tmpUrl = conUrl.getHost().replace("http://", "").replace("https://", "").replace("www.", "");;
            if(shortList.contains(tmpUrl)) {
                return tmpUrl;
            } else {
                expandUrl = String.valueOf(conUrl);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR";
        }

        String[] splitUrl1 = expandUrl.split("&");
        String[] splitUrl2 = splitUrl1[1].split("=");

        String expandDomain = "";

        if (splitUrl2[1].startsWith("http")) {
            String[] splitUrl3 = splitUrl2[1].split("%2F%2F");
            expandDomain = splitUrl3[1].split("%2F")[0];
        } else {
            expandDomain = splitUrl2[1].split("%2F")[0];
        }

        return expandDomain;
    }

    private StringBuilder getPage(String url) {
        String googleUrl = "https://www.google.com/search?q=" + url;
        StringBuilder page = new StringBuilder();
        String pageContents = "";

        try {
            URL queryUrl = new URL(googleUrl);

            System.out.println("[QUERY URL] : " + queryUrl);

            URLConnection connection = (URLConnection) queryUrl.openConnection();
            connection.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
            InputStreamReader reader = new InputStreamReader(connection.getInputStream(), "utf-8");

            BufferedReader buff = new BufferedReader(reader);

            while ((pageContents = buff.readLine()) != null) {
                page.append(pageContents);
                page.append("\r\n");
            }

            buff.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return page;
    }

    private StringBuilder removeTags(StringBuilder page) {
        StringBuilder exPage = new StringBuilder();

        Matcher matcher;

        Pattern pTags = Pattern.compile("<(\"[^\"]*\"|\'[^\']*\'|[^\'\">])*>");

        matcher = pTags.matcher(page);
        exPage.append(matcher.replaceAll(""));

        return exPage;
    }

    private int getCount(StringBuilder exPage, String inputUrl) {
        int count = 0;
        int pos = 0;
        int index = 0;

        while ((index = exPage.indexOf(inputUrl, pos)) != -1) {
            count++;
            pos = index + inputUrl.length();
        }

        return count;
    }
}
