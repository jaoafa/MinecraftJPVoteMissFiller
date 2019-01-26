package com.jaoafa.MinecraftJPVoteMissFiller;

import java.sql.Connection;
import java.sql.SQLException;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.jaoafa.MinecraftJPVoteMissFiller.Event.OnVotifierEvent;

public class MinecraftJPVoteMissFiller extends JavaPlugin {
	public static Connection c = null;
	public static String sqluser;
	public static String sqlpassword;
	public static String sqlserver = "jaoafa.com";
	public static long ConnectionCreate = 0;
	public static JavaPlugin instance = null;
	public static MinecraftJPVoteMissFiller MinecraftJPVoteMissFiller = null;
	public static String discordtoken = null;
	public static String serverchat_id = null;

	/**
	 * プラグインが起動したときに呼び出し
	 * @author mine_book000
	 * @since 2017/11/25
	 */
	@Override
	public void onEnable() {
		// クレジット
		getLogger().info("(c) jao Minecraft Server MinecraftJPVoteMissFiller Project.");
		getLogger().info("Product by tomachi.");

		instance = this;
		MinecraftJPVoteMissFiller = this;

		// リスナーを設定
		Import_Listener();
		// コンフィグロード
		loadConfig();

		OnVotifierEvent.first();
		MCJPChecker.first();
	}

	/**
	 * リスナー設定
	 * @author mine_book000
	 */
	private void Import_Listener(){
		getServer().getPluginManager().registerEvents(new OnVotifierEvent(this), this);
	}

	private void loadConfig(){
		FileConfiguration conf = getConfig();
		if(conf.contains("sqluser") && conf.contains("sqlpassword")){
			sqluser = conf.getString("sqluser");
			sqlpassword = conf.getString("sqlpassword");
			if(conf.contains("sqlserver")){
				sqlserver = (String) conf.get("sqlserver");
			}
			MySQL_Enable();
		}else{
			getLogger().info("MySQL Connect err. [conf NotFound]");
			getLogger().info("Disable MinecraftJPVoteMissFiller...");
			getServer().getPluginManager().disablePlugin(this);
		}
		if(conf.contains("discordtoken")){
			discordtoken = conf.getString("discordtoken");
		}else{
			getLogger().info("Discordへの接続に失敗しました。 [conf NotFound]");
			getLogger().info("Disable MinecraftJPVoteMissFiller...");
			getServer().getPluginManager().disablePlugin(this);
		}
		if(conf.contains("serverchat_id")){
			serverchat_id = (String) conf.get("serverchat_id");
		}else{
			serverchat_id = "250613942106193921"; // #server-chat
		}

		new MCJPChecker(this).runTaskTimerAsynchronously(this, 0L, 6000L);
	}

	/**
	 * MySQLの初期設定
	 * @author mine_book000
	 */
	private void MySQL_Enable(){
		MySQL MySQL = new MySQL(sqlserver, "3306", "jaoafa", sqluser, sqlpassword);

		try {
			c = MySQL.openConnection();
		} catch (ClassNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			getLogger().info("MySQL Connect err. [ClassNotFoundException]");
			getLogger().info("Disable MinecraftJPVoteMissFiller...");
			getServer().getPluginManager().disablePlugin(this);
			return;
		} catch (SQLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			getLogger().info("MySQL Connect err. [SQLException: " + e.getSQLState() + "]");
			getLogger().info("Disable MinecraftJPVoteMissFiller...");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		getLogger().info("MySQL Connect successful.");
	}

	/**
	 * プラグインが停止したときに呼び出し
	 * @author mine_book000
	 * @since 2017/11/25
	 */
	@Override
	public void onDisable() {

	}


	/**
	 * 連携プラグイン確認
	 * @author mine_book000
	 */
/*
	private void Load_Plugin(String PluginName){
		if(getServer().getPluginManager().isPluginEnabled(PluginName)){
			getLogger().info("MinecraftJPVoteMissFiller Success(LOADED: " + PluginName + ")");
			getLogger().info("Using " + PluginName);
		}else{
			getLogger().warning("MinecraftJPVoteMissFiller ERR(NOTLOADED: " + PluginName + ")");
			getLogger().info("Disable MinecraftJPVoteMissFiller...");
			getServer().getPluginManager().disablePlugin(this);
			return;MinecraftJPVoteMissFiller
		}
	}
*/

	/**
	 * コマンドの実行に対してメッセージを返信します。
	 * @param sender 実行者のCommandSender
	 * @param cmd コマンド情報
	 * @param text 返信するテキスト
	 * @author mine_book000
	 */
	public static void CommandReply(CommandSender sender, Command cmd, String text){
		sender.sendMessage("[MinecraftJPVoteMissFiller] " + ChatColor.GREEN + text);
	}

	public static JavaPlugin getJavaPlugin(){
		return instance;
	}
	public static MinecraftJPVoteMissFiller getInstance(){
		return MinecraftJPVoteMissFiller;
	}
}
