package com.ragemachine.moon.impl.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import java.awt.*;

/** Various functions to draw gui objects using GL11 and BufferBuilder.
 * @author Icovid | Icovid#3888
 * @since 1.0.0 **/
public class GlUtil {

    /** {@link GL11} scissor using minecraft screen positions.
     * @param xPosition X start location
     * @param yPosition Y start location
     * @param width scissor width
     * @param height scissor height **/
    public static void glScissor(double xPosition, double yPosition, double width, double height) {
        ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        GL11.glScissor((int) ((xPosition * Minecraft.getMinecraft().displayWidth) / scaledResolution.getScaledWidth()),
                (int) (((scaledResolution.getScaledHeight() - (yPosition + height)) * Minecraft.getMinecraft().displayHeight) / scaledResolution.getScaledHeight()),
                (int) (width * Minecraft.getMinecraft().displayWidth / scaledResolution.getScaledWidth()),
                (int) (height * Minecraft.getMinecraft().displayHeight / scaledResolution.getScaledHeight()));
    }

    /** Draw line on screen.
     * @param x x start location
     * @param y y start location
     * @param x1 x end location
     * @param y1 y end location
     * @param color color of line **/
    public static void drawLine(float x, float y, float x1, float y1, float width, Color color) {
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GL11.glLineWidth(width);
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        BufferBuilder worldrenderer = Tessellator.getInstance().getBuffer();
        GlStateManager.color((float)color.getRed() / 255.0F, (float)color.getGreen() / 255.0F, (float)color.getBlue() / 255.0F, (float)color.getAlpha() / 255.0F);
        worldrenderer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
        worldrenderer.pos(x, y, 0.0D).endVertex();
        worldrenderer.pos(x1, y1, 0.0D).endVertex();
        Tessellator.getInstance().draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GL11.glLineWidth(2.0F);
        GlStateManager.bindTexture(0);
        GlStateManager.color(1f, 1f, 1f, 1f);
    }

