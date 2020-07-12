package com.ssundance.betman.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.DateUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.api.client.json.Json;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.ssundance.betman.model.Game;
import com.ssundance.betman.model.Schedule;
import com.ssundance.betman.model.Stat;

@Controller
public class MainController {
	@RequestMapping("/")
	public String stat(Model model) throws Exception {
	    Calendar to = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"));
	    String toStr = DateUtils.formatDate(to.getTime(), "yyyy-MM-dd");

	    int x = 0;
	    while (x < 20) {
	        if (to.get(Calendar.DAY_OF_WEEK) == 2) {
	            to.add(Calendar.DATE, -1);
	        }
	        
            to.add(Calendar.DATE, -1);
            x++;
	        
	        if (x == 10) {
	            break;
	        }
	    }
	    
	    String fromStr = DateUtils.formatDate(to.getTime(), "yyyy-MM-dd");
	    
	    model.addAttribute("fromStr", fromStr);
	    model.addAttribute("toStr", toStr);
	    
	    Map<String, Stat> teamMap = this.makeTeams();
	    
	    List<Game> gameList = this.getSchedule(toStr);
	    
	    this.getR27(teamMap, fromStr, toStr);
	    
	    this.getAwayReliefEra(teamMap, fromStr, toStr);
	    
	    this.getHomeReliefEra(teamMap, fromStr, toStr);
	    
	    this.getAwayStarter(teamMap, gameList);
	    
	    this.getHomeStarter(teamMap, gameList);
	    
	    for (int i = 0; i < gameList.size(); i++) {
	        Game game = gameList.get(i);
	        game.setHomeStat(teamMap.get(game.getHome()));
	        game.setAwayStat(teamMap.get(game.getAway()));
	    }

	    model.addAttribute("gameList", gameList);
        model.addAttribute("teamMap", teamMap);

	    return "main";
	}
	
    private void getHomeStarter(Map<String, Stat> teamMap, List<Game> gameList) throws Exception {
        for (int i = 0; i < gameList.size(); i++) {
            Game game = gameList.get(i);
            
            String home = game.getHome();
            if (StringUtils.equals(home, "기아")) {
                home = "KIA";
            }
            home = URLEncoder.encode(home, "UTF-8");
            String homeStarter = game.getHomeStarter();
            
            CloseableHttpClient httpClient = HttpClients.createDefault();
            String url = "http://www.statiz.co.kr/stat.php?mid=stat&re=1&ys=2020&ye=2020&se=0&te=" + home + "&tm=&ty=0&qu=auto&po=0&as=&ae=&hi=&un=&pl=&da=15&o1=ERAP&o2=OutCount&de=1&lr=0&tr=&cv=&ml=1&sn=30&pa=0&cn=&si=999&si_it=&si_wd=&si_tm=&si_ha=1&si_te=&si_st=&si_as=271&si_or=&si_ty=&si_pl=&si_in=&si_on=&si_um=&si_oc=&si_bs=&si_sc=&si_cnt=&si_aft=&si_li=";
            HttpGet httpGet = new HttpGet(url);
            httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.116 Safari/537.36");
            //httpGet.addHeader("Cookie", "JSESSIONID=6646FA65DE5DEB830F24AFF037B709B1.was01_01");
            //httpGet.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            httpGet.addHeader("Accept", "*/*");
            
            HttpResponse response = httpClient.execute(httpGet);
            String body = EntityUtils.toString(response.getEntity());
            Document doc = Jsoup.parse(body);
            
            //System.out.println(body);

            Elements list = doc.getElementsContainingOwnText(homeStarter);
            try {
                if (list != null && list.size() > 0) {
                    Element e = list.get(0);
                    Elements spans = e.parent().parent().getElementsByTag("span");
                    Stat stat = teamMap.get(game.getHome());
                    stat.setHomeStarterEra(spans.get(3).text());
                }
            } catch (Exception e) {
                
            }
        }
    }
    
