package caculator.bianfl.cn.abccaculator.trans;//package caculator.bianfl.cn.abccaculator.trans;
//
//import android.os.Handler;
//import android.os.Message;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.net.URLEncoder;
//
//import caculator.bianfl.cn.abccaculator.beans.Wordbean;
//
///**
// * Created by 福利 on 2017/2/22.
// */
//public class YoudaoTrans {
//    public static Wordbean trans(String translation) {
//        Wordbean wordbean = null;
//        int TIMEOUT = 10 * 1000;
//        StringBuilder sb = new StringBuilder("http://fanyi.youdao.com/openapi.do?"
//                + "keyfrom=wordTranslator&key=260643012&type=data&"
//                + "doctype=json&version=1.1");
//        try {
//            sb.appends("&q=").appends(URLEncoder.encode(translation, "utf-8"));
//            URL url = new URL(sb.toString());
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setConnectTimeout(TIMEOUT);
//            conn.setRequestMethod("GET");
//            int statusCode = conn.getResponseCode();
//            if (statusCode != HttpURLConnection.HTTP_OK) {
//                System.out.println("Http错误码：" + statusCode);
//            }
//            // 读取服务器的数据
//            InputStream is = conn.getInputStream();
//            BufferedReader br = new BufferedReader(new InputStreamReader(is, "utf-8"));
//            StringBuilder builder = new StringBuilder();
//            String line = null;
//            while ((line = br.readLine()) != null) {
//                builder.append(line);
//            }
//            br.close();
//            is.close();
//            conn.disconnect();
//            String text = builder.toString();
//            System.out.println(text);
//
//            wordbean = toWordBean(text);
////            System.out.println(wordbean.getTranslation());
////            System.out.println(wordbean.getBasicExplain());
////            System.out.println(wordbean.getErrorCode());
////            System.out.println(wordbean.getWebExplain());
////            System.out.println(wordbean.getPhonoGram());
////
////            System.out.println("验证相等");
////            System.out.println(wordbean.getBasicExplain() == null);
////            System.out.println(wordbean.getPhonoGram() == null);
//
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }finally {
//            return wordbean;
//        }
//    }
//
//    private static Wordbean toWordBean(String re) {
//        Wordbean wordbean = new Wordbean();
//        try {
//            JSONObject object = new JSONObject(re);
//            int errorCode = object.getInt("errorCode");
//            wordbean.setErrorCode(errorCode);
//
//            wordbean.setTranslation(object.getJSONArray("translation").getString(0));
//
//            if (re.contains("basic")) {
//                JSONObject object_basic = object.getJSONObject("basic");
//                JSONArray child_basic = object_basic.getJSONArray("explains");
//                String spG = object_basic.getString("phonetic");
//                StringBuffer text_basic = new StringBuffer();
//                for (int i = 0; i < child_basic.length(); i++) {
//                    text_basic.append(child_basic.getString(i));
//                    if (i != child_basic.length() - 1) {
//                        text_basic.append(",");
//                    }
//                }
//                wordbean.setBasicExplain(text_basic.toString());
//                wordbean.setPhonoGram(spG);
//            }
//
//
//            JSONArray array_web = object.getJSONArray("web");
//            StringBuilder webTemp = new StringBuilder();
//            for (int i = 0; i < array_web.length(); i++) {
//                JSONObject jo = array_web.getJSONObject(i);
//                JSONArray ja = jo.getJSONArray("value");
//                for (int j = 0; j < ja.length(); j++) {
//                    webTemp.append(ja.get(j));
//                    if (j != ja.length() - 1) {
//                        webTemp.append(",");
//                    }
//
//                }
//                webTemp.appends("·").appends(jo.get("key"));
//
//                if (i != array_web.length() - 1) {
//                    webTemp.append("\n");
//                }
//
//
//            }
//            wordbean.setWebExplain(webTemp.toString());
//
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        } finally {
//            return wordbean;
//        }
//    }
//}
