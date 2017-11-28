package com.jaoafa.MinecraftJPVoteMissFiller;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class MCJPChecker extends BukkitRunnable{
	JavaPlugin plugin;
	public MCJPChecker(JavaPlugin plugin) {
		this.plugin = plugin;
	}
	@Override
	public void run() {
		String regex = "<li><a href=\"/players/(.+)\">(.+)</a></li>";
		Pattern p = Pattern.compile(regex);
		List<Matcher> data = getHttpsString("https://minecraft.jp/servers/jaoafa.com", p);


		for(Matcher m : data){
			String player = m.group(1);
			System.out.println(player);
		}
	}

	private static List<Matcher> getHttpsString(String address, Pattern p){
		List<Matcher> data = new ArrayList<Matcher>();
		StringBuilder builder = new StringBuilder();
		try{
			TrustManager[] tm = { new X509TrustManager() {
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
				@Override
				public void checkClientTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {
				}
				@Override
				public void checkServerTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {
				}
			} };
			SSLContext sslcontext = SSLContext.getInstance("SSL");
			sslcontext.init(null, tm, null);
			HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
				@Override
				public boolean verify(String hostname,
						SSLSession session) {
					return true;
				}
			});

			URL url = new URL(address);

			HttpsURLConnection  connect = (HttpsURLConnection)url.openConnection();
			connect.setRequestMethod("GET");
			connect.setSSLSocketFactory(sslcontext.getSocketFactory());
			connect.setRequestProperty("User-Agent","Mozilla/5.0 (Macintosh; U; Intel Mac OS X; ja-JP-mac; rv:1.8.1.6) Gecko/20070725 Firefox/2.0.0.6");

			connect.connect();

			if(connect.getResponseCode() != HttpURLConnection.HTTP_OK){
				InputStream in = connect.getErrorStream();

				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
				String line;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
				in.close();
				connect.disconnect();

				System.out.println("[MCJPMissFiller] URLGetConnected(Error): " + address);
				System.out.println("[MCJPMissFiller] Response: " + connect.getResponseMessage() + "\n" + builder.toString());

				return null;
			}

			InputStream in = connect.getInputStream();

			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String line;
			while ((line = reader.readLine()) != null) {
				Matcher m = p.matcher(line);
				if (m.find()){
					data.add(m);
				}

				//builder.append(line);
			}
			in.close();
			connect.disconnect();
			System.out.println("[MCJPMissFiller] URLGetConnected: " + address);
			//System.out.println("[MCJPMissFiller] Data: " + builder.toString());
			System.out.println("[MCJPMissFiller] DataCount: " + data.size() + "'s");

			return data;
		}catch(Exception e){
			System.out.println("[MCJPMissFiller] URLGetConnected(Error): " + address);
			e.printStackTrace();
			return null;
		}
	}
}
