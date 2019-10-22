import components.simplereader.SimpleReader;
import components.simplereader.SimpleReader1L;
import components.simplewriter.SimpleWriter;
import components.simplewriter.SimpleWriter1L;

public class FlatnessTestHelper {

    public static void testFlatness() {

        for (int k = 20; k <= 20020; k += 1000) {

            final int currentFrequency = k;
            Thread t5 = new Thread(new Runnable() {
                int value = currentFrequency;

                @Override
                public void run() {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    double[] audioSample = NNCalcController1
                            .recordSample(32768);

                    StringBuilder sample = new StringBuilder(
                            String.valueOf(audioSample[0]));
                    for (int i = 1; i < audioSample.length; i++) {
                        sample.append(" " + String.valueOf(audioSample[0]));
                    }

                    SimpleWriter out = new SimpleWriter1L(
                            "data/" + this.value + ".txt");

                    out.println(sample.toString());

                    out.close();
                }
            });
            t5.start();

//            try {
//                Thread.sleep(500);
//            } catch (InterruptedException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }

//            try {
//                GenerateShortSine.playSound(k);
//            } catch (LineUnavailableException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }

        }

        //  Map<Integer, Double> loudnessAtEachFreq = new Map1L<>();
        for (int k = 20; k <= 20020; k += 1000) {
            SimpleReader in = new SimpleReader1L("data/" + k + ".txt");
            int counter = 0;
            while (!in.atEOS()) {
                double value = in.nextDouble();
                counter++;
            }
            System.out.println(counter);
        }

    }

    public static void main(String[] args) throws Exception {

        //        for (int i = 0; i < buffer.length; i++) {
//            System.out.println(buffer[i]);
//        }

//        double[] outR = new double[buffer.length];
//        double[] outI = new double[buffer.length];
//
//        dft(buffer, outR, outI);
//
//        double[] results = new double[buffer.length];
//        for (int i = 0; i < buffer.length; i++) {
//            double real = outR[i];
//            double imaginary = outI[i];
//            results[i] = Math.sqrt(real * real + imaginary * imaginary);
//        }

//      System.out.println("length is " + audioSample.length);
//      double[] outR = new double[audioSample.length];
//      double[] outI = new double[audioSample.length];
//
//      double[] fftBuffer = new double[16384];
//      for (int i = 0; i < fftBuffer.length; i++) {
//          fftBuffer[i] = audioSample[i];
//      }
//      FastFourierTransformer fft = new FastFourierTransformer(
//              DftNormalization.STANDARD);
//      Complex resultC[] = fft.transform(fftBuffer,
//              TransformType.FORWARD);
//
//      double[] results = new double[audioSample.length];
//      for (int i = 0; i < resultC.length; i++) {
//          double real = resultC[i].getReal();
//          double imaginary = resultC[i].getImaginary();
//          results[i] = Math
//                  .sqrt(real * real + imaginary * imaginary);
//      }
        //double fftResolution = 44100.0 / fftBuffer.length;

    }

}