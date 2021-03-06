package com.webpagebytes.cms.engine;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;

import static org.powermock.api.support.membermodification.MemberMatcher.method;
import static org.powermock.api.support.membermodification.MemberModifier.suppress;

import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import com.webpagebytes.cms.WPBModel;
import com.webpagebytes.cms.WPBParametersCache;
import com.webpagebytes.cms.WPBProjectCache;
import com.webpagebytes.cms.WPBPublicContentServlet;
import com.webpagebytes.cms.cmsdata.WPBParameter;
import com.webpagebytes.cms.cmsdata.WPBUri;
import com.webpagebytes.cms.cmsdata.WPBPage;
import com.webpagebytes.cms.engine.InternalModel;
import com.webpagebytes.cms.engine.ModelBuilder;
import com.webpagebytes.cms.engine.URLMatcherResult;
import com.webpagebytes.cms.exception.WPBException;
import com.webpagebytes.cms.exception.WPBLocaleException;
import com.webpagebytes.cms.utility.Pair;
import com.webpagebytes.cms.utility.CmsConfiguration;
import com.webpagebytes.cms.utility.CmsConfiguration.WPBSECTION;
import com.webpagebytes.cms.utility.CmsConfigurationFactory;

@RunWith(PowerMockRunner.class)
@PrepareForTest({WPBModel.class, ModelBuilder.class})
public class TestModelBuilder {
	
HttpServletRequest requestMock;
WPBCacheInstances cacheInstancesMock;
ModelBuilder modelBuilder;
WPBUri uriMock;
URLMatcherResult urlMatcherResultMock;
InternalModel modelMock;
WPBProjectCache projectCacheMock;
WPBPage webPageMock;
CmsConfiguration configurationMock;
Map<String, String> modelConfigs = new HashMap<String, String>();

@Before
public void setUp()
{
	configurationMock = EasyMock.createMock(CmsConfiguration.class);
	Whitebox.setInternalState(CmsConfigurationFactory.class, "configuration", configurationMock);
	EasyMock.expect(configurationMock.getSectionParams(WPBSECTION.SECTION_MODEL_CONFIGURATOR)).andReturn(modelConfigs);
	
	requestMock = EasyMock.createMock(HttpServletRequest.class);
	cacheInstancesMock = EasyMock.createMock(WPBCacheInstances.class);
	uriMock = EasyMock.createMock(WPBUri.class);
	urlMatcherResultMock = EasyMock.createMock(URLMatcherResult.class);
	modelMock = EasyMock.createMock(InternalModel.class);
	webPageMock = EasyMock.createMock(WPBPage.class);
	projectCacheMock  = EasyMock.createMock(WPBProjectCache.class);
}

@After
public void tearDown()
{
	Whitebox.setInternalState(CmsConfigurationFactory.class, "configuration", (CmsConfiguration) null);
}

@Test
public void test_populateModelForUriData()
{
	suppress(method(ModelBuilder.class, "populateUriParameters"));
	suppress(method(ModelBuilder.class, "populateGlobalParameters"));	
	suppress(method(ModelBuilder.class, "populateStaticParameters"));	
	try
	{
		EasyMock.expect(uriMock.getExternalKey()).andReturn("123");
		EasyMock.replay(requestMock, uriMock, urlMatcherResultMock, modelMock, configurationMock);
		modelBuilder = new ModelBuilder(cacheInstancesMock);
		
		modelBuilder.populateModelForUriData(requestMock, uriMock, urlMatcherResultMock, modelMock);		
		EasyMock.verify(requestMock, uriMock, urlMatcherResultMock, modelMock, configurationMock);
	} catch (WPBException e)
	{
		assertTrue (false);
	}
}

@Test
public void test_populateModelForWebPage()
{
	try
	{
		String pageExternalKey = "abc";
		List<WPBParameter> pageParams = new ArrayList<WPBParameter>();
		InternalModel model = new InternalModel();
		Map<String, String> mapParams = new HashMap<String, String>(); 
		String param1 = "param1";
		String value1 = "value1";
		mapParams.put(param1, value1);
		
		WPBParameter parameter = new WPBParameter();
		parameter.setName(param1);
		parameter.setValue(value1);
		pageParams.add(parameter);
		
		
		WPBParametersCache paramsCacheMock = EasyMock.createMock(WPBParametersCache.class);		
		EasyMock.expect(webPageMock.getExternalKey()).andReturn(pageExternalKey);		
		EasyMock.expect(cacheInstancesMock.getParameterCache()).andReturn(paramsCacheMock);
		EasyMock.expect(paramsCacheMock.getAllForOwner("abc")).andReturn(pageParams);
		
		EasyMock.replay(requestMock, webPageMock, cacheInstancesMock, paramsCacheMock, configurationMock);
		modelBuilder = new ModelBuilder(cacheInstancesMock);
		
		
		modelBuilder.populateModelForWebPage(webPageMock, model);		
		EasyMock.verify(requestMock, webPageMock, cacheInstancesMock, paramsCacheMock, configurationMock);
		
		assertTrue (model.getCmsModel().get(WPBModel.PAGE_PARAMETERS_KEY).equals(mapParams));
		
	} catch (WPBException e)
	{
		assertTrue (false);
	}
}

@Test
public void test_populateUriParameters_OK_language_and_country()
{
	try
	{
		suppress(method(ModelBuilder.class, "populateLocale"));
		
		String uriExternalKey = "abc";
		List<WPBParameter> pageParams = new ArrayList<WPBParameter>();
		InternalModel model = new InternalModel();
		Map<String, String> mapParams = new HashMap<String, String>(); 
		String param1 = "language";
		String value1 = "en";
		String param2 = "country";
		String value2 = "GB";
		mapParams.put(param1, value1);
		mapParams.put(param2, value2);
		
		WPBParameter parameter1 = new WPBParameter();
		parameter1.setName(param1);
		parameter1.setValue(value1);
		parameter1.setOverwriteFromUrl(1);
		parameter1.setLocaleType(WPBParameter.PARAMETER_LOCALE_LANGUAGE);
		pageParams.add(parameter1);
		WPBParameter parameter2 = new WPBParameter();
		parameter2.setName(param2);
		parameter2.setValue(value2);
		parameter2.setOverwriteFromUrl(1);
		parameter2.setLocaleType(WPBParameter.PARAMETER_LOCALE_COUNTRY);
		pageParams.add(parameter2);		
		
		
		WPBParametersCache paramsCacheMock = EasyMock.createMock(WPBParametersCache.class);				
		EasyMock.expect(cacheInstancesMock.getParameterCache()).andReturn(paramsCacheMock);
		EasyMock.expect(cacheInstancesMock.getProjectCache()).andReturn(projectCacheMock);
		
		Pair<String, String> defaultLocale = new Pair<String, String>("en", "");
		EasyMock.expect(projectCacheMock.getDefaultLocale()).andReturn(defaultLocale);
		
		Set<String> supportedLanguages = new HashSet<String>();
		supportedLanguages.add("en_GB");
		supportedLanguages.add("en");
		EasyMock.expect(projectCacheMock.getSupportedLocales()).andReturn(supportedLanguages);
		
		URLMatcherResult urlMatcherResult = new URLMatcherResult();
		Map<String, String> patternParams = new HashMap<String, String>();
		patternParams.put(param1, value1);
		patternParams.put(param2, value2);
		urlMatcherResult.setPatternParams(patternParams);
		
		EasyMock.expect(paramsCacheMock.getAllForOwner(uriExternalKey)).andReturn(pageParams);
		
		
		EasyMock.replay(requestMock, cacheInstancesMock, paramsCacheMock, projectCacheMock, configurationMock);
		modelBuilder = new ModelBuilder(cacheInstancesMock);
		
		try
		{
			Whitebox.invokeMethod(modelBuilder, "populateUriParameters", requestMock, uriExternalKey, urlMatcherResult, model);
		} catch(Exception e)
		{
			assertTrue(false);
		}
		
		EasyMock.verify(requestMock, cacheInstancesMock, paramsCacheMock, projectCacheMock, configurationMock);
		
		assertTrue (model.getCmsModel().get(WPBModel.URI_PARAMETERS_KEY).equals(mapParams));
		
	} catch (WPBException e)
	{
		assertTrue (false);
	}
}

@Test
public void test_populateUriParameters_OK_only_language()
{
	try
	{
		suppress(method(ModelBuilder.class, "populateLocale"));
		
		String uriExternalKey = "abc";
		List<WPBParameter> pageParams = new ArrayList<WPBParameter>();
		InternalModel model = new InternalModel();
		Map<String, String> mapParams = new HashMap<String, String>(); 
		String param1 = "language";
		String value1 = "en";
		mapParams.put(param1, value1);
		
		WPBParameter parameter1 = new WPBParameter();
		parameter1.setName(param1);
		parameter1.setValue(value1);
		parameter1.setOverwriteFromUrl(1);
		parameter1.setLocaleType(WPBParameter.PARAMETER_LOCALE_LANGUAGE);
		pageParams.add(parameter1);
		
		
		WPBParametersCache paramsCacheMock = EasyMock.createMock(WPBParametersCache.class);				
		EasyMock.expect(cacheInstancesMock.getParameterCache()).andReturn(paramsCacheMock);
		EasyMock.expect(cacheInstancesMock.getProjectCache()).andReturn(projectCacheMock);
		
		Pair<String, String> defaultLocale = new Pair<String, String>("en", "");
		EasyMock.expect(projectCacheMock.getDefaultLocale()).andReturn(defaultLocale);
		
		Set<String> supportedLanguages = new HashSet<String>();
		supportedLanguages.add("en_GB");
		supportedLanguages.add("en");
		EasyMock.expect(projectCacheMock.getSupportedLocales()).andReturn(supportedLanguages);
		
		URLMatcherResult urlMatcherResult = new URLMatcherResult();
		Map<String, String> patternParams = new HashMap<String, String>();
		patternParams.put(param1, value1);
		urlMatcherResult.setPatternParams(patternParams);
		
		EasyMock.expect(paramsCacheMock.getAllForOwner(uriExternalKey)).andReturn(pageParams);
		
		
		EasyMock.replay(requestMock, cacheInstancesMock, paramsCacheMock, projectCacheMock, configurationMock);
		
		modelBuilder = new ModelBuilder(cacheInstancesMock);
		
		try
		{
			Whitebox.invokeMethod(modelBuilder, "populateUriParameters", requestMock, uriExternalKey, urlMatcherResult, model);
		} catch(Exception e)
		{
			assertTrue(false);
		}
		
		EasyMock.verify(requestMock, cacheInstancesMock, paramsCacheMock, projectCacheMock, configurationMock);
		
		assertTrue (model.getCmsModel().get(WPBModel.URI_PARAMETERS_KEY).equals(mapParams));
		
	} catch (WPBException e)
	{
		assertTrue (false);
	}
}

@Test
public void test_populateUriParameters_empty_urlmatcher()
{
	try
	{
		suppress(method(ModelBuilder.class, "populateLocale"));
		
		String uriExternalKey = "abc";
		List<WPBParameter> pageParams = new ArrayList<WPBParameter>();
		Map<String, String> mapParams = new HashMap<String, String>();
		InternalModel model = new InternalModel();
		String param1 = "language";
		String value1 = "en";
		mapParams.put(param1, value1);
		
		WPBParameter parameter1 = new WPBParameter();
		parameter1.setName(param1);
		parameter1.setValue(value1);
		parameter1.setOverwriteFromUrl(0);
		parameter1.setLocaleType(WPBParameter.PARAMETER_NO_TYPE);
		pageParams.add(parameter1);		
		
		WPBParametersCache paramsCacheMock = EasyMock.createMock(WPBParametersCache.class);				
		EasyMock.expect(cacheInstancesMock.getParameterCache()).andReturn(paramsCacheMock);
		EasyMock.expect(cacheInstancesMock.getProjectCache()).andReturn(projectCacheMock);
		
		Pair<String, String> defaultLocale = new Pair<String, String>("en", "");
		EasyMock.expect(projectCacheMock.getDefaultLocale()).andReturn(defaultLocale);
		
		Set<String> supportedLanguages = new HashSet<String>();
		supportedLanguages.add("en_GB");
		supportedLanguages.add("en");
		EasyMock.expect(projectCacheMock.getSupportedLocales()).andReturn(supportedLanguages);
		
		URLMatcherResult urlMatcherResult = new URLMatcherResult();
		
		EasyMock.expect(paramsCacheMock.getAllForOwner(uriExternalKey)).andReturn(pageParams);
		
		EasyMock.replay(requestMock, cacheInstancesMock, paramsCacheMock, projectCacheMock, configurationMock);
		modelBuilder = new ModelBuilder(cacheInstancesMock);
		
		try
		{
			Whitebox.invokeMethod(modelBuilder, "populateUriParameters", requestMock, uriExternalKey, urlMatcherResult, model);
		} catch(Exception e)
		{
			assertTrue(false);
		}
		
		EasyMock.verify(requestMock, cacheInstancesMock, paramsCacheMock, projectCacheMock, configurationMock);
		
		assertTrue (model.getCmsModel().get(WPBModel.URI_PARAMETERS_KEY).equals(mapParams));
		
	} catch (WPBException e)
	{
		assertTrue (false);
	}
}

/*
 * language is not supported
 */
@Test
public void test_populateUriParameters_language_not_supported()
{
	try
	{
		suppress(method(ModelBuilder.class, "populateLocale"));
		
		String uriExternalKey = "abc";
		List<WPBParameter> pageParams = new ArrayList<WPBParameter>();
		InternalModel model = new InternalModel();
		Map<String, String> mapParams = new HashMap<String, String>(); 
		String param1 = "language";
		String value1 = "en";
		String param2 = "country";
		String value2 = "AU";
		mapParams.put(param1, value1);
		mapParams.put(param2, value2);
		
		WPBParameter parameter1 = new WPBParameter();
		parameter1.setName(param1);
		parameter1.setValue(value1);
		parameter1.setOverwriteFromUrl(1);
		parameter1.setLocaleType(WPBParameter.PARAMETER_LOCALE_LANGUAGE);
		pageParams.add(parameter1);
		WPBParameter parameter2 = new WPBParameter();
		parameter2.setName(param2);
		parameter2.setValue(value2);
		parameter2.setOverwriteFromUrl(1);
		parameter2.setLocaleType(WPBParameter.PARAMETER_LOCALE_COUNTRY);
		pageParams.add(parameter2);		
		
		
		WPBParametersCache paramsCacheMock = EasyMock.createMock(WPBParametersCache.class);				
		EasyMock.expect(cacheInstancesMock.getParameterCache()).andReturn(paramsCacheMock);
		EasyMock.expect(cacheInstancesMock.getProjectCache()).andReturn(projectCacheMock);
		
		Pair<String, String> defaultLocale = new Pair<String, String>("en", "");
		EasyMock.expect(projectCacheMock.getDefaultLocale()).andReturn(defaultLocale);
		
		Set<String> supportedLanguages = new HashSet<String>();
		supportedLanguages.add("en_GB");
		supportedLanguages.add("en");
		EasyMock.expect(projectCacheMock.getSupportedLocales()).andReturn(supportedLanguages);
		
		URLMatcherResult urlMatcherResult = new URLMatcherResult();
		Map<String, String> patternParams = new HashMap<String, String>();
		patternParams.put(param1, value1);
		patternParams.put(param2, value2);
		urlMatcherResult.setPatternParams(patternParams);
		
		EasyMock.expect(paramsCacheMock.getAllForOwner(uriExternalKey)).andReturn(pageParams);
		
		
		EasyMock.replay(requestMock, cacheInstancesMock, paramsCacheMock, projectCacheMock, configurationMock);
		modelBuilder = new ModelBuilder(cacheInstancesMock);
		
		
		try
		{
			Whitebox.invokeMethod(modelBuilder, "populateUriParameters", requestMock, uriExternalKey, urlMatcherResult, model);
		} 
		catch (WPBLocaleException e)
		{
			// OK
		}
		catch(Exception e)
		{
			assertTrue(false);
		}
		
		EasyMock.verify(requestMock, cacheInstancesMock, paramsCacheMock, projectCacheMock, configurationMock);
				
	} catch (WPBException e)
	{
		assertTrue (false);
	}
}

/*
 * simulate the case where the url is /{xyz}/test.html and there is a {language} param that is overwrite from url and language identifier
 */
@Test
public void test_populateUriParameters_no_language_param()
{
	try
	{
		
		suppress(method(ModelBuilder.class, "populateLocale"));
		
		String uriExternalKey = "abc";
		List<WPBParameter> pageParams = new ArrayList<WPBParameter>();
		InternalModel model = new InternalModel();
		Map<String, String> mapParams = new HashMap<String, String>(); 
		String param1 = "language";
		String value1 = "en";
		String param2 = "xyz";
		String value2 = "";
		mapParams.put(param1, value1);
		mapParams.put(param2, value2);
		
		WPBParameter parameter1 = new WPBParameter();
		parameter1.setName(param1);
		parameter1.setValue(value1);
		parameter1.setOverwriteFromUrl(1);
		parameter1.setLocaleType(WPBParameter.PARAMETER_LOCALE_LANGUAGE);
		pageParams.add(parameter1);
		WPBParameter parameter2 = new WPBParameter();
		parameter2.setName(param2);
		parameter2.setValue(value2);
		parameter2.setOverwriteFromUrl(0);
		parameter2.setLocaleType(WPBParameter.PARAMETER_NO_TYPE);
		pageParams.add(parameter2);		
		
		
		WPBParametersCache paramsCacheMock = EasyMock.createMock(WPBParametersCache.class);				
		EasyMock.expect(cacheInstancesMock.getParameterCache()).andReturn(paramsCacheMock);
		EasyMock.expect(cacheInstancesMock.getProjectCache()).andReturn(projectCacheMock);
		
		Pair<String, String> defaultLocale = new Pair<String, String>("en", "");
		EasyMock.expect(projectCacheMock.getDefaultLocale()).andReturn(defaultLocale);
		
		Set<String> supportedLanguages = new HashSet<String>();
		supportedLanguages.add("en_GB");
		supportedLanguages.add("en");
		EasyMock.expect(projectCacheMock.getSupportedLocales()).andReturn(supportedLanguages);
		
		URLMatcherResult urlMatcherResult = new URLMatcherResult();
		Map<String, String> patternParams = new HashMap<String, String>();
		// pattern params contains only the xyz param
		patternParams.put(param2, value2);
		urlMatcherResult.setPatternParams(patternParams);
		
		EasyMock.expect(paramsCacheMock.getAllForOwner(uriExternalKey)).andReturn(pageParams);
		
		
		EasyMock.replay(requestMock, cacheInstancesMock, paramsCacheMock, projectCacheMock, configurationMock);
		modelBuilder = new ModelBuilder(cacheInstancesMock);
		
		try
		{
			Whitebox.invokeMethod(modelBuilder, "populateUriParameters", requestMock, uriExternalKey, urlMatcherResult, model);
		} 
		catch (WPBLocaleException e)
		{
			// OK
		}
		catch(Exception e)
		{
			assertTrue(false);
		}
		
		EasyMock.verify(requestMock, cacheInstancesMock, paramsCacheMock, projectCacheMock, configurationMock);
			
	} catch (WPBException e)
	{
		assertTrue (false);
	}
}

/*
 * simulate the case where the url is /{xyz}/test.html and there is a {language} param that is overwrite from url and language identifier
 */
@Test
public void test_populateUriParameters_no_country_param()
{
	try
	{
		suppress(method(ModelBuilder.class, "populateLocale"));
		
		String uriExternalKey = "abc";
		List<WPBParameter> pageParams = new ArrayList<WPBParameter>();
		InternalModel model = new InternalModel();
		Map<String, String> mapParams = new HashMap<String, String>(); 
		String param1 = "language";
		String value1 = "en";
		String param2 = "country";
		String value2 = "GB";
		mapParams.put(param1, value1);
		mapParams.put(param2, value2);
		
		WPBParameter parameter1 = new WPBParameter();
		parameter1.setName(param1);
		parameter1.setValue(value1);
		parameter1.setOverwriteFromUrl(1);
		parameter1.setLocaleType(WPBParameter.PARAMETER_LOCALE_LANGUAGE);
		pageParams.add(parameter1);
		WPBParameter parameter2 = new WPBParameter();
		parameter2.setName(param2);
		parameter2.setValue(value2);
		parameter2.setOverwriteFromUrl(1);
		parameter2.setLocaleType(WPBParameter.PARAMETER_LOCALE_COUNTRY);
		pageParams.add(parameter2);		
		
		
		WPBParametersCache paramsCacheMock = EasyMock.createMock(WPBParametersCache.class);				
		EasyMock.expect(cacheInstancesMock.getParameterCache()).andReturn(paramsCacheMock);
		EasyMock.expect(cacheInstancesMock.getProjectCache()).andReturn(projectCacheMock);
		
		Pair<String, String> defaultLocale = new Pair<String, String>("en", "");
		EasyMock.expect(projectCacheMock.getDefaultLocale()).andReturn(defaultLocale);
		
		Set<String> supportedLanguages = new HashSet<String>();
		supportedLanguages.add("en_GB");
		supportedLanguages.add("en");
		EasyMock.expect(projectCacheMock.getSupportedLocales()).andReturn(supportedLanguages);
		
		URLMatcherResult urlMatcherResult = new URLMatcherResult();
		Map<String, String> patternParams = new HashMap<String, String>();
		// pattern params contains only the language param
		patternParams.put(param1, value1);
		urlMatcherResult.setPatternParams(patternParams);
		
		EasyMock.expect(paramsCacheMock.getAllForOwner(uriExternalKey)).andReturn(pageParams);
		
		
		EasyMock.replay(requestMock, cacheInstancesMock, paramsCacheMock, projectCacheMock, configurationMock);
		modelBuilder = new ModelBuilder(cacheInstancesMock);
		
		
		try
		{
			Whitebox.invokeMethod(modelBuilder, "populateUriParameters", requestMock, uriExternalKey, urlMatcherResult, model);
		} 
		catch (WPBLocaleException e)
		{
			// OK
		}
		catch(Exception e)
		{
			assertTrue(false);
		}
		
		EasyMock.verify(requestMock, cacheInstancesMock, paramsCacheMock, projectCacheMock, configurationMock);
			
	} catch (WPBException e)
	{
		assertTrue (false);
	}
}


@Test
public void test_populateLocale()
{
	modelBuilder = new ModelBuilder(cacheInstancesMock);
	
	Map<String, String> mapLocale = new HashMap<String, String>();
	mapLocale.put(WPBModel.LOCALE_LANGUAGE_KEY, "en");
	mapLocale.put(WPBModel.LOCALE_COUNTRY_KEY, "GB");
	InternalModel model = new InternalModel();
	
	try
	{
		Whitebox.invokeMethod(modelBuilder, "populateLocale", "en", "GB", model);
	} catch (Exception e)
	{
		assertTrue(false);
	}
	assertTrue(model.getCmsModel().get(WPBModel.LOCALE_KEY).equals(mapLocale));

}

@Test
public void test_populateGlobalParameters()
{
	try
	{
		modelBuilder = new ModelBuilder(cacheInstancesMock);
		
		List<WPBParameter> globalParams = new ArrayList<WPBParameter>();
		InternalModel model = new InternalModel();
		Map<String, String> mapParams = new HashMap<String, String>(); 
		String param1 = "param1";
		String value1 = "value1";
		mapParams.put(param1, value1);
		
		WPBParameter parameter = new WPBParameter();
		parameter.setName(param1);
		parameter.setValue(value1);
		globalParams.add(parameter);
		
		
		WPBParametersCache paramsCacheMock = EasyMock.createMock(WPBParametersCache.class);		
		EasyMock.expect(cacheInstancesMock.getParameterCache()).andReturn(paramsCacheMock);
		EasyMock.expect(paramsCacheMock.getAllForOwner("")).andReturn(globalParams);
		
		EasyMock.replay(cacheInstancesMock, paramsCacheMock);
		
		try
		{
			Whitebox.invokeMethod(modelBuilder, "populateGlobalParameters", model);
		} catch (Exception e)
		{
			assertTrue(false);
		}
		EasyMock.verify(cacheInstancesMock, paramsCacheMock);
		
		assertTrue (model.getCmsModel().get(WPBModel.GLOBALS_KEY).equals(mapParams));
		
	} catch (WPBException e)
	{
		assertTrue (false);
	}
}

@Test
public void test_populateStaticParametersFromHeader_http()
{
	modelBuilder = new ModelBuilder(cacheInstancesMock);
	
	String url = "http://www.example.com/test/";
	EasyMock.expect(requestMock.getHeader(ModelBuilder.BASE_MODEL_URL_PATH_HEADER)).andReturn(url);
	Map<String, String> mapStaticParams = new HashMap<String, String>();
	mapStaticParams.put(WPBModel.GLOBAL_PROTOCOL, "http");
	mapStaticParams.put(WPBModel.GLOBAL_DOMAIN, "www.example.com");
	mapStaticParams.put(WPBModel.GLOBAL_CONTEXT_PATH, "/test");
	mapStaticParams.put(WPBModel.GLOBAL_BASE_URL, "http://www.example.com/test");
	InternalModel model = new InternalModel();
	
	EasyMock.replay(requestMock);

	try
	{		
		Whitebox.invokeMethod(modelBuilder, "populateStaticParameters", requestMock, model);
	} catch (Exception e)
	{
		assertTrue(false);
	}
	EasyMock.verify(requestMock);
	assertTrue(model.getCmsModel().get(WPBModel.REQUEST_KEY).equals(mapStaticParams));
}

@Test
public void test_populateStaticParametersFromHeader_https()
{
	String url = "https://www.example.com/test1/test2";
	EasyMock.expect(requestMock.getHeader(ModelBuilder.BASE_MODEL_URL_PATH_HEADER)).andReturn(url);
	Map<String, String> mapStaticParams = new HashMap<String, String>();
	mapStaticParams.put(WPBModel.GLOBAL_PROTOCOL, "https");
	mapStaticParams.put(WPBModel.GLOBAL_DOMAIN, "www.example.com");
	mapStaticParams.put(WPBModel.GLOBAL_CONTEXT_PATH, "/test1/test2");
	mapStaticParams.put(WPBModel.GLOBAL_BASE_URL, "https://www.example.com/test1/test2");
	InternalModel model = new InternalModel();
	
	EasyMock.replay(requestMock, configurationMock);
	modelBuilder = new ModelBuilder(cacheInstancesMock);
	
	try
	{		
		Whitebox.invokeMethod(modelBuilder, "populateStaticParameters", requestMock, model);
	} catch (Exception e)
	{
		assertTrue(false);
	}
	EasyMock.verify(requestMock, configurationMock);
	assertTrue(model.getCmsModel().get(WPBModel.REQUEST_KEY).equals(mapStaticParams));
}

@Test
public void test_populateStaticParametersFromConfig_http()
{
	
	String url = "https://www.example.com/test1/test2";
	EasyMock.expect(requestMock.getHeader(ModelBuilder.BASE_MODEL_URL_PATH_HEADER)).andReturn(null);
	modelConfigs.put("baseModelUrlPath", "https://www.example.com/test1/test2/");
	Map<String, String> mapStaticParams = new HashMap<String, String>();
	mapStaticParams.put(WPBModel.GLOBAL_PROTOCOL, "https");
	mapStaticParams.put(WPBModel.GLOBAL_DOMAIN, "www.example.com");
	mapStaticParams.put(WPBModel.GLOBAL_CONTEXT_PATH, "/test1/test2");
	mapStaticParams.put(WPBModel.GLOBAL_BASE_URL, "https://www.example.com/test1/test2");
	InternalModel model = new InternalModel();
	
	EasyMock.replay(requestMock, configurationMock);
	modelBuilder = new ModelBuilder(cacheInstancesMock);
	
	try
	{		
		Whitebox.invokeMethod(modelBuilder, "populateStaticParameters", requestMock, model);
	} catch (Exception e)
	{
		assertTrue(false);
	}
	EasyMock.verify(requestMock, configurationMock);
	assertTrue(model.getCmsModel().get(WPBModel.REQUEST_KEY).equals(mapStaticParams));
}

@Test
public void test_populateStaticParametersFromRequest_http()
{
	
	String url = "https://www.example.com/test1/test2/aaa.html?abc=1";
	EasyMock.expect(requestMock.getHeader(ModelBuilder.BASE_MODEL_URL_PATH_HEADER)).andReturn(null);
	EasyMock.expect(requestMock.getRequestURL()).andReturn(new StringBuffer(url));
	EasyMock.expect(requestMock.getAttribute(WPBPublicContentServlet.CONTEXT_PATH)).andReturn("/test1/test2");
	Map<String, String> mapStaticParams = new HashMap<String, String>();
	mapStaticParams.put(WPBModel.GLOBAL_PROTOCOL, "https");
	mapStaticParams.put(WPBModel.GLOBAL_DOMAIN, "www.example.com");
	mapStaticParams.put(WPBModel.GLOBAL_CONTEXT_PATH, "/test1/test2");
	mapStaticParams.put(WPBModel.GLOBAL_BASE_URL, "https://www.example.com/test1/test2");
	InternalModel model = new InternalModel();
	
	EasyMock.replay(requestMock, configurationMock);
	modelBuilder = new ModelBuilder(cacheInstancesMock);
	
	try
	{		
		Whitebox.invokeMethod(modelBuilder, "populateStaticParameters", requestMock, model);
	} catch (Exception e)
	{
		assertTrue(false);
	}
	EasyMock.verify(requestMock, configurationMock);
	assertTrue(model.getCmsModel().get(WPBModel.REQUEST_KEY).equals(mapStaticParams));
}

@Test
public void test_populateStaticParameters_uppercase()
{
	String url = "http://www.EXAMPLE.com/test1/TEST2";
	EasyMock.expect(requestMock.getHeader(ModelBuilder.BASE_MODEL_URL_PATH_HEADER)).andReturn(url);
	Map<String, String> mapStaticParams = new HashMap<String, String>();
	mapStaticParams.put(WPBModel.GLOBAL_PROTOCOL, "http");
	mapStaticParams.put(WPBModel.GLOBAL_DOMAIN, "www.example.com");
	mapStaticParams.put(WPBModel.GLOBAL_CONTEXT_PATH, "/test1/TEST2");
	mapStaticParams.put(WPBModel.GLOBAL_BASE_URL, "http://www.EXAMPLE.com/test1/TEST2");
	InternalModel model = new InternalModel();
	
	EasyMock.replay(requestMock, configurationMock);
	modelBuilder = new ModelBuilder(cacheInstancesMock);
	
	try
	{		
		Whitebox.invokeMethod(modelBuilder, "populateStaticParameters", requestMock, model);
	} catch (Exception e)
	{
		assertTrue(false);
	}
	EasyMock.verify(requestMock, configurationMock);
	assertTrue(model.getCmsModel().get(WPBModel.REQUEST_KEY).equals(mapStaticParams));
}

@Test
public void test_populateStaticParameters_justdomain()
{
	String url = "http://EXAMPLE.com";
	EasyMock.expect(requestMock.getHeader(ModelBuilder.BASE_MODEL_URL_PATH_HEADER)).andReturn(url);
	Map<String, String> mapStaticParams = new HashMap<String, String>();
	mapStaticParams.put(WPBModel.GLOBAL_PROTOCOL, "http");
	mapStaticParams.put(WPBModel.GLOBAL_DOMAIN, "example.com");
	mapStaticParams.put(WPBModel.GLOBAL_CONTEXT_PATH, "");
	mapStaticParams.put(WPBModel.GLOBAL_BASE_URL, "http://EXAMPLE.com");
	InternalModel model = new InternalModel();
	
	EasyMock.replay(requestMock, configurationMock);
	modelBuilder = new ModelBuilder(cacheInstancesMock);
	
	try
	{		
		Whitebox.invokeMethod(modelBuilder, "populateStaticParameters", requestMock, model);
	} catch (Exception e)
	{
		assertTrue(false);
	}
	EasyMock.verify(requestMock, configurationMock);
	assertTrue(model.getCmsModel().get(WPBModel.REQUEST_KEY).equals(mapStaticParams));
}


}