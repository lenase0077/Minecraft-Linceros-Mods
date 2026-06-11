package com.linceros.leveltools.api;

import net.minecraft.world.item.Item;

import java.util.HashMap;
import java.util.Map;

/**
 * Public API for LincerosLevelTools mod.
 * Modders can use this to register their own tool evolutions.
 */
public class LevelToolsAPI {
    
    private static final Map<Item, Item> EVOLUTIONS = new HashMap<>();

    /**
     * Registers a tool evolution. When a tool reaches the evolution level interval,
     * it will transform into the target item.
     * 
     * @param from The base item (e.g. Items.WOODEN_PICKAXE)
     * @param to The upgraded item (e.g. Items.STONE_PICKAXE)
     */
    public static void registerEvolution(Item from, Item to) {
        if (from != null && to != null) {
            EVOLUTIONS.put(from, to);
        }
    }

    /**
     * Returns the item that the given item evolves into.
     * 
     * @param current The current item
     * @return The evolved item, or null if no evolution is registered
     */
    public static Item getEvolution(Item current) {
        return EVOLUTIONS.get(current);
    }
}