    private void getAwayStarter(Map<String, Stat> teamMap, List<Game> gameList) throws Exception {
        for (int i = 0; i < gameList.size(); i++) {
            Game game = gameList.get(i);
            
            String away = game.getAway();
            if (StringUtils.equals(away, "기아")) {
                away = "KIA";
            }
            away = URLEncoder.encode(away, "UTF-8");
            String awayStarter = game.getAwayStarter();
            
            CloseableHttpClient httpClient = HttpClients.createDefault();
            String url = "http://www.statiz.co.kr/stat.php?mid=stat&re=1&ys=2020&ye=2020&se=0&te=" + away + "&tm=&ty=0&qu=auto&po=0&as=&ae=&hi=&un=&pl=&da=15&o1=ERAP&o2=OutCount&de=1&lr=0&tr=&cv=&ml=1&sn=30&pa=0&cn=&si=999&si_it=&si_wd=&si_tm=&si_ha=2&si_te=&si_st=&si_as=271&si_or=&si_ty=&si_pl=&si_in=&si_on=&si_um=&si_oc=&si_bs=&si_sc=&si_cnt=&si_aft=&si_li=";
            HttpGet httpGet = new HttpGet(url);
            httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.116 Safari/537.36");
            //httpGet.addHeader("Cookie", "JSESSIONID=6646FA65DE5DEB830F24AFF037B709B1.was01_01");
            //httpGet.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            httpGet.addHeader("Accept", "*/*");
            
            HttpResponse response = httpClient.execute(httpGet);
            String body = EntityUtils.toString(response.getEntity());
            Document doc = Jsoup.parse(body);
            
            Elements list = doc.getElementsContainingOwnText(awayStarter);
            try {
                if (list != null && list.size() > 0) {
                    Element e = list.get(0);
                    Elements spans = e.parent().parent().getElementsByTag("span");
                    Stat stat = teamMap.get(game.getAway());
                    stat.setAwayStarterEra(spans.get(3).text());
                }
            } catch (Exception e) {
                
            }
            
        }
    }

    private void getHomeReliefEra(Map<String, Stat> teamMap, String fromStr, String toStr) throws Exception {
        String[] date = StringUtils.split(fromStr, "-");
        String siDs = date[1] + "-" + date[2];
        
        CloseableHttpClient httpClient = HttpClients.createDefault();

        //si_ha=1 홈 / si_ha=2 원정
        String url = "http://www.statiz.co.kr/stat.php?mid=stat&re=1&ys=2020&ye=2020&se=0&te=&tm=&ty=0&qu=auto&po=0&as=&ae=&hi=&un=&pl=&da=15&o1=ERAP&o2=OutCount&de=1&lr=5&tr=&cv=&ml=1&sn=30&pa=0&cn=&si=999&si_it=1&si_ds=" + siDs + "&si_de=11-30&si_wd=&si_tm=&si_ha=1&si_te=&si_st=&si_as=272&si_or=&si_ty=&si_pl=&si_in=&si_on=&si_um=&si_oc=&si_bs=&si_sc=&si_cnt=&si_aft=&si_li=";
        HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.116 Safari/537.36");
        //httpGet.addHeader("Cookie", "JSESSIONID=6646FA65DE5DEB830F24AFF037B709B1.was01_01");
        //httpGet.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        httpGet.addHeader("Accept", "*/*");

        HttpResponse response = httpClient.execute(httpGet);
        String body = EntityUtils.toString(response.getEntity());
        Document doc = Jsoup.parse(body);
        
        Elements tables = doc.getElementsByAttributeValue("class", "table table-striped table-responsive  table-condensed no-space table-bordered");
        Element table = tables.get(0);
        Elements tds = table.getElementsByTag("td");
        int eraIndex = 0;
        
        for (int i = 0; i < tds.size(); i++) {
            Element td = tds.get(i);
            eraIndex = i + 2;
            
            if (StringUtils.equals(td.text(), "두산")) {
                teamMap.get("두산").setHomeReliefEra(tds.get(eraIndex).text());
            }
            
            if (StringUtils.equals(td.text(), "키움")) {
                teamMap.get("키움").setHomeReliefEra(tds.get(eraIndex).text());
            }
            
            if (StringUtils.equals(td.text(), "NC")) {
                teamMap.get("NC").setHomeReliefEra(tds.get(eraIndex).text());
            }
            
            if (StringUtils.equals(td.text(), "삼성")) {
                teamMap.get("삼성").setHomeReliefEra(tds.get(eraIndex).text());
            }
            
            if (StringUtils.equals(td.text(), "한화")) {
                teamMap.get("한화").setHomeReliefEra(tds.get(eraIndex).text());
            }
            
            if (StringUtils.equals(td.text(), "SK")) {
                teamMap.get("SK").setHomeReliefEra(tds.get(eraIndex).text());
            }
            
            if (StringUtils.equals(td.text(), "KT")) {
                teamMap.get("KT").setHomeReliefEra(tds.get(eraIndex).text());
            }
            
            if (StringUtils.equals(td.text(), "LG")) {
                teamMap.get("LG").setHomeReliefEra(tds.get(eraIndex).text());
            }
            
            if (StringUtils.equals(td.text(), "롯데")) {
                teamMap.get("롯데").setHomeReliefEra(tds.get(eraIndex).text());
            }
            
            if (StringUtils.equals(td.text(), "KIA")) {
                teamMap.get("기아").setHomeReliefEra(tds.get(eraIndex).text());
            }
        }
    }
	
