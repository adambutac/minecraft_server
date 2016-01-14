package com.super_deathagon.macrobuilder;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class CommandPaste implements ICommand{
	public static void paste(final World world, final String filename, final double x, final double y, final double z){
		if(world.isRemote)
			return;
		
		MinecraftServer.getServer().addScheduledTask(new Thread(){
			@Override
			public void run(){
				try {
					FileInputStream fis = new FileInputStream(filename);
					ObjectInputStream ois = new ObjectInputStream(fis);
					int l = ois.readInt();
					int w = ois.readInt();
					int h = ois.readInt();
					int[][][] list = (int[][][]) ois.readObject();
					
					for(int k = 0; k < h; k++){
						for(int j = 0; j < w; j++){
							for(int i = 0; i < l; i++){
								int id = list[i][k][j];
								IBlockState state = Block.getStateById(id);
								BlockPos pos = new BlockPos(i+x,k+y,j+z);
								world.setBlockState(pos, state);
							}
						}
					}
					ois.close();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public String getName() {
		return "paste";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "paste <file> <x><y><z>";
	}

	@Override
	public void execute(ICommandSender sender, String[] args)throws CommandException {
		if(args.length != 4)
			throw new CommandException(getCommandUsage(sender));
		else
			paste(sender.getEntityWorld(),args[0],
										Integer.parseInt(args[1]),
										Integer.parseInt(args[2]),
										Integer.parseInt(args[3]));
	}

	@Override
	public int compareTo(Object arg0) {
		return 0;
	}

	@Override
	public List getAliases() {
		ArrayList list = new ArrayList();
		list.add("paste");
		return list;
	}

	@Override
	public boolean canCommandSenderUse(ICommandSender sender) {
		return true;
	}

	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
		return null;
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index) {
		// TODO Auto-generated method stub
		return false;
	}
}
