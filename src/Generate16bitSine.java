import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class Generate16bitSine {

    static void playSound(double frequency) throws LineUnavailableException {
        float sampleRate = 44100;
        double f = frequency;
        double a = .5;
        double twoPiF = 2 * Math.PI * f;

        double[] buffer = new double[44100 * 15];
        for (int sample = 0; sample < buffer.length; sample++) {
            double time = sample / sampleRate;

            buffer[sample] = a * Math.sin(twoPiF * time);
        }

        byte[] byteBuffer = new byte[buffer.length * 2];
        int idx = 0;
        for (int i = 0; i < byteBuffer.length;) {
            int x = (int) (buffer[idx++] * 32767);
            byteBuffer[i++] = (byte) x;
            byteBuffer[i++] = (byte) (x >>> 8);
        }

        boolean bigEndian = false;
        boolean signed = true;
        int bits = 16;
        int channels = 1;
        AudioFormat format = new AudioFormat(sampleRate, bits, channels, signed,
                bigEndian);

        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
        line.open(format);
        line.start();
        long now = System.currentTimeMillis();
        System.out.println("buffer size:" + line.available());
        int written = line.write(byteBuffer, 0, byteBuffer.length);
        System.out.println(written + " bytes written.");
        line.drain();
        line.close();
        long total = System.currentTimeMillis() - now;
        System.out.println(total + " ms.");
    }

    public static void main(String[] args) throws Exception {
        float sampleRate = 44100;
        double f = 1761;
        double a = .5;
        double twoPiF = 2 * Math.PI * f;

        double[] buffer = new double[44100 * 20];
        for (int sample = 0; sample < buffer.length; sample++) {
            double time = sample / sampleRate;

            buffer[sample] = a * Math.sin(twoPiF * time);
        }

        byte[] byteBuffer = new byte[buffer.length * 2];
        int idx = 0;
        for (int i = 0; i < byteBuffer.length;) {
            int x = (int) (buffer[idx++] * 32767);
            byteBuffer[i++] = (byte) x;
            byteBuffer[i++] = (byte) (x >>> 8);
        }

        boolean bigEndian = false;
        boolean signed = true;
        int bits = 16;
        int channels = 1;
        AudioFormat format = new AudioFormat(sampleRate, bits, channels, signed,
                bigEndian);

        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
        line.open(format);
        line.start();
        long now = System.currentTimeMillis();
        System.out.println("buffer size:" + line.available());
        int written = line.write(byteBuffer, 0, byteBuffer.length);
        System.out.println(written + " bytes written.");
        line.drain();
        line.close();
        long total = System.currentTimeMillis() - now;
        System.out.println(total + " ms.");
    }
}