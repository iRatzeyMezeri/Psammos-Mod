package psammos.world.blocks.radiation;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.scene.ui.layout.*;
import arc.util.io.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;
import mindustry.world.meta.*;
import psammos.*;
import psammos.type.*;

import static mindustry.Vars.*;

public class RadiationSource extends Block {

    float radAmount = 100f;
    int range = 10;

    TextureRegion radiationRegion;
    TextureRegion[] topRegions;

    public RadiationSource(String name){
        super(name);
        update = true;
        rotate = true;
        rotateDraw = false;
        solid = true;
        configurable = true;
        saveConfig = true;
        noUpdateDisabled = true;
        envEnabled = Env.any;
        clearOnDoubleTap = true;
        buildVisibility = BuildVisibility.sandboxOnly;

        config(RadiationType.class, (RadiationSourceBuild tile, RadiationType rad) -> tile.radOutputType = rad);
        configClear((RadiationSourceBuild tile) -> tile.radOutputType = null);
    }

    @Override
    public void load() {
        super.load();
        radiationRegion = Core.atlas.find(name + "-radiation");
        topRegions = new TextureRegion[2];
        topRegions[0] = Core.atlas.find(name + "-top1");
        topRegions[1] = Core.atlas.find(name + "-top2");
    }

    @Override
    public void setStats() {
        super.setStats();

        stats.add(Stat.range, range, StatUnit.blocks);
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        super.drawPlace(x, y, rotation, valid);
        Draw.rect(Mathf.mod(rotation, 4) > 1 ? topRegions[1] : topRegions[0], x * tilesize, y * tilesize, rotation * 90);

        int maxLen = range + size/2;
        int gx = Geometry.d4x[rotation];
        int gy = Geometry.d4y[rotation];
        Drawf.dashLine(PPal.desertGlass,
                x * tilesize + gx * (tilesize * size / 2f + 2),
                y * tilesize + gy * (tilesize * size / 2f + 2),
                x * tilesize + gx * maxLen * tilesize,
                y * tilesize + gy * maxLen * tilesize
        );
    }

    public class RadiationSourceBuild extends Building implements RadiationEmitter {
        RadiationType radOutputType = RadiationType.light;

        @Override
        public void draw() {
            super.draw();
            Draw.rect(Mathf.mod(rotation, 4) > 1 ? topRegions[1] : topRegions[0], x, y, rotation * 90);

            if(radOutputType != null){
                Draw.color(radOutputType.color);
                Draw.rect(radiationRegion, x, y);
                Draw.color();
            }

            RadiationUtil.drawRadiationBeams(this);
        }

        @Override
        public void drawSelect() {
            super.drawSelect();

            int maxLen = range + size/2;
            int gx = Geometry.d4x[rotation];
            int gy = Geometry.d4y[rotation];
            Drawf.dashLine(radOutputType == null ? PPal.desertGlass : radOutputType.color,
                    x + gx * (tilesize * size / 2f + 2),
                    y + gy * (tilesize * size / 2f + 2),
                    x + gx * maxLen * tilesize,
                    y + gy * maxLen * tilesize
            );

            if (radOutputType != null) {
                float dx = x - block.size * tilesize / 2f;
                float dy = y + block.size * tilesize / 2f;
                float s = 6f * radOutputType.icon().ratio();
                float h = 6f;
                Draw.mixcol(Color.darkGray, 1f);
                Draw.rect(radOutputType.icon(), dx, dy - 1f, s, h);
                Draw.reset();
                Draw.rect(radOutputType.icon(), dx, dy, s, h);
            }
        }

        @Override
        public RadiationStack[] outputRadiation() {
            RadiationStack[] output = new RadiationStack[4];
            for(int i = 0; i < 4; i++){
                if(rotation == i){
                    output[i] = new RadiationStack(radOutputType, radAmount);
                }
            }
            return output;
        }

        @Override
        public float[] outputRadiationFrac() {
            return new float[]{1f, 1f, 1f, 1f};
        }

        @Override
        public float radBeamRange() {
            return range;
        }

        @Override
        public void buildConfiguration(Table table){
            //TODO
        }

        @Override
        public RadiationType config() {
            return radOutputType;
        }

        public void write(Writes write) {
            super.write(write);
            write.i(radOutputType == null ? -1 : radOutputType.ordinal());
        }

        public void read(Reads read, byte revision) {
            super.read(read, revision);
            int readRadOutputType = read.i();
            radOutputType = readRadOutputType == -1 ? null : RadiationType.values()[readRadOutputType];
        }
    }
}
