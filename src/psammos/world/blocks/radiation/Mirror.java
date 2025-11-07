package psammos.world.blocks.radiation;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.geom.Geometry;
import arc.struct.Seq;
import arc.util.Eachable;
import mindustry.Vars;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.world.Block;
import mindustry.world.draw.*;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import psammos.PPal;
import psammos.type.RadiationStack;
import psammos.type.RadiationType;
import psammos.world.draw.*;

import static mindustry.Vars.*;

public class Mirror extends Block {

    public DrawBlock drawer = new DrawMulti(new DrawDefault(), new DrawRadiationBeams());

    public int range = 10;
    public float shadowOffset = -0.25f;
    public float shadowAlpha = 0.25f;

    TextureRegion topRegion1, topRegion2;
    TextureRegion topShadowRegion;

    public Mirror(String name) {
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
        topRegion1 = Core.atlas.find(name + "-top1");
        topRegion2 = Core.atlas.find(name + "-top2");
        topShadowRegion = Core.atlas.find(name + "-top-shadow");
    }

    @Override
    public void setStats() {
        super.setStats();

        stats.add(Stat.range, range, StatUnit.blocks);
    }

    @Override
    protected TextureRegion[] icons() {
        return new TextureRegion[]{region, topRegion2};
    }

    @Override
    public TextureRegion getPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
        return region;
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
        drawer.drawPlan(this, plan, list);

        Draw.alpha(shadowAlpha);
        Draw.rect(topShadowRegion, plan.drawx() + shadowOffset, plan.drawy() + shadowOffset, plan.rotation * 90);
        Draw.alpha(1);
        if (plan.rotation % 2 == 0) {
            Draw.rect(topRegion1, plan.drawx(), plan.drawy());
        }else{
            Draw.rect(topRegion2, plan.drawx(), plan.drawy());
        }
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        super.drawPlace(x, y, rotation, valid);
        for(int i = 0; i < 4; i++){
            int maxLen = range + size/2;
            int gx = Geometry.d4x[i];
            int gy = Geometry.d4y[i];
            Drawf.dashLine(PPal.desertGlass,
                    x * tilesize + gx * (tilesize * size / 2f + 2),
                    y * tilesize + gy * (tilesize * size / 2f + 2),
                    x * tilesize + gx * maxLen * tilesize,
                    y * tilesize + gy * maxLen * tilesize
            );
        }
    }

    public class MirrorBuild extends Building implements RadiationEmitter, RadiationConsumer {
        public Seq<Building> radiationInputs = new Seq<>();
        public RadiationStack[] sideRadiation;

        @Override
        public void draw() {
            drawer.draw(this);

            Draw.z(Layer.blockOver);
            Draw.alpha(shadowAlpha);
            Draw.rect(topShadowRegion, x + shadowOffset, y + shadowOffset, rotdeg());
            Draw.alpha(1);
            if (rotation % 2 == 0) {
                Draw.rect(topRegion1, x, y);
            }else{
                Draw.rect(topRegion2, x, y);
            }
            Draw.reset();
        }

        @Override
        public void drawLight(){
            super.drawLight();
            drawer.drawLight(this);
        }

        @Override
        public void updateTile() {
            super.updateTile();
            sideRadiation = calculateSideRadiation(this, radiationInputs);
            handleRadiationEmission(this);
        }

        @Override
        public RadiationStack[] outputRadiation() {
            if (rotation % 2 == 0){
                return new RadiationStack[]{sideRadiation[3], sideRadiation[2], sideRadiation[1], sideRadiation[0]};
            }else{
                return new RadiationStack[]{sideRadiation[1], sideRadiation[0], sideRadiation[3], sideRadiation[2]};
            }
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

        @Override
        public boolean acceptsRadiation(RadiationType type, int from) {
            return true;
        }

        @Override
        public float incomingBeamOffset() {
            return -Vars.tilesize * 0.3f;
        }

        @Override
        public float emittedBeamOffset() {
            return -Vars.tilesize * 0.3f;
        }
    }
}
