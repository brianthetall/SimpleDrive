package com.brianthetall.storage;

//import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{

    @Test
    public void testApp()
    {
	
        assertTrue( true );
    }

    @Before
    public void testSetup(){
	System.out.println("setup");
    }

    @After
    public void testUnsetup(){
	System.out.println("unsetup");
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

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static junit.framework.Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    
}
