package com.webpagebytes.cms.controllers;

import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.*;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Map;
import java.util.HashMap;

import com.webpagebytes.cms.cmsdata.WPBPage;
import com.webpagebytes.cms.cmsdata.WPBPageModule;
import com.webpagebytes.cms.controllers.PageModuleValidator;


@RunWith(PowerMockRunner.class)
public class TestWBPageModuleValidator {

private PageModuleValidator pageModuleValidator;
private WPBPageModule wbPageModule;
private Map<String,String> noErrors;

@Before
public void before()
{
	pageModuleValidator = new PageModuleValidator();
	wbPageModule = new WPBPageModule();
	noErrors = new HashMap();
}

@Test
public void test_validateCreate()
{
	wbPageModule.setName("test");
	Map errors = pageModuleValidator.validateCreate(wbPageModule);
	assertTrue(errors.equals(noErrors));
}

@Test
public void test_validateUpdate()
{
	wbPageModule.setName("test");
	wbPageModule.setPrivkey(1L);
	Map errors = pageModuleValidator.validateUpdate(wbPageModule);
	assertTrue(errors.equals(noErrors));
}

}
