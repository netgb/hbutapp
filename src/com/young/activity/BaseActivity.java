package com.young.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.young.R;
import com.young.util.UpdateManager;

import java.lang.reflect.Method;

public abstract class BaseActivity extends Activity {

	// 监听非MainActivity所有的返回按钮，如果返回就杀这个activity用来释放资源
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		setIconEnable(menu, true);
		getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.about_us:
			Toast.makeText(this, "关于我们", Toast.LENGTH_LONG).show();
			break;
		case R.id.check_update:
			new Thread() {
				@Override
				public void run() {
					Looper.prepare();
					UpdateManager manager = new UpdateManager(BaseActivity.this);
					// 检查软件更新
					manager.checkUpdate();
					Looper.loop();
				}
			}.start();

			break;
		case R.id.exit:
			@SuppressWarnings("deprecation")
			SharedPreferences sp = this.getSharedPreferences("userInfo",
					Context.MODE_WORLD_READABLE);
			SharedPreferences.Editor editor = sp.edit();
			editor.remove("USER_NAME");
			editor.remove("PASSWORD");
			editor.commit();
			Intent intent = new Intent(this, LoginActivity.class);
			this.startActivity(intent);
			Toast.makeText(this, "注销成功", Toast.LENGTH_LONG).show();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	// enable为true时，菜单添加图标有效，enable为false时无效。4.0系统默认无效
	private void setIconEnable(Menu menu, boolean enable) {
		try {
			Class<?> clazz = Class
					.forName("com.android.internal.view.menu.MenuBuilder");
			Method m = clazz.getDeclaredMethod("setOptionalIconsVisible",
					boolean.class);
			m.setAccessible(true);

			// MenuBuilder实现Menu接口，创建菜单时，传进来的menu其实就是MenuBuilder对象(java的多态特征)
			m.invoke(menu, enable);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
