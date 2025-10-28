package psammos.type;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.TextureRegion;
import mindustry.graphics.Pal;

public enum RadiationType {
    radio("radio-waves", Color.valueOf("ab9fbc")),
    IR("infrared-light", Color.valueOf("ff5740")),
    light("visible-light", Pal.accent.cpy()),
    UV("ultraviolet-light", Color.valueOf("a53cf0")),
    xRays("x-rays", Color.valueOf("6aaaf6")),
    gamma("gamma-rays", Color.valueOf("ea833e"));

    public final String name;
    public final Color color;

    RadiationType(String name, Color color){
        this.name = name;
        this.color = color;
    }

    public TextureRegion icon() {
        return Core.atlas.find("psammos-" + name);
    }

    public String localizedName() {
        return Core.bundle.get("radiation.psammos-" + name + ".name");
    }
}
