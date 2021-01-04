package com.xu.xunlei.ui;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.custom.TableCursor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class XunLei {

	protected Shell shell;
	private Table table;
	private Text text;
	private Display display;
	
	private Map<String,Task> map = new HashMap<String,Task>();

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			XunLei window = new XunLei();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(1005, 634);
		shell.setText("下载工具");
		shell.setLayout(new FillLayout(SWT.HORIZONTAL));
		
//		shell.setLocation((display.getClientArea().width-shell.getSize().x)/2,(display.getClientArea().height-shell.getSize().y/2));
		
		Composite composite = new Composite(shell, SWT.NONE);
		composite.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		SashForm sashForm = new SashForm(composite, SWT.VERTICAL);
		sashForm.setOrientation(SWT.VERTICAL);
		
		Composite composite_1 = new Composite(sashForm, SWT.NONE);
		
		Label lblNewLabel = new Label(composite_1, SWT.NONE);
		lblNewLabel.setAlignment(SWT.RIGHT);
		lblNewLabel.setBounds(21, 10, 92, 20);
		lblNewLabel.setText("下载地址");
		
		text = new Text(composite_1, SWT.BORDER);
		text.setBounds(119, 7, 737, 26);
		
		Button button = new Button(composite_1, SWT.NONE);
		
		button.setBounds(862, 5, 98, 30);
		button.setText("下载");
		
		Composite composite_2 = new Composite(sashForm, SWT.NONE);
		composite_2.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		table = new Table(composite_2, SWT.BORDER | SWT.FULL_SELECTION);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		TableColumn tblclmnNewColumn = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn.setWidth(303);
		tblclmnNewColumn.setText("下载地址");
		
		TableColumn tblclmnNewColumn_1 = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn_1.setWidth(323);
		tblclmnNewColumn_1.setText("保存路径");
		
		TableColumn tblclmnNewColumn_2 = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn_2.setWidth(227);
		tblclmnNewColumn_2.setText("下载进度");
		
		TableColumn tblclmnNewColumn_3 = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn_3.setWidth(158);
		tblclmnNewColumn_3.setText("状态");
		
		TableCursor tableCursor = new TableCursor(table,SWT.NONE);
		
		Menu menu = new Menu(tableCursor);
		tableCursor.setMenu(menu);
		
		MenuItem menuItem = new MenuItem(menu,SWT.NONE);
		
		menuItem.setText("暂停");
		
		MenuItem menuItem_1 = new MenuItem(menu,SWT.NONE);
		menuItem_1.setText("删除");
		
		MenuItem menuItem_2 = new MenuItem(menu,SWT.NONE);
		
		menuItem_2.setText("继续");
		sashForm.setWeights(new int[] {47, 537});
		
		//点击下载时
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String url = text.getText().trim();	//获取下载地址
				if(url==null || "".equals(url)) {
					MessageDialog.openInformation(shell, "提示", "请输入下载地址");
					return;
				}
				
				text.setText("");
				DirectoryDialog fd  = new DirectoryDialog(shell,SWT.SAVE);
				fd.setText("下载保存路径选择");
				fd.setMessage("请选择您要保存的路径");
				fd.setFilterPath("SystemDrive");
				String path = fd.open();
				if( path == null || "".equals(path)) {
					return;
				}
				
				Task task = new Task(table,path,url);
				map.put(url.substring(url.lastIndexOf("/")+1),task);
				Thread th = new Thread(task);
				th.start();
			}
		});
		
		
		//暂停
		menuItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TableItem ti = tableCursor.getRow();
				Task task = map.get(ti.getData("fileName"));
				task.pause = true;
			}
		});
		
		//继续
		menuItem_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TableItem ti = tableCursor.getRow();
				Task task = map.get(ti.getData("fileName"));
				task.restart();
			}
		});
		

	}
}