    private void getAwayReliefEra(Map<String, Stat> teamMap, String fromStr, String toStr) throws Exception {
        String[] date = StringUtils.split(fromStr, "-");
        String siDs = date[1] + "-" + date[2];
        
        //http client 생성
        CloseableHttpClient httpClient = HttpClients.createDefault();

        String url = "http://www.statiz.co.kr/stat.php?mid=stat&re=1&ys=2020&ye=2020&se=0&te=&tm=&ty=0&qu=auto&po=0&as=&ae=&hi=&un=&pl=&da=15&o1=ERAP&o2=OutCount&de=1&lr=5&tr=&cv=&ml=1&sn=30&pa=0&cn=&si=999&si_it=1&si_ds=" + siDs + "&si_de=11-30&si_wd=&si_tm=&si_ha=2&si_te=&si_st=&si_as=272&si_or=&si_ty=&si_pl=&si_in=&si_on=&si_um=&si_oc=&si_bs=&si_sc=&si_cnt=&si_aft=&si_li=";
        HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.116 Safari/537.36");
        //httpGet.addHeader("Cookie", "JSESSIONID=6646FA65DE5DEB830F24AFF037B709B1.was01_01");
        //httpGet.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        httpGet.addHeader("Accept", "*/*");

        HttpResponse response = httpClient.execute(httpGet);
        String body = EntityUtils.toString(response.getEntity());
        Document doc = Jsoup.parse(body);
        
        Elements tables = doc.getElementsByAttributeValue("class", "table table-striped table-responsive  table-condensed no-space table-bordered");
        Element table = tables.get(0);
        Elements tds = table.getElementsByTag("td");
        int eraIndex = 0;
        
        for (int i = 0; i < tds.size(); i++) {
            Element td = tds.get(i);
            eraIndex = i + 2;
            
            if (StringUtils.equals(td.text(), "두산")) {
                teamMap.get("두산").setAwayReliefEra(tds.get(eraIndex).text());
            }
            
            if (StringUtils.equals(td.text(), "키움")) {
                teamMap.get("키움").setAwayReliefEra(tds.get(eraIndex).text());
            }
            
            if (StringUtils.equals(td.text(), "NC")) {
                teamMap.get("NC").setAwayReliefEra(tds.get(eraIndex).text());
            }
            
            if (StringUtils.equals(td.text(), "삼성")) {
                teamMap.get("삼성").setAwayReliefEra(tds.get(eraIndex).text());
            }
            
            if (StringUtils.equals(td.text(), "한화")) {
                teamMap.get("한화").setAwayReliefEra(tds.get(eraIndex).text());
            }
            
            if (StringUtils.equals(td.text(), "SK")) {
                teamMap.get("SK").setAwayReliefEra(tds.get(eraIndex).text());
            }
            
            if (StringUtils.equals(td.text(), "KT")) {
                teamMap.get("KT").setAwayReliefEra(tds.get(eraIndex).text());
            }
            
            if (StringUtils.equals(td.text(), "LG")) {
                teamMap.get("LG").setAwayReliefEra(tds.get(eraIndex).text());
            }
            
            if (StringUtils.equals(td.text(), "롯데")) {
                teamMap.get("롯데").setAwayReliefEra(tds.get(eraIndex).text());
            }
            
            if (StringUtils.equals(td.text(), "KIA")) {
                teamMap.get("기아").setAwayReliefEra(tds.get(eraIndex).text());
            }
        }
	}
	
