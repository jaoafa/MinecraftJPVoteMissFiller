package com.jaoafa.MinecraftJPVoteMissFiller.Discord;

import org.bukkit.plugin.java.JavaPlugin;

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
	String token;
	public static IDiscordClient client = null;
	static IGuild guild = null;
	static IChannel channel = null;

	public Discord(JavaPlugin plugin, String token) {
		this.plugin = plugin;
		this.token = token;
	}

	public void start(){
		try {
			setClient(new ClientBuilder().withToken(token).build());
		} catch (DiscordException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			plugin.getLogger().info("Discordへの接続に失敗しました。(DiscordException Token)");
			plugin.getLogger().info("Disable MinecraftJPVoteMissFiller...");
			plugin.getServer().getPluginManager().disablePlugin(plugin);
			return;
		}
		plugin.getLogger().info("Discordへの接続に成功しました。");
		Discord4J.disableAudio();

		//リスナー
		client.getDispatcher().registerListener(this);

		try {
			client.login();
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
