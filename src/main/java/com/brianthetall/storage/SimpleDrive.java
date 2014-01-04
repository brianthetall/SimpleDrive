package com.brianthetall.storage;

import com.brianthetall.util.FileUtil;
import com.brianthetall.api.sdrive.SecureFileBean;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.net.*;
import javax.net.ssl.HttpsURLConnection;
import java.net.URLConnection;
import java.io.*;
import java.util.Enumeration;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.io.PrintWriter;
import java.lang.String;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential.Builder;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.CredentialRefreshListener;
import com.google.api.client.auth.oauth2.CredentialStore;
import com.google.api.client.auth.oauth2.TokenRequest;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.Permission;
import com.google.api.services.drive.model.PermissionList;
import com.google.api.services.oauth2.model.Userinfo;
import com.google.api.services.oauth2.Oauth2Scopes;
import com.google.api.services.oauth2.Oauth2;
import com.google.gson.Gson;

public class SimpleDrive{

    private Drive drive;

    /**
     * @param token Google issued OAuth token
     */
    public SimpleDrive(String token){
	
	NetHttpTransport transport = new NetHttpTransport();
	JacksonFactory jsonFactory = new JacksonFactory();
	GoogleCredential credential = new GoogleCredential();
	Gson g=new Gson();

	credential.setAccessToken(token);
	System.out.println("JSON CRED w/ Token:"+g.toJson(credential));

	try{//GET USER EMAIL
	    Oauth2 userInfoService = new Oauth2.Builder(new NetHttpTransport(), new JacksonFactory(), credential).setApplicationName("GLUnet").build();
	    Userinfo userinfo =  userInfoService.userinfo().get().execute();
	    //	    System.out.println(userinfo.getEmail());
	}catch(Exception e){
	    System.out.println("AuthServiceError:SimpleDrive");
	}

	try{//BUILD DRIVE OBJECT-USING CREDENTIAL
	    drive = new Drive.Builder(new NetHttpTransport(),new JacksonFactory(),credential).setApplicationName("GLUnet").build();
	    if(drive == null)
		System.out.println("Error building drive");

	}catch(Exception e){
	    System.out.println(e.getMessage()+"Error building Drive");
	}
	
    }

    /*
      newUser: email address of new user
      fileId: fileid of Drive file to share
      @returns: false on fail
     */    
    public boolean shareFile(String newUser,String fileId){
	try{
	    Permission p = new Permission().setName(newUser).setRole("reader").setType("user").setValue(newUser);
	    Permission pNew = drive.permissions().insert(fileId,p).setSendNotificationEmails(true).setEmailMessage("smells like debugging").execute();
	    if(pNew != null)
		return true;
	}catch(Exception e){
	    System.out.println("SimpleDrive.shareFIle Error");
	}
	return false;
    }
    
    //NEED JUnit Test for THIS!
    public Map<String,String> lsDownloadMap(){
	List<File> listOfFiles = retrieveAllFiles();
	Map<String,String> retval=new LinkedHashMap<>(listOfFiles.size()*2);//big map,quick map
	for(int i=0;i<listOfFiles.size();i++)
	    retval.put(listOfFiles.get(i).getId(), listOfFiles.get(i).getTitle());
	return retval;
    }

    public String[] lsDownloadLinks(){

	List<File> listOfFiles = retrieveAllFiles();
	String[] buffer = new String[listOfFiles.size()];

	for(int i=0;i<listOfFiles.size();i++)
	    buffer[i] = new String("<a href=\"/start/DownloadDriveFile?fileID="+listOfFiles.get(i).getId() +"\">"+listOfFiles.get(i).getTitle()+"</a>");

	return buffer;
    }

    public SecureFileBean[] lsBeans(){

	List<File> listOfFiles = retrieveAllFiles();
	SecureFileBean [] retval=new SecureFileBean[listOfFiles.size()];

	for(int i=0;i<retval.length;i++){
	    retval[i] = new SecureFileBean().setGid( listOfFiles.get(i).getId() ).setName(listOfFiles.get(i).getTitle());
	}

	return retval;
    }

    public String[] ls(){

	List<File> listOfFiles = retrieveAllFiles();
	//	ArrayList<String> retval = new ArrayList<String>(listOfFiles.size());
	String[] retval = new String[listOfFiles.size()];
	for(int i=0;i<listOfFiles.size() ; i++)
	    retval[i] = listOfFiles.get(i).getTitle();
	
	return (retval);
    }

    public String[] lsId(){

	List<File> listOfFiles = retrieveAllFiles();
	String[] a = new String[listOfFiles.size()];
	for(int i=0;i<listOfFiles.size() ; i++)
	    a[i] = listOfFiles.get(i).getId();
	
	return a;
    }

    private List<File> retrieveAllFiles() {
	List<File> result = new ArrayList<File>();
	Files.List request=null;
	try{
	    request = drive.files().list();
	}catch(IOException e){
	    System.out.println("retllfiles "+e.getMessage());
	}
	do {
	    try {
		FileList files = request.execute();
		result.addAll(files.getItems());
		request.setPageToken(files.getNextPageToken());
	    } catch (IOException e) {
		System.out.println("An error occurred: " + e);
		request.setPageToken(null);
	    }
	} while (request.getPageToken() != null &&
		 request.getPageToken().length() > 0);
	return result;
    }

