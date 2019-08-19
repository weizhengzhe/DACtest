import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import components.naturalnumber.NaturalNumber;
import components.naturalnumber.NaturalNumber2;

/**
 * Controller class.
 *
 * @author Zhengzhe Wei
 */
public final class NNCalcController1 implements NNCalcController {

    /**
     * Model object.
     */
    private final NNCalcModel model;

    /**
     * View object.
     */
    private final NNCalcView view;

    /**
     * Useful constants.
     */
    private static final NaturalNumber TWO = new NaturalNumber2(2),
            INT_LIMIT = new NaturalNumber2(Integer.MAX_VALUE);

    /**
     * Updates this.view to display this.model, and to allow only operations
     * that are legal given this.model.
     *
     * @param model
     *            the model
     * @param view
     *            the view
     * @ensures [view has been updated to be consistent with model]
     */
    private static void updateViewToMatchModel(NNCalcModel model,
            NNCalcView view) {

        NaturalNumber topN = model.top();
        NaturalNumber bottomN = model.bottom();

    }

    static double[] recordSample() {
        AudioFormat fmt = new AudioFormat(44100f, 16, 1, true, false);
        final int bufferByteSize = 88200;//2048;
        double[] samples = new double[bufferByteSize / 2];
        TargetDataLine line;
        try {
            line = AudioSystem.getTargetDataLine(fmt);
            line.open(fmt, bufferByteSize);
        } catch (LineUnavailableException e) {
            System.err.println(e);
            return samples;
        }

        byte[] buf = new byte[bufferByteSize];

        float lastPeak = 0f;

        line.start();
        int lengthRead = line.read(buf, 0, buf.length);
        System.out.println(lengthRead);
        // convert bytes to samples here
        for (int i = 0, s = 0; i < lengthRead;) {
            int sample = 0;

            sample |= buf[i++] & 0xFF; // (reverse these two lines
            sample |= buf[i++] << 8; //  if the format is big endian)

            // normalize to range of +/-1.0f
            samples[s++] = sample / 32768f;
        }

        return samples;
    }

    /**
     * Constructor.
     *
     * @param model
     *            model to connect to
     * @param view
     *            view to connect to
     */
    public NNCalcController1(NNCalcModel model, NNCalcView view) {
        this.model = model;
        this.view = view;
        updateViewToMatchModel(model, view);
    }

    @Override
    public void processClearEvent() {
        /*
         * Get alias to bottom from model
         */
        NaturalNumber bottom = this.model.bottom();
        /*
         * Update model in response to this event
         */
        bottom.clear();
        /*
         * Update view to reflect changes in model
         */
        updateViewToMatchModel(this.model, this.view);
    }

    @Override
    public void processSwapEvent() {
        /*
         * Get aliases to top and bottom from model
         */
        NaturalNumber top = this.model.top();
        NaturalNumber bottom = this.model.bottom();
        /*
         * Update model in response to this event
         */
        NaturalNumber temp = top.newInstance();
        temp.transferFrom(top);
        top.transferFrom(bottom);
        bottom.transferFrom(temp);
        /*
         * Update view to reflect changes in model
         */
        updateViewToMatchModel(this.model, this.view);
    }

    @Override
    public void processEnterEvent() {
        NaturalNumber tmp = new NaturalNumber2(this.model.bottom());
        this.model.top().transferFrom(tmp);

        updateViewToMatchModel(this.model, this.view);

    }

    @Override
    public void processAddEvent() {

        NaturalNumber topN = this.model.top();
        NaturalNumber bottomN = this.model.bottom();
        bottomN.add(topN);
        topN.clear();
        updateViewToMatchModel(this.model, this.view);
    }

    @Override
    public void processSubtractEvent() {

        NaturalNumber topN = this.model.top();
        NaturalNumber bottomN = this.model.bottom();
        topN.subtract(bottomN);
        bottomN.transferFrom(topN);
        updateViewToMatchModel(this.model, this.view);

    }

    @Override
    public void processMultiplyEvent() {
        NaturalNumber topN = this.model.top();
        NaturalNumber bottomN = this.model.bottom();
        bottomN.multiply(topN);
        topN.clear();
        updateViewToMatchModel(this.model, this.view);

    }

    @Override
    public void processDivideEvent() {
        NaturalNumber topN = this.model.top();
        NaturalNumber bottomN = this.model.bottom();
        topN.divide(bottomN);
        bottomN.transferFrom(topN);
        updateViewToMatchModel(this.model, this.view);

    }

    @Override
    public void processPowerEvent() {

        NaturalNumber topN = this.model.top();
        NaturalNumber bottomN = this.model.bottom();
        topN.power(bottomN.toInt());
        bottomN.transferFrom(topN);
        updateViewToMatchModel(this.model, this.view);

    }

    @Override
    public void processRootEvent() {

        NaturalNumber topN = this.model.top();
        NaturalNumber bottomN = this.model.bottom();
        topN.root(bottomN.toInt());
        bottomN.transferFrom(topN);
        updateViewToMatchModel(this.model, this.view);

    }

}
