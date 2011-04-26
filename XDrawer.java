import org.jfree.data.xy.XYSeries;

public class XDrawer implements Runnable
{
	Dispatcher main;
	public XDrawer(Dispatcher m)
	{
		main = m;
	}
	public void run()
	{
		XYSeries plot;
		double v, p;
		int sleepTime = main.getTimeTick();
		while(true)
		{
			try
			{
				Thread.sleep(sleepTime);
			}
			catch (InterruptedException e)
			{
				return;
			}
			v = main.getXAngularVelocity();
			p = main.getPhase();
			
			plot = new XYSeries("");
			for(double s = 0; s <= 2*Math.PI*(1+3/Dispatcher.pointCount); s+=(2*Math.PI/Dispatcher.pointCount))
			{
				plot.add(s, Math.sin(v*s+p));
			}
			main.xCollection.removeAllSeries();
			main.xCollection.addSeries(plot);
		}
	}

}