    public Drive getDrive(){
	return drive;
    }

    /** DEPRECATED
     * Upload 'textFileFromServer' to Drive
     */
    public boolean createFile(String data){
	try{
	    File metadata=new File();
	    metadata.setTitle(data);
	    metadata.setMimeType("text/plain");
	    //	    metadata.setParents(Arrays.asList(new ParentReference().setId(/*parentID*/));
	    java.io.File input = new java.io.File("/home/ubuntu/textFileFromServer");
	    File file = drive.files().insert(metadata, new FileContent("text/plain",input)).execute();
	}catch(IOException e){
	    System.out.println("[0]I/O Error in SimpleDrive.createFile:"+e.getMessage());
	    return false;
	}catch(Exception e){
	    System.out.println("[0]Error in SimpleDrive.createFile:"+e.getMessage());
	    return false;
	}
	return true;
    }

    /**
     * Creates a file in Google Drive
     * Local file from server (with name localFile)
     */
    public boolean createFile(String newFileName,String localFile){
	try{
	    File metadata=new File();
	    metadata.setTitle(newFileName);
	    metadata.setMimeType("text/plain");
	    java.io.File input = new java.io.File(localFile);
	    File file = drive.files().insert(metadata, new FileContent("text/plain",input)).execute();
	}catch(IOException e){
	    System.out.println("[1]I/O Error in SimpleDrive.createFile:"+e.getMessage());
	    return false;
	}catch(Exception e){
	    System.out.println("[1]Error in SimpleDrive.createFile:"+e.getMessage());
	    return false;
	}
	return true;
    }

    public File createFile(java.io.File file){
	if(file == null)
	    return null;
	return this.createFile(file.getName(),FileUtil.readFile(file));
    }

    /**
     * Creates a file in Google Drive
     * Use byte[] as file-body
     */
    public File createFile(String newFileName,byte[] body){
	if(newFileName==null || body==null){
	    System.out.println("Why are you sending NULL values to SimpleDrive.createFile()?");
	    return null;
	}
	try{
	    File metadata=new File();
	    metadata.setTitle(newFileName);
	    metadata.setMimeType("text/plain");
	    return drive.files().insert(metadata, new ByteArrayContent("text/plain",body)).execute();
	}catch(IOException e){
	    System.out.println("[2]I/O Error in SimpleDrive.createFile:"+e.getMessage());
	    return null;
	}
    }

    public ArrayList<Permission> lsPermissions(List<File> files){
	if(files != null){
	    ArrayList<Permission> retval = new ArrayList<Permission>(files.size());//minimum size
	    for(File file:files){
		try{
		    PermissionList pl = drive.permissions().list(file.getId()).execute();
		    retval.ensureCapacity(retval.size()+pl.getItems().size());
		    retval.addAll(pl.getItems());
		}catch(IOException e){
		    System.out.println("SimpleDrive.getPermissions Drive Error");
		}
	    }
	    return retval;
	}
	return null;
    }

    /*
      Input: String[] of fileIds
      @return Arraylist of Permissions
     */
    public ArrayList<Permission> lsPermissions(String[] files){
	if(files != null){
	    ArrayList<Permission> retval = new ArrayList<Permission>(files.length);//minimum size
	    for(String file:files){
		try{
		    PermissionList pl = drive.permissions().list(file).execute();
		    retval.ensureCapacity(retval.size()+pl.getItems().size());
		    retval.addAll(pl.getItems());
		}catch(IOException e){
		    System.out.println("SimpleDrive.getPermissions String[] Drive Error");
		}
	    }
	    return retval;
	}
	return null;
    }
    

    public File getFileBean(String fileId) throws IOException{
	return drive.files().get(fileId).execute();
    }

    public InputStream downloadFile(File file) {
	if (file.getDownloadUrl() != null && file.getDownloadUrl().length() > 0) {
	    try {
		//buildHttpRequest(new Generic
		HttpResponse resp =drive.getRequestFactory().buildGetRequest(new GenericUrl(file.getDownloadUrl())).execute();
		return resp.getContent();
	    } catch (IOException e) {
		// An error occurred.
		e.printStackTrace();
		return null;
	    }
	} else {
	    // The file doesn't have any content stored on Drive.
	    return null;
	}
    }

    /**
     * delete - remove file from google drive
     * @param - fileId of file on drive
     * @return true upon success
     */
    public boolean delete(String fileId) throws IOException{
	if( drive.files().delete(fileId).execute() == null )
	    return false;
	return true;
    }

    private void printFile(String fileId) {

    }

    public static void main(String args[])throws IOException{

	if(args.length !=1){
	    System.out.println("SimpleDrive <Token>");
	    System.exit(-1);
	}
	SimpleDrive sd = new SimpleDrive(args[0]);

    }
}
