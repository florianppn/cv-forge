package com.cvforge.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CvStyle implements Serializable {

    private String primaryColor = "#1a365d";
    private String fontFamily = "Helvetica";
    private Density density = Density.NORMAL;
    private BulletStyle bulletStyle = BulletStyle.DISC;
    private List<String> sectionOrder = new ArrayList<>(Arrays.asList("SUMMARY", "EXPERIENCE", "EDUCATION", "SKILLS"));
    private Boolean showPhoto = true;

    public enum Density {
        COMPACT,
        NORMAL,
        SPACIOUS
    }

    public enum BulletStyle {
        DISC,
        CIRCLE,
        SQUARE,
        HYPHEN
    }

    public CvStyle() {}

    public CvStyle(String primaryColor, String fontFamily, Density density, BulletStyle bulletStyle, List<String> sectionOrder, Boolean showPhoto) {
        this.primaryColor = primaryColor != null ? primaryColor : "#1a365d";
        this.fontFamily = fontFamily != null ? fontFamily : "Helvetica";
        this.density = density != null ? density : Density.NORMAL;
        this.bulletStyle = bulletStyle != null ? bulletStyle : BulletStyle.DISC;
        this.sectionOrder = sectionOrder != null ? sectionOrder : new ArrayList<>(Arrays.asList("SUMMARY", "EXPERIENCE", "EDUCATION", "SKILLS"));
        this.showPhoto = showPhoto != null ? showPhoto : true;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String primaryColor = "#1a365d";
        private String fontFamily = "Helvetica";
        private Density density = Density.NORMAL;
        private BulletStyle bulletStyle = BulletStyle.DISC;
        private List<String> sectionOrder = new ArrayList<>(Arrays.asList("SUMMARY", "EXPERIENCE", "EDUCATION", "SKILLS"));
        private Boolean showPhoto = true;

        public Builder primaryColor(String primaryColor) { this.primaryColor = primaryColor; return this; }
        public Builder fontFamily(String fontFamily) { this.fontFamily = fontFamily; return this; }
        public Builder density(Density density) { this.density = density; return this; }
        public Builder bulletStyle(BulletStyle bulletStyle) { this.bulletStyle = bulletStyle; return this; }
        public Builder sectionOrder(List<String> sectionOrder) { this.sectionOrder = sectionOrder; return this; }
        public Builder showPhoto(Boolean showPhoto) { this.showPhoto = showPhoto; return this; }
        public CvStyle build() {
            return new CvStyle(primaryColor, fontFamily, density, bulletStyle, sectionOrder, showPhoto);
        }
    }

    public static CvStyle defaultStyle() {
        return builder().build();
    }

    public String getPrimaryColor() { return primaryColor; }
    public void setPrimaryColor(String primaryColor) { this.primaryColor = primaryColor; }

    public String getFontFamily() { return fontFamily; }
    public void setFontFamily(String fontFamily) { this.fontFamily = fontFamily; }

    public Density getDensity() { return density; }
    public void setDensity(Density density) { this.density = density; }

    public BulletStyle getBulletStyle() { return bulletStyle; }
    public void setBulletStyle(BulletStyle bulletStyle) { this.bulletStyle = bulletStyle; }

    public List<String> getSectionOrder() { return sectionOrder; }
    public void setSectionOrder(List<String> sectionOrder) { this.sectionOrder = sectionOrder; }

    public Boolean getShowPhoto() { return showPhoto; }
    public void setShowPhoto(Boolean showPhoto) { this.showPhoto = showPhoto; }
}
