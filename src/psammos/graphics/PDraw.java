package psammos.graphics;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import mindustry.graphics.Drawf;

public class PDraw {
    public static void spinLineSprite(TextureRegion region, float x, float y, float r){
        float a = Draw.getColorAlpha();
        r = Mathf.mod(r, 360f);

        Draw.rect(region, x, y, r);

        float alphaMult = Mathf.clamp(-Math.abs(r - 135f) / 90f + 1.5f);
        Draw.alpha(alphaMult * a);
        Draw.scl(1, -1);
        Draw.rect(region, x, y, r);

        Draw.alpha(a);
        Draw.scl(1);
    }

    public static void gradientLaser(TextureRegion line, TextureRegion edge, float x, float y, Color c, float x2, float y2, Color c2, float scale){
        gradientLaser(line, edge, edge, x, y, c, x2, y2, c2, scale);
    }

    public static void gradientLaser(TextureRegion line, TextureRegion start, TextureRegion end, float x, float y, Color c, float x2, float y2, Color c2, float scale){
        float scl = 8f * scale * Draw.scl, rot = Mathf.angle(x2 - x, y2 - y);
        float vx = Mathf.cosDeg(rot) * scl, vy = Mathf.sinDeg(rot) * scl;

        Draw.color(c);
        Draw.rect(start, x, y, start.width * scale * start.scl(), start.height * scale * start.scl(), rot + 180);
        Draw.color(c2);
        Draw.rect(end, x2, y2, end.width * scale * end.scl(), end.height * scale * end.scl(), rot);

        Lines.stroke(12f * scale);
        PDraw.line(line, x + vx, y + vy, c, x2 - vx, y2 - vy, c2, false);
        Lines.stroke(1f);

        Drawf.light(x, y, x2, y2);

        Draw.reset();
    }

    public static void line(TextureRegion region, float x, float y, Color c, float x2, float y2, Color c2, boolean cap){
        float color1 = c.toFloatBits();
        float color2 = c2.toFloatBits();
        float hstroke = Lines.getStroke() / 2.0F;
        float len = Mathf.len(x2 - x, y2 - y);
        float diffx = (x2 - x) / len * hstroke;
        float diffy = (y2 - y) / len * hstroke;
        if (cap) {
            Fill.quad(region, x - diffx - diffy, y - diffy + diffx, color1, x - diffx + diffy, y - diffy - diffx, color1, x2 + diffx + diffy, y2 + diffy - diffx, color2, x2 + diffx - diffy, y2 + diffy + diffx, color2);
        } else {
            Fill.quad(region, x - diffy, y + diffx, color1, x + diffy, y - diffx, color1, x2 + diffy, y2 - diffx, color2, x2 - diffy, y2 + diffx, color2);
        }
    }
}
