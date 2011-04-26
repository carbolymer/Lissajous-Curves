public class ParameterUpdater implements Runnable
{
	Dispatcher main;
	public ParameterUpdater(Dispatcher m)
	{
		main = m;
	}
	public void run()
	{
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
			main.xAngularVelocity.setValue(main.getXAngularVelocity()+main.getXAngularStep());
			main.yAngularVelocity.setValue(main.getYAngularVelocity()+main.getYAngularStep());
			main.xPhase.setValue(main.getPhase()+main.getPhaseStep());
		}
	}
}
