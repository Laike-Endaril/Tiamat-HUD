package com.fantasticsource.tiamathud.hudelement;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.text.*;
import com.fantasticsource.mctools.gui.element.text.filter.*;
import com.fantasticsource.mctools.gui.screen.ColorSelectionGUI;
import com.fantasticsource.mctools.gui.screen.TextSelectionGUI;
import com.fantasticsource.tools.PNG;
import com.fantasticsource.tools.component.CDouble;
import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.CStringUTF8;
import com.fantasticsource.tools.datastructures.Color;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import org.lwjgl.opengl.Display;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedHashMap;

import static com.fantasticsource.tiamathud.TiamatHUD.MODID;
import static org.lwjgl.opengl.GL11.GL_QUADS;

public class CBarElement extends CHUDElement
{
    public static final ResourceLocation
            DEFAULT_BACK_RL = new ResourceLocation(MODID, "image/default_bar_back.png"),
            DEFAULT_FILL_RL = new ResourceLocation(MODID, "image/default_bar_fill.png"),
            DEFAULT_FORE_RL = new ResourceLocation(MODID, "image/default_bar_fore.png");

    public static final PNG
            DEFAULT_BACK_PNG = MCTools.getPNG(DEFAULT_BACK_RL),
            DEFAULT_FILL_PNG = MCTools.getPNG(DEFAULT_FILL_RL),
            DEFAULT_FORE_PNG = MCTools.getPNG(DEFAULT_FORE_RL);

    public static final int
            DIRECTION_LEFT_TO_RIGHT = 0,
            DIRECTION_BOTTOM_TO_TOP = 1,
            DIRECTION_RIGHT_TO_LEFT = 2,
            DIRECTION_TOP_TO_BOTTOM = 3;

    public static final LinkedHashMap<String, Integer> DIRECTIONS_S_I = new LinkedHashMap<>();
    public static final LinkedHashMap<Integer, String> DIRECTIONS_I_S = new LinkedHashMap<>();

    static
    {
        DIRECTIONS_S_I.put("Left to right", DIRECTION_LEFT_TO_RIGHT);
        DIRECTIONS_S_I.put("Bottom to top", DIRECTION_BOTTOM_TO_TOP);
        DIRECTIONS_S_I.put("Right to left", DIRECTION_RIGHT_TO_LEFT);
        DIRECTIONS_S_I.put("Top to bottom", DIRECTION_TOP_TO_BOTTOM);

        DIRECTIONS_I_S.put(DIRECTION_LEFT_TO_RIGHT, "Left to right");
        DIRECTIONS_I_S.put(DIRECTION_BOTTOM_TO_TOP, "Bottom to top");
        DIRECTIONS_I_S.put(DIRECTION_RIGHT_TO_LEFT, "Right to left");
        DIRECTIONS_I_S.put(DIRECTION_TOP_TO_BOTTOM, "Top to bottom");
    }


    protected boolean error = false;

    public int x = 0, y = 0;
    public double hScale = 1, vScale = 1;
    public int direction = DIRECTION_LEFT_TO_RIGHT;

    protected ResourceLocation backRL = DEFAULT_BACK_RL, fillRL = DEFAULT_FILL_RL, foreRL = DEFAULT_FORE_RL;
    protected PNG backPNG = DEFAULT_BACK_PNG, fillPNG = DEFAULT_FILL_PNG, forePNG = DEFAULT_FORE_PNG;
    public Color backColor = Color.WHITE, fillColor = Color.WHITE, foreColor = Color.WHITE;

    public String centerText = "current", lowEndText = "", highEndText = "";
    public Color centerTextColor = Color.BLACK, lowEndTextColor = Color.BLACK, highEndTextColor = Color.BLACK;
    public Color centerTextOutlineColor = Color.WHITE, lowEndTextOutlineColor = Color.WHITE, highEndTextOutlineColor = Color.WHITE;
    public double centerTextScale = 1, lowEndTextScale = 1, highEndTextScale = 1;

    public String min = "0", current = "health", max = "attribute:generic.maxHealth";


