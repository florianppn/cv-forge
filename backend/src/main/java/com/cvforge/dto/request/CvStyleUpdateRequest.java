package com.cvforge.dto.request;

import com.cvforge.domain.CvStyle;
import jakarta.validation.constraints.Pattern;

import java.util.List;

public class CvStyleUpdateRequest {

    @Pattern(regexp = "^#([A-Fa-f0-9]{6})$", message = "La couleur d'accent doit être un code hexadécimal valide (ex: #1a365d)")
    private String primaryColor;

    private String fontFamily;

    private CvStyle.Density density;

    private CvStyle.BulletStyle bulletStyle;

    private List<String> sectionOrder;

    private Boolean showPhoto;

    public CvStyleUpdateRequest() {}

    public CvStyleUpdateRequest(String primaryColor, String fontFamily, CvStyle.Density density, CvStyle.BulletStyle bulletStyle, List<String> sectionOrder, Boolean showPhoto) {
        this.primaryColor = primaryColor;
        this.fontFamily = fontFamily;
        this.density = density;
        this.bulletStyle = bulletStyle;
        this.sectionOrder = sectionOrder;
        this.showPhoto = showPhoto;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String primaryColor;
        private String fontFamily;
        private CvStyle.Density density;
        private CvStyle.BulletStyle bulletStyle;
        private List<String> sectionOrder;
        private Boolean showPhoto;

        public Builder primaryColor(String primaryColor) { this.primaryColor = primaryColor; return this; }
        public Builder fontFamily(String fontFamily) { this.fontFamily = fontFamily; return this; }
        public Builder density(CvStyle.Density density) { this.density = density; return this; }
        public Builder bulletStyle(CvStyle.BulletStyle bulletStyle) { this.bulletStyle = bulletStyle; return this; }
        public Builder sectionOrder(List<String> sectionOrder) { this.sectionOrder = sectionOrder; return this; }
        public Builder showPhoto(Boolean showPhoto) { this.showPhoto = showPhoto; return this; }
        public CvStyleUpdateRequest build() {
            return new CvStyleUpdateRequest(primaryColor, fontFamily, density, bulletStyle, sectionOrder, showPhoto);
        }
    }

    public String getPrimaryColor() { return primaryColor; }
    public void setPrimaryColor(String primaryColor) { this.primaryColor = primaryColor; }

    public String getFontFamily() { return fontFamily; }
    public void setFontFamily(String fontFamily) { this.fontFamily = fontFamily; }

    public CvStyle.Density getDensity() { return density; }
    public void setDensity(CvStyle.Density density) { this.density = density; }

    public CvStyle.BulletStyle getBulletStyle() { return bulletStyle; }
    public void setBulletStyle(CvStyle.BulletStyle bulletStyle) { this.bulletStyle = bulletStyle; }

    public List<String> getSectionOrder() { return sectionOrder; }
    public void setSectionOrder(List<String> sectionOrder) { this.sectionOrder = sectionOrder; }

    public Boolean getShowPhoto() { return showPhoto; }
    public void setShowPhoto(Boolean showPhoto) { this.showPhoto = showPhoto; }
}
