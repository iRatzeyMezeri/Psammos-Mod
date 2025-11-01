package psammos.world.blocks.radiation;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.geom.Geometry;
import arc.struct.Seq;
import arc.util.Eachable;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.world.Block;
import mindustry.world.draw.*;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import psammos.PPal;
import psammos.graphics.PDraw;
import psammos.type.RadiationStack;
import psammos.world.draw.*;

import static mindustry.Vars.*;

public class Mirror extends Block {

    public DrawBlock drawer = new DrawMulti(new DrawDefault(), new DrawRadiationBeams());

    public int range = 10;
    public float shadowOffset = -0.25f;
    public float shadowAlpha = 0.25f;

    TextureRegion topRegion;
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
    public void setStats() {
        super.setStats();

        stats.add(Stat.range, range, StatUnit.blocks);
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
        drawer.drawPlan(this, plan, list);

        Draw.alpha(shadowAlpha);
        Draw.rect(topShadowRegion, plan.drawx() + shadowOffset, plan.drawy() + shadowOffset, plan.rotation * 90 + 45f);
        Draw.alpha(1);
        PDraw.spinLineSprite(topRegion, plan.drawx(), plan.drawy(), plan.rotation * 90 + 45f);
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

    @Override
    public void load() {
        super.load();
        drawer.load(this);
        topRegion = Core.atlas.find(name + "-top");
        topShadowRegion = Core.atlas.find(name + "-top-shadow");
    }

    public class MirrorBuild extends Building implements RadiationEmitter, RadiationConsumer {
        public Seq<Building> radiationInputs = new Seq<>();
        public RadiationStack[] sideRadiation;

        @Override
        public void draw() {
            drawer.draw(this);

            Draw.z(Layer.blockOver);
            Draw.alpha(shadowAlpha);
            Draw.rect(topShadowRegion, x + shadowOffset, y + shadowOffset, rotdeg() + 45f);
            Draw.alpha(1);
            PDraw.spinLineSprite(topRegion, x, y, rotdeg() + 45f);
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
            sideRadiation = RadiationUtil.calculateSideRadiation(this, radiationInputs);
            radiationInputs.clear();
            RadiationUtil.handleRadiationEmission(this);
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
    }
}
