package mc.alk.arena.controllers;

import mc.alk.arena.BattleArena;
import mc.alk.arena.Defaults;
import mc.alk.arena.util.Log;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import com.nijikokun.register.payment.Methods;

public class MoneyController implements Listener{
	static boolean initialized = false;
	static boolean hasVault = false;
	static boolean hasRegister = false;
	static boolean useVault = false;
	public static Economy economy = null;
	public static boolean hasEconomy(){
		return initialized;
	}
	public static boolean hasAccount(String name) {
		if (!initialized) return true;
		return useVault? economy.hasAccount(name) : Methods.getMethod().hasAccount(name);
	}
	public static boolean hasEnough(String name, double fee) {
		if (!initialized) return true;
		return hasEnough(name, (float) fee);
	}
	public static boolean hasEnough(String name, float amount) {
		if (!initialized) return true;
		return useVault? economy.getBalance(name) >= amount :Methods.getMethod().getAccount(name).hasEnough(amount); 
	}
	public static void subtract(String name, double fee) {
		if (!initialized) return;
		subtract(name,(float) fee);
	}
	public static void subtract(String name, float amount) {
		if (!initialized) return;
		if (useVault) economy.withdrawPlayer(name, amount); 
		else Methods.getMethod().getAccount(name).subtract(amount);	
	}
	public static void add(String name, float amount) {
		if (!initialized) return;
		if (useVault) economy.depositPlayer(name, amount) ;
		else Methods.getMethod().getAccount(name).add(amount);
	}
	public static Double balance(String name) {
		if (!initialized) return 0.0;
		return useVault ? economy.getBalance(name) : Methods.getMethod().getAccount(name).balance();
	}
	public static void add(String name, double amount) {
		if (!initialized) return;
		add(name,(float)amount);
	}

	public static void setup() {
		Bukkit.getServer().getPluginManager().registerEvents(new MoneyController(), BattleArena.getSelf());		
	}

	@EventHandler
    public void setup(PluginEnableEvent event) {
		MoneyController.checkRegisteredPlugins();
	}
	private static void checkRegisteredPlugins(){
		if (useVault) /// We are good to go already
			return;
		Plugin controller = Bukkit.getServer().getPluginManager().getPlugin("Register");
		if (controller != null) {
			hasRegister = true;
			initialized = true;
			if (!hasVault)
				useVault = false;
			Log.info(BattleArena.getPName() +" found economy plugin Register");
		}
    	if (MoneyController.economy == null){ /// We want to use vault if we can
    		controller = Bukkit.getServer().getPluginManager().getPlugin("Vault");
    		if (controller != null) {
    			RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().
    					getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
    			if (economyProvider==null || economyProvider.getProvider() == null){
    				MoneyController.economy = null;
    				Log.warn(BattleArena.getPName() +" found no economy plugin. Attempts to use money in arenas might result in errors.");
    				return;
    			}
    			MoneyController.economy = economyProvider.getProvider();
    			useVault = hasVault = true;
    			initialized = true;
    			Log.info(BattleArena.getPName() +" found economy plugin Vault. [Default]");
    			/// Certain economy plugins don't implement this method correctly due to a NPE (I'm looking at you BOSEconomy! -_-)
    			try{Defaults.MONEY_STR = MoneyController.economy.currencyNameSingular();} catch (Exception e){}
    		}
    	}

	}

}
