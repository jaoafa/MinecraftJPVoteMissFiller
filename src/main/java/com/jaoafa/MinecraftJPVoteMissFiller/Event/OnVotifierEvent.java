package com.jaoafa.MinecraftJPVoteMissFiller.Event;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

import com.jaoafa.MinecraftJPVoteMissFiller.MinecraftJPVoteMissFiller;
import com.jaoafa.MinecraftJPVoteMissFiller.MySQL;
import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;


public class OnVotifierEvent {
	JavaPlugin plugin;
	public OnVotifierEvent(JavaPlugin plugin) {
		this.plugin = plugin;
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
		}else if(Bukkit.getOfflinePlayer(uuid) != null){
			OfflinePlayer offplayer = Bukkit.getOfflinePlayer(uuid);
			if(offplayer == null){
				plugin.getLogger().warning(name + "のBukkit.getOfflinePlayerがnullを返却したため、投票処理が正常に行われませんでした。");
				return;
			}
			name = offplayer.getName();
		}else{
			plugin.getLogger().warning(name + "のオフラインプレイヤーデータが取得できなかったため、投票処理が正常に行われませんでした。");
			return;
		}
	}
}
