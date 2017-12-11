package com.jaoafa.MinecraftJPVoteMissFiller.Discord;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;

import com.jaoafa.MinecraftJPVoteMissFiller.MinecraftJPVoteMissFiller;

import sx.blah.discord.Discord4J;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.impl.events.guild.GuildCreateEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

public class Discord {
	JavaPlugin plugin;
	static String token;
	public static IDiscordClient client = null;
	static IGuild guild = null;
	static IChannel channel = null;

	public Discord(JavaPlugin plugin, String token) {
		this.plugin = plugin;
		Discord.token = token;
	}

	public void start(){
		try {
			ClientBuilder builder = new ClientBuilder()
				.withToken(token)
				.registerListener(this);
			Discord4J.disableAudio();
			setClient(builder.login());
		} catch (DiscordException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			plugin.getLogger().info("Discordへの接続に失敗しました。(Login DiscordException)");
			plugin.getLogger().info("Disable MinecraftJPVoteMissFiller...");
			plugin.getServer().getPluginManager().disablePlugin(plugin);
			return;
		} catch (RateLimitException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			plugin.getLogger().info("Discordへの接続に失敗しました。(Login RateLimitException)");
			plugin.getLogger().info("Disable MinecraftJPVoteMissFiller...");
			plugin.getServer().getPluginManager().disablePlugin(plugin);
			return;
		}
	}

	public void end(){
		try {
			client.logout();
		} catch (DiscordException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			plugin.getLogger().info("Discordへの接続解除に失敗しました。");
		}
	}

	public static boolean send(String message){
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/json");
		headers.put("Authorization", "Bot " + token);
		headers.put("User-Agent", "DiscordBot (https://jaoafa.com, v0.0.1)");

		Map<String, String> contents = new HashMap<String, String>();
		contents.put("content", message);
		return postHttpJsonByJson("https://discordapp.com/api/channels/250613942106193921/messages", headers, contents);
	}