    public ResourceLocation getBackRL()
    {
        return backRL;
    }

    public ResourceLocation getFillRL()
    {
        return fillRL;
    }

    public ResourceLocation getForeRL()
    {
        return foreRL;
    }

    public void setBackRL(ResourceLocation backRL)
    {
        this.backRL = backRL;
        backPNG = MCTools.getPNG(backRL);
    }

    public void setFillRL(ResourceLocation fillRL)
    {
        this.fillRL = fillRL;
        fillPNG = MCTools.getPNG(fillRL);

        error = false;
    }

    public void setForeRL(ResourceLocation foreRL)
    {
        this.foreRL = foreRL;
        forePNG = MCTools.getPNG(foreRL);
    }


    @Override
    protected void draw() //Render
    {
        if (fillPNG == null)
        {
            if (!error)
            {
                System.err.println(TextFormatting.RED + "Bar fill image not found: " + fillRL);
                error = true;
            }

            return;
        }


        GlStateManager.enableBlend();
        GlStateManager.enableTexture2D();


        Minecraft mc = Minecraft.getMinecraft();
        TextureManager textureManager = mc.getTextureManager();


        GlStateManager.pushMatrix();
        GlStateManager.translate(x >= 0 ? x : Display.getWidth() + x, y >= 0 ? y : Display.getHeight() + y, 0);
        GlStateManager.scale(hScale, vScale, 1);
        if (direction == DIRECTION_TOP_TO_BOTTOM || direction == DIRECTION_BOTTOM_TO_TOP)
        {
            GlStateManager.pushMatrix();
            GlStateManager.rotate(90, 0, 0, 1);
        }


        //Background
        if (backPNG != null && backColor.af() > 0)
        {
            textureManager.bindTexture(backRL);
            GlStateManager.color(backColor.rf(), backColor.gf(), backColor.bf(), backColor.af());

            int halfW = backPNG.getWidth() >>> 1, halfH = backPNG.getHeight() >>> 1;
            GlStateManager.glBegin(GL_QUADS);
            //TODO check this and similar rendering for any issues along borders.  If any show, may need half-pixel UV offsets
            GlStateManager.glTexCoord2f(0, 0);
            GlStateManager.glVertex3f(-halfW, -halfH, 0);
            GlStateManager.glTexCoord2f(0, 1);
            GlStateManager.glVertex3f(-halfW, halfH, 0);
            GlStateManager.glTexCoord2f(1, 1);
            GlStateManager.glVertex3f(halfW, halfH, 0);
            GlStateManager.glTexCoord2f(1, 0);
            GlStateManager.glVertex3f(halfW, -halfH, 0);
            GlStateManager.glEnd();
        }

        //Fill
        if (fillPNG != null && fillColor.af() > 0)
        {
            Double min = null, current = null, max = null;
            try
            {
                min = Double.parseDouble(this.min);
            }
            catch (NumberFormatException e)
            {
                min = MCTools.getAttribute(Minecraft.getMinecraft().player, this.min);
            }
            if (min == null) min = parseVal(this.min);

            current = parseVal(this.current);
            if (current == null)
            {
                try
                {
                    current = Double.parseDouble(this.min);
                }
                catch (NumberFormatException e)
                {
                    current = MCTools.getAttribute(Minecraft.getMinecraft().player, this.min);
                }
            }

            max = MCTools.getAttribute(Minecraft.getMinecraft().player, this.max);
            if (max == null)
            {
                try
                {
                    max = Double.parseDouble(this.max);
                }
                catch (NumberFormatException e)
                {
                }
            }
            if (max == null) max = parseVal(this.max);


            if (min != null && current != null && max != null && max - min != 0 && current <= max)
            {
                float fillPercent = (float) ((current - min) / (max - min));

                textureManager.bindTexture(fillRL);
                GlStateManager.color(fillColor.rf(), fillColor.gf(), fillColor.bf(), fillColor.af());

                int w = fillPNG.getWidth(), h = fillPNG.getHeight();
                int halfW = w >>> 1, halfH = h >>> 1;

                switch (direction)
                {
                    case DIRECTION_LEFT_TO_RIGHT:
                    case DIRECTION_BOTTOM_TO_TOP:
                        GlStateManager.glBegin(GL_QUADS);
                        GlStateManager.glTexCoord2f(0, 0);
                        GlStateManager.glVertex3f(-halfW, -halfH, 0);
                        GlStateManager.glTexCoord2f(0, 1);
                        GlStateManager.glVertex3f(-halfW, halfH, 0);
                        GlStateManager.glTexCoord2f(fillPercent, 1);
                        GlStateManager.glVertex3f(-halfW + w * fillPercent, halfH, 0);
                        GlStateManager.glTexCoord2f(fillPercent, 0);
                        GlStateManager.glVertex3f(-halfW + w * fillPercent, -halfH, 0);
                        GlStateManager.glEnd();
                        break;

                    default:
                        GlStateManager.glBegin(GL_QUADS);
                        GlStateManager.glTexCoord2f(1 - fillPercent, 0);
                        GlStateManager.glVertex3f(halfW - w * fillPercent, -halfH, 0);
                        GlStateManager.glTexCoord2f(1 - fillPercent, 1);
                        GlStateManager.glVertex3f(halfW - w * fillPercent, halfH, 0);
                        GlStateManager.glTexCoord2f(1, 1);
                        GlStateManager.glVertex3f(halfW, halfH, 0);
                        GlStateManager.glTexCoord2f(1, 0);
                        GlStateManager.glVertex3f(halfW, -halfH, 0);
                        GlStateManager.glEnd();
                }
            }
        }

        //Foreground
        if (forePNG != null && foreColor.af() > 0)
        {
            textureManager.bindTexture(foreRL);
            GlStateManager.color(foreColor.rf(), foreColor.gf(), foreColor.bf(), foreColor.af());

            int halfW = forePNG.getWidth() >>> 1, halfH = forePNG.getHeight() >>> 1;
            GlStateManager.glBegin(GL_QUADS);
            GlStateManager.glTexCoord2f(0, 0);
            GlStateManager.glVertex3f(-halfW, -halfH, 0);
            GlStateManager.glTexCoord2f(0, 1);
            GlStateManager.glVertex3f(-halfW, halfH, 0);
            GlStateManager.glTexCoord2f(1, 1);
            GlStateManager.glVertex3f(halfW, halfH, 0);
            GlStateManager.glTexCoord2f(1, 0);
            GlStateManager.glVertex3f(halfW, -halfH, 0);
            GlStateManager.glEnd();
        }

        //Reset rotation if necessary
        if (direction == DIRECTION_TOP_TO_BOTTOM || direction == DIRECTION_BOTTOM_TO_TOP)
        {
            GlStateManager.popMatrix();
        }

        //Low-End Text
        if (!lowEndText.equals(""))
        {
            //Outline
            if (lowEndTextOutlineColor.af() > 0)
            {
                //TODO
            }

            //Text
            if (lowEndTextColor.af() > 0)
            {
                //TODO
            }
        }

        //High-End Text
        if (!highEndText.equals(""))
        {
            //Outline
            if (highEndTextOutlineColor.af() > 0)
            {
                //TODO
            }

            //Text
            if (highEndTextColor.af() > 0)
            {
                //TODO
            }
        }

        //Center Text
        if (!centerText.equals(""))
        {
            //Outline
            if (centerTextOutlineColor.af() > 0)
            {
                //TODO
            }

            //Text
            if (centerTextColor.af() > 0)
            {
                //TODO
            }
        }


        GlStateManager.popMatrix();
    }


