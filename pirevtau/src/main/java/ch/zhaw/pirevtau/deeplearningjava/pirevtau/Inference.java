package ch.zhaw.pirevtau.deeplearningjava.pirevtau;

import ai.djl.Model;
import ai.djl.ModelException;
import ai.djl.inference.Predictor;
import ai.djl.modality.Classifications;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.modality.cv.transform.Resize;
import ai.djl.modality.cv.transform.ToTensor;
import ai.djl.modality.cv.translator.ImageClassificationTranslator;
import ai.djl.translate.TranslateException;
import ai.djl.translate.Translator;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

public class Inference {

    Predictor<Image, Classifications> predictor;
    
    public Inference() {
        try {
            Model model = Models.getModel();
            Path modelDir = Paths.get("models");
            System.out.println("Model directory: " + modelDir.toAbsolutePath());
            System.out.println("Model name: " + Models.MODEL_NAME);

            if (!modelDir.toFile().exists()) {
                throw new IOException("Model directory does not exist: " + modelDir.toAbsolutePath());
            }

            model.load(modelDir, Models.MODEL_NAME);
            System.out.println("Model loaded successfully.");

            // Define a translator for pre and post processing
            Translator<Image, Classifications> translator = ImageClassificationTranslator.builder()
                    .addTransform(new Resize(Models.IMAGE_WIDTH, Models.IMAGE_HEIGHT))
                    .addTransform(new ToTensor())
                    .optApplySoftmax(true)
                    .build();
            predictor = model.newPredictor(translator);
            System.out.println("Predictor initialized successfully.");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error during model loading: " + e.getMessage());
        }
    }

    public Classifications predict(byte[] image) throws ModelException, TranslateException, IOException {
        if (predictor == null) {
            throw new IllegalStateException("Predictor is not initialized.");
        }

        InputStream is = new ByteArrayInputStream(image);
        BufferedImage bi = ImageIO.read(is);
        Image img = ImageFactory.getInstance().fromImage(bi);

        Classifications predictResult = this.predictor.predict(img);
        return predictResult;
    }
}