    private void getR27(Map<String, Stat> teamMap, String fromStr, String toStr) throws Exception {
        //http client 생성
        CloseableHttpClient httpClient = HttpClients.createDefault();

        String url = "http://www.kbreport.com/teams/advancedList/ajax";
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.116 Safari/537.36");
        //httpGet.addHeader("Cookie", "JSESSIONID=6646FA65DE5DEB830F24AFF037B709B1.was01_01");
        httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        httpPost.addHeader("Accept", "*/*");
        
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(new BasicNameValuePair("rows", "20"));
        nameValuePairs.add(new BasicNameValuePair("order", "TPCT"));
        nameValuePairs.add(new BasicNameValuePair("orderType", "DESC"));
        nameValuePairs.add(new BasicNameValuePair("teamId", null));
        nameValuePairs.add(new BasicNameValuePair("defense_no", null));
        nameValuePairs.add(new BasicNameValuePair("year_from", "2020"));
        nameValuePairs.add(new BasicNameValuePair("year_to", "2020"));
        nameValuePairs.add(new BasicNameValuePair("split01", "day"));
        nameValuePairs.add(new BasicNameValuePair("split02_1", fromStr));
        nameValuePairs.add(new BasicNameValuePair("split02_2", toStr));
        nameValuePairs.add(new BasicNameValuePair("page", "1"));
        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, Consts.UTF_8));

        HttpResponse response = httpClient.execute(httpPost);
        String body = EntityUtils.toString(response.getEntity());
        String html = "<div class=\"page-row-box\">" + StringUtils.splitByWholeSeparator(body, "<div class=\"page-row-box\">")[1];

        Document doc = Jsoup.parse(html);
        Elements tds = doc.getElementsByTag("td");
        int r27Index = 0;
        
        for (int i = 0; i < tds.size(); i++) {
            Element td = tds.get(i);
            r27Index = i + 12;
            
            if (StringUtils.equals(td.text(), "두산")) {
                teamMap.get("두산").setR27(tds.get(r27Index).text());
            }
            
            if (StringUtils.equals(td.text(), "Hero")) {
                teamMap.get("키움").setR27(tds.get(r27Index).text());
            }
            
            if (StringUtils.equals(td.text(), "NC")) {
                teamMap.get("NC").setR27(tds.get(r27Index).text());
            }
            
            if (StringUtils.equals(td.text(), "삼성")) {
                teamMap.get("삼성").setR27(tds.get(r27Index).text());
            }
            
            if (StringUtils.equals(td.text(), "한화")) {
                teamMap.get("한화").setR27(tds.get(r27Index).text());
            }
            
            if (StringUtils.equals(td.text(), "SK")) {
                teamMap.get("SK").setR27(tds.get(r27Index).text());
            }
            
            if (StringUtils.equals(td.text(), "KT")) {
                teamMap.get("KT").setR27(tds.get(r27Index).text());
            }
            
            if (StringUtils.equals(td.text(), "LG")) {
                teamMap.get("LG").setR27(tds.get(r27Index).text());
            }
            
            if (StringUtils.equals(td.text(), "롯데")) {
                teamMap.get("롯데").setR27(tds.get(r27Index).text());
            }
            
            if (StringUtils.equals(td.text(), "KIA")) {
                teamMap.get("기아").setR27(tds.get(r27Index).text());
            }
        }
	}
    
    private List<Game> getSchedule(String toStr) throws Exception {
        List<Game> gameList = new ArrayList<>();
        
        //http client 생성
        CloseableHttpClient httpClient = HttpClients.createDefault();

        String url = "https://www.koreabaseball.com/ws/Main.asmx/GetKboGameList";
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.116 Safari/537.36");
        //httpGet.addHeader("Cookie", "JSESSIONID=6646FA65DE5DEB830F24AFF037B709B1.was01_01");
        
        //httpPost.addHeader("Cookie", "ASPSESSIONIDQQCBACCB=CLMK111HIJAPGEBOELMEKHOFEAM");
        httpPost.addHeader("Accept", "*/*");
        httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(new BasicNameValuePair("leId", "1"));
        nameValuePairs.add(new BasicNameValuePair("srId", "0,1,3,4,5,7,8,9"));
        nameValuePairs.add(new BasicNameValuePair("date", StringUtils.remove(toStr, "-")));
        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, Consts.UTF_8));

        HttpResponse response = httpClient.execute(httpPost);
        String body = EntityUtils.toString(response.getEntity());
        
        //System.out.println(body);
        
        JsonElement json = JsonParser.parseString(body);
        JsonArray jArray = json.getAsJsonObject().get("game").getAsJsonArray();
        
        for (int i = 0; i < jArray.size(); i++) {
            JsonElement j = jArray.get(i);
            
            String away = j.getAsJsonObject().get("AWAY_NM").getAsString();
            String home = j.getAsJsonObject().get("HOME_NM").getAsString();
            
            Game game = new Game();
            if (StringUtils.equals(away, "KIA")) {
                game.setAway("기아");
            } else {
                game.setAway(away);
            }
            game.setAwayStarter(StringUtils.trim(j.getAsJsonObject().get("T_PIT_P_NM").getAsString()));
            
            if (StringUtils.equals(home, "KIA")) {
                game.setHome("기아");
            } else {
                game.setHome(home);
            }
            game.setHomeStarter(StringUtils.trim(j.getAsJsonObject().get("B_PIT_P_NM").getAsString()));
            
            gameList.add(game);
        }
        
        return gameList;
    }
    
    private Map<String, Stat> makeTeams() {
        Map<String, Stat> teamMap = new HashMap<>();
        teamMap.put("두산", new Stat("두산"));
        teamMap.put("키움", new Stat("키움"));
        teamMap.put("NC", new Stat("NC"));
        teamMap.put("삼성", new Stat("삼성"));
        teamMap.put("한화", new Stat("한화"));
        teamMap.put("SK", new Stat("SK"));
        teamMap.put("KT", new Stat("KT"));
        teamMap.put("LG", new Stat("LG"));
        teamMap.put("롯데", new Stat("롯데"));
        teamMap.put("기아", new Stat("기아"));
        
        
        /*
         * List<Stat> list = new ArrayList<>(); list.add(new Stat("두산")); list.add(new
         * Stat("키움")); list.add(new Stat("NC")); list.add(new Stat("삼성")); list.add(new
         * Stat("한화")); list.add(new Stat("SK")); list.add(new Stat("KT")); list.add(new
         * Stat("LG")); list.add(new Stat("롯데")); list.add(new Stat("기아"));
         */
        
        return teamMap;
    }
	
	private void sample() throws Exception {
	  //http client 생성
        CloseableHttpClient httpClient = HttpClients.createDefault();

        //get 메서드와 URL 설정
        String url = "http://www.kbreport.com/teams/advancedList/ajax";
        //url = "http://www.kbreport.com";
        //HttpGet httpGet = new HttpGet(url);
        HttpPost httpPost = new HttpPost(url);
        
        //agent 정보 설정
        
        httpPost.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.116 Safari/537.36");
        //httpGet.addHeader("Cookie", "JSESSIONID=6646FA65DE5DEB830F24AFF037B709B1.was01_01");
        httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        httpPost.addHeader("Accept", "*/*");
        
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(new BasicNameValuePair("rows", "20"));
        nameValuePairs.add(new BasicNameValuePair("order", "TPCT"));
        nameValuePairs.add(new BasicNameValuePair("orderType", "DESC"));
        nameValuePairs.add(new BasicNameValuePair("teamId", null));
        nameValuePairs.add(new BasicNameValuePair("defense_no", null));
        nameValuePairs.add(new BasicNameValuePair("year_from", "2020"));
        nameValuePairs.add(new BasicNameValuePair("year_to", "2020"));
        nameValuePairs.add(new BasicNameValuePair("split01", "day"));
        nameValuePairs.add(new BasicNameValuePair("split02_1", "2020-06-01"));
        nameValuePairs.add(new BasicNameValuePair("split02_2", "2020-07-01"));
        nameValuePairs.add(new BasicNameValuePair("page", "1"));
        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, Consts.UTF_8));

        //HttpClient httpClient = HttpClientBuilder.create().build();
        HttpResponse response = httpClient.execute(httpPost);
        String body = EntityUtils.toString(response.getEntity());
	}
}
