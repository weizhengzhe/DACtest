import java.awt.EventQueue;
import java.nio.ByteBuffer;
import java.util.Random;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.swing.JFrame;

public class WhiteNoise extends JFrame {

    private GeneratorThread generatorThread;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    WhiteNoise frame = new WhiteNoise();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public WhiteNoise() {

        this.generatorThread = new GeneratorThread();
        this.generatorThread.start();
    }

    class GeneratorThread extends Thread {

        final static public int SAMPLE_SIZE = 2;
        final static public int PACKET_SIZE = 44100 * 8;

        SourceDataLine line;
        public boolean exitExecution = false;

        @Override
        public void run() {

            try {
                AudioFormat format = new AudioFormat(44100, 16, 1, true, true);
                DataLine.Info info = new DataLine.Info(SourceDataLine.class,
                        format, PACKET_SIZE * 2);

                if (!AudioSystem.isLineSupported(info)) {
                    throw new LineUnavailableException();
                }

                this.line = (SourceDataLine) AudioSystem.getLine(info);
                this.line.open(format);
                this.line.start();
            } catch (LineUnavailableException e) {
                e.printStackTrace();
                System.exit(-1);
            }

            ByteBuffer buffer = ByteBuffer.allocate(PACKET_SIZE);

            Random random = new Random();

            buffer.clear();
            for (int i = 0; i < PACKET_SIZE / SAMPLE_SIZE; i++) {
                buffer.putShort(
                        (short) (random.nextGaussian() * Short.MAX_VALUE));
            }
            this.line.write(buffer.array(), 0, buffer.position());

            this.line.drain();
            this.line.close();
        }

        public void exit() {
            this.exitExecution = true;
        }
    }
}