    protected Double parseVal(String s)
    {
        switch (s.toLowerCase())
        {
            case "health":
                return (double) Minecraft.getMinecraft().player.getHealth();

            default:
                return null;
        }
    }


    @Override
    public void showEditingGUI(String name)
    {
        new BarEditingGUI(this, name).showStacked();
    }


    @Override
    public CBarElement write(ByteBuf buf)
    {
        super.write(buf);

        buf.writeInt(x);
        buf.writeInt(y);

        buf.writeDouble(hScale);
        buf.writeDouble(vScale);

        buf.writeInt(direction);

        ByteBufUtils.writeUTF8String(buf, backRL.toString());
        ByteBufUtils.writeUTF8String(buf, fillRL.toString());
        ByteBufUtils.writeUTF8String(buf, foreRL.toString());

        buf.writeInt(backColor.color());
        buf.writeInt(fillColor.color());
        buf.writeInt(foreColor.color());

        ByteBufUtils.writeUTF8String(buf, centerText);
        ByteBufUtils.writeUTF8String(buf, lowEndText);
        ByteBufUtils.writeUTF8String(buf, highEndText);

        buf.writeInt(centerTextColor.color());
        buf.writeInt(lowEndTextColor.color());
        buf.writeInt(highEndTextColor.color());

        buf.writeInt(centerTextOutlineColor.color());
        buf.writeInt(lowEndTextOutlineColor.color());
        buf.writeInt(highEndTextOutlineColor.color());

        buf.writeDouble(centerTextScale);
        buf.writeDouble(lowEndTextScale);
        buf.writeDouble(highEndTextScale);

        ByteBufUtils.writeUTF8String(buf, min);
        ByteBufUtils.writeUTF8String(buf, current);
        ByteBufUtils.writeUTF8String(buf, max);

        return this;
    }

