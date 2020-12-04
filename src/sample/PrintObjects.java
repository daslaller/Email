package sample;

import escpos.Style;
import escpos.image.EscPosImage;
import escpos.image.ImageWrapperInterface;
import escpos.image.RasterBitImageWrapper;

public class PrintObjects {

   public static Style CURRENT_DEFAULT_STYLE = new Style();
   static {
       CURRENT_DEFAULT_STYLE.setBold(true);
       CURRENT_DEFAULT_STYLE.setFontSize(Style.FontSize._6, Style.FontSize._7);
   }
    public static class PosImg {
        public EscPosImage escPosImage;
        public ImageWrapperInterface imageWrapperInterface;

        public PosImg(EscPosImage escPosImage) {
            this(escPosImage, null);
        }

        public PosImg(EscPosImage escPosImage, ImageWrapperInterface imageWrapperInterface) {
            this.escPosImage = escPosImage;
            this.imageWrapperInterface = (imageWrapperInterface == null ? new RasterBitImageWrapper() : imageWrapperInterface);
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
        public String text = "";
        public Style escPosStyle;

        public PosText(String text, Style newStyle) {
            this.text = text;
            this.escPosStyle = newStyle;
        }
        public PosText(String text){
            this(text, CURRENT_DEFAULT_STYLE);
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