	@SuppressWarnings("unchecked")
	private static boolean postHttpJsonByJson(String address, Map<String, String> headers, Map<String, String> contents){
		StringBuilder builder = new StringBuilder();
		try{
			URL url = new URL(address);

			/*TrustManager[] tm = { new X509TrustManager() {
				@Override
				public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType)
						throws CertificateException {
					// TODO 自動生成されたメソッド・スタブ

				}
				@Override
				public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)
						throws CertificateException {
					// TODO 自動生成されたメソッド・スタブ

				}
				@Override
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					// TODO 自動生成されたメソッド・スタブ
					return null;
				}
			}};
			SSLContext sslcontext = SSLContext.getInstance("SSL");
			sslcontext.init(null, tm, null);
			HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
				@Override
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			});
			 */
			HttpsURLConnection connect = (HttpsURLConnection)url.openConnection();
			connect.setRequestMethod("POST");
			// connect.setSSLSocketFactory(sslcontext.getSocketFactory());
			if(headers != null){
				for(Map.Entry<String, String> header : headers.entrySet()){
					connect.setRequestProperty(header.getKey(), header.getValue());
				}
			}

			connect.setDoOutput(true);
			OutputStreamWriter out = new OutputStreamWriter(connect.getOutputStream());
			//List<String> list = new ArrayList<>();
			JSONObject paramobj = new JSONObject();
			for(Map.Entry<String, String> content : contents.entrySet()){
				//list.add(content.getKey() + "=" + content.getValue());
				paramobj.put(content.getKey(), content.getValue());
			}
			//String param = implode(list, "&");
			out.write(paramobj.toJSONString());
			//Bukkit.getLogger().info(paramobj.toJSONString());
			out.close();

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

				Bukkit.getLogger().warning("DiscordWARN: " + builder.toString());
				return false;
			}

			InputStream in = connect.getInputStream();

			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String line;
			while ((line = reader.readLine()) != null) {
				builder.append(line);
			}
			in.close();
			connect.disconnect();
			//JSONParser parser = new JSONParser();
			//Object obj = parser.parse(builder.toString());
			//JSONObject json = (JSONObject) obj;
			return true;
		}catch(Exception e){
			e.printStackTrace();
			//BugReport.report(e);
			return false;
		}
	}
	/*
	public static boolean send(String message){
		if(channel == null){
			return false;
		}
		try {
			channel.sendMessage(message);
		} catch (MissingPermissionsException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			MinecraftJPVoteMissFiller.getJavaPlugin().getLogger().info("Discordへのメッセージ送信に失敗しました。(MissingPermissionsException)");
			return false;
		} catch (DiscordException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			MinecraftJPVoteMissFiller.getJavaPlugin().getLogger().info("Discordへのメッセージ送信に失敗しました。(DiscordException)");
			return false;
		}catch (RateLimitException e){
			// Since I want to retry try to add it.
			e.printStackTrace();
			MinecraftJPVoteMissFiller.getJavaPlugin().getLogger().info("Discordへのメッセージ送信に失敗しました。(RateLimitException)");
			return false;
		}
		return true;
	}
	*/


	public static boolean send(IChannel channel, String message){
		if(channel == null){
			return false;
		}
		try {
			channel.sendMessage(message);
		} catch (MissingPermissionsException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			MinecraftJPVoteMissFiller.getJavaPlugin().getLogger().info("Discordへのメッセージ送信に失敗しました。(MissingPermissionsException)");
			return false;
		} catch (DiscordException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			MinecraftJPVoteMissFiller.getJavaPlugin().getLogger().info("Discordへのメッセージ送信に失敗しました。(DiscordException)");
			return false;
		}catch (RateLimitException e){
			// Since I want to retry try to add it.
			e.printStackTrace();
			MinecraftJPVoteMissFiller.getJavaPlugin().getLogger().info("Discordへのメッセージ送信に失敗しました。(RateLimitException)");
			return false;
		}
		return true;
	}

	public static boolean send(IChannel channel, String message, EmbedObject embed){
		if(channel == null){
			return false;
		}
		try {
			channel.sendMessage(message, embed, false);
		} catch (MissingPermissionsException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			MinecraftJPVoteMissFiller.getJavaPlugin().getLogger().info("Discordへのメッセージ送信に失敗しました。(MissingPermissionsException)");
			return false;
		} catch (DiscordException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			MinecraftJPVoteMissFiller.getJavaPlugin().getLogger().info("Discordへのメッセージ送信に失敗しました。(DiscordException)");
			return false;
		}catch (RateLimitException e){
			// Since I want to retry try to add it.
			e.printStackTrace();
			MinecraftJPVoteMissFiller.getJavaPlugin().getLogger().info("Discordへのメッセージ送信に失敗しました。(RateLimitException)");
			return false;
		}
		return true;
	}

	/*
	public static boolean send(String channelid_or_name, String message){
		IChannel channel = null;
		for (IChannel one : guild.getChannels()) {
			if(one.getLongID() != new Long(channelid_or_name)){
				continue;
			}
			channel = one;
        }
		if(channel == null){
			for (IChannel one : guild.getChannels()) {
				if(!one.getName().equalsIgnoreCase(channelid_or_name)){
					continue;
				}
				channel = one;
	        }
		}
		if(channel == null){
			MinecraftJPVoteMissFiller.getJavaPlugin().getLogger().info("Discordへのメッセージ送信に失敗しました。(指定されたチャンネルが見つかりません。)");
			return false;
		}
		send(channel, message);
		return true;
	}
	*/
	public static boolean send(String channelid, String message){
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/json");
		headers.put("Authorization", "Bot " + token);
		headers.put("User-Agent", "DiscordBot (https://jaoafa.com, v0.0.1)");

		Map<String, String> contents = new HashMap<String, String>();
		contents.put("content", message);
		return postHttpJsonByJson("https://discordapp.com/api/channels/" + channelid + "/messages", headers, contents);
	}

	public static boolean isChannel(String channelid_or_name){
		IChannel channel = null;
		for (IChannel one : guild.getChannels()) {
			if(one.getLongID() != new Long(channelid_or_name)){
				continue;
			}
			channel = one;
        }
		if(channel == null){
			for (IChannel one : guild.getChannels()) {
				if(!one.getName().equalsIgnoreCase(channelid_or_name)){
					continue;
				}
				channel = one;
	        }
		}
		if(channel == null){
			return false;
		}
		return true;
	}

	@EventSubscriber
    public void onGuildCreate(GuildCreateEvent event) {
		if (event.getGuild() == null) {
            return;
        }

		if(event.getGuild().getLongID() == new Long("189377932429492224")){
			plugin.getLogger().info("DiscordGuildを選択しました。" + event.getGuild().getName());
			setGuild(event.getGuild());
		}

		if(guild == null){
			plugin.getLogger().info("Discordサーバへの接続に失敗しました。(Guildが見つかりません。)");
		}

		for (IChannel channel : event.getGuild().getChannels()) {
			if(channel.getLongID() != new Long("250613942106193921")){
				continue;
			}
			if(channel.getGuild().getLongID() != Discord.guild.getLongID()){
				continue;
			}
			Discord.channel = channel;
        }
		if(channel == null){
			plugin.getLogger().info("Discordサーバへの接続に失敗しました。(Channelが見つかりません。)");
		}
		plugin.getLogger().info("Discordサーバへの接続に完了しました。ID: " + event.getClient().getOurUser().getName());
	}
	private void setClient(IDiscordClient client){
		Discord.client = client;
	}
	private void setGuild(IGuild guild){
		Discord.guild = guild;
	}
}