    /** Draw filled circle on screen.
     * @param xPosition x start location
     * @param yPosition y start location
     * @param radius radius of circle
     * @param color color of circle **/
    public static void drawCircle(float xPosition, float yPosition, float radius, Color color) {
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GlStateManager.color((float)color.getRed() / 255.0F, (float)color.getGreen() / 255.0F, (float)color.getBlue() / 255.0F, (float)color.getAlpha() / 255.0F);
        BufferBuilder worldRenderer = Tessellator.getInstance().getBuffer();
        worldRenderer.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION);
        worldRenderer.pos(xPosition, yPosition, 0).endVertex();
        for (int i = 0; i <= 100; i++) {
            double angle = (Math.PI * 2 * i / 100) + Math.toRadians(180);
            worldRenderer.pos(xPosition + Math.sin(angle) * radius, yPosition + Math.cos(angle) * radius, 0).endVertex();
        }
        Tessellator.getInstance().draw();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.bindTexture(0);
    }

    /** Draw hollow circle on screen.
     * @param xPosition x start location
     * @param yPosition y start location
     * @param radius radius of circle
     * @param color color of circle
     * @param thickness width of circle outline **/
    public static void drawHollowCircle(float xPosition, float yPosition, float radius, float thickness, Color color) {
        GlStateManager.pushMatrix();
        GlStateManager.color((float)color.getRed() / 255.0F, (float)color.getGreen() / 255.0F, (float)color.getBlue() / 255.0F, (float)color.getAlpha() / 255.0F);
        GL11.glEnable(2848);
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glEnable(3042);
        GL11.glLineWidth(thickness);
        GL11.glBegin(2);
        for (int i = 0; i < 70; i++) {
            float x = radius * MathHelper.cos((float) (i * 0.08975979010256552D));
            float y = radius * MathHelper.sin((float) (i * 0.08975979010256552D));
            GL11.glVertex2f(xPosition + x, yPosition + y);
        }
        GL11.glEnd();
        GL11.glDisable(2848);
        GL11.glEnable(3553);
        GlStateManager.popMatrix();
        GL11.glLineWidth(2.0F);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.bindTexture(0);
    }

    /** Same as {@link #drawCircle(float, float, float, Color)} accept can be confided
     * by start angle and end angle.
     * @param xPosition x start location
     * @param yPosition y start location
     * @param radius radius of angle
     * @param startAngle Start orientation of angle
     * @param endAngle end orientation of angle
     * @param color color of angle
     * @implNote Strait Angles - 90|180|270|360 **/
    public static void drawPartialCircle(float xPosition, float yPosition, float radius, int startAngle, int endAngle, Color color) {
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GlStateManager.color((float)color.getRed() / 255.0F, (float)color.getGreen() / 255.0F, (float)color.getBlue() / 255.0F, (float)color.getAlpha() / 255.0F);
        BufferBuilder worldRenderer = Tessellator.getInstance().getBuffer();
        worldRenderer.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION);
        worldRenderer.pos(xPosition, yPosition, 0).endVertex();
        for (int i = (int) (startAngle / 360.0 * 100); i <= (int) (endAngle / 360.0 * 100); i++) {
            double angle = (Math.PI * 2 * i / 100) + Math.toRadians(180);
            worldRenderer.pos(xPosition + Math.sin(angle) * radius, yPosition + Math.cos(angle) * radius, 0).endVertex();
        }
        Tessellator.getInstance().draw();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.bindTexture(0);
    }

    /** Same as {@link #drawHollowCircle(float, float, float, float, Color)} accept can be confided
     * by start angle and end angle.
     * @param xPosition x start location
     * @param yPosition y start location
     * @param radius radius of angle
     * @param startAngle Start orientation of angle
     * @param endAngle end orientation of angle
     * @param color color of angle
     * @param thickness width of circle outline
     * @implNote Strait Angles - 90|180|270|360 **/
    public static void drawHollowPartialCircle(float xPosition, float yPosition, float radius, int startAngle, int endAngle, float thickness, Color color) {
        GL11.glPushMatrix();
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glLineWidth(thickness);
        GlStateManager.color((float)color.getRed() / 255.0F, (float)color.getGreen() / 255.0F, (float)color.getBlue() / 255.0F, (float)color.getAlpha() / 255.0F);
        GL11.glBegin(3);
        float f1 = 0.017453292F;
        for(int j = startAngle; j <= endAngle; ++j) {
            float f = (float)(j - 90) * f1;
            GL11.glVertex2f(xPosition + (float)Math.cos((double)f) * radius, yPosition + (float)Math.sin((double)f) * radius);
        }
        GL11.glEnd();
        GL11.glEnable(3553);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glLineWidth(2.0F);
        GL11.glPopMatrix();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.bindTexture(0);
    }

    /** Draw rectangle on screen.
     * @param xPosition x start location
     * @param yPosition y start location
     * @param width width of rectangle
     * @param height height of rectangle
     * @param color color of rectangle **/
    public static void drawRectangle(float xPosition, float yPosition, float width, float height, Color color) {
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        BufferBuilder worldrenderer = Tessellator.getInstance().getBuffer();
        GlStateManager.color((float)color.getRed() / 255.0F, (float)color.getGreen() / 255.0F, (float)color.getBlue() / 255.0F, (float)color.getAlpha() / 255.0F);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos(xPosition, yPosition + height, 0.0D).endVertex();
        worldrenderer.pos(xPosition + width, yPosition + height, 0.0D).endVertex();
        worldrenderer.pos(xPosition + width, yPosition, 0.0D).endVertex();
        worldrenderer.pos(xPosition, yPosition, 0.0D).endVertex();
        Tessellator.getInstance().draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.bindTexture(0);
        GlStateManager.color(1f, 1f, 1f, 1f);
    }

    /** Same as {@link #drawRectangle(float, float, float, float, Color)} accept corners can be adjusts
     * by angle measure.
     * @param xPosition x location
     * @param yPosition y location
     * @param width width of rectangle
     * @param height height of rectangle
     * @param angle angle of rectangle corners
     * @param color color of rectangle **/
    public static void drawRoundedRectangle(float xPosition, float yPosition, float width, float height, float angle, Color color) {
        drawPartialCircle(xPosition + angle, yPosition + angle, angle, 0, 90, color);
        drawPartialCircle(xPosition + width - angle, yPosition + angle, angle, 270, 360, color);
        drawPartialCircle(xPosition + width - angle, yPosition + height - angle, angle, 180, 270, color);
        drawPartialCircle(xPosition + angle, yPosition + height - angle, angle, 90, 180, color);
        drawRectangle(xPosition + angle, yPosition, width - (angle * 2), angle, color);
        drawRectangle(xPosition + angle, yPosition + height - angle, width - (angle * 2), angle, color);
        drawRectangle(xPosition, yPosition + angle, width, height - (angle * 2), color);
    }

    /** Same as {@link #drawRectangle(float, float, float, float, Color)} but with a border surrounding it
     * @param xPosition x start location
     * @param yPosition y start location
     * @param width width of rectangle
     * @param height height of rectangle
     * @param borderWidth width of rectangle border
     * @param color color of rectangle
     * @param borderColor color of rectangle border **/
    public static void drawBorderedRectangle(float xPosition, float yPosition, float width, float height, float borderWidth, Color color, Color borderColor) {
        drawRectangle(xPosition, yPosition, width, height, color);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glPushMatrix();
        GlStateManager.color((float)borderColor.getRed() / 255.0F, (float)borderColor.getGreen() / 255.0F, (float)borderColor.getBlue() / 255.0F, (float)borderColor.getAlpha() / 255.0F);
        GL11.glLineWidth(borderWidth);
        GL11.glBegin(1);
        GL11.glVertex2d(xPosition, yPosition);
        GL11.glVertex2d(xPosition, (yPosition + height));
        GL11.glVertex2d((xPosition + width), (yPosition + height));
        GL11.glVertex2d((xPosition + width), yPosition);
        GL11.glVertex2d(xPosition, yPosition);
        GL11.glVertex2d((xPosition + width), yPosition);
        GL11.glVertex2d(xPosition, (yPosition + height));
        GL11.glVertex2d((xPosition + width), (yPosition + height));
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
    }

    /** Draw textured rectangle on screen.
     * @param resourceLocation ResourceLocation of texture
     * @param xPosition x start location
     * @param yPosition y start location
     * @param width width of rectangle
     * @param height height of rectangle **/
    public static void drawTexturedRectangle(ResourceLocation resourceLocation, float xPosition, float yPosition, float width, float height) {
        GL11.glEnable(GL11.GL_BLEND);
        Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2d(1, 1);
        GL11.glVertex2d(xPosition, yPosition);
        GL11.glTexCoord2d(1, 1);
        GL11.glVertex2d(xPosition, yPosition + height);
        GL11.glTexCoord2d(1, 1);
        GL11.glVertex2d(xPosition + width, yPosition + height);
        GL11.glTexCoord2d(1, 1);
        GL11.glVertex2d(xPosition + width, yPosition);
        GL11.glEnd();
        GL11.glDisable(GL11.GL_BLEND);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.bindTexture(0);
    }

    /** Draw polygon based on number of sides.
     * @param xPosition x start location
     * @param yPosition y start location
     * @param radius radius of polygon
     * @param sides number of sides in polygon
     * @param color color of polygon **/
    public static void drawRegularPolygon(float xPosition, float yPosition, float radius, float sides, Color color) {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        BufferBuilder worldrenderer = Tessellator.getInstance().getBuffer();
        GlStateManager.color((float)color.getRed() / 255.0F, (float)color.getGreen() / 255.0F, (float)color.getBlue() / 255.0F, (float)color.getAlpha() / 255.0F);
        worldrenderer.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION);
        worldrenderer.pos(xPosition, yPosition, 0).endVertex();
        for(int i = 0; i <= sides ;i++) {
            double angle = ((Math.PI*2) * i / sides) + Math.toRadians(180);
            worldrenderer.pos(xPosition + Math.sin(angle) * radius, yPosition + Math.cos(angle) * radius, 0).endVertex();
        }
        Tessellator.getInstance().draw();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GlStateManager.bindTexture(0);
        GlStateManager.color(1f, 1f, 1f, 1f);
    }
}
