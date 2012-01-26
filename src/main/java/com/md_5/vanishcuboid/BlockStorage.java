package com.md_5.vanishcuboid;

import java.io.Serializable;

public class BlockStorage implements Serializable {

    public int l1x, l1y, l1z;
    public int l2x, l2y, l2z;
    public int cx, cy, cz;
    public int c2x, c2y, c2z;
    public String world;
    byte[] blockID;
    byte[] blockData;
    byte replaceID, replaceData;
    public String owner;
    public String name;
    public boolean vanished;
}
