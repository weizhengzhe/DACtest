import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class recorderClass extends JPanel implements ActionListener {
    final int bufSize = 16384;

    Capture capture = new Capture();

    Playback playback = new Playback();

    AudioInputStream audioInputStream;

    JButton playB, captB;

    JTextField textField;

    String errStr;

    double duration, seconds;

    File file;

    public recorderClass() {
//        this.setLayout(new BorderLayout());
//        EmptyBorder eb = new EmptyBorder(5, 5, 5, 5);
//        SoftBevelBorder sbb = new SoftBevelBorder(SoftBevelBorder.LOWERED);
//        this.setBorder(new EmptyBorder(5, 5, 5, 5));
//
//        JPanel p1 = new JPanel();
//        p1.setLayout(new BoxLayout(p1, BoxLayout.X_AXIS));
//
//        JPanel p2 = new JPanel();
//        p2.setBorder(sbb);
//        p2.setLayout(new BoxLayout(p2, BoxLayout.Y_AXIS));
//
//        JPanel buttonsPanel = new JPanel();
//        buttonsPanel.setBorder(new EmptyBorder(10, 0, 5, 0));
//        this.playB = this.addButton("Play", buttonsPanel, false);
//        this.captB = this.addButton("Record", buttonsPanel, true);
//        p2.add(buttonsPanel);
//
//        p1.add(p2);
//        this.add(p1);
    }

    public void open() {
    }

    public void close() {
        if (this.playback.thread != null) {
            this.playB.doClick(0);
        }
        if (this.capture.thread != null) {
            this.captB.doClick(0);
        }
    }

    private JButton addButton(String name, JPanel p, boolean state) {
        JButton b = new JButton(name);
        b.addActionListener(this);
        b.setEnabled(state);
        p.add(b);
        return b;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object obj = e.getSource();
        if (obj.equals(this.playB)) {
            if (this.playB.getText().startsWith("Play")) {
                this.playback.start();
                this.captB.setEnabled(false);
                this.playB.setText("Stop");
            } else {
                this.playback.stop();
                this.captB.setEnabled(true);
                this.playB.setText("Play");
            }
        } else if (obj.equals(this.captB)) {
            if (this.captB.getText().startsWith("Record")) {
                this.capture.start(); ///////////
                this.playB.setEnabled(false);
                this.captB.setText("Stop");
            } else {
                this.capture.stop();////////
                this.playB.setEnabled(true);
            }

        }
    }

    /**
     * Write data to the OutputChannel.
     */
    public class Playback implements Runnable {

        SourceDataLine line;

        Thread thread;

        public void start() {
            recorderClass.this.errStr = null;
            this.thread = new Thread(this);
            this.thread.setName("Playback");
            this.thread.start();
        }

        public void stop() {
            this.thread = null;
        }

        private void shutDown(String message) {
            if ((recorderClass.this.errStr = message) != null) {
                System.err.println(recorderClass.this.errStr);
            }
            if (this.thread != null) {
                this.thread = null;
                recorderClass.this.captB.setEnabled(true);
                recorderClass.this.playB.setText("Play");
            }
        }

        @Override
        public void run() {

            // make sure we have something to play
            if (recorderClass.this.audioInputStream == null) {
                this.shutDown("No loaded audio to play back");
                return;
            }
            // reset to the beginnning of the stream
            try {
                recorderClass.this.audioInputStream.reset();
            } catch (Exception e) {
                this.shutDown("Unable to reset the stream\n" + e);
                return;
            }

            // get an AudioInputStream of the desired format for playback

            AudioFormat.Encoding encoding = AudioFormat.Encoding.PCM_SIGNED;
            float rate = 44100.0f;
            int channels = 1;
            int frameSize = 4;
            int sampleSize = 24;
            boolean bigEndian = true;

            AudioFormat format = new AudioFormat(encoding, rate, sampleSize,
                    channels, (sampleSize / 8) * channels, rate, bigEndian);

            AudioInputStream playbackInputStream = AudioSystem
                    .getAudioInputStream(format,
                            recorderClass.this.audioInputStream);

            if (playbackInputStream == null) {
                this.shutDown("Unable to convert stream of format "
                        + recorderClass.this.audioInputStream + " to format "
                        + format);
                return;
            }

            // define the required attributes for our line,
            // and make sure a compatible line is supported.

            DataLine.Info info = new DataLine.Info(SourceDataLine.class,
                    format);
            if (!AudioSystem.isLineSupported(info)) {
                this.shutDown("Line matching " + info + " not supported.");
                return;
            }

            // get and open the source data line for playback.

            try {
                this.line = (SourceDataLine) AudioSystem.getLine(info);
                this.line.open(format, recorderClass.this.bufSize);
            } catch (LineUnavailableException ex) {
                this.shutDown("Unable to open the line: " + ex);
                return;
            }

            // play back the captured audio data

            int frameSizeInBytes = format.getFrameSize();
            int bufferLengthInFrames = this.line.getBufferSize() / 8;
            int bufferLengthInBytes = bufferLengthInFrames * frameSizeInBytes;
            byte[] data = new byte[bufferLengthInBytes];
            int numBytesRead = 0;

            // start the source data line
            this.line.start();

            while (this.thread != null) {
                try {
                    if ((numBytesRead = playbackInputStream.read(data)) == -1) {
                        break;
                    }
                    int numBytesRemaining = numBytesRead;
                    while (numBytesRemaining > 0) {
                        numBytesRemaining -= this.line.write(data, 0,
                                numBytesRemaining);
                    }
                } catch (Exception e) {
                    this.shutDown("Error during playback: " + e);
                    break;
                }
            }
            // we reached the end of the stream.
            // let the data play out, then
            // stop and close the line.
            if (this.thread != null) {
                this.line.drain();
            }
            this.line.stop();
            this.line.close();
            this.line = null;
            this.shutDown(null);
        }
    } // End class Playback

    /**
     * Reads data from the input channel and writes to the output stream
     */
    class Capture implements Runnable {

        TargetDataLine line;

        Thread thread;

        public void start() {
            recorderClass.this.errStr = null;
            this.thread = new Thread(this);
            this.thread.setName("Capture");
            this.thread.start();
        }

        public void stop() {
            this.thread = null;
        }

        private void shutDown(String message) {
            if ((recorderClass.this.errStr = message) != null
                    && this.thread != null) {
                this.thread = null;
                recorderClass.this.playB.setEnabled(true);
                recorderClass.this.captB.setText("Record");
                System.err.println(recorderClass.this.errStr);
            }
        }

        @Override
        public void run() {

            recorderClass.this.duration = 0;
            recorderClass.this.audioInputStream = null;

            // define the required attributes for our line,
            // and make sure a compatible line is supported.

            AudioFormat.Encoding encoding = AudioFormat.Encoding.PCM_SIGNED;
            float rate = 44100.0f;
            int channels = 1;
            int frameSize = 4;
            int sampleSize = 24;
            boolean bigEndian = true;

            AudioFormat format = new AudioFormat(encoding, rate, sampleSize,
                    channels, (sampleSize / 8) * channels, rate, bigEndian);

            DataLine.Info info = new DataLine.Info(TargetDataLine.class,
                    format);

            if (!AudioSystem.isLineSupported(info)) {
                this.shutDown("Line matching " + info + " not supported.");
                return;
            }

            // get and open the target data line for capture.

            try {
                this.line = (TargetDataLine) AudioSystem.getLine(info);
                this.line.open(format, this.line.getBufferSize());
            } catch (LineUnavailableException ex) {
                this.shutDown("Unable to open the line: " + ex);
                return;
            } catch (SecurityException ex) {
                this.shutDown(ex.toString());
                //JavaSound.showInfoDialog();
                return;
            } catch (Exception ex) {
                this.shutDown(ex.toString());
                return;
            }

            // play back the captured audio data
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int frameSizeInBytes = format.getFrameSize();
            int bufferLengthInFrames = this.line.getBufferSize() / 8;
            int bufferLengthInBytes = bufferLengthInFrames * frameSizeInBytes;
            byte[] data = new byte[bufferLengthInBytes];
            int numBytesRead;

            this.line.start();

            while (this.thread != null) {
                if ((numBytesRead = this.line.read(data, 0,
                        bufferLengthInBytes)) == -1) {
                    break;
                }
                out.write(data, 0, numBytesRead);
            }

            // we reached the end of the stream.
            // stop and close the line.
            this.line.stop();
            this.line.close();
            this.line = null;

            // stop and close the output stream
            try {
                out.flush();
                out.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            // load bytes into the audio input stream for playback

            byte audioBytes[] = out.toByteArray();
            ByteArrayInputStream bais = new ByteArrayInputStream(audioBytes);
            recorderClass.this.audioInputStream = new AudioInputStream(bais,
                    format, audioBytes.length / frameSizeInBytes);

            long milliseconds = (long) ((recorderClass.this.audioInputStream
                    .getFrameLength() * 1000) / format.getFrameRate());
            recorderClass.this.duration = milliseconds / 1000.0;

            try {
                AudioSystem.write(recorderClass.this.audioInputStream,
                        AudioFileFormat.Type.WAVE, new File("output.wav"));
            } catch (Exception ex) {
                ex.printStackTrace();
                return;
            }

        }
    } // End class Capture

    public static void main(String s[]) {
        recorderClass ssc = new recorderClass();
        ssc.open();
        JFrame f = new JFrame("Capture/Playback");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().add("Center", ssc);
        f.pack();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int w = 360;
        int h = 170;
        f.setLocation(screenSize.width / 2 - w / 2,
                screenSize.height / 2 - h / 2);
        f.setSize(w, h);
        f.show();
    }
}