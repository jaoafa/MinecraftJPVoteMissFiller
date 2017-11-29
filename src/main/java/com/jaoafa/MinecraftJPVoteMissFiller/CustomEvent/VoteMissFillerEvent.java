package com.jaoafa.MinecraftJPVoteMissFiller.CustomEvent;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class VoteMissFillerEvent extends Event {
    private String player;
    private static final HandlerList handlers = new HandlerList();

    public VoteMissFillerEvent(String player){
        this.player = player;
    }

    public String getStringPlayer(){
    	return player;
    }

	@Override
	public HandlerList getHandlers() {
		// TODO 自動生成されたメソッド・スタブ
		return handlers;
	}

}
