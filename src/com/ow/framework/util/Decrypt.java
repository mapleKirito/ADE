package com.ow.framework.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.lang.ArrayUtils;

import com.bean.UserInfoRstl;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class Decrypt {

	/**
	 * 账户跟密码
	 */
	private static String account = "hefei10zhong_PTA6110839";
	private static String password = "linktones_sd33957504";
	
	/**
     * 获取公匙
     * @throws FileNotFoundException,ClassNotFoundException
	 * @throws NoSuchMethodException 
	 * @throws SecurityException 
     */
	private static Key loadKey(UserInfoRstl uir) throws IOException, FileNotFoundException, SecurityException {
		Key publicKey=null;
		if(uir!=null&&uir.getUrl()!=null){
			URL url = uir.getUrl();
			URLClassLoader loader = new URLClassLoader(new URL[]{ url });
			Class c = null;
			try {
				c = loader.loadClass("com.Decrypt.DecryptUtil");
			} catch (Exception e1) {
				System.exit(0);
			}
	        Method method;
	        
			try {
				method = c.getDeclaredMethod("loadKey");
				publicKey = (Key)method.invoke(null);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			URL url = new URL("file:/C:/Windows/Decrypt1.0.jar");  
			URLClassLoader loader = new URLClassLoader(new URL[]{ url });
			Class c = null;
			try {
				c = loader.loadClass("com.Decrypt.DecryptUtil");
			} catch (Exception e1) {
				System.exit(0);
			}
	        Method method;
	        
			try {
				method = c.getDeclaredMethod("loadKey");
				publicKey = (Key)method.invoke(null);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return publicKey;
	}

    
    /**
     * 公匙解密
     * @throws IOException 
     * @throws FileNotFoundException 
     * @throws InvalidKeyException 
     * @throws NoSuchPaddingException 
     * @throws NoSuchAlgorithmException 
     * @throws Exception
     */
    public static InputStream toDecrypt(InputStream is) throws FileNotFoundException, IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, ClassNotFoundException  {
    	System.out.println("正在获取认证结果...");
    	//认证信息初始化
    	
    	String Identification=GetpcInfo.getCPUSerial()+GetpcInfo.getHardDiskSN("c")+GetpcInfo.getMotherboardSN();
    	Identification = Encrypt.getMD5(Identification.trim());
    	String urls = "http://211.149.234.199:19997/UserInfoCheck/InfoCheck?";
        urls = urls + "account=" + account + "&psd=" + password + "&Identification=" + Identification;
    	URL url=new URL(urls);
    	UserInfoRstl icr=null;
    	try {
			URLConnection con=url.openConnection();
			ObjectInputStream ois=new ObjectInputStream(con.getInputStream());
			
			icr = (UserInfoRstl)ois.readObject();
			System.out.println("已获取认证结果");
			System.out.println("用户账户:"+icr.getAccount());
			System.out.println("用户密码:"+icr.getPsd());
			System.out.println("认证结果:"+icr.getContent());
		} catch (ClassNotFoundException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}catch (Exception e) {
			// TODO: handle exception
			System.err.println("认证失败,转用2方案");
		}
    	
    	Cipher cipher = Cipher.getInstance("RSA");

        // 从硬盘中读取公匙
        Key publicKey=null;
		publicKey = loadKey(icr);
		//publicKey = Test.loadKey();
		byte[] data=new BASE64Decoder().decodeBuffer(is);
        //设置为解密模式，用公钥解密
         cipher.init(Cipher.DECRYPT_MODE, publicKey);
         StringBuilder sb = new StringBuilder();  
         try{
	         for (int i = 0; i < data.length; i += 128) {  
	        	 int m=i+128;
	        	 if(m>data.length){
	        		 m=data.length;
	        	 }
            	 byte[] temp=ArrayUtils.subarray(data, i,  
	                     m);
            	 
	        	 byte[] doFinal = cipher.doFinal(temp);  
	        	 //fos.write(doFinal);
	             sb.append(new String(doFinal));  
	            
	         }  
	         //System.out.println(sb.toString());
	         is.close();
         //fos.close();
         //生成解密后的输入流
         } catch (IllegalBlockSizeException e) {
   			// TODO Auto-generated catch block
   			//e.printStackTrace();
   			System.err.println("数据完整性破坏");
   		} catch (BadPaddingException e) {
   			// TODO Auto-generated catch block
   			//e.printStackTrace();
   			System.err.println("数据解密失败，解密密匙不正确");
   		}
         ByteArrayInputStream stringInputStream = new ByteArrayInputStream(
                sb.toString().getBytes());
         
         return stringInputStream;
    }
    
    public static boolean toDecrypt() throws FileNotFoundException, IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, ClassNotFoundException  {
    	/*boolean rslt=false;
    	System.out.println("正在获取认证结果...");
    	//认证信息初始化
    	String Identification=GetpcInfo.getCPUSerial()+GetpcInfo.getHardDiskSN("c")+GetpcInfo.getMotherboardSN();
    	Identification = Encrypt.getMD5(Identification.trim());
    	String urls = "http://211.149.234.199:19997/UserInfoCheck/InfoCheck?";
        urls = urls + "account=" + account + "&psd=" + password + "&Identification=" + Identification;
    	URL url=new URL(urls);
    	
		URLConnection con=url.openConnection();
		ObjectInputStream ois=new ObjectInputStream(con.getInputStream());
		UserInfoRstl icr=null;
		try {
			icr = (UserInfoRstl)ois.readObject();
			System.out.println("已获取认证结果");
			System.out.println("用户账户:"+icr.getAccount());
			System.out.println("用户密码:"+icr.getPsd());
			System.out.println("认证结果:"+icr.getContent());
			if("T".equals(icr.getState())){
				rslt=true;
			}
		} catch (ClassNotFoundException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
    	
    	
         return rslt;*/
    	return true;
    }
}
