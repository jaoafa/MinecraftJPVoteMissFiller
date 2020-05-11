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
import org.json.simple.JSONObject;

import com.jaoafa.MinecraftJPVoteMissFiller.Main;

public class Discord {
	/**
	 * Discordへメッセージを送信します。(#server-chat)
	 * @param message 送信するメッセージ
	 * @return 送信できたかどうか
	 */
	public static boolean DiscordSend(String message){
		if(Main.discordtoken == null){
			throw new NullPointerException("DiscordSendが呼び出されましたが、discordtokenが登録されていませんでした。");
		}
		Map<String, String> headers = new HashMap<>();
		headers.put("Content-Type", "application/json");
		headers.put("Authorization", "Bot " + Main.discordtoken);
		headers.put("User-Agent", "DiscordBot (https://jaoafa.com, v0.0.1)");

		Map<String, String> contents = new HashMap<>();
		contents.put("content", message);
		return postHttpJsonByJson("https://discordapp.com/api/channels/" + Main.serverchat_id + "/messages", headers, contents);
	}

	/**
	 * Discordへチャンネルを指定してメッセージを送信します。
	 * @param channel 送信先のチャンネルID
	 * @param message 送信するメッセージ
	 * @return 送信できたかどうか
	 */
	public static boolean DiscordSend(String channel, String message){
		if(Main.discordtoken == null){
			throw new NullPointerException("DiscordSendが呼び出されましたが、discordtokenが登録されていませんでした。");
		}
		Map<String, String> headers = new HashMap<>();
		headers.put("Content-Type", "application/json");
		headers.put("Authorization", "Bot " + Main.discordtoken);
		headers.put("User-Agent", "DiscordBot (https://jaoafa.com, v0.0.1)");

		Map<String, String> contents = new HashMap<>();
		contents.put("content", message);
		return postHttpJsonByJson("https://discordapp.com/api/channels/" + channel + "/messages", headers, contents);
	}

	@SuppressWarnings("unchecked")
	private static boolean postHttpJsonByJson(String address, Map<String, String> headers, Map<String, String> contents){
		StringBuilder builder = new StringBuilder();
		try{
			URL url = new URL(address);

			HttpsURLConnection connect = (HttpsURLConnection)url.openConnection();
			connect.setRequestMethod("POST");

			if(headers != null){
				for(Map.Entry<String, String> header : headers.entrySet()){
					connect.setRequestProperty(header.getKey(), header.getValue());
				}
			}

			connect.setDoOutput(true);
			OutputStreamWriter out = new OutputStreamWriter(connect.getOutputStream());
			JSONObject paramobj = new JSONObject();
			for(Map.Entry<String, String> content : contents.entrySet()){
				paramobj.put(content.getKey(), content.getValue());
			}
			out.write(paramobj.toJSONString());
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
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
}
