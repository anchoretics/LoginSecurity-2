package com.lenis0012.bukkit.ls;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ThreadManager {
	private LoginSecurity plugin;
	private int msg = -1, ses = -1, to = -1;
	public Map<String, Integer> session = new HashMap<String, Integer>();
	public Map<String, Integer> timeout = new HashMap<String, Integer>();
	
	public ThreadManager(LoginSecurity plugin) {
		this.plugin = plugin;
	}
	
	public void startMsgTask() {
		msg = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			public void run() {
				for(Player player : Bukkit.getServer().getOnlinePlayers()) {
					String name = player.getName();
					if(plugin.AuthList.containsKey(name)) {
						boolean register = plugin.AuthList.get(name);
						if(register)
							player.sendMessage(ChatColor.RED+"Please register using /register <password>");
						else
							player.sendMessage(ChatColor.RED+"Please login using /login <password>");
					}
				}
			}
		}, 200L, 200L);
	}
	
	public void stopMsgTask() {
		if(msg > 0)
			plugin.getServer().getScheduler().cancelTask(msg);
		msg = -1;
	}
	
	public void startSessionTask() {
		ses = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			public void run() {
				for(String user : session.keySet()) {
					int current = session.get(user);
					if(current >= 1)
						current -= 1;
					else
						session.remove(user);
				}
				
			}
		}, 20, 20);
	}
	
	public void stopSessionTask() {
		if(ses >= 0)
			plugin.getServer().getScheduler().cancelTask(ses);
		ses = -1;
	}
	
	public void startTimeoutTask() {
		ses = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			public void run() {
				for(String user : timeout.keySet()) {
					int current = timeout.get(user);
					if(current >= 1)
						current -= 1;
					else {
						session.remove(user);
						Player player = Bukkit.getPlayer(user);
						if(player != null && player.isOnline())
							player.kickPlayer("Login timed out");
					}
				}
				
			}
		}, 20, 20);
	}
	
	public void stopTimeoutTask() {
		if(to >= 0)
			plugin.getServer().getScheduler().cancelTask(to);
		to = -1;
	}
}