    @Override
    public CBarElement read(ByteBuf buf)
    {
        super.read(buf);

        x = buf.readInt();
        y = buf.readInt();

        hScale = buf.readDouble();
        vScale = buf.readDouble();

        direction = buf.readInt();

        backRL = new ResourceLocation(ByteBufUtils.readUTF8String(buf));
        fillRL = new ResourceLocation(ByteBufUtils.readUTF8String(buf));
        foreRL = new ResourceLocation(ByteBufUtils.readUTF8String(buf));

        backColor = new Color(buf.readInt());
        fillColor = new Color(buf.readInt());
        foreColor = new Color(buf.readInt());

        centerText = ByteBufUtils.readUTF8String(buf);
        lowEndText = ByteBufUtils.readUTF8String(buf);
        highEndText = ByteBufUtils.readUTF8String(buf);

        centerTextColor = new Color(buf.readInt());
        lowEndTextColor = new Color(buf.readInt());
        highEndTextColor = new Color(buf.readInt());

        centerTextOutlineColor = new Color(buf.readInt());
        lowEndTextOutlineColor = new Color(buf.readInt());
        highEndTextOutlineColor = new Color(buf.readInt());

        centerTextScale = buf.readDouble();
        lowEndTextScale = buf.readDouble();
        highEndTextScale = buf.readDouble();

        min = ByteBufUtils.readUTF8String(buf);
        current = ByteBufUtils.readUTF8String(buf);
        max = ByteBufUtils.readUTF8String(buf);

        return this;
    }

    @Override
    public CBarElement save(OutputStream stream)
    {
        super.save(stream);

        CInt ci = new CInt().set(x).save(stream).set(y).save(stream);

        CDouble cd = new CDouble().set(hScale).save(stream).set(vScale).save(stream);

        ci.set(direction).save(stream);

        CStringUTF8 cs = new CStringUTF8().set(backRL.toString()).save(stream).set(fillRL.toString()).save(stream).set(foreRL.toString()).save(stream);

        ci.set(backColor.color()).save(stream).set(fillColor.color()).save(stream).set(foreColor.color()).save(stream);

        cs.set(centerText).save(stream).set(lowEndText).save(stream).set(highEndText).save(stream);

        ci.set(centerTextColor.color()).save(stream).set(lowEndTextColor.color()).save(stream).set(highEndTextColor.color()).save(stream);

        ci.set(centerTextOutlineColor.color()).save(stream).set(lowEndTextOutlineColor.color()).save(stream).set(highEndTextOutlineColor.color()).save(stream);

        cd.set(centerTextScale).save(stream).set(lowEndTextScale).save(stream).set(highEndTextScale).save(stream);

        cs.set(min).save(stream).set(current).save(stream).set(max).save(stream);

        return this;
    }

