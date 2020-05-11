package com.jaoafa.MinecraftJPVoteMissFiller;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.sql.SQLException;
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

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.jaoafa.MinecraftJPVoteMissFiller.Event.OnVotifierEvent;

public class MCJPChecker extends BukkitRunnable{
	static JavaPlugin plugin;
	public MCJPChecker(JavaPlugin plugin) {
		System.out.println("[MCJPCheck_Debug] RunTask Run Starting.");
		MCJPChecker.plugin = plugin;
	}
	@Override
	public void run() {
		if(plugin == null){
			plugin = Main.getInstance();
		}
		List<String> newdata = getHttpsStringToVotedPlayers("https://minecraft.jp/servers/jaoafa.com");
		if(newdata == null){
			return;
		}
		List<String> olddata = Load();

		List<String> newvoted = new ArrayList<String>();

		int nowkey = 0;
		boolean flag = false;
		for(String player : newdata){
			int i = olddata.indexOf(player);
			if(i != -1){
				if(flag){
					continue;
				}

				if(check(olddata, newdata, i + 1, nowkey + 1) && check(olddata, newdata, i + 2, nowkey + 2) && check(olddata, newdata, i + 3, nowkey + 3)){
					flag = true;
					continue;
				}else{
					newvoted.add(player);
				}
			}else{
				newvoted.add(player);
			}
			nowkey++;
		}

		//System.out.println("[MCJPCheck_Debug] LastCheckVotedPlayers: " + String.join(", ", olddata));
		//System.out.println("[MCJPCheck_Debug] NowCheckVotedPlayers: " + String.join(", ", newdata));
		System.out.println("[MCJPCheck_Debug] NewVoted: " + String.join(", ", newvoted));

		int i = 0;
		List<String> alreadyreceived = OnVotifierEvent.Load();
		for(String player : newvoted){
			if(alreadyreceived.contains(player)){
				System.out.println("[MCJPCheck_Debug] " + player + ": Vote Already Received!");
				alreadyreceived.remove(player);
			}else{
				System.out.println("[MCJPCheck_Debug] " + player + ": Vote Miss Received!");
				@SuppressWarnings("deprecation")
				PlayerVoteData pvd = new PlayerVoteData(player);
				int nowcount;
				try {
					nowcount = pvd.get();
				} catch (ClassNotFoundException | UnsupportedOperationException | NullPointerException
						| SQLException e) {
					e.printStackTrace();
					nowcount = -1;
				}
				new MissVoteFillerEventGoClass(plugin, player, nowcount).runTaskLaterAsynchronously(plugin, 20 + i);
				i++;
			}
		}
		OnVotifierEvent.Save(alreadyreceived);
		Save(newdata);
	}

	boolean check(List<String> olddata, List<String> newdata, int oldindex, int newindex){
		if(oldindex < 0 || oldindex >= olddata.size()){
			return false;
		}
		if(newindex < 0 || newindex >= newdata.size()){
			return false;
		}
		if(olddata.get(oldindex).equals(newdata.get(newindex))){
			return true;
		}else{
			return false;
		}
	}

	static File file = null;
	/**
	 * 投票情報をロードしたりデータを保存したり初期設定をします。
	 * @param plugin プラグインのJavaPluginを指定
	 * @return 初期設定を完了したかどうか
	 * @author mine_book000
	 */
	public static boolean first(){
		if(plugin == null){
			plugin = Main.getInstance();
		}

		// 設定ファイルがなければ作成
		File file = new File(plugin.getDataFolder(), "voted.yml");
		if(!file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}

			MCJPChecker.file = file;
			Save(new ArrayList<String>());
		}else{
			MCJPChecker.file = file;
		}
		return true;
	}

	/**
	 * 投票情報をセーブします。
	 * @param votedplayers 投票情報
	 * @return 完了したかどうか
	 * @author mine_book000
	 */
	public static boolean Save(List<String> votedplayers){
		FileConfiguration data = YamlConfiguration.loadConfiguration(file);
		data.set("VotedPlayers", votedplayers);
		try {
			data.save(file);
			return true;
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 投票済み情報をロードします。
	 * @return 投票済み情報
	 * @author mine_book000
	 */
	public static List<String> Load(){
		FileConfiguration data = YamlConfiguration.loadConfiguration(file);
		if(data.contains("VotedPlayers")){
			return data.getStringList("VotedPlayers");
		}else{
			return null;
		}
	}

	private static List<String> getHttpsStringToVotedPlayers(String address){
		String regex = "<li><a href=\"/players/(.+)\">(.+)</a></li>";
		Pattern p = Pattern.compile(regex);
		List<String> players = new ArrayList<String>();
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
				System.out.println("[MCJPMissFiller] Response: " + connect.getResponseCode() + " " + connect.getResponseMessage()/* + "\n" + builder.toString() */);

				return null;
			}

			InputStream in = connect.getInputStream();

			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String line;
			while ((line = reader.readLine()) != null) {
				Matcher m = p.matcher(line);
				if (m.find()){
					String player = m.group(1);
					players.add(player);
				}

				//builder.append(line);
			}
			in.close();
			connect.disconnect();
			//System.out.println("[MCJPMissFiller] URLGetConnected: " + address);
			//System.out.println("[MCJPMissFiller] Data: " + builder.toString());
			//System.out.println("[MCJPMissFiller] DataCount: " + players.size() + "'s");

			return players;
		}catch(Exception e){
			System.out.println("[MCJPMissFiller] URLGetConnected(Error): " + address);
			e.printStackTrace();
			return null;
		}
	}
}
