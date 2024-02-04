package fr.rush.romain.rush.objects;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;

import static fr.rush.romain.rush.Core.logger;

public class BedBlock {

    private final Block headBlock;
    private final BlockData headBlockData;
    private final Block bottomBlock;
    private final BlockData bottomBlockData;

    public BedBlock(Block block){
        this.headBlock = block;
        this.headBlockData = block.getBlockData();
        Directional direction = (Directional) this.headBlockData;

        if(block.getRelative(direction.getFacing().getOppositeFace()).getType().equals(Material.AIR)){
            this.bottomBlock = block.getRelative(direction.getFacing());
        } else {
            this.bottomBlock = block.getRelative(direction.getFacing().getOppositeFace());
        }
        this.bottomBlockData = this.bottomBlock.getBlockData();
        logger(headBlock.getType().name() + " -> " + bottomBlock.getType().name());
        logger(headBlock.getX() + " " + headBlock.getZ() + " | " + bottomBlock.getX() + " " + bottomBlock.getZ());
    }

    public void place(){

        Block block = this.headBlock.getWorld().getBlockAt(headBlock.getLocation());

        block.setType(headBlock.getType());
        block.setBlockData(headBlockData);

        Block block2 = this.bottomBlock.getWorld().getBlockAt(bottomBlock.getLocation());
        block2.setType(bottomBlock.getType());
        block2.setBlockData(bottomBlockData);

    }

    public void remove(){
        Block block = this.headBlock.getWorld().getBlockAt(headBlock.getLocation());
        block.setType(Material.AIR);

        Block block2 = this.bottomBlock.getWorld().getBlockAt(bottomBlock.getLocation());
        block2.setType(Material.AIR);

    }
}
