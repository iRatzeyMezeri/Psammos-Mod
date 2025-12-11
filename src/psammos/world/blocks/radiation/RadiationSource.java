package psammos.world.blocks.radiation;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.geom.*;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.ButtonGroup;
import arc.scene.ui.ImageButton;
import arc.scene.ui.layout.*;
import arc.util.Eachable;
import arc.util.io.*;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.ui.Bar;
import mindustry.ui.Styles;
import mindustry.world.*;
import mindustry.world.draw.*;
import mindustry.world.meta.*;
import psammos.*;
import psammos.type.*;
import psammos.world.draw.*;

import static mindustry.Vars.*;

public class RadiationSource extends Block {

    public DrawBlock drawer = new DrawMulti(new DrawDefault(), new DrawDirectionalRegion(), new DrawRadiationBeams());

    public float radOutputAmount = 1000f;
    public int range = 10;

    TextureRegion radiationRegion;

    public RadiationSource(String name){
        super(name);
        update = true;
        rotate = true;
        rotateDraw = false;
        clipSize = range * tilesize * 2;
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
        drawer.load(this);
        radiationRegion = Core.atlas.find(name + "-radiation");
    }

    @Override
    public void setStats() {
        super.setStats();

        stats.add(Stat.range, range, StatUnit.blocks);
    }

    @Override
    public void setBars() {
        super.setBars();
        addBar("psammos-radiation", (RadiationSourceBuild b) -> new Bar(
                () -> b.radOutputType == null ? Core.bundle.get("bar.psammos-radiation") : Core.bundle.format("bar.psammos-radiation-amount", b.radOutputType.localizedName(), radOutputAmount),
                () -> b.radOutputType == null ? Color.clear : b.radOutputType.color,
                () -> b.radOutputType == null ? 0f : 1f
        ));
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
        drawer.drawPlan(this, plan, list);
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        super.drawPlace(x, y, rotation, valid);

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
        public RadiationType radOutputType = RadiationType.light;

        @Override
        public void draw() {
            drawer.draw(this);

            if(radOutputType != null){
                Draw.color(radOutputType.color);
                Draw.rect(radiationRegion, x, y);
                Draw.color();
            }
        }

        @Override
        public void drawLight(){
            super.drawLight();
            drawer.drawLight(this);
        }

        @Override
        public void updateTile() {
            super.updateTile();
            handleRadiationEmission(this, rotation);
        }

        @Override
        public void drawSelect() {
            super.drawSelect();

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
            if (radOutputType != null) {
                output[rotation] = new RadiationStack(radOutputType, radOutputAmount);
            }
            return output;
        }

        @Override
        public float radBeamRange() {
            return range;
        }

        @Override
        public void buildConfiguration(Table table){
            ButtonGroup<ImageButton> group = new ButtonGroup<>();
            group.setMinCheckCount(0);
            Table cont = new Table().background(Styles.black6).top();
            cont.defaults().size(40);

            for(RadiationType type : RadiationType.values()){
                ImageButton button = cont.button(Tex.whiteui, Styles.clearNoneTogglei, 24f, () -> {
                    control.input.config.hideConfig();
                }).tooltip(type.localizedName()).group(group).get();
                button.changed(() -> configure(button.isChecked() ? type : null));
                button.getStyle().imageUp = new TextureRegionDrawable(type.icon());
                button.update(() -> button.setChecked(radOutputType == type));
            }

            table.add(cont);
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
