import org.jfree.data.xy.XYSeries;

public class YDrawer implements Runnable
{
	Dispatcher main;
	public YDrawer(Dispatcher m)
	{
		main = m;
	}
	public void run()
	{
		XYSeries plot;
		double v;
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
			v = main.getYAngularVelocity();
			plot = new XYSeries("");
			for(double s = 0; s <= 2*Math.PI*(1+3/Dispatcher.pointCount); s+=(2*Math.PI/Dispatcher.pointCount))
			{
				plot.add(s, Math.sin(v*s));
			}
			main.yCollection.removeAllSeries();
			main.yCollection.addSeries(plot);
		}
	}

}