    @Override
    public CBarElement load(InputStream stream)
    {
        super.load(stream);

        CInt ci = new CInt();
        CDouble cd = new CDouble();
        CStringUTF8 cs = new CStringUTF8();

        x = ci.load(stream).value;
        y = ci.load(stream).value;

        hScale = cd.load(stream).value;
        vScale = cd.load(stream).value;

        direction = ci.load(stream).value;

        backRL = new ResourceLocation(cs.load(stream).value);
        fillRL = new ResourceLocation(cs.load(stream).value);
        foreRL = new ResourceLocation(cs.load(stream).value);

        backColor = new Color(ci.load(stream).value);
        fillColor = new Color(ci.load(stream).value);
        foreColor = new Color(ci.load(stream).value);

        centerText = cs.load(stream).value;
        lowEndText = cs.load(stream).value;
        highEndText = cs.load(stream).value;

        centerTextColor = new Color(ci.load(stream).value);
        lowEndTextColor = new Color(ci.load(stream).value);
        highEndTextColor = new Color(ci.load(stream).value);

        centerTextOutlineColor = new Color(ci.load(stream).value);
        lowEndTextOutlineColor = new Color(ci.load(stream).value);
        highEndTextOutlineColor = new Color(ci.load(stream).value);

        centerTextScale = cd.load(stream).value;
        lowEndTextScale = cd.load(stream).value;
        highEndTextScale = cd.load(stream).value;

        min = cs.load(stream).value;
        current = cs.load(stream).value;
        max = cs.load(stream).value;

        return this;
    }


    public static class BarEditingGUI extends GUIScreen
    {
        protected String name = "";

