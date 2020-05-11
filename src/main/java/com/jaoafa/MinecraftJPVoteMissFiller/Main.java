package com.jaoafa.MinecraftJPVoteMissFiller;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.jaoafa.MinecraftJPVoteMissFiller.Event.OnVotifierEvent;

public class Main extends JavaPlugin {
	static MySQLDBManager sqlmanager;
	public static JavaPlugin instance = null;
	public static Main MinecraftJPVoteMissFiller = null;
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
	private void Import_Listener() {
		getServer().getPluginManager().registerEvents(new OnVotifierEvent(this), this);
	}

	private void loadConfig() {
		FileConfiguration conf = getConfig();
		if (!conf.contains("sqlserver")) {
			getLogger().warning("sqlserverが定義されていません。プラグインを無効化します。");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		if (!conf.contains("sqlport")) {
			getLogger().warning("sqlportが定義されていません。プラグインを無効化します。");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		if (!conf.contains("sqldatabase")) {
			getLogger().warning("sqldatabaseが定義されていません。プラグインを無効化します。");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		if (!conf.contains("sqlusername")) {
			getLogger().warning("sqlusernameが定義されていません。プラグインを無効化します。");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		if (!conf.contains("sqlpassword")) {
			getLogger().warning("sqlpasswordが定義されていません。プラグインを無効化します。");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		try {
			sqlmanager = new MySQLDBManager(
					conf.getString("sqlserver"),
					conf.getString("sqlport"),
					conf.getString("sqldatabase"),
					conf.getString("sqlusername"),
					conf.getString("sqlpassword"));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			getLogger().warning("データベースの接続に失敗しました。プラグインを無効化します。");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		getLogger().info("MySQL Connect successful.");

		if (conf.contains("discordtoken")) {
			discordtoken = conf.getString("discordtoken");
		} else {
			getLogger().info("Discordへの接続に失敗しました。 [conf NotFound]");
			getLogger().info("Disable MinecraftJPVoteMissFiller...");
			getServer().getPluginManager().disablePlugin(this);
		}
		if (conf.contains("serverchat_id")) {
			serverchat_id = (String) conf.get("serverchat_id");
		} else {
			serverchat_id = "250613942106193921"; // #server-chat
		}

		new MCJPChecker(this).runTaskTimerAsynchronously(this, 0L, 6000L);
	}

	/**
	 * プラグインが停止したときに呼び出し
	 * @author mine_book000
	 * @since 2017/11/25
	 */
	@Override
	public void onDisable() {

	}

	public static MySQLDBManager getMySQLDBManager() {
		return sqlmanager;
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
	public static void CommandReply(CommandSender sender, Command cmd, String text) {
		sender.sendMessage("[MinecraftJPVoteMissFiller] " + ChatColor.GREEN + text);
	}

	public static JavaPlugin getJavaPlugin() {
		return instance;
	}

	public static Main getInstance() {
		return MinecraftJPVoteMissFiller;
	}
}
