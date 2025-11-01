package psammos.world.blocks.radiation;

import arc.math.geom.Geometry;
import arc.struct.Seq;
import arc.util.Eachable;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.world.Block;
import mindustry.world.draw.DrawBlock;
import mindustry.world.draw.DrawDefault;
import mindustry.world.draw.DrawMulti;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import psammos.PPal;
import psammos.type.RadiationStack;
import psammos.world.draw.DrawRadiationBeams;
import psammos.world.draw.DrawDirectionalRegion;

import static mindustry.Vars.tilesize;

public class Focuser extends Block {

    public DrawBlock drawer = new DrawMulti(new DrawDefault(), new DrawDirectionalRegion(), new DrawRadiationBeams());

    int range = 10;

    public Focuser(String name) {
        super(name);
        update = true;
        rotate = true;
        rotateDraw = false;
        clipSize = range * tilesize * 2;
        solid = true;
    }

    @Override
    public void load() {
        super.load();
        drawer.load(this);
    }

    @Override
    public void setStats() {
        super.setStats();

        stats.add(Stat.range, range, StatUnit.blocks);
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
        drawer.drawPlan(this, plan, list);
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        super.drawPlace(x, y, rotation, valid);

        int maxLen = range + size / 2;
        int gx = Geometry.d4x[rotation];
        int gy = Geometry.d4y[rotation];
        Drawf.dashLine(PPal.desertGlass,
                x * tilesize + gx * (tilesize * size / 2f + 2),
                y * tilesize + gy * (tilesize * size / 2f + 2),
                x * tilesize + gx * maxLen * tilesize,
                y * tilesize + gy * maxLen * tilesize
        );
    }

    public class FocuserBuild extends Building implements RadiationEmitter, RadiationConsumer {
        public Seq<Building> radiationInputs = new Seq<>();
        public RadiationStack[] sideRadiation;

        @Override
        public void draw() {
            drawer.draw(this);
        }

        @Override
        public void drawLight(){
            super.drawLight();
            drawer.drawLight(this);
        }

        @Override
        public void updateTile() {
            super.updateTile();
            sideRadiation = RadiationUtil.calculateSideRadiation(this, radiationInputs);
            radiationInputs.clear();
            RadiationUtil.handleRadiationEmission(this);
        }

        @Override
        public RadiationStack[] outputRadiation() {
            RadiationStack[] output = new RadiationStack[4];
            output[rotation] = sideRadiation[(rotation + 2) % 4];
            return output;
        }

        @Override
        public float radBeamRange() {
            return range;
        }

        @Override
        public void addRadiationInput(Building build) {
            if (!radiationInputs.contains(build)){
                radiationInputs.add(build);
            }
        }
    }
}
