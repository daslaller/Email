package sample;

import escpos.image.Bitonal;
import escpos.image.BitonalOrderedDither;
import escpos.image.BitonalThreshold;
import escpos.image.EscPosImage;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;

import java.awt.image.BufferedImage;

@SuppressWarnings("SpellCheckingInspection")
public enum BitonalEnum {
    BITONAL(new BitonalThreshold()),
    BITONAL_DARKEN(new BitonalThreshold(50)),
    BITONAL_STRAIGHT(new BitonalThreshold(100)),
    BITONAL_LIGHTEN(new BitonalThreshold(150)),
    BITONAL_DITHER(new BitonalOrderedDither()),
    BITONAL_DITHER_DARKEN(new BitonalOrderedDither(2, 2, 20, 130)),
    BITONAL_DITHER_STRAIGHT(new BitonalOrderedDither(2, 2, 120, 170)),
    BITONAL_DITHER_LIGHTEN(new BitonalOrderedDither(3, 3, 100, 130)),
    BITONAL_DITHER_MATRIX(new BitonalOrderedDither(3, 3)) {
        @Override
        public EscPosImage image(BufferedImage image) {
            System.out.println("Appending dither matrix!");
            int[][] ditherMatrix = new int[][]{
                    {100, 130, 100},
                    {130, 0, 130},
                    {100, 130, 100},};
            if (bitonal() instanceof BitonalOrderedDither) {
                ((BitonalOrderedDither) bitonal()).setDitherMatrix(ditherMatrix);
            }
            return super.image(image);
        }
    };

    private Bitonal bitonalThreshold;

    BitonalEnum(Bitonal bitonalThreshold) {
        this.bitonalThreshold = bitonalThreshold;
    }

    public EscPosImage image(Image image) {
        
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, new BufferedImage((int) image.getWidth(), (int) image.getHeight(), BufferedImage.TYPE_INT_ARGB));
        PixelFormat.Type type = image.getPixelReader().getPixelFormat().getType();
        System.out.println("Type: " + type + " ordinal" + type.ordinal() + " complete, " + BufferedImage.TYPE_INT_ARGB + "(ARGB)");
        return image(bufferedImage);
    }

    public EscPosImage image(BufferedImage image) {
        return new EscPosImage(image, bitonal());
    }

    public Bitonal bitonal() {
        return bitonalThreshold;
    }
}
