package me.mjkaufer.BlockStock;

import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.ChatColor;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
//import org.bukkit.material.Dye;
//import org.bukkit.material.Sandstone;
//import org.bukkit.DyeColor;
//import org.bukkit.SandstoneType;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class BlockStock extends JavaPlugin{

	public final Logger logger = Logger.getLogger("Minecraft");
	public HashMap<Material, MaterialMap> materials = new HashMap<Material, MaterialMap>();
	
	@Override
    public void onDisable()
    {
    	PluginDescriptionFile p = this.getDescription();
    	this.logger.info(p.getName() + " V" + p.getVersion() + " has been disabled.");           		
    }
   
    @Override
    public void onEnable()
    {
    		//this.saveDefaultConfig();  
    		PluginDescriptionFile p = this.getDescription();
    		this.logger.info(p.getName() + " V" + p.getVersion() + " has been enabled.");
    		if(materials.size() == 0)
    			populateMaterials();
    }
    
    public void populateMaterials(){
    	materials.put(Material.DIAMOND, new MaterialMap(Material.DIAMOND_BLOCK, 9));
    	materials.put(Material.COAL, new MaterialMap(Material.COAL_BLOCK, 9));
    	materials.put(Material.REDSTONE, new MaterialMap(Material.REDSTONE_BLOCK, 9));
    	materials.put(Material.IRON_INGOT, new MaterialMap(Material.IRON_BLOCK, 9));
    	materials.put(Material.GOLD_INGOT, new MaterialMap(Material.GOLD_BLOCK, 9));
    	materials.put(Material.STONE, new MaterialMap(Material.SMOOTH_BRICK, 4));
    	materials.put(Material.CLAY_BRICK,new MaterialMap( Material.BRICK, 4));
    	materials.put(Material.CLAY_BALL, new MaterialMap(Material.CLAY, 4));
    	materials.put(Material.SNOW_BALL, new MaterialMap(Material.SNOW_BLOCK, 4));
    	materials.put(Material.NETHER_BRICK_ITEM, new MaterialMap(Material.NETHER_BRICK, 9));
    	materials.put(Material.SAND, new MaterialMap(Material.SANDSTONE, 4));
    	materials.put(Material.GLOWSTONE_DUST, new MaterialMap(Material.GLOWSTONE, 4));
    	materials.put(Material.QUARTZ, new MaterialMap(Material.QUARTZ_BLOCK, 4));
    	materials.put(Material.WHEAT, new MaterialMap(Material.HAY_BLOCK, 9));
    	
//    	Sandstone smoothSandstone = new Sandstone();
//    	smoothSandstone.setType(SandstoneType.SMOOTH);
//    	materials.put(Material.SANDSTONE, new MaterialMap(smoothSandstone.getItemType(), 4, 4));//probably going to default to normal sandstone, which will be a problem
//    	
//    	Dye lapis = new Dye();
//    	lapis.setColor(DyeColor.BLUE);
//    	materials.put(lapis.getItemType(), new MaterialMap(Material.LAPIS_BLOCK, 9));//buggy because of weird id shit
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
    	
		if(materials.size() == 0)
			populateMaterials();
		
        Player player = (Player)sender;
		PluginDescriptionFile p = this.getDescription();

		if(commandLabel.equalsIgnoreCase("blockstock") || commandLabel.equalsIgnoreCase("bs"))
        {
        	if(args.length > 0){
        		if(args[0].equalsIgnoreCase("about")){
                    player.sendMessage(ChatColor.AQUA + p.getName() + ChatColor.GREEN + " V" + p.getVersion() + ChatColor.AQUA + " , by " + ChatColor.RED + "mjkaufer");
                    return true;
        		}
        		else if(args[0].equalsIgnoreCase("?") || args[0].equalsIgnoreCase("help"))
        			return false;//will print help stuff
        	}
        	
        	if(player.hasPermission("BlockStock.block") || player.hasPermission("BlockStock.*")){
    			MaterialMap data;
    			Material before = null;
    			int num = -1;
        		if(args.length == 0){
        			ItemStack inHand = player.getItemInHand();
        			if(inHand.getType().toString().equalsIgnoreCase("AIR")){
    					player.sendMessage(ChatColor.RED + "Error: You can't make a block out of nothing!");
        				return true;
        			}
        			Material mat = inHand.getType();
        			data = findMat(mat);
        			before = mat;
        			System.out.println("MAT: " + mat.toString());
        			System.out.println(data);
        		}
        		else if (args.length >= 1){
        			Material mat = Material.matchMaterial(args[0]);
        			data = findMat(mat);
        			before = mat;
        			if(args.length == 2){
        				num = Integer.parseInt(args[1]);
        				if(num <= 0){
        					player.sendMessage(ChatColor.RED + "Error: Amount of blocks has to be more than zero!");
        					return true;
        				}
        			}
        			System.out.println("MAT: " + mat.toString());
        		}
        		else{
        			data = null;
        		}
        		if(data == null){
        			player.sendMessage(ChatColor.RED + "Error: That material doesn't exist/can't be BlockStock'd!");
        			return true;
        		}
        		
        		HashMap<Integer, ? extends ItemStack> match = player.getInventory().all(before);//all of the thing to be block'd
        		int total = 0;
        		for(Integer key : match.keySet()){
        			ItemStack stack = (ItemStack)match.get(key);
        			total+= stack.getAmount();
        		}
        		
        		if(total < data.getAmount()){//not enough
        			player.sendMessage(ChatColor.RED + "Error: Not enough to make a block! You have " + total + " and need at least " + data.getAmount());
        			return true;
        		}
        		

        		System.out.println("Total: " + total);
        		System.out.println("new blocks: " + (total / data.getAmount()) * data.getYield());
        		
        		ItemStack blocks = new ItemStack(data.getAfter(), (total / data.getAmount()) * data.getYield());
        		ItemStack remove = new ItemStack(before, total - total % data.getAmount());
        		//ItemStack keep = new ItemStack(before, total % data.getAmount());
        		
        		System.out.println("Keep: " + total % data.getAmount());
        		System.out.println(total);
        		System.out.println(data.getAmount());
        		if(num > 0){//want only 2 blocks of sandstone, have 13 sand, 4 sand makes 4 sandstone, would make 3
        			if(blocks.getAmount() > num){
        				int diff = blocks.getAmount() - num;
        				blocks.setAmount(num);
        				remove.setAmount(remove.getAmount() - diff * data.getAmount());
        				//keep.setAmount(keep.getAmount() + diff * data.getAmount());
        			}
        		}
        		
        		//player.getInventory().remove(remove);
        		if(remove.getAmount() > 0)
        			player.getInventory().remove(remove);
        		if(blocks.getAmount() > 0)
        			player.getInventory().addItem(blocks);
        		
        		return true;
        		
        	}
        	else{
        		player.sendMessage("Well shucks! " + cmd.getPermissionMessage());
        		return true;
        	}
                    
        }

            

        player.sendMessage("You goofed somehow.");
        return false;
           
    }

    public MaterialMap findMat(Material mat){
    	return materials.get(mat);
    }
}
