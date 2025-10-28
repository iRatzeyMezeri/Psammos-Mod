package psammos.world.blocks.radiation;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.struct.Seq;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.StaticWall;
import psammos.graphics.PDraw;
import psammos.type.RadiationStack;

import static mindustry.Vars.*;

public class RadiationUtil {

    /** Gets the center point of the specified side of the build*/
    public static Point2 side(Building build, int rotation){
        int x = build.tileX() + Geometry.d4x[rotation] * Mathf.ceil(build.block.size / 2f);
        int y = build.tileY() + Geometry.d4y[rotation] * Mathf.ceil(build.block.size / 2f);
        return new Point2(x, y);
    }

    public static void handleRadiationEmission(Building build, int rotation){
        Tile target = RadiationUtil.findRadiationTarget(build, rotation);
        if (target != null && target.build instanceof RadiationConsumer consumer){
            consumer.addRadiationInput(build);
        }
    }

    public static void handleRadiationEmission(Building build){
        for (int rotation = 0; rotation < 4; rotation++){
            handleRadiationEmission(build, rotation);
        }
    }

    public static RadiationStack[] calculateSideRadiation(Building build, Seq<Building> inputs){
        RadiationStack[] sideRadiation = new RadiationStack[4];
        inputs.forEach(b -> {
            if (!(b instanceof RadiationEmitter emitter) || b.dead){
                return;
            }
            int rotation = build.relativeTo(b);

            if (emitter.outputRadiation()[(rotation + 2) % 4] != null){
                if (sideRadiation[rotation] == null){
                    sideRadiation[rotation] = new RadiationStack(emitter.outputRadiation()[(rotation + 2) % 4].type, 0);
                }
                if (sideRadiation[rotation].type == emitter.outputRadiation()[(rotation + 2) % 4].type){
                    sideRadiation[rotation].amount += emitter.outputRadiation()[(rotation + 2) % 4].amount;
                }
            }
        });
        return sideRadiation;
    }

    /** Finds the tile radiation hits if the building emits it in the specified direction.
     * Returns null if no target was found */
    public static Tile findRadiationTarget(Building build, int rotation){
        if (!(build instanceof RadiationEmitter)){
            return null;
        }
        RadiationEmitter emitter = (RadiationEmitter) build;

        int x = side(build, rotation).x;
        int y = side(build, rotation).y;
        int dist = 0;
        while (!(world.tile(x, y) == null ||
                world.tile(x, y).block() instanceof StaticWall ||
                world.tile(x, y).build instanceof RadiationConsumer)){
            x += Geometry.d4x(rotation);
            y += Geometry.d4y(rotation);
            dist++;
            if (dist >= emitter.radBeamRange()){
                return null;
            }
        }
        return world.tile(x, y);
    }

    public static void drawRadiationBeams(Building build){
        //TODO: Maybe make this a drawer?
        if (!(build instanceof RadiationEmitter)){
            return;
        }
        RadiationEmitter emitter = (RadiationEmitter) build;

        TextureRegion beam = Core.atlas.find("psammos-radiation-beam");
        TextureRegion beamEnd = Core.atlas.find("psammos-radiation-beam-end");

        for (int rotation = 0; rotation < 4; rotation++){
            RadiationStack radStack = emitter.outputRadiation()[rotation];

            if (radStack == null || radStack.type == null || radStack.amount == 0){
                continue;
            }

            Tile target = RadiationUtil.findRadiationTarget(build, rotation);
            float dx = Geometry.d4x[rotation] * tilesize;
            float dy = Geometry.d4y[rotation] * tilesize;
            Color color = radStack.type.color.cpy();
            float scale = Mathf.clamp(radStack.amount / 100f) *  0.5f;
            Draw.z(Layer.effect);

            if (target != null){
                Draw.color(color);
                Drawf.laser(beam, beamEnd,
                        build.x + dx * build.block.size / 2f, build.y + dy * build.block.size / 2f,
                        target.worldx() - dx / 2f, target.worldy() - dy / 2f,
                        scale);
            }else{
                PDraw.gradientLaser(beam, beamEnd,
                        build.x + dx * build.block.size / 2f, build.y + dy * build.block.size / 2f, color,
                        build.x + dx * emitter.radBeamRange() * 1.2f, build.y + dy * emitter.radBeamRange() * 1.2f, Color.clear,
                        scale);
            }
            Draw.reset();
        }
    }
}
