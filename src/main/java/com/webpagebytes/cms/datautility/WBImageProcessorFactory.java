package com.webpagebytes.cms.datautility;

import com.webpagebytes.cms.utility.WBConfiguration;

import com.webpagebytes.cms.utility.WBConfigurationFactory;
import com.webpagebytes.cms.utility.WBConfiguration.WPBSECTION;

public class WBImageProcessorFactory {

	private static WBImageProcessor instance;
	private static final Object lock = new Object();

	private WBImageProcessorFactory() {};
	
	public static WBImageProcessor getInstance()
	{
		if (instance == null) 
		{
			synchronized (lock) {
				if (instance == null)
				{
					WBConfiguration config = WBConfigurationFactory.getConfiguration();
					String factoryClass = "";
					if (config!=null)
					{
						factoryClass = config.getSectionClassFactory(WPBSECTION.SECTION_IMAGEPROCESSOR);
					}
					try
					{
						instance = (WBImageProcessor) Class.forName(factoryClass).newInstance();
						return instance;
					} 
					
					catch (Exception e)
					{
						return null;
					}
				}
			}
		}
		return instance;
	}
	
}
