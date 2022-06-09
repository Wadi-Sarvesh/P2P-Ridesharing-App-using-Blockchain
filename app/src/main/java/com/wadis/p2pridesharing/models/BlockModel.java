package com.wadis.p2pridesharing.models;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class BlockModel {
    private int index,nonce;
    private long timestamp;
    private String hash,previousHash,data;

    public BlockModel(int index, long timestamp, String previousHash, String data) {
        this.index = index;
        this.timestamp = timestamp;
        this.previousHash = previousHash;
        this.data = data;
        nonce = 0;
        hash = BlockModel.calculateHash_detail(this);
    }

    public static String calculateHash_detail(BlockModel blockModel) {
        if(blockModel != null)
        {
            MessageDigest messageDigest;
            try
            {
                messageDigest = MessageDigest.getInstance("SHA-256");
            } catch (NoSuchAlgorithmException e) {
                return null;
            }
            String text = blockModel.sumStr();
            final byte[] bytes  = messageDigest.digest(text.getBytes());
            final StringBuilder builder = new StringBuilder();
            for(final byte b : bytes)
            {
                String hex = Integer.toHexString(0xff & b);
                if(hex.length() == 1)
                    builder.append('0');
                builder.append(hex);
            }
            return builder.toString();
        }
        return null;
    }

    private  String sumStr() {
        return index + timestamp + previousHash + data + nonce;
    }
    public void mineBlock(int difficulty)
    {
        nonce = 0;
        while(!getHash().substring(0,difficulty).equals(addZeros(difficulty)))
        {
            nonce++;
            hash = calculateHash_detail(this);
        }
    }

    private String addZeros(int difficulty) {
        StringBuilder builder = new StringBuilder();
        for(int i=0;i<difficulty;i++)
        {
            builder.append('0');
        }
        return builder.toString();
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getNonce() {
        return nonce;
    }

    public void setNonce(int nonce) {
        this.nonce = nonce;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
