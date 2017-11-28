package com.jaoafa.MinecraftJPVoteMissFiller.Event;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.jaoafa.MinecraftJPVoteMissFiller.MinecraftJPVoteMissFiller;
import com.jaoafa.MinecraftJPVoteMissFiller.MySQL;
import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;


public class OnVotifierEvent implements Listener {
	static JavaPlugin plugin;
	public OnVotifierEvent(JavaPlugin plugin) {
		OnVotifierEvent.plugin = plugin;
	}
	@EventHandler
	public void onVotifierEvent(VotifierEvent event) {
		Vote vote = event.getVote();
		String name = vote.getUsername();

		Statement statement;
		try {
			statement = MinecraftJPVoteMissFiller.c.createStatement();
		} catch (NullPointerException e) {
			MySQL MySQL = new MySQL("jaoafa.com", "3306", "jaoafa", MinecraftJPVoteMissFiller.sqluser, MinecraftJPVoteMissFiller.sqlpassword);
			try {
				MinecraftJPVoteMissFiller.c = MySQL.openConnection();
				statement = MinecraftJPVoteMissFiller.c.createStatement();
			} catch (ClassNotFoundException | SQLException e1) {
				// TODO 自動生成された catch ブロック
				e1.printStackTrace();
				return;
			}
		} catch (SQLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			return;
		}

		statement = MySQL.check(statement);

		UUID uuid = null;
		try {
			ResultSet res = statement.executeQuery("SELECT * FROM log WHERE player = '" + name + "'");
			if(res.next()){
				uuid = UUID.fromString(res.getString("uuid"));
			}
		} catch (SQLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			return;
		}

		if(uuid == null){
			plugin.getLogger().warning(name + "のプレイヤーデータがデータベースから取得できなかったため、投票処理が正常に行われませんでした。");
			return;
		}

		if(Bukkit.getPlayer(uuid) != null){
			Player player = Bukkit.getPlayer(uuid);
			if(player == null){
				plugin.getLogger().warning(name + "のBukkit.getPlayerがnullを返却したため、投票処理が正常に行われませんでした。");
				return;
			}
			name = player.getName();
			addVotedPlayer(name);
		}else if(Bukkit.getOfflinePlayer(uuid) != null){
			OfflinePlayer offplayer = Bukkit.getOfflinePlayer(uuid);
			if(offplayer == null){
				plugin.getLogger().warning(name + "のBukkit.getOfflinePlayerがnullを返却したため、投票処理が正常に行われませんでした。");
				return;
			}
			name = offplayer.getName();
			addVotedPlayer(name);
		}else{
			plugin.getLogger().warning(name + "のオフラインプレイヤーデータが取得できなかったため、投票処理が正常に行われませんでした。");
			return;
		}
	}
	boolean addVotedPlayer(String name){
		List<String> votedplayers = Load();
		votedplayers.add(name);
		return Save(votedplayers);
	}

	static File file = null;
	/**
	 * 投票受信済み情報をロードしたりデータを保存したり初期設定をします。
	 * @param plugin プラグインのJavaPluginを指定
	 * @return 初期設定を完了したかどうか
	 * @author mine_book000
	 */
	public static boolean first(){
		// 設定ファイルがなければ作成
		File file = new File(plugin.getDataFolder(), "voted.yml");
		if(!file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}

			OnVotifierEvent.file = file;
			Save(new ArrayList<String>());
		}else{
			OnVotifierEvent.file = file;
		}
		return true;
	}

	/**
	 * 投票受信済み情報をセーブします。
	 * @param votedplayers 投票受信済み情報
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
	 * 投票受信済み情報をロードします。
	 * @return 投票受信済み情報
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
}
