package sample;

import escpos.Style;
import escpos.image.EscPosImage;
import escpos.image.ImageWrapperInterface;
import escpos.image.RasterBitImageWrapper;

import java.util.Objects;

public class PrintObjects {
    public static class PosImg {
        public EscPosImage escPosImage;
        public ImageWrapperInterface imageWrapperInterface;

        public PosImg(EscPosImage escPosImage) {
            this(escPosImage, null);
        }

        public PosImg(EscPosImage escPosImage, ImageWrapperInterface imageWrapperInterface) {
            this.escPosImage = escPosImage;
            this.imageWrapperInterface = Objects.requireNonNullElse(imageWrapperInterface, new RasterBitImageWrapper());
        }

        @Override
        public String toString() {
            return "PosImg{" +
                    "escPosImage=" + escPosImage +
                    ", imageWrapperInterface=" + imageWrapperInterface +
                    '}';
        }

    }

    public static class PosText {
        public String text;
        public Style escPosStyle;

        public PosText(String text) {
            this(text, null);
        }

        public PosText(String text, Style escPosStyle) {
            this.text = Objects.requireNonNullElse(text, "");
            this.escPosStyle = Objects.requireNonNullElseGet(escPosStyle, () -> {
                Style style = new Style();
                style.setBold(false);
                style.setFontName(Style.FontName.Font_B);
                style.setFontSize(Style.FontSize._4, Style.FontSize._4);
                return style;
            });
        }

        @Override
        public String toString() {
            return "PosText{" +
                    "text='" + text + '\'' +
                    ", escPosStyle=" + escPosStyle +
                    '}';
        }
    }
}
