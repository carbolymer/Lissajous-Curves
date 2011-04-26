import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;


public class Dispatcher
{
	private JFrame window = new JFrame("LISARZU");
	protected volatile JSpinner
		xAngularVelocity = new JSpinner(new SpinnerNumberModel(1, -20, 20, 0.1)),
		yAngularVelocity = new JSpinner(new SpinnerNumberModel(1, -20, 20, 0.1)),
		xAngularStep = new JSpinner(new SpinnerNumberModel(0, -5, 5, 0.01)),
		yAngularStep = new JSpinner(new SpinnerNumberModel(0, -5, 5, 0.01)),
		xPhase = new JSpinner(new SpinnerNumberModel(0, -50, 50, 0.16667)),
		xPhaseStep = new JSpinner(new SpinnerNumberModel(0, -5, 5, 0.01667)),
		timeTick = new JSpinner(new SpinnerNumberModel(150, 0, 1000, 50));
	
	private JButton
		startBtn = new JButton("Start"),
		stopBtn = new JButton("Stop");
	
	protected volatile XYSeriesCollection
		yCollection = new XYSeriesCollection(),
		xCollection = new XYSeriesCollection(),
		xyCollection = new XYSeriesCollection();
	
	private Thread
		parameterUpdater = null,
		xDrawer = null,
		yDrawer = null,
		xyDrawer = null;
	
	final static int pointCount = 1000;		
	
	public Dispatcher()
	{
	    try
	    {
	    	SwingUtilities.invokeAndWait(new Runnable()
	        {
	            public void run()
	            {
	            	createGUI();
	            }
	        });
	    }
	    catch (Exception e)
	    {
	        System.err.println("Nie można byłu narysować interfejsu.");
	    }
	}
	
	public static void main(String[] args)
	{
		try
		{
			new Dispatcher();
		}
		catch(Exception e)
		{
			System.out.println(e.toString());
		}
	}
	
	public void createGUI()
	{
		GridBagConstraints c = new GridBagConstraints();
		ChartPanel xChart, yChart, xyChart;
		
		window.setSize(800,750);
		window.setLayout(new GridBagLayout());
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		stopBtn.setEnabled(false);
		
		final Dispatcher myself = this;
		
		startBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				startBtn.setEnabled(false);
				stopBtn.setEnabled(true);
				timeTick.setEnabled(false);
				parameterUpdater = new Thread(new ParameterUpdater(myself));
				xDrawer = new Thread(new XDrawer(myself));
				yDrawer = new Thread(new YDrawer(myself));
				xyDrawer = new Thread(new XYDrawer(myself));
				
				parameterUpdater.start();
				xDrawer.start();
				yDrawer.start();
				xyDrawer.start();
			}
		});
		
		stopBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				startBtn.setEnabled(true);
				stopBtn.setEnabled(false);
				timeTick.setEnabled(true);
				
				parameterUpdater.interrupt();
				xDrawer.interrupt();
				yDrawer.interrupt();
				xyDrawer.interrupt();
			}
		});
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(2, 2, 2, 2);
		c.gridy = 0;
		window.add(new JLabel("<html>&omega;<sub>x</sub>: </html>"), c);
		c.gridx = 1;
		window.add(xAngularVelocity,c);
		c.gridy = 1;
		c.gridx = 0;
		window.add(new JLabel("<html>&Delta;&omega;<sub>x</sub>:</html>"),c);
		c.gridx = 1;
		window.add(xAngularStep, c);
		c.gridx = 2;
		c.gridy = 0;
		window.add(new JLabel("<html>&omega;<sub>y</sub>:</html>"), c);
		c.gridx = 3;
		window.add(yAngularVelocity,c);
		c.gridy = 1;
		c.gridx = 2;
		window.add(new JLabel("<html>&Delta;&omega;<sub>y</sub>:</html>"),c);
		c.gridx = 3;
		window.add(yAngularStep, c);		
		c.gridy = 3;
		c.gridx = 0;
		window.add(new JLabel("<html>&delta;<sub>x</sub>: &pi;*</html>"),c);
		c.gridx = 1;
		window.add(xPhase, c);
		c.gridx = 2;
		window.add(new JLabel("<html>&Delta;&delta<sub>x</sub>: &pi;*</html>"),c);
		c.gridx = 3;
		window.add(xPhaseStep,c);
		
		c.gridx = 0;
		c.gridy = 4;
		window.add(new JLabel("<html>&Delta; t [ms]:</html>"),c);
		c.gridx = 1;
		window.add(timeTick,c);
		
		c.gridx = 0;
		c.gridy = 5;
		c.gridwidth = 2;
		window.add(startBtn,c);
		c.gridx = 2;
		window.add(stopBtn,c);
		
		c.gridy = 0;
		c.gridx = 5;
		c.gridheight = 7;
		c.gridwidth = 1;
		xChart = new ChartPanel(ChartFactory.createXYLineChart(
				"", "s", "x(s)", xCollection,
				PlotOrientation.HORIZONTAL, false, false, false));
		xChart.setPreferredSize(new Dimension(450,250));
		xChart.getChart().getXYPlot().getDomainAxis().setInverted(true);
		xChart.getChart().getXYPlot().getRangeAxis().setRange(-1.1, 1.1);
		xChart.getChart().getXYPlot().getDomainAxis().setRange(-0.1, 0.1+Math.PI*2);
		window.add(xChart,c);
		
		c.gridy = 7;
		c.gridx = 0;
		c.gridheight = 1;
		c.gridwidth = 5;

		yChart = new ChartPanel(ChartFactory.createXYLineChart(
				"", "s", "y(s)", yCollection,
				PlotOrientation.VERTICAL, false, false, false));
		yChart.setPreferredSize(new Dimension(300,400));
		yChart.getChart().getXYPlot().getRangeAxis().setInverted(true);
		yChart.getChart().getXYPlot().getRangeAxis().setRange(-1.1, 1.1);
		yChart.getChart().getXYPlot().getDomainAxis().setRange(-0.1, 0.1+Math.PI*2);
		window.add(yChart,c);
		
		c.gridx = 5;
		c.gridy = 7;
		c.gridheight = 1;
		c.gridwidth = 1;
		xyChart = new ChartPanel(ChartFactory.createXYLineChart(
				"", "x(s)", "y(s)", xyCollection,
				PlotOrientation.VERTICAL, false, false, false));
		xyChart.setPreferredSize(new Dimension(350,400));
		xyChart.getChart().getXYPlot().getRangeAxis().setInverted(true);
		xyChart.getChart().getXYPlot().getRangeAxis().setRange(-1.1, 1.1);
		xyChart.getChart().getXYPlot().getDomainAxis().setRange(-1.1, 1.1);
		window.add(xyChart,c);
		
		window.setVisible(true);
	}
	
	public int getTimeTick()
	{
		return Integer.parseInt(timeTick.getValue().toString());
	}
	
	public double getXAngularVelocity()
	{
		return Double.parseDouble(xAngularVelocity.getValue().toString());
	}
	
	public double getYAngularVelocity()
	{
		return Double.parseDouble(yAngularVelocity.getValue().toString());
	}

	public double getXAngularStep()
	{
		return Double.parseDouble(xAngularStep.getValue().toString());
	}
	
	public double getYAngularStep()
	{
		return Double.parseDouble(yAngularStep.getValue().toString());
	}
	
	public double getPhase()
	{
		return Double.parseDouble(xPhase.getValue().toString());
	}
	
	public double getPhaseStep()
	{
		return Double.parseDouble(xPhaseStep.getValue().toString());
	}
}
