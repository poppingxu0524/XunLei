package com.xu.xunlei.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

public class Task implements Runnable{
	private String urlPath; //下载地址
	private String savePath;  //保存地址
	private String fileName;  // 文件名
	private Table table; 	//表格
	private TableItem ti; 
	private ProgressBar bar;	//进度条
	private int totalSize = 0;  //总大小
	private int size=0;		//已下载大小
	
	volatile boolean pause = false; //是否暂停
	
	public Task(Table table,String savePath,String urlPath) {
		this.table = table;
		this.savePath = savePath;
		this.urlPath = urlPath;
		this.fileName = urlPath.substring(urlPath.lastIndexOf("/") + 1);	//截取最后一截做文件名
	}
	

	@Override
	public void run() {
		//说明要开始下载了
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				ti = new TableItem(table,SWT.NONE);
				ti.setData("fileName",fileName);
				ti.setText(0,urlPath);
				ti.setText(1,savePath);
				ti.setText(3,"下载中");
				
				TableEditor  editor = new TableEditor(table);
				bar = new ProgressBar(table,SWT.NONE);
				bar.setMinimum(0);
				bar.setMaximum(100);
				
				editor.grabHorizontal = true;
				ti.setData("bar",bar);
			}
			
		});
		
		FileOutputStream fos = null;
		InputStream is = null;
		HttpsURLConnection con= null;
		
		try {
			URL url = new URL(urlPath);
			con = (HttpsURLConnection) url.openConnection();
			
			con.setRequestMethod("GET");  //设置连接方式
			con.setConnectTimeout(5000); //设置超时时间
			
			//断点续传
			con.addRequestProperty("Range", "bytes=100-200"); 
			
			con.connect();
			
			totalSize =  con.getContentLength();  //获取总资源大小
			
			is  = con.getInputStream();
			File fl = new File(savePath);
			fos = new FileOutputStream(fl);
			
			byte[] bt  = new byte[1024];
			int len = -1;
			
			while((len = is.read(bt)) !=-1 ) {
				
				synchronized (this) {
					if(pause) {
						this.wait();
					}
				}
				fos.write(bt,0,len);
				fos.flush();
				
				size += len;
				
				//改变进度条
				Display.getDefault().asyncExec(new Runnable() {

					@Override
					public void run() {
						bar.setSelection((int)(size * 1.0 / totalSize * 100));
					}
					
				});
			}
			
			Display.getDefault().asyncExec(new Runnable() {

				@Override
				public void run() {
					bar.setSelection(100);
					ti.setText("下载完成");
				}
				
			});
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(fos!=null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if(is!=null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if(con!=null) {
				con.disconnect();
			}
		}

	}
	
	public void restart() {
		this.pause =false;
		synchronized (this) {
			this.notifyAll();
		}
	}

}
