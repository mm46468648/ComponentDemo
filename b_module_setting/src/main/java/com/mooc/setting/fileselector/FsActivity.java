package com.mooc.setting.fileselector;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class FsActivity extends FileSelectActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void initParams() {
		// 这些参数都有默认值，不设置也是可以的
		// setSelectorFileTitle("this is file title");
		// setSelectorFolderTitle("this is folder title");
		// setSelectorFileIcon(R.drawable.ic_fileselect_file);
		// setSelectorFolderIcon(R.drawable.ic_fileselect_folder);
		// setSelectorIconHeight(150);
		// setSelectorIconWidth(150); }
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	@Override
	public void onClickOkBtn() {
		super.onClickOkBtn();
	}
}
