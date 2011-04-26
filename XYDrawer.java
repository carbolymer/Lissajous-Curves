import org.jfree.data.xy.XYSeries;

public class XYDrawer implements Runnable
{
	Dispatcher main;
	public XYDrawer(Dispatcher m)
	{
		main = m;
	}
	public void run()
	{
		int sleepTime = main.getTimeTick();
		XYSeries x,y,plot;
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
			if(main.xCollection.getSeriesCount() == 0 || main.yCollection.getSeriesCount() == 0)
				continue;
			x = main.xCollection.getSeries(0);
			y = main.yCollection.getSeries(0);
			plot = new XYSeries("", false, true);
			for(int i = 0; i < Math.min(x.getItemCount(),y.getItemCount()); ++i)
			{
				plot.add(x.getY(i),y.getY(i));
//				System.out.println("i:"+i+"\t"+x.getY(i)+"\t"+y.getY(i) + "\t@ "+i);
			}
			main.xyCollection.removeAllSeries();
			main.xyCollection.addSeries(plot);
		}
	}

}
