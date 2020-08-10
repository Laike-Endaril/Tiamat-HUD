package com.fantasticsource.tiamathud.hudelement;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.mctools.OutlinedFontRenderer;
import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.*;
import com.fantasticsource.mctools.gui.element.text.filter.*;
import com.fantasticsource.mctools.gui.element.view.GUIScrollView;
import com.fantasticsource.mctools.gui.screen.ColorSelectionGUI;
import com.fantasticsource.mctools.gui.screen.TextSelectionGUI;
import com.fantasticsource.tools.PNG;
import com.fantasticsource.tools.Tools;
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
            DEFAULT_FORE_RL = null;

    public static final PNG
            DEFAULT_BACK_PNG = MCTools.getPNG(DEFAULT_BACK_RL),
            DEFAULT_FILL_PNG = MCTools.getPNG(DEFAULT_FILL_RL),
            DEFAULT_FORE_PNG = null;

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

    public double hScale = 1, vScale = 1;
    public int direction = DIRECTION_LEFT_TO_RIGHT;

    protected ResourceLocation backRL = DEFAULT_BACK_RL, fillRL = DEFAULT_FILL_RL, foreRL = DEFAULT_FORE_RL;
    protected PNG backPNG = DEFAULT_BACK_PNG, fillPNG = DEFAULT_FILL_PNG, forePNG = DEFAULT_FORE_PNG;
    public Color backColor = Color.WHITE, fillColor = Color.GREEN, foreColor = Color.WHITE;

    public String text = "@current/@max";
    public Color textColor = Color.WHITE, textOutlineColor = Color.BLACK;
    public double textScale = 1;

    public String min = "0", current = "health", max = "generic.maxHealth";


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
        backPNG = backRL == null ? null : MCTools.getPNG(backRL);
    }

    public void setFillRL(ResourceLocation fillRL)
    {
        this.fillRL = fillRL;
        fillPNG = fillRL == null ? null : MCTools.getPNG(fillRL);

        error = false;
    }

    public void setForeRL(ResourceLocation foreRL)
    {
        this.foreRL = foreRL;
        forePNG = foreRL == null ? null : MCTools.getPNG(foreRL);
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
        GlStateManager.scale(hScale, vScale, 1);
        if (direction == DIRECTION_TOP_TO_BOTTOM || direction == DIRECTION_BOTTOM_TO_TOP) GlStateManager.rotate(90, 0, 0, -1);


        //Min, current, and max value computation
        Double min, current, max;
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
                current = Double.parseDouble(this.current);
            }
            catch (NumberFormatException e)
            {
                current = MCTools.getAttribute(Minecraft.getMinecraft().player, this.current);
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


        //Background
        if (backPNG != null && backColor.af() > 0)
        {
            textureManager.bindTexture(backRL);
            GlStateManager.color(backColor.rf(), backColor.gf(), backColor.bf(), backColor.af());

            int halfW = backPNG.getWidth() >>> 1, halfH = backPNG.getHeight() >>> 1;
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

        //Fill
        if (fillPNG != null && fillColor.af() > 0 && min != null && current != null && max != null && max - min != 0)
        {
            float fillPercent = Tools.min(1, (float) ((current - min) / (max - min)));

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


        GlStateManager.popMatrix();


        //Text
        if (!text.equals("") && (textColor.af() > 0 || textOutlineColor.af() > 0))
        {
            GlStateManager.scale(textScale, textScale, 1);
            String text = this.text.replaceAll("[@][mM][iI][nN]", min == null ? "null" : String.format("%.1f", min));
            text = text.replaceAll("[@][cC][uU][rR][rR][eE][nN][tT]", current == null ? "null" : String.format("%.1f", current));
            text = text.replaceAll("[@][mM][aA][xX]", max == null ? "null" : String.format("%.1f", max));
            text = text.replaceAll("[@][lL][eE][vV][eE][lL]", "" + Minecraft.getMinecraft().player.experienceLevel);
            text = text.replaceAll("[@][pP][eE][rR][cC][eE][nN][tT]", min == null || current == null || max == null ? "N/A%" : "" + Math.floor(100 * (current - min) / (max - min)) + "%");
            OutlinedFontRenderer.draw(text, -(OutlinedFontRenderer.getStringWidth(text) >>> 1), -(OutlinedFontRenderer.LINE_HEIGHT >>> 1), textColor, textOutlineColor);
        }
    }


    protected Double parseVal(String s)
    {
        switch (s.toLowerCase())
        {
            case "health":
                return (double) Minecraft.getMinecraft().player.getHealth();

            case "food":
                return (double) Minecraft.getMinecraft().player.getFoodStats().getFoodLevel();

            case "saturation":
                return (double) Minecraft.getMinecraft().player.getFoodStats().getSaturationLevel();

            case "breath":
                return (double) Minecraft.getMinecraft().player.getAir();

            case "exp":
                return (double) Minecraft.getMinecraft().player.experience;

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

        buf.writeDouble(hScale);
        buf.writeDouble(vScale);

        buf.writeInt(direction);

        ByteBufUtils.writeUTF8String(buf, backRL == null ? "" : backRL.toString());
        ByteBufUtils.writeUTF8String(buf, fillRL == null ? "" : fillRL.toString());
        ByteBufUtils.writeUTF8String(buf, foreRL == null ? "" : foreRL.toString());

        buf.writeInt(backColor.color());
        buf.writeInt(fillColor.color());
        buf.writeInt(foreColor.color());

        ByteBufUtils.writeUTF8String(buf, text);
        buf.writeInt(textColor.color());
        buf.writeInt(textOutlineColor.color());
        buf.writeDouble(textScale);

        ByteBufUtils.writeUTF8String(buf, min);
        ByteBufUtils.writeUTF8String(buf, current);
        ByteBufUtils.writeUTF8String(buf, max);

        return this;
    }

    @Override
    public CBarElement read(ByteBuf buf)
    {
        super.read(buf);

        hScale = buf.readDouble();
        vScale = buf.readDouble();

        direction = buf.readInt();

        String s = ByteBufUtils.readUTF8String(buf);
        setBackRL(s.equals("") ? null : new ResourceLocation(s));
        s = ByteBufUtils.readUTF8String(buf);
        setFillRL(s.equals("") ? null : new ResourceLocation(s));
        s = ByteBufUtils.readUTF8String(buf);
        setForeRL(s.equals("") ? null : new ResourceLocation(s));

        backColor = new Color(buf.readInt());
        fillColor = new Color(buf.readInt());
        foreColor = new Color(buf.readInt());

        text = ByteBufUtils.readUTF8String(buf);
        textColor = new Color(buf.readInt());
        textOutlineColor = new Color(buf.readInt());
        textScale = buf.readDouble();

        min = ByteBufUtils.readUTF8String(buf);
        current = ByteBufUtils.readUTF8String(buf);
        max = ByteBufUtils.readUTF8String(buf);

        return this;
    }

    @Override
    public CBarElement save(OutputStream stream)
    {
        super.save(stream);

        CDouble cd = new CDouble().set(hScale).save(stream).set(vScale).save(stream);

        CInt ci = new CInt().set(direction).save(stream);

        CStringUTF8 cs = new CStringUTF8().set(backRL == null ? "" : backRL.toString()).save(stream).set(fillRL == null ? "" : fillRL.toString()).save(stream).set(foreRL == null ? "" : foreRL.toString()).save(stream);

        ci.set(backColor.color()).save(stream).set(fillColor.color()).save(stream).set(foreColor.color()).save(stream);

        cs.set(text).save(stream);
        ci.set(textColor.color()).save(stream);
        ci.set(textOutlineColor.color()).save(stream);
        cd.set(textScale).save(stream);

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

        hScale = cd.load(stream).value;
        vScale = cd.load(stream).value;

        direction = ci.load(stream).value;

        String s = cs.load(stream).value;
        setBackRL(s.equals("") ? null : new ResourceLocation(s));
        s = cs.load(stream).value;
        setFillRL(s.equals("") ? null : new ResourceLocation(s));
        s = cs.load(stream).value;
        setForeRL(s.equals("") ? null : new ResourceLocation(s));

        backColor = new Color(ci.load(stream).value);
        fillColor = new Color(ci.load(stream).value);
        foreColor = new Color(ci.load(stream).value);

        text = cs.load(stream).value;
        textColor = new Color(ci.load(stream).value);
        textOutlineColor = new Color(ci.load(stream).value);
        textScale = cd.load(stream).value;

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
            this.name = name;
            drawStack = false;


            //Background
            root.add(new GUIDarkenedBackground(this));


            //Header
            GUINavbar navbar = new GUINavbar(this);
            root.add(navbar);


            GUIScrollView scrollView = new GUIScrollView(this, 0.98, 1 - navbar.height);
            GUIVerticalScrollbar scrollbar = new GUIVerticalScrollbar(this, 0.02, 1 - navbar.height, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, scrollView);
            root.addAll(scrollView, scrollbar);


            GUILabeledBoolean enabled = new GUILabeledBoolean(this, "Enabled: ", element.enabled);
            enabled.input.addClickActions(() -> element.enabled = enabled.getValue());

            GUILabeledTextInput xOffset = new GUILabeledTextInput(this, "X Offset: ", "" + element.xOffset, FilterInt.INSTANCE);
            GUILabeledTextInput yOffset = new GUILabeledTextInput(this, "Y Offset: ", "" + element.yOffset, FilterInt.INSTANCE);

            GUIText xAnchorLabel = new GUIText(this, "X Anchor: ");
            GUIText xAnchor = new GUIText(this, X_ANCHORS_I_S.get(element.xAnchor), getIdleColor(Color.WHITE), getHoverColor(Color.WHITE), Color.WHITE);
            xAnchorLabel.linkMouseActivity(xAnchor);
            xAnchor.linkMouseActivity(xAnchorLabel);

            GUIText yAnchorLabel = new GUIText(this, "Y Anchor: ");
            GUIText yAnchor = new GUIText(this, Y_ANCHORS_I_S.get(element.yAnchor), getIdleColor(Color.WHITE), getHoverColor(Color.WHITE), Color.WHITE);
            yAnchorLabel.linkMouseActivity(yAnchor);
            yAnchor.linkMouseActivity(yAnchorLabel);

            GUILabeledTextInput hScale = new GUILabeledTextInput(this, "Horizontal Scaling: ", "" + element.hScale, FilterFloat.INSTANCE);
            GUILabeledTextInput vScale = new GUILabeledTextInput(this, "Vertical Scaling: ", "" + element.vScale, FilterFloat.INSTANCE);

            GUIText directionLabel = new GUIText(this, "Direction: ");
            GUIText direction = new GUIText(this, DIRECTIONS_I_S.get(element.direction), getIdleColor(Color.WHITE), getHoverColor(Color.WHITE), Color.WHITE);
            directionLabel.linkMouseActivity(direction);
            direction.linkMouseActivity(directionLabel);

            GUILabeledTextInput backRL = new GUILabeledTextInput(this, "Background Texture: ", element.backRL == null ? "" : element.backRL.toString(), FilterNullableResourceLocation.INSTANCE);
            GUILabeledTextInput fillRL = new GUILabeledTextInput(this, "Fill Texture: ", element.fillRL == null ? "" : element.fillRL.toString(), FilterNullableResourceLocation.INSTANCE);
            GUILabeledTextInput foreRL = new GUILabeledTextInput(this, "Foreground Texture: ", element.foreRL == null ? "" : element.foreRL.toString(), FilterNullableResourceLocation.INSTANCE);

            GUIColor backColor = new GUIColor(this, element.backColor);
            GUIColor fillColor = new GUIColor(this, element.fillColor);
            GUIColor foreColor = new GUIColor(this, element.foreColor);

            GUILabeledTextInput text = new GUILabeledTextInput(this, "Text: ", element.text, FilterNone.INSTANCE);
            GUIColor textColor = new GUIColor(this, element.textColor);
            GUIColor textOutlineColor = new GUIColor(this, element.textOutlineColor);
            GUILabeledTextInput textScale = new GUILabeledTextInput(this, "Text Scale: ", "" + element.textScale, FilterFloat.INSTANCE);

            GUILabeledTextInput min = new GUILabeledTextInput(this, "Minimum Value: ", element.min, FilterNotEmpty.INSTANCE);
            GUILabeledTextInput current = new GUILabeledTextInput(this, "Fill Value: ", element.current, FilterNotEmpty.INSTANCE);
            GUILabeledTextInput max = new GUILabeledTextInput(this, "Maximum Value: ", element.max, FilterNotEmpty.INSTANCE);


            scrollView.addAll(
                    enabled,

                    new GUITextSpacer(this),
                    xAnchorLabel.addClickActions(xAnchor::click),
                    xAnchor.addClickActions(() -> new TextSelectionGUI(xAnchor, "X Anchor", X_ANCHORS_S_I.keySet().toArray(new String[0])).addOnClosedActions(() -> element.xAnchor = X_ANCHORS_S_I.get(xAnchor.getText()))),
                    new GUIElement(this, 1, 0),
                    yAnchorLabel.addClickActions(yAnchor::click),
                    yAnchor.addClickActions(() -> new TextSelectionGUI(yAnchor, "Y Anchor", Y_ANCHORS_S_I.keySet().toArray(new String[0])).addOnClosedActions(() -> element.yAnchor = Y_ANCHORS_S_I.get(yAnchor.getText()))),

                    new GUITextSpacer(this),
                    xOffset.addEditActions(() ->
                    {
                        if (xOffset.valid()) element.xOffset = FilterInt.INSTANCE.parse(xOffset.getText());
                    }),
                    new GUIElement(this, 1, 0),
                    yOffset.addEditActions(() ->
                    {
                        if (yOffset.valid()) element.yOffset = FilterInt.INSTANCE.parse(yOffset.getText());
                    }),

                    new GUITextSpacer(this),
                    hScale.addEditActions(() ->
                    {
                        if (hScale.valid()) element.hScale = FilterFloat.INSTANCE.parse(hScale.getText());
                    }),
                    new GUIElement(this, 1, 0),
                    vScale.addEditActions(() ->
                    {
                        if (vScale.valid()) element.vScale = FilterFloat.INSTANCE.parse(vScale.getText());
                    }),

                    new GUITextSpacer(this),
                    directionLabel.addClickActions(direction::click),
                    direction.addClickActions(() -> new TextSelectionGUI(direction, "Direction", DIRECTIONS_S_I.keySet().toArray(new String[0])).addOnClosedActions(() -> element.direction = DIRECTIONS_S_I.get(direction.getText()))),

                    new GUITextSpacer(this),
                    backRL.addEditActions(() ->
                    {
                        if (backRL.valid()) element.backRL = FilterNullableResourceLocation.INSTANCE.parse(backRL.getText());
                    }),
                    new GUIElement(this, 1, 0),
                    fillRL.addEditActions(() ->
                    {
                        if (fillRL.valid()) element.fillRL = FilterNullableResourceLocation.INSTANCE.parse(fillRL.getText());
                    }),
                    new GUIElement(this, 1, 0),
                    foreRL.addEditActions(() ->
                    {
                        if (foreRL.valid()) element.foreRL = FilterNullableResourceLocation.INSTANCE.parse(foreRL.getText());
                    }),

                    new GUITextSpacer(this),
                    new GUIText(this, "Background Color: "),
                    backColor.addClickActions(() -> new ColorSelectionGUI(backColor).addOnClosedActions(() -> element.backColor = backColor.getValue())),
                    new GUIElement(this, 1, 0),
                    new GUIText(this, "Fill Color: "),
                    fillColor.addClickActions(() -> new ColorSelectionGUI(fillColor).addOnClosedActions(() -> element.fillColor = fillColor.getValue())),
                    new GUIElement(this, 1, 0),
                    new GUIText(this, "Foreground Color: "),
                    foreColor.addClickActions(() -> new ColorSelectionGUI(foreColor).addOnClosedActions(() -> element.foreColor = foreColor.getValue())),

                    new GUITextSpacer(this),
                    text.addEditActions(() -> element.text = text.getText()),
                    new GUIElement(this, 1, 0),
                    new GUIText(this, "Text Color: "),
                    textColor.addClickActions(() -> new ColorSelectionGUI(textColor).addOnClosedActions(() -> element.textColor = textColor.getValue())),
                    new GUIElement(this, 1, 0),
                    new GUIText(this, "Text Outline Color: "),
                    textOutlineColor.addClickActions(() -> new ColorSelectionGUI(textOutlineColor).addOnClosedActions(() -> element.textOutlineColor = textOutlineColor.getValue())),
                    new GUIElement(this, 1, 0),
                    textScale.addEditActions(() ->
                    {
                        if (textScale.valid()) element.textScale = FilterFloat.INSTANCE.parse(textScale.getText());
                    }),

                    new GUITextSpacer(this),
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


            //Add recalc actions
            navbar.addRecalcActions(() ->
            {
                scrollView.height = 1 - navbar.height;
                scrollbar.height = 1 - navbar.height;
            });
        }

        @Override
        public String title()
        {
            return name + " (Bar)";
        }
    }
}
