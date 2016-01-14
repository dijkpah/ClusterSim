package cluster;

import graph.Node;

public class World extends Node {

    public World(int id) {
        super(id);
    }

    @Override
    public void tick() {

    }

    @Override
    public String toString() {
        return "World(id=" + id + ")";
    }
}
