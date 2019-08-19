import java.awt.Color;
import java.awt.Cursor;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.sound.sampled.LineUnavailableException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import components.naturalnumber.NaturalNumber;
import components.simplewriter.SimpleWriter;
import components.simplewriter.SimpleWriter1L;

/**
 * View class.
 *
 * @author Zhengzhe Wei
 */
public final class NNCalcView1 extends JFrame implements NNCalcView {

    /**
     * Controller object registered with this view to observe user-interaction
     * events.
     */
    private NNCalcController controller;
    /**
     * Text areas.
     */
    // private final JTextArea tTop;

    /**
     * Operator and related buttons.
     */
    private final JButton bMonotonic, bEnter, bTestINL, bDrawMonotonic;
    private final JComboBox<String> portList, bitSelection;
    private final boolean flag = true;

    /**
     * Useful constants.
     */
    private static final int TEXT_AREA_HEIGHT = 5, TEXT_AREA_WIDTH = 20,
            CALC_GRID_ROWS = 2, CALC_GRID_COLUMNS = 1, OPERATION_PANEL_ROW = 6,
            OPERATION_PANEL_COLLUM = 1;

    /**
     * Default constructor.
     */
    public NNCalcView1() {
        // Create the JFrame being extended
        /*
         * Call the JFrame (superclass) constructor with a String parameter to
         * name the window in its title bar
         */
        super("Trusted electronics--Zhengzhe Wei-2019");

        // Set up the GUI widgets --------------------------------------------
        recorderClass ssc = new recorderClass();
        ssc.open();
        /*
         * Create widgets
         */

        this.bMonotonic = new JButton("Max bits with 100% monotonicity");
        //this.bMonotonic.setEnabled(false);

        this.bDrawMonotonic = new JButton("Graph non-monotonic region");
        this.bDrawMonotonic.setEnabled(false);
        this.bEnter = new JButton("Connect");
        this.bTestINL = new JButton("Test INL performance at selected bits");
        //this.bTestINL.setEnabled(false);
        this.portList = new JComboBox<String>();
        this.bitSelection = new JComboBox<String>();
        /*
         * Create main button panel
         */

        this.bDrawMonotonic.addActionListener(this);

        this.bitSelection.addItem("selectBits");
        this.bitSelection.addItem("8");
        this.bitSelection.addItem("9");
        this.bitSelection.addItem("10");
        this.bitSelection.addItem("11");
        this.bitSelection.addItem("12");
        this.bitSelection.addItem("13");
        this.bitSelection.addItem("14");
        this.bitSelection.addItem("15");
        this.bitSelection.addItem("16");
        this.bitSelection.addActionListener(this);
        this.bitSelection.setEnabled(false);
        /*
         * Create operation panel
         */
        JPanel operationPanel = new JPanel(
                new GridLayout(OPERATION_PANEL_ROW, OPERATION_PANEL_COLLUM));
        operationPanel.setBorder(BorderFactory.createLineBorder(Color.green));

        /*
         * Add the buttons to the operation panel.
         */

        operationPanel.add(this.bEnter);
        operationPanel.add(this.portList);
        operationPanel.add(this.bMonotonic);
        operationPanel.add(this.bitSelection);
        operationPanel.add(this.bTestINL);
        operationPanel.add(this.bDrawMonotonic);
        /*
         * Organize main window
         */
        this.setLayout(new GridLayout(CALC_GRID_ROWS, CALC_GRID_COLUMNS));
        /*
         * Add scroll panes and button panel to main window, from left to right
         * and top to bottom
         */
        // this.add(topScrollPane);

        this.add(operationPanel);
        // Set up the observers ----------------------------------------------

        this.bEnter.addActionListener(this);
        this.bMonotonic.addActionListener(this);
        this.bTestINL.addActionListener(this);
        // Set up the main application window --------------------------------
        this.pack();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        /*
         * Make sure the main window is appropriately sized, exits this program
         * on close, and becomes visible to the user
         */

    }

    @Override
    public void registerObserver(NNCalcController controller) {

        this.controller = controller;

    }

    @Override
    public void setButtomMonotonicEnable() {

        this.bMonotonic.setEnabled(true);
        this.bTestINL.setEnabled(true);
        this.bitSelection.setEnabled(true);
        this.bDrawMonotonic.setEnabled(true);
    }

    @Override
    public void updateTopDisplay(NaturalNumber n) {

        //  this.tTop.setText(n.toString());

    }

    @Override
    public void actionPerformed(ActionEvent event) {
        /*
         * Set cursor to indicate computation on-going; this matters only if
         * processing the event might take a noticeable amount of time as seen
         * by the user
         */

        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        Object source = event.getSource();
        if (source == this.bEnter) {

            Thread t1 = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Generate16bitSine.playSound(400.0);
                    } catch (LineUnavailableException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            });
            t1.start();

            double[] audioSample = NNCalcController1.recordSample();
            System.out.println("length is " + audioSample.length);
            double[] outR = new double[audioSample.length];
            double[] outI = new double[audioSample.length];

            DFTclass.dft(audioSample, outR, outI);

            double[] results = new double[audioSample.length];
            for (int i = 0; i < audioSample.length; i++) {
                double real = outR[i];
                double imaginary = outI[i];
                results[i] = Math.sqrt(real * real + imaginary * imaginary);
            }

            SimpleWriter out = new SimpleWriter1L("DFTresult.txt");
            for (int i = 0; i < results.length; i++) {
                out.println(results[i]);
            }

        } else if (source == this.bMonotonic) {
            //this.controller.processAddEvent();   //load data into Map

            ScatterPlotExample example = new ScatterPlotExample(
                    "Scatter Chart Example | BORAJI.COM");
            example.setSize(1000, 500);
            example.setLocationRelativeTo(null);
            example.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
            example.setVisible(true);

//            XYSeriesCollection dataset = new XYSeriesCollection();
//            XYSeries series = new XYSeries("Boys");
//            for (int j = 0; j < transformBins.size(); j++) {
//                series.add(j, transformBins.value(j));
//            }
//            dataset.addSeries(series);
//            JFreeChart chart = ChartFactory.createScatterPlot(
//                    "Boys VS Girls weight comparison chart", "X-Axis", "Y-Axis",
//                    dataset);
//            ChartPanel panel = new ChartPanel(chart);
//            this.setContentPane(panel);
//            panel.setVisible(true);

        } else if (source == this.bTestINL) {

        } else if (source == this.bDrawMonotonic) {

        }

        this.setCursor(Cursor.getDefaultCursor());
    }

    @Override
    public void setConnectDisable() {
        this.bEnter.setEnabled(false);

    }

    @Override
    public void setPortSelectionDisable() {
        this.portList.setEnabled(false);

    }

}
