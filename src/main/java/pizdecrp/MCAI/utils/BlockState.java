package pizdecrp.MCAI.utils;

import com.github.steveice10.mc.protocol.data.game.entity.metadata.Position;

public class BlockState {
    private int id;
    private Position pos;

    public BlockState(int id, Position pos) {
        this.id = id;
        this.pos = pos;
    }

    public int getId() {
        return this.id;
    }

    public Position getPos() {
        return this.pos;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof BlockState)) return false;

        BlockState that = (BlockState) o;
        return this.id == that.id &&
                this.pos == that.pos;
    }
}
