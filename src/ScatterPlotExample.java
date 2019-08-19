import java.awt.Color;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import components.map.Map;
import components.map.Map1L;
import components.simplereader.SimpleReader;
import components.simplereader.SimpleReader1L;

public class ScatterPlotExample extends JFrame {
    private static final long serialVersionUID = 6294689542092367723L;

    public ScatterPlotExample(String title) {
        super(title);

        // Create dataset
        XYDataset dataset = this.createDataset();

        // Create chart
        JFreeChart chart = ChartFactory.createScatterPlot(
                "Boys VS Girls weight comparison chart", "X-Axis", "Y-Axis",
                dataset);

        //Changes background color
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(new Color(255, 228, 196));

        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot
                .getRenderer();
        renderer.setSeriesShapesVisible(0, true);
        renderer.setSeriesShape(0,
                new Rectangle2D.Double(-1.0, -1.0, 2.0, 2.0));

        // Create Panel
        ChartPanel panel = new ChartPanel(chart, false);
        this.setContentPane(panel);
    }

    private XYDataset createDataset() {
        XYSeriesCollection dataset = new XYSeriesCollection();

        Map<Integer, Double> transformBins = new Map1L<>();

        SimpleReader in = new SimpleReader1L("DFTresult.txt");
        int mapIndex = 0;
        while (!in.atEOS()) {
            double value = in.nextDouble();
            transformBins.add(mapIndex, value);
            mapIndex++;
        }
        System.out.println("added " + mapIndex + " value");

        in.close();

        //Boys (Age,weight) series
        XYSeries series1 = new XYSeries("Boys");
        for (int j = 0; j < transformBins.size() / 2; j++) {
            series1.add(j, transformBins.value(j));
        }

        dataset.addSeries(series1);

        return dataset;
    }

    public static void main(String[] args) {

//        ScatterPlotExample example = new ScatterPlotExample(
//                "Scatter Chart Example | BORAJI.COM");
//        example.setSize(800, 400);
//        example.setLocationRelativeTo(null);
//        example.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//        example.setVisible(true);

    }
}