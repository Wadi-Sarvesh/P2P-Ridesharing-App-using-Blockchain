package com.wadis.p2pridesharing.managers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.wadis.p2pridesharing.models.BlockModel;

import java.util.ArrayList;
import java.util.List;

public class BlockChainManager {
    private int difficulty;
    public List<BlockModel> blocks;

    public BlockChainManager(int difficulty) {
        this.difficulty = difficulty;
        blocks = new ArrayList<>();
        BlockModel block = new BlockModel(0,System.currentTimeMillis()/1000,null,"Genesis Block");
        block.mineBlock(difficulty);
        blocks.add(block);
    }
    public BlockModel newBlock(String data)
    {
        BlockModel latestBlock = lastBlock();
        return new BlockModel(latestBlock.getIndex()+1,System.currentTimeMillis()/1000,latestBlock.getHash(),data);


    }

    private BlockModel lastBlock() {
        return blocks.get(blocks.size()-1);
    }
    public void addBlock(BlockModel block)
    {
        block.mineBlock(difficulty);
        blocks.add(block);
    }
    private boolean isFirstBlockValid()
    {
        BlockModel firstBlock = blocks.get(0);
        if(firstBlock.getIndex() != 0)
            return false;
        if(firstBlock.getPreviousHash() != null)
            return false;
        return firstBlock.getHash() != null && BlockModel.calculateHash_detail(firstBlock).equals(firstBlock.getHash());
    }
    private boolean isValidNewBlock(@Nullable BlockModel newBlock, @Nullable BlockModel previousBlock) {
        if (newBlock != null && previousBlock != null) {
            if (previousBlock.getIndex() + 1 != newBlock.getIndex())
                return false;

            if (newBlock.getPreviousHash() == null || !newBlock.getPreviousHash().equals(previousBlock.getHash()))
                return false;
        }
        return newBlock.getHash() != null && BlockModel.calculateHash_detail(newBlock).equals(newBlock.getHash());
    }
    public boolean isBlockChainValid()
    {
        if(!isFirstBlockValid())
            return false;
        for (int i = 1;i<blocks.size();i++)
        {
            BlockModel currentBlock = blocks.get(i);
            BlockModel previousBlock = blocks.get(i-1);
            if(!isValidNewBlock(currentBlock,previousBlock))
                return false;

        }
        return true;
    }
}
