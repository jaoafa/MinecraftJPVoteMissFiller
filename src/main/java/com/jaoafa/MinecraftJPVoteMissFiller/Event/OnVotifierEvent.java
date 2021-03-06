package com.jaoafa.MinecraftJPVoteMissFiller.Event;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

import com.jaoafa.MinecraftJPVoteMissFiller.Main;
import com.jaoafa.MinecraftJPVoteMissFiller.MySQLDBManager;
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
		System.out.println(
				"onVotifierEvent[MinecraftJPVoteMissFiller]: " + vote.getUsername() + " " + vote.getAddress() + " "
						+ vote.getServiceName() + " " + vote.getTimeStamp());
		if (!vote.getAddress().equalsIgnoreCase("minecraft.jp")) {
			return;
		}
		String name = vote.getUsername();

		UUID uuid = null;
		try {
			MySQLDBManager sqlmanager = Main.getMySQLDBManager();
			Connection conn = sqlmanager.getConnection();
			PreparedStatement statement = conn
					.prepareStatement("SELECT * FROM login WHERE player = ? ORDER BY id DESC");
			statement.setString(1, name);
			ResultSet res = statement.executeQuery();
			if (res.next()) {
				uuid = UUID.fromString(res.getString("uuid"));
			}
		} catch (SQLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			return;
		}

		if (uuid == null) {
			plugin.getLogger().warning(name + "のプレイヤーデータがデータベースから取得できなかったため、投票処理が正常に行われませんでした。");
			return;
		}

		if (Bukkit.getPlayer(uuid) != null) {
			Player player = Bukkit.getPlayer(uuid);
			if (player == null) {
				plugin.getLogger().warning(name + "のBukkit.getPlayerがnullを返却したため、投票処理が正常に行われませんでした。");
				return;
			}
			name = player.getName();
			addVotedPlayer(name);
		} else if (Bukkit.getOfflinePlayer(uuid) != null) {
			OfflinePlayer offplayer = Bukkit.getOfflinePlayer(uuid);
			if (offplayer == null) {
				plugin.getLogger().warning(name + "のBukkit.getOfflinePlayerがnullを返却したため、投票処理が正常に行われませんでした。");
				return;
			}
			name = offplayer.getName();
			addVotedPlayer(name);
		} else {
			plugin.getLogger().warning(name + "のオフラインプレイヤーデータが取得できなかったため、投票処理が正常に行われませんでした。");
			return;
		}
	}

	boolean addVotedPlayer(String name) {
		List<String> votedplayers = Load();
		plugin.getLogger().info("プレイヤー「" + name + "」の投票を受信しました。");
		votedplayers.add(name);
		return Save(votedplayers);
	}

	static File file = null;

	/**
	 * 投票受信済み情報をロードしたりデータを保存したり初期設定をします。
	 * @return 初期設定を完了したかどうか
	 * @author mine_book000
	 */
	public static boolean first() {
		if (plugin == null) {
			plugin = Main.getInstance();
		}

		// 設定ファイルがなければ作成
		File file = new File(plugin.getDataFolder(), "voted_received.yml");
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}

			OnVotifierEvent.file = file;
			Save(new ArrayList<String>());
		} else {
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
	public static boolean Save(List<String> votedplayers) {
		FileConfiguration data = YamlConfiguration.loadConfiguration(file);
		data.set("VotedPlayersReceived", votedplayers);
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
	public static List<String> Load() {
		FileConfiguration data = YamlConfiguration.loadConfiguration(file);
		if (data.contains("VotedPlayersReceived")) {
			return data.getStringList("VotedPlayersReceived");
		} else {
			return null;
		}
	}
}