        public BarEditingGUI(CBarElement element, String name)
        {
            //Background
            root.add(new GUIDarkenedBackground(this));


            //Header
            GUINavbar navbar = new GUINavbar(this);
            root.add(navbar);


            GUILabeledBoolean enabled = new GUILabeledBoolean(this, "Enabled: ", element.enabled);

            GUILabeledTextInput x = new GUILabeledTextInput(this, "X: ", "" + element.x, FilterInt.INSTANCE);
            GUILabeledTextInput y = new GUILabeledTextInput(this, "Y: ", "" + element.y, FilterInt.INSTANCE);

            GUILabeledTextInput hScale = new GUILabeledTextInput(this, "Horizontal Scaling: ", "" + element.hScale, FilterFloat.INSTANCE);
            GUILabeledTextInput vScale = new GUILabeledTextInput(this, "Vertical Scaling: ", "" + element.vScale, FilterFloat.INSTANCE);

            GUIText directionLabel = new GUIText(this, "Direction: ");
            GUIText direction = new GUIText(this, DIRECTIONS_I_S.get(element.direction));
            directionLabel.linkMouseActivity(direction);
            direction.linkMouseActivity(directionLabel);

            GUILabeledTextInput backRL = new GUILabeledTextInput(this, "Background Texture: ", element.backRL.toString(), FilterResourceLocation.INSTANCE);
            GUILabeledTextInput fillRL = new GUILabeledTextInput(this, "Fill Texture: ", element.fillRL.toString(), FilterResourceLocation.INSTANCE);
            GUILabeledTextInput foreRL = new GUILabeledTextInput(this, "Foreground Texture: ", element.foreRL.toString(), FilterResourceLocation.INSTANCE);

            GUIColor backColor = new GUIColor(this, element.backColor);
            GUIColor fillColor = new GUIColor(this, element.fillColor);
            GUIColor foreColor = new GUIColor(this, element.foreColor);

            GUILabeledTextInput centerText = new GUILabeledTextInput(this, "Center Text: ", element.centerText, FilterNone.INSTANCE);
            GUILabeledTextInput lowEndText = new GUILabeledTextInput(this, "Low-End Text: ", element.lowEndText, FilterNone.INSTANCE);
            GUILabeledTextInput highEndText = new GUILabeledTextInput(this, "High-End Text: ", element.highEndText, FilterNone.INSTANCE);

            GUIColor centerTextColor = new GUIColor(this, element.centerTextColor);
            GUIColor lowEndTextColor = new GUIColor(this, element.lowEndTextColor);
            GUIColor highEndTextColor = new GUIColor(this, element.highEndTextColor);

            GUIColor centerTextOutlineColor = new GUIColor(this, element.centerTextOutlineColor);
            GUIColor lowEndTextOutlineColor = new GUIColor(this, element.lowEndTextOutlineColor);
            GUIColor highEndTextOutlineColor = new GUIColor(this, element.highEndTextOutlineColor);

            GUILabeledTextInput centerTextScale = new GUILabeledTextInput(this, "Center Text Scale: ", "" + element.centerTextScale, FilterFloat.INSTANCE);
            GUILabeledTextInput lowEndTextScale = new GUILabeledTextInput(this, "Low-End Text Scale: ", "" + element.lowEndTextScale, FilterFloat.INSTANCE);
            GUILabeledTextInput highEndTextScale = new GUILabeledTextInput(this, "High-End Text Scale: ", "" + element.highEndTextScale, FilterFloat.INSTANCE);

            GUILabeledTextInput min = new GUILabeledTextInput(this, "Minimum Value: ", element.min, FilterNotEmpty.INSTANCE);
            GUILabeledTextInput current = new GUILabeledTextInput(this, "Fill Value: ", element.current, FilterNotEmpty.INSTANCE);
            GUILabeledTextInput max = new GUILabeledTextInput(this, "Maximum Value: ", element.max, FilterNotEmpty.INSTANCE);


            root.addAll(
                    enabled.addClickActions(() -> element.enabled = enabled.getValue()),

                    new GUIElement(this, 1, 0),
                    new GUIElement(this, 1, 0),
                    x.addEditActions(() ->
                    {
                        if (x.valid()) element.x = FilterInt.INSTANCE.parse(x.getText());
                    }),
                    new GUIElement(this, 1, 0),
                    y.addEditActions(() ->
                    {
                        if (y.valid()) element.y = FilterInt.INSTANCE.parse(y.getText());
                    }),

                    new GUIElement(this, 1, 0),
                    new GUIElement(this, 1, 0),
                    hScale.addEditActions(() ->
                    {
                        if (hScale.valid()) element.hScale = FilterFloat.INSTANCE.parse(hScale.getText());
                    }),
                    new GUIElement(this, 1, 0),
                    vScale.addEditActions(() ->
                    {
                        if (vScale.valid()) element.vScale = FilterFloat.INSTANCE.parse(vScale.getText());
                    }),

                    new GUIElement(this, 1, 0),
                    new GUIElement(this, 1, 0),
                    directionLabel.addClickActions(direction::click),
                    direction.addClickActions(() -> new TextSelectionGUI(direction, "Direction", DIRECTIONS_S_I.keySet().toArray(new String[0]))),

                    new GUIElement(this, 1, 0),
                    new GUIElement(this, 1, 0),
                    backRL.addEditActions(() ->
                    {
                        if (backRL.valid()) element.backRL = FilterResourceLocation.INSTANCE.parse(backRL.getText());
                    }),
                    new GUIElement(this, 1, 0),
                    fillRL.addEditActions(() ->
                    {
                        if (fillRL.valid()) element.fillRL = FilterResourceLocation.INSTANCE.parse(fillRL.getText());
                    }),
                    new GUIElement(this, 1, 0),
                    foreRL.addEditActions(() ->
                    {
                        if (foreRL.valid()) element.foreRL = FilterResourceLocation.INSTANCE.parse(foreRL.getText());
                    }),

                    new GUIElement(this, 1, 0),
                    new GUIElement(this, 1, 0),
                    new GUIText(this, "Background Color: "),
                    backColor.addClickActions(() -> new ColorSelectionGUI(backColor).addOnClosedActions(() -> element.backColor = backColor.getValue())),
                    new GUIElement(this, 1, 0),
                    new GUIText(this, "Fill Color: "),
                    fillColor.addClickActions(() -> new ColorSelectionGUI(fillColor).addOnClosedActions(() -> element.fillColor = fillColor.getValue())),
                    new GUIElement(this, 1, 0),
                    new GUIText(this, "Foreground Color: "),
                    foreColor.addClickActions(() -> new ColorSelectionGUI(foreColor).addOnClosedActions(() -> element.foreColor = foreColor.getValue())),

                    new GUIElement(this, 1, 0),
                    new GUIElement(this, 1, 0),
                    centerText.addEditActions(() -> element.centerText = centerText.getText()),
                    new GUIElement(this, 1, 0),
                    lowEndText.addEditActions(() -> element.lowEndText = lowEndText.getText()),
                    new GUIElement(this, 1, 0),
                    highEndText.addEditActions(() -> element.highEndText = highEndText.getText()),

                    new GUIElement(this, 1, 0),
                    new GUIElement(this, 1, 0),
                    new GUIText(this, "Center Text Color: "),
                    centerTextColor.addClickActions(() -> new ColorSelectionGUI(centerTextColor).addOnClosedActions(() -> element.centerTextColor = centerTextColor.getValue())),
                    new GUIElement(this, 1, 0),
                    new GUIText(this, "Low-End Text Color: "),
                    lowEndTextColor.addClickActions(() -> new ColorSelectionGUI(lowEndTextColor).addOnClosedActions(() -> element.lowEndTextColor = lowEndTextColor.getValue())),
                    new GUIElement(this, 1, 0),
                    new GUIText(this, "High-End Text Color: "),
                    highEndTextColor.addClickActions(() -> new ColorSelectionGUI(highEndTextColor).addOnClosedActions(() -> element.highEndTextColor = highEndTextColor.getValue())),

                    new GUIElement(this, 1, 0),
                    new GUIElement(this, 1, 0),
                    new GUIText(this, "Center Text Color: "),
                    centerTextOutlineColor.addClickActions(() -> new ColorSelectionGUI(centerTextOutlineColor).addOnClosedActions(() -> element.centerTextOutlineColor = centerTextOutlineColor.getValue())),
                    new GUIElement(this, 1, 0),
                    new GUIText(this, "Low-End Text Color: "),
                    lowEndTextOutlineColor.addClickActions(() -> new ColorSelectionGUI(lowEndTextOutlineColor).addOnClosedActions(() -> element.lowEndTextOutlineColor = lowEndTextOutlineColor.getValue())),
                    new GUIElement(this, 1, 0),
                    new GUIText(this, "High-End Text Color: "),
                    highEndTextOutlineColor.addClickActions(() -> new ColorSelectionGUI(highEndTextOutlineColor).addOnClosedActions(() -> element.highEndTextOutlineColor = highEndTextOutlineColor.getValue())),

                    new GUIElement(this, 1, 0),
                    new GUIElement(this, 1, 0),
                    centerTextScale.addEditActions(() ->
                    {
                        if (centerTextScale.valid()) element.centerTextScale = FilterFloat.INSTANCE.parse(centerTextScale.getText());
                    }),
                    new GUIElement(this, 1, 0),
                    lowEndTextScale.addEditActions(() ->
                    {
                        if (lowEndTextScale.valid()) element.lowEndTextScale = FilterFloat.INSTANCE.parse(lowEndTextScale.getText());
                    }),
                    new GUIElement(this, 1, 0),
                    highEndTextScale.addEditActions(() ->
                    {
                        if (highEndTextScale.valid()) element.highEndTextScale = FilterFloat.INSTANCE.parse(highEndTextScale.getText());
                    }),

                    new GUIElement(this, 1, 0),
                    new GUIElement(this, 1, 0),
                    min.addEditActions(() ->
                    {
                        if (min.valid()) element.min = min.getText();
                    }),
                    new GUIElement(this, 1, 0),
                    current.addEditActions(() ->
                    {
                        if (current.valid()) element.current = current.getText();
                    }),
                    new GUIElement(this, 1, 0),
                    max.addEditActions(() ->
                    {
                        if (max.valid()) element.max = max.getText();
                    })
            );

        }

        @Override
        public String title()
        {
            return name + " (Bar)";
        }
    }
}
