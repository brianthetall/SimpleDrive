package com.brianthetall.storage;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.junit.Assert;

/**
 * Unit test for SimpleDrive
 * JUnit4
 */
public class AppTest {

    private SimpleDrive sd;
    
    @Before
    public void beforeSetup(){
	System.out.println("setup");
	sd=new SimpleDrive("token,,,use DI?");
    }

    @After
    public void testUnsetup(){
	System.out.println("unsetup");
	sd=null;
    }

    @Test
    public void testShareFile(){
		System.out.println("ShareFile");
    }
    
    @Test 
    public void testLsDownloadLinks(){
	System.out.println("LsDownloadLinks");
    }
    
    @Test
    public void testLsBeans(){	
	System.out.println("LsBeans");
    }
    
    @Test
    public void testLs(){}
    
    @Test
    public void testLsId(){}
    
    @Test
    public void testGetDrive(){}
    
    @Test//test the MANY overloaded versions of this function
    public void testCreateFile(){}
    
    @Test//2 versions of this
    public void testLsPermissions(){}
    
    @Test
    public void testGetFileBean(){}
    
    @Test
    public void testDownloadFile(){}
    
    @Test
    public void testDelete(){}
    
}
