package com.super_deathagon.macrobuilder;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class CommandCopy extends CommandBase{
	
	public void copy(World world, double x, double y, double z, double x2, double y2, double z2){
		if(world.isRemote)
			return;
		if(x > x2 || y > y2 || z > z2)
			return;
		
		try {
			FileOutputStream fos = new FileOutputStream("C:/users/super/desktop/minecraft.txt");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			int l = (int) (x2 - x) + 1;
			int w = (int) (z2 - z) + 1;
			int h = (int) (y2 - y) + 1;	
			int[][][] list = new int[l][h][w];
			BlockPos pos;
			int id;
			
			for(int k = 0; k < h; k++){
				for(int j = 0; j < w; j++){
					for(int i = 0; i < l; i++){
						pos = new BlockPos(i+x,k+y,j+z);
						id = Block.getStateId(world.getBlockState(pos));
						list[i][k][j] = id;
						//world.setBlockState(pos, Blocks.air.getDefaultState());
					}
				}
				System.out.println(k + "/" + h);
			}
			
			oos.writeInt(l);
			oos.writeInt(w);
			oos.writeInt(h);
			oos.writeObject(list);
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getName() {
		return "copy";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "copy <x><y><z> <x2><y2><z2>";
	}

    /**
     * Return the required permission level for this command.
     */
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

	@Override
	public int compareTo(Object arg0) {
		return 0;
	}

	@Override
	public List getAliases() {
		ArrayList list = new ArrayList();
		list.add("copy");
		return list;
	}

	@Override
	public boolean canCommandSenderUse(ICommandSender sender) {
		for(String mod: MacroBuilder.ModUserList)
			if(sender.getName().equals(mod))
				return true;
		return false;
	}

    public List addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos){
        return args.length > 0 && args.length <= 3 ? func_175771_a(args, 0, pos) : ((args.length > 3 && args.length <= 6 ? func_175771_a(args, 3, pos) : null));
    }
	
	@Override
	public void execute(ICommandSender sender, String[] args)throws CommandException {
		if(args.length != 6)
			throw new CommandException(getCommandUsage(sender));
		else
			copy(sender.getEntityWorld(),Integer.parseInt(args[0]),
										Integer.parseInt(args[1]),
										Integer.parseInt(args[2]),
										Integer.parseInt(args[3]),
										Integer.parseInt(args[4]),
										Integer.parseInt(args[5]));
	}
}
