package cluster;

import graph.Node;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class World extends Node {

    public World(int id) {
        super(id);
    }

    @Override
    public void tick() {

    }

    @Override
    public void reset() {

    }

    @Override
    public String toString() {
        return "World(id=" + id + ")";
    }
}
