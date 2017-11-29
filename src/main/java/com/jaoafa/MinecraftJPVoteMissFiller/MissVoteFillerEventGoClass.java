package com.jaoafa.MinecraftJPVoteMissFiller;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.jaoafa.MinecraftJPVoteMissFiller.CustomEvent.VoteMissFillerEvent;

public class MissVoteFillerEventGoClass extends BukkitRunnable{
	static JavaPlugin plugin;
	String player;
	public MissVoteFillerEventGoClass(JavaPlugin plugin, String player) {
		MCJPChecker.plugin = plugin;
		this.player = player;
	}

	@Override
	public void run() {
		// TODO 自動生成されたメソッド・スタブ
		VoteMissFillerEvent VoteMissFillerEvent = new VoteMissFillerEvent(player);
		Bukkit.getServer().getPluginManager().callEvent(VoteMissFillerEvent);
	}

}
