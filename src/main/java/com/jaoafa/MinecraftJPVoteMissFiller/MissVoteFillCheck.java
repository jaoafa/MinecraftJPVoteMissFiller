package com.jaoafa.MinecraftJPVoteMissFiller;

import java.sql.SQLException;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.jaoafa.MinecraftJPVoteMissFiller.Discord.Discord;

public class MissVoteFillCheck extends BukkitRunnable {
	static JavaPlugin plugin;
	String player;
	int nowCount;
	public MissVoteFillCheck(JavaPlugin plugin, String player, int nowCount) {
		MCJPChecker.plugin = plugin;
		this.player = player;
		this.nowCount = nowCount;
	}

	@Override
	public void run() {
		@SuppressWarnings("deprecation")
		PlayerVoteData pvd = new PlayerVoteData(player);
		try {
			int filled = pvd.get();
			if(nowCount != filled){
				return; // 補填済み？
			}
			Discord.DiscordSend("499922840871632896", "<@221991565567066112> :warning:__**補填処理に失敗しました**__\nプレイヤー「" + player + "」の補填処理に失敗しました。\n補填前: " + nowCount + "回\n補填後: " + filled + "回");
		} catch (ClassNotFoundException | UnsupportedOperationException | NullPointerException
				| SQLException e) {
			e.printStackTrace();
		}
	}

